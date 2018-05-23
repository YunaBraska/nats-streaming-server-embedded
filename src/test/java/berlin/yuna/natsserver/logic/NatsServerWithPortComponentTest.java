package berlin.yuna.natsserver.logic;

import berlin.yuna.natsserver.annotation.EnableNatsServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.Socket;

import static berlin.yuna.natsserver.util.PortUtil.waitForPortShutdown;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableNatsServer(port = 5222, natsServerConfig = "port:6222")
public class NatsServerWithPortComponentTest {

    @Autowired
    private NatsServer natsServer;

    @Test
    public void natsServer_customShouldPortOverwritePort() throws IOException {
        new Socket("localhost", 6222).close();
        assertThat(natsServer, is(notNullValue()));
        assertThat(natsServer.getPort(), is(6222));
        natsServer.stop();
        waitForPortShutdown(5222);
        waitForPortShutdown(6222);
    }
}
