package berlin.yuna.natsserver.streaming.embedded.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static berlin.yuna.natsserver.config.NatsStreamingConfig.PORT;
import static berlin.yuna.natsserver.config.NatsStreamingOptions.natsStreamingBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

@Tag("UnitTest")
@DisplayName("Configuration unit test")
class NatsServerTest {

    @Test
    void runNatsServer() throws Exception {
        try (final NatsStreamingServer natsServer = new NatsStreamingServer(natsStreamingBuilder().timeoutMs(5000).config(PORT, "-1").build())) {
            assertThat(natsServer.pid(), is(greaterThan(-1)));
        }
    }
}
