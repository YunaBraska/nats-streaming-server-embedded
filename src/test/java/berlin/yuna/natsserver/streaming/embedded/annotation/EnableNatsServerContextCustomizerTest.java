package berlin.yuna.natsserver.streaming.embedded.annotation;

import berlin.yuna.natsserver.config.NatsStreamingConfig;
import berlin.yuna.natsserver.streaming.embedded.logic.NatsStreamingServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Tag("IntegrationTest")
@DisplayName("ContextCustomizerTest")
class EnableNatsServerContextCustomizerTest {

    @Autowired
    private ConfigurableApplicationContext context;

    @Test
    @DisplayName("with invalid port [FAIL]")
    void runCustomizer_withInvalidPort_shouldNotStartNatsServer() {
        final EnableNatsStreamingServer enableNatsServer = mock(EnableNatsStreamingServer.class);
        when(enableNatsServer.config()).thenReturn(new String[]{NatsStreamingConfig.PORT.name(), "invalidPortValue"});
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> context.getBean(NatsStreamingServer.class),
                "No qualifying bean of type"
        );
    }
}