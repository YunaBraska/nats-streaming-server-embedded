package berlin.yuna.natsserver.streaming.embedded.logic;

import berlin.yuna.natsserver.streaming.embedded.annotation.EnableNatsStreamingServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.Socket;

import static berlin.yuna.natsserver.config.NatsStreamingConfig.PORT;
import static berlin.yuna.natsserver.config.NatsStreamingOptions.natsStreamingBuilder;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableNatsStreamingServer(port = -1)
@Tag("IntegrationTest")
@DisplayName("NatsServerComponentTest")
class NatsServerComponentTest {

    @Autowired
    private NatsStreamingServer natsServer;

    @Test
    @DisplayName("Start server on a random port")
    void natsServer_shouldStartOnARandomPort() throws IOException {
        assertThat(natsServer, is(notNullValue()));
        assertThat(natsServer.port(), is(greaterThan((int) PORT.defaultValue())));
        assertThat(natsServer.pid(), is(greaterThan(-1)));
        assertNatsServerReachable(natsServer);
    }

    @Test
    @DisplayName("Port config with double dash")
    void secondNatsServer_withDoubleDotSeparatedProperty_shouldStartSuccessful() {
        assertNatsServerStart("--port", "-1");
    }

    @Test
    @DisplayName("Port config without dashes")
    void secondNatsServer_withOutMinusProperty_shouldStartSuccessful() {
        assertNatsServerStart("port", "-1");
    }

    @Test
    @DisplayName("Invalid config [FAIL]")
    void secondNatsServer_withInvalidProperty_shouldFailToStart() {
        assertThrows(
                IllegalArgumentException.class,
                () -> assertNatsServerStart("p", "-1"),
                "No enum constant"
        );
    }

    @Test
    @DisplayName("ToString")
    void toString_shouldPrintPortAndOs() {
        final String serverString = natsServer.toString();
        assertThat(serverString, containsString(String.valueOf(natsServer.port())));
    }

    private void assertNatsServerStart(final String... config) {
        try (final NatsStreamingServer natsServer = new NatsStreamingServer(natsStreamingBuilder().timeoutMs(10000).config(config).build())) {
            assertNatsServerReachable(natsServer);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void assertNatsServerReachable(final NatsStreamingServer server) throws IOException {
        new Socket("localhost", server.port()).close();
    }
}
