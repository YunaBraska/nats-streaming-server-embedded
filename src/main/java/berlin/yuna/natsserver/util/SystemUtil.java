package berlin.yuna.natsserver.util;

import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.ARM;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.LINUX;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.MAC;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.SOLARIS;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.UNKNOWN;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.WINDOWS;

public class SystemUtil {

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
}
