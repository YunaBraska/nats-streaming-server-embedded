package berlin.yuna.natsserver.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.ARM;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.LINUX;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.MAC;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.SOLARIS;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.UNKNOWN;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.WINDOWS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SystemUtilTest {

    private final static String osName = System.getProperty("os.name");
    private final static String osArch = System.getProperty("os.arch");
    
    @After
    public void tearDown() {
        System.setProperty("os.name", osName);
        System.setProperty("os.arch", osArch);
    }

    @Test
    public void getOsType_withArm_shouldReturnArm() {
        System.setProperty("os.arch", "arm-linux");
        System.setProperty("os.name", "notRelevant");
        assertThat(SystemUtil.getOsType(), is(ARM));
    }

    @Test
    public void getOsType_withLinux_shouldReturnLinux() {
        System.setProperty("os.name", "linux");
        assertThat(SystemUtil.getOsType(), is(LINUX));
    }

    @Test
    public void getOsType_withUnix_shouldReturnLinux() {
        System.setProperty("os.name", "unix");
        assertThat(SystemUtil.getOsType(), is(LINUX));
    }

    @Test
    public void getOsType_withMac_shouldReturnMac() {
        System.setProperty("os.name", "mac");
        assertThat(SystemUtil.getOsType(), is(MAC));
    }

    @Test
    public void getOsType_withSonus_shouldReturnSolaris() {
        System.setProperty("os.name", "sunos");
        assertThat(SystemUtil.getOsType(), is(SOLARIS));
    }

    @Test
    public void getOsType_withAix_shouldReturnLinux() {
        System.setProperty("os.name", "ibm-aix");
        assertThat(SystemUtil.getOsType(), is(LINUX));
    }

    @Test
    public void getOsType_withWindows_shouldReturnWindows() {
        System.setProperty("os.name", "MsDos Windows 3.1");
        assertThat(SystemUtil.getOsType(), is(WINDOWS));
    }

    @Test
    public void getOsType_withOtherOS_shouldReturnUnknown() {
        System.setProperty("os.name", "otherOth");
        assertThat(SystemUtil.getOsType(), is(UNKNOWN));
    }
}