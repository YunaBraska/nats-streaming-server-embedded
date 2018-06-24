package berlin.yuna.natsserver.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.ARM;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.LINUX;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.MAC;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.SOLARIS;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.UNKNOWN;
import static berlin.yuna.natsserver.util.SystemUtil.OperatingSystem.WINDOWS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SystemUtilTest {

    private final static String osName = System.getProperty("os.name");
    private final static String osArch = System.getProperty("os.arch");
    private final static String javaTmpDir = System.getProperty("java.io.tmpdir");

    private final String testFileOrigin = "banner.png";
    private final File testFileCopy = new File(System.getProperty("java.io.tmpdir"), testFileOrigin);

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(testFileCopy.toPath());
    }

    @After
    public void tearDown() {
        System.setProperty("os.name", osName);
        System.setProperty("os.arch", osArch);
        System.setProperty("java.io.tmpdir", javaTmpDir);
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

    @Test
    public void copyResourceFile_shouldBeSuccessful() {
        Path outputPath = SystemUtil.copyResourceFile(testFileOrigin);
        assertThat(outputPath, is(notNullValue()));
    }

    @Test
    public void copyResourceFile_WithExistingFile_shouldBeSuccessful() {
        Path outputPathFirst = SystemUtil.copyResourceFile(testFileOrigin);
        Path outputPathSecond = SystemUtil.copyResourceFile(testFileOrigin);
        assertThat(outputPathFirst, is(notNullValue()));
        assertThat(outputPathSecond, is(notNullValue()));
        assertThat(outputPathSecond, is(outputPathFirst));
    }

    @Test(expected = RuntimeException.class)
    public void copyResourceFile_WithOutPermission_shouldThrowRuntimeException() {
        System.setProperty("java.io.tmpdir", "/tmp_removable/");
        SystemUtil.copyResourceFile(testFileOrigin);
    }

    @Test
    public void fixFilePermissions_shouldBeSuccessful() {
        SystemUtil.fixFilePermissions(SystemUtil.copyResourceFile(testFileOrigin));
    }

    @Test
    public void fixFilePermissions_WithNonExistingPath_shouldNotThrowException() throws IOException {
        Path path = SystemUtil.copyResourceFile(testFileOrigin);
        Files.deleteIfExists(path);
        assertThat(path, is(notNullValue()));

        SystemUtil.fixFilePermissions(path);
    }

    @Test
    public void getCommandByOsType_withUnix_shouldBuildCorrectly() {
        System.setProperty("os.name", "linux");
        String[] command = SystemUtil.getCommandByOsType("ls");

        assertThat(command, is(notNullValue()));
        assertThat(command, is(new String[]{"sh", "-c", "ls"}));
    }

    @Test
    public void getCommandByOsType_withWindows_shouldBuildCorrectly() {
        System.setProperty("os.name", "MsDos Windows 3.1");
        String[] command = SystemUtil.getCommandByOsType("dir");

        assertThat(command, is(notNullValue()));
        assertThat(command, is(new String[]{"cmd.exe", "/c", "dir"}));
    }

    @Test
    public void newInstance_smokeTest() {
        assertThat(new SystemUtil(), is(notNullValue()));
    }
}