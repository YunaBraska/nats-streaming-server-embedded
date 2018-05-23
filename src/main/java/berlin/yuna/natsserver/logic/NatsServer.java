package berlin.yuna.natsserver.logic;

import berlin.yuna.natsserver.util.SystemUtil.OperatingSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceConfigurationError;

import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.WINDOWS;
import static berlin.yuna.natsserver.util.SystemUtil.getOsType;
import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * {@link NatsServer}
 *
 * @author Yuna Morgenstern
 * @see OperatingSystem
 * @see NatsServer
 * @since 1.0
 */
public class NatsServer implements DisposableBean {

    public static final String name = NatsServer.class.getSimpleName();
    private static final Logger LOG = LoggerFactory.getLogger(NatsServer.class);
    private static final String NATS_EXECUTION_FILE = "nats-streaming-server";
    private static final String NATS_SERVER_FOLDER_VERSION = "nats-streaming-server-v0.9.2";

    private Map<String, String> natsServerConfig = new HashMap<>();

    private Process process;

    /**
     * Create non start able {@link NatsServer} without any configuration
     */
    public NatsServer() {
    }

    /**
     * Create start able {@link NatsServer} with simplest configuration
     *
     * @param port start port - common default port is 4222
     */
    public NatsServer(int port) {
        natsServerConfig.put("port", String.valueOf(port));
    }

    /**
     * Create custom {@link NatsServer} with simplest configuration
     *
     * @param natsServerConfig passes the original parameters to the server. example: port:4222, user:admin, password:admin
     */
    public NatsServer(String... natsServerConfig) {
        this.setNatsServerConfig(natsServerConfig);
    }

    public Map<String, String> getNatsServerConfig() {
        return natsServerConfig;
    }

    /**
     * Passes the original parameters to the server on startup
     *
     * @param natsServerConfig passes the original parameters to the server.
     * @see NatsServer#setNatsServerConfig(String...)
     */
    public void setNatsServerConfig(Map<String, String> natsServerConfig) {
        this.natsServerConfig = natsServerConfig;
    }

    /**
     * Passes the original parameters to the server on startup
     *
     * @param natsServerConfigArray example: port:4222, user:admin, password:admin
     */
    public void setNatsServerConfig(String... natsServerConfigArray) {
        Map<String, String> natsServerConfig = new HashMap<>();
        for (String property : natsServerConfigArray) {
            if (!StringUtils.hasText(property)) {
                continue;
            }
            String[] pair = property.split(":");
            if (pair.length < 2) {
                pair = property.split("=");
            }
            if (pair.length < 2) {
                pair = property.split(" ");
            }
            if (pair.length != 2) {
                LOG.error("Could not parse property [{}] pair length [{}]", property, pair.length);
                continue;
            }
            natsServerConfig.put(pair[0], pair[1]);
        }
        this.natsServerConfig = natsServerConfig;
    }

    public void start() throws IOException {
        if (process != null) {
            LOG.error("[{}] is already running", name);
            return;
        }
        if (!isPortAvailable(getPort())) {
            throw new BindException("Address already in use [" + getPort() + "]");
        }
        Path natsServerPath = getNatsServerPath();
        Path os = natsServerPath.getParent().getFileName();
        Path version = natsServerPath.getParent().getParent().getFileName();
        LOG.info("Starting [{}] version [{}-{}]", name, version, os);

        try {
            Files.setPosixFilePermissions(natsServerPath, EnumSet.of(OTHERS_EXECUTE, GROUP_EXECUTE, OWNER_EXECUTE));
        } catch (IOException e) {
            LOG.warn("Could not set permission to file [{}]", Arrays.asList(Files.getPosixFilePermissions(natsServerPath).toArray()), e.getMessage());
        }

        StringBuilder command = new StringBuilder();
        command.append(natsServerPath.toString());
        for (Entry<String, String> entry : natsServerConfig.entrySet()) {
            String key = entry.getKey().trim().toLowerCase();
            command.append(" ");
            if (!entry.getKey().startsWith("--") && !entry.getKey().startsWith("-")) {
                command.append("--");
            }
            command.append(key);
            command.append(" ");
            command.append(entry.getValue().trim().toLowerCase());
        }

        ProcessBuilder builder = new ProcessBuilder();
        if (getOsType() == WINDOWS) {
            builder.command("cmd.exe", "/c", command.toString());
        } else {
            builder.command("sh", "-c", command.toString());
        }

        process = builder.start();
        waitForPort();
        LOG.info("Started [{}] version [{}-{}]", name, version, os);
    }

    public void stop() {
        LOG.info("Stopping [{}]", name);
        if (process == null) {
            LOG.warn("Could not stop [{}] cause cant find process", name);
            return;
        }
        process.destroy();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            LOG.error("Shutdown was interrupted", e);
        } finally {
            LOG.info("Stopped [{}]", name);
        }
    }

    public int getPort() {
        for (String key : new String[]{"port", "--port", "-p"}) {
            String port = natsServerConfig.get(key);
            if (port != null) {
                return Integer.valueOf(port);
            }
        }
        throw new RuntimeException(new ServiceConfigurationError("Could not initialise port" + name));
    }

    @Override
    public void destroy() {
        stop();
    }

    private static boolean isPortAvailable(int port) {
        try {
            new Socket("localhost", port).close();
            return false;
        } catch (IOException e) {
            return true;
        }

    }

    private void waitForPort() {
        final long start = System.currentTimeMillis();
        long timeout = SECONDS.toMillis(10);

        while (System.currentTimeMillis() - start < timeout) {
            if (!isPortAvailable(getPort())) {
                return;
            }
            Thread.yield();
        }
        throw new RuntimeException(new ConnectException(name + "failed to start."));
    }

    private Path getNatsServerPath() {
        return Paths.get(requireNonNull(getClass().getClassLoader().getResource(NATS_SERVER_FOLDER_VERSION + File.separator + getOsType().toString().toLowerCase() + File.separator + NATS_EXECUTION_FILE)).getFile());
    }
}