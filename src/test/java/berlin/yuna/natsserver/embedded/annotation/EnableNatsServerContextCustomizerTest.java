package berlin.yuna.natsserver.embedded.annotation;

import berlin.yuna.natsserver.config.NatsServerConfig;
import berlin.yuna.natsserver.embedded.logic.NatsServer;
import berlin.yuna.natsserver.embedded.model.exception.NatsStartException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.MergedContextConfiguration;

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
        EnableNatsServer enableNatsServer = mock(EnableNatsServer.class);
        when(enableNatsServer.natsServerConfig()).thenReturn(new String[]{NatsServerConfig.PORT + ":invalidPortValue"});
        EnableNatsServerContextCustomizer customizer = new EnableNatsServerContextCustomizer(enableNatsServer);
        Assertions.assertThrows(
                NatsStartException.class,
                () -> customizer.customizeContext(context, mock(MergedContextConfiguration.class)),
                "Failed to initialise EnableNatsServer"
        );
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> context.getBean(NatsServer.class),
                "No qualifying bean of type"
        );
    }
}