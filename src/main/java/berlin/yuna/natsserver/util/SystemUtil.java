package berlin.yuna.natsserver.util;

import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;

import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.ARM;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.LINUX;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.MAC;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.SOLARIS;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.UNKNOWN;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.WINDOWS;
import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static org.slf4j.LoggerFactory.getLogger;

public class SystemUtil {

    private static final Logger LOG = getLogger(SystemUtil.class);

    /**
     * Supported operation system enum
     */
    public enum OperatingSystem {
        ARM, LINUX, MAC, WINDOWS, SOLARIS, UNKNOWN
    }

    /**
     * Get current operating system
     *
     * @return current {@link OperatingSystem} if supported, else {@link OperatingSystem#UNKNOWN}
     */
    public static OperatingSystem getOsType() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        if (osArch.contains("arm")) {
            return ARM;
        } else if ((osName.contains("nix") || osName.contains("nux") || osName.indexOf("aix") > 0)) {
            return LINUX;
        } else if (osName.contains("mac")) {
            return MAC;
        } else if (osName.contains("win")) {
            return WINDOWS;
        } else if (osName.contains("sunos")) {
            return SOLARIS;
        } else {
            return UNKNOWN;
        }
    }

    public static Process executeCommand(String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
            builder.command(getCommandByOsType(command));
        return builder.start();
    }

    public static Path copyResourceFile(final String relativeResource) {
        File tmpFile = new File(System.getProperty("java.io.tmpdir"), new File(relativeResource).getName());
        if (!tmpFile.exists()) {
            LOG.info("Creating resource file[{}]", tmpFile.getAbsolutePath());
            try {
                Files.copy(SystemUtil.class.getClassLoader().getResourceAsStream(relativeResource), tmpFile.toPath());
                fixFilePermissions(tmpFile.toPath());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return tmpFile.toPath();
    }

    static String[] getCommandByOsType(String command) {
        if (getOsType() == WINDOWS) {
            return new String[]{"cmd.exe", "/c", command};
        } else {
            return new String[]{"sh", "-c", command};
        }
    }

    static void fixFilePermissions(Path natsServerPath) {
        try {
            Files.setPosixFilePermissions(natsServerPath, EnumSet.of(OTHERS_EXECUTE, GROUP_EXECUTE, OWNER_EXECUTE, OTHERS_READ, GROUP_READ, OWNER_READ));
        } catch (IOException e) {
            LOG.warn("Could not save permissions for [{}]", natsServerPath, e);
        }
    }
}
