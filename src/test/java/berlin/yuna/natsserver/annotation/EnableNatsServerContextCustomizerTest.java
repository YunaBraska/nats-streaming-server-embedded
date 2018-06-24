package berlin.yuna.natsserver.annotation;

import berlin.yuna.natsserver.logic.NatsServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static berlin.yuna.natsserver.config.NatsServerConfig.PORT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EnableNatsServerContextCustomizerTest {

    @Autowired
    private ConfigurableApplicationContext context;

    @Test(expected = IllegalArgumentException.class)
    public void runCustomizer_withInvalidPort_shouldNotStartNatsServer() {
        EnableNatsServer enableNatsServer = mock(EnableNatsServer.class);
        when(enableNatsServer.natsServerConfig()).thenReturn(new String[]{PORT + ":invalidPortValue"});
        EnableNatsServerContextCustomizer customizer = new EnableNatsServerContextCustomizer(enableNatsServer);
        customizer.customizeContext(context, mock(MergedContextConfiguration.class));
        assertThat(context.getBean(NatsServer.class), is(nullValue()));
    }
}