package berlin.yuna.natsserver.logic;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class NatsServerWithoutAnnotationTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    public void natsServer_withoutAnnotation_shouldNotBeStarted() throws Exception {
        expectedException.expect(ConnectException.class);
        new Socket("localhost", 4222).close();
    }

    public void natsServer_withoutConfig_shouldNotStart() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("ServiceConfigurationError");
        new NatsServer().start();
    }

    @Test
    public void natsServer_configureConfig_shouldOverwriteOldOne() {
        NatsServer natsServer = new NatsServer();
        natsServer.setNatsServerConfig("user:adminUser", "password:adminPw");

        assertThat(natsServer.getNatsServerConfig().get("user"), is(equalTo("adminUser")));
        assertThat(natsServer.getNatsServerConfig().get("password"), is(equalTo("adminPw")));

        natsServer.setNatsServerConfig("user:newUser");
        assertThat(natsServer.getNatsServerConfig().get("user"), is(equalTo("newUser")));
        assertThat(natsServer.getNatsServerConfig().get("password"), is(nullValue()));

        Map<String, String> newConfig = new HashMap<>();
        newConfig.put("user", "oldUser");
        natsServer.setNatsServerConfig(newConfig);
        assertThat(natsServer.getNatsServerConfig().get("user"), is(equalTo("oldUser")));
    }

    @Test
    public void natsServer_invalidConfig_shouldNotRunIntroException() {
        NatsServer natsServer = new NatsServer();
        natsServer.setNatsServerConfig("user:adminUser:password", " ", "");
        assertThat(natsServer.getNatsServerConfig().size(), is(0));
    }

    @Test
    public void natsServer_duplicateStart_shouldNotRunIntroExceptionOrInterrupt() throws IOException {
        NatsServer natsServer = new NatsServer(4231);
        natsServer.start();
        natsServer.start();
        natsServer.stop();
    }

    @Test
    public void natsServer_withWrongConfig_shouldNotStartAndThrowException() throws IOException {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("ConnectException");
        NatsServer natsServer = new NatsServer("unknown:config", "port:4232");
        natsServer.start();
    }

    @Test
    public void natsServer_asTwoInstances_shouldThrowBindException() {
        NatsServer natsServer_one = new NatsServer(4233);
        NatsServer natsServer_two = new NatsServer(4233);
        Exception exception = null;
        try {
            natsServer_one.start();
            natsServer_two.start();
        } catch (Exception e) {
            exception = e;
        } finally {
            natsServer_one.stop();
            natsServer_two.stop();
        }
        assertThat(exception.getClass().getSimpleName(), is(equalTo(BindException.class.getSimpleName())));
    }

    @Test
    public void natsServer_stopWithoutStart_shouldNotRunIntroExceptionOrInterrupt() {
        NatsServer natsServer = new NatsServer();
        natsServer.stop();
    }
}
