package berlin.yuna.natsserver.logic;

import berlin.yuna.natsserver.annotation.EnableNatsServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableNatsServer(port = 4230, natsServerConfig = "invalid:config")
public class NatsServerWithInvalidConfigComponentTest {

    @Test(expected = ConnectException.class)
    public void natsServer_shouldStart() throws IOException {
        new Socket("localhost", 4230).close();
    }
}
