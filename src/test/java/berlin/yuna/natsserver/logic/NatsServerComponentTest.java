package berlin.yuna.natsserver.logic;

import berlin.yuna.natsserver.annotation.EnableNatsServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.Socket;

import static berlin.yuna.natsserver.util.PortUtil.waitUntilPortIsFree;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableNatsServer
public class NatsServerComponentTest {

    @Autowired
    private NatsServer natsServer;

    @Test
    public void natsServer_shouldStart() throws IOException {
        new Socket("localhost", 4222).close();
        assertThat(natsServer, is(notNullValue()));
        assertThat(natsServer.getPort(), is(4222));
        natsServer.stop();
    }

    @Test
    public void secondNatsServer_withSpaceSeparatedProperty_shouldStartSuccessful() {
        assertNatsServerStart("--port 5222");
    }

    @Test
    public void secondNatsServer_withEqualsSeparatedProperty_shouldStartSuccessful() {
        assertNatsServerStart("--port=5222");
    }

    @Test
    public void secondNatsServer_withDoublePointSeparatedProperty_shouldStartSuccessful() {
        assertNatsServerStart("--port:5222");
    }

    @Test
    public void secondNatsServer_withOutMinusProperty_shouldStartSuccessful() {
        assertNatsServerStart("port:5222");
    }

    @Test
    public void secondNatsServer_withOneMinusProperty_shouldStartSuccessful() {
        assertNatsServerStart("-p:5222");
    }

    @Test(expected = RuntimeException.class)
    public void secondNatsServer_withInvalidProperty_shouldFailToStart() {
        assertNatsServerStart("p:5222");
    }

    private void assertNatsServerStart(String... natsServerConfig) {
        NatsServer natsServer = new NatsServer(natsServerConfig);
        try {
            natsServer.start();
            new Socket("localhost", 5222).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            natsServer.stop();
            waitUntilPortIsFree(5222);
        }
    }
}
