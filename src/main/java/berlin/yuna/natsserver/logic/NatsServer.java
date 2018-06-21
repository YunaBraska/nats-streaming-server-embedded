package berlin.yuna.natsserver.logic;

import berlin.yuna.natsserver.util.SystemUtil.OperatingSystem;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.PortUnreachableException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.WINDOWS;
import static berlin.yuna.natsserver.util.SystemUtil.getOsType;
import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * {@link NatsServer}
 *
 * @author Yuna Morgenstern
 * @see OperatingSystem
 * @see NatsServer
 * @since 1.0
 */
public class NatsServer implements DisposableBean {

    /**
     * simpleName from {@link NatsServer} class
     */
    public static final String BEAN_NAME = NatsServer.class.getSimpleName();
    private static final Logger LOG = getLogger(NatsServer.class);
    private static final String NATS_SERVER_VERSION = "v0.9.2";
    private static final OperatingSystem OPERATING_SYSTEM = getOsType();

    private Map<String, String> natsServerConfig = new HashMap<>();

    private Process process;

    /**
     * Create {@link NatsServer} without any start able configuration
     */
    public NatsServer() {
    }

    /**
     * Create {@link NatsServer} with simplest start able configuration
     *
     * @param port start port - common default port is 4222
     */
    public NatsServer(int port) {
        natsServerConfig.put("port", String.valueOf(port));
    }

    /**
     * Create custom {@link NatsServer} with simplest configuration {@link NatsServer#setNatsServerConfig(String...)}
     *
     * @param natsServerConfig passes the original parameters to the server. example: port:4222, user:admin, password:admin
     */
    public NatsServer(String... natsServerConfig) {
        this.setNatsServerConfig(natsServerConfig);
    }

    /**
     * GetNatServerConfig
     *
     * @return the {@link NatsServer} configuration but not the config of the real PID
     */
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
        natsServerConfig = new HashMap<>();
        for (String property : natsServerConfigArray) {
            String[] pair = property.split(":");
            if (!StringUtils.hasText(property) || pair.length != 2) {
                LOG.error("Could not parse property [{}] pair length [{}]", property, pair.length);
                continue;
            }
            natsServerConfig.put(pair[0], pair[1]);
        }
    }

    /**
     * Starts the server in {@link ProcessBuilder} with the given parameterConfig {@link NatsServer#setNatsServerConfig(String...)}
     *
     * @throws IOException              if {@link NatsServer} is not found or unsupported on the {@link OperatingSystem}
     * @throws BindException            if port is already taken
     * @throws PortUnreachableException if {@link NatsServer} is not starting cause port is not free
     */
    public void start() throws IOException {
        if (process != null) {
            LOG.error("[{}] is already running", BEAN_NAME);
            return;
        }

        if (!waitForPort(true)) {
            throw new BindException("Address already in use [" + getPort() + "]");
        }

        Path natsServerPath = getNatsServerPath();
        LOG.info("Starting [{}] port [{}] version [{}-{}]", BEAN_NAME, getPort(), NATS_SERVER_VERSION, OPERATING_SYSTEM);

        String command = prepareCommand(natsServerPath);

        executeCommand(command);

        if (!waitForPort(false)) {
            throw new PortUnreachableException(BEAN_NAME + "failed to start.");
        }
        LOG.info("Started [{}] port [{}] version [{}-{}]", BEAN_NAME, getPort(), NATS_SERVER_VERSION, OPERATING_SYSTEM);
    }

    /**
     * Stops the {@link ProcessBuilder} and kills the {@link NatsServer}
     * Only a log error will occur if the {@link NatsServer} were never started
     *
     * @throws RuntimeException as {@link InterruptedException} if shutdown is interrupted
     */
    public void stop() {
        try {
            LOG.info("Stopping [{}]", BEAN_NAME);
            process.destroy();
            process.waitFor();
        } catch (NullPointerException | InterruptedException e) {
            LOG.warn("Could not stop [{}] cause cant find process", BEAN_NAME);
        } finally {
            LOG.info("Stopped [{}]", BEAN_NAME);
        }
    }

    /**
     * Gets the port out of the configuration not from the real PID
     *
     * @return configured port of the server
     * @throws RuntimeException with {@link ConnectException} when there is no port configured
     */
    public int getPort() {
        for (String key : new String[]{"port", "--port", "-p"}) {
            String port = natsServerConfig.get(key);
            if (port != null) {
                return Integer.valueOf(port);
            }
        }
        throw new RuntimeException(new ConnectException("Could not initialise port" + BEAN_NAME));
    }

    /**
     * Simply stops the {@link NatsServer}
     *
     * @see NatsServer#stop()
     */
    @Override
    public void destroy() {
        stop();
    }

    /**
     * Gets Nats server path
     *
     * @return Resource/{SIMPLE_CLASS_NAME}/{NATS_SERVER_VERSION}/{OPERATING_SYSTEM}/{SIMPLE_CLASS_NAME}
     */
    private Path getNatsServerPath() {
        StringBuilder path = new StringBuilder();
        path.append(BEAN_NAME.toLowerCase()).append(File.separator);
        path.append(NATS_SERVER_VERSION).append(File.separator);
        path.append(OPERATING_SYSTEM.toString().toLowerCase()).append(File.separator);
        path.append(BEAN_NAME.toLowerCase()).append((OPERATING_SYSTEM == WINDOWS ? ".exe" : ""));
        return copyResourceFile(path.toString());
    }

    private Path copyResourceFile(final String path) {
        File tmpFile = new File(System.getProperty("java.io.tmpdir"), new File(path).getName());
        if (!tmpFile.exists()) {
            LOG.info("Creating [{}]", BEAN_NAME);
            try {
                Files.copy(getClass().getClassLoader().getResourceAsStream(path), tmpFile.toPath());
                fixFilePermissions(tmpFile.toPath());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return tmpFile.toPath();
    }

    private boolean waitForPort(boolean isFree) {
        final long start = System.currentTimeMillis();
        long timeout = SECONDS.toMillis(10);

        while (System.currentTimeMillis() - start < timeout) {
            if (isPortAvailable(getPort()) == isFree) {
                return true;
            }
            Thread.yield();
        }
        return false;
    }

    private static boolean isPortAvailable(int port) {
        try {
            new Socket("localhost", port).close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    private void fixFilePermissions(Path natsServerPath) {
        try {
            Files.setPosixFilePermissions(natsServerPath, EnumSet.of(OTHERS_EXECUTE, GROUP_EXECUTE, OWNER_EXECUTE, OTHERS_READ, GROUP_READ, OWNER_READ));
        } catch (IOException e) {
            LOG.warn("Could not save permissions for [{}]", natsServerPath.toString(), e);
        }
    }

    private void executeCommand(String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        if (OPERATING_SYSTEM == WINDOWS) {
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("sh", "-c", command);
        }
        process = builder.start();
    }

    private String prepareCommand(Path natsServerPath) {
        StringBuilder command = new StringBuilder();
        command.append(natsServerPath.toString());
        for (Entry<String, String> entry : getNatsServerConfig().entrySet()) {
            String key = entry.getKey().trim().toLowerCase();
            command.append(" ");
            if (!entry.getKey().startsWith("--") && !entry.getKey().startsWith("-")) {
                command.append("--");
            }
            command.append(key);
            command.append(" ");
            command.append(entry.getValue().trim().toLowerCase());
        }
        return command.toString();
    }

    @Override
    public String toString() {
        int port;
        try {
            port = getPort();
        } catch (Exception e) {
            port = -1;
        }
        return BEAN_NAME + "{" +
                "NATS_SERVER_VERSION=" + NATS_SERVER_VERSION +
                ", OPERATING_SYSTEM=" + OPERATING_SYSTEM +
                ", port=" + port +
                '}';
    }
}