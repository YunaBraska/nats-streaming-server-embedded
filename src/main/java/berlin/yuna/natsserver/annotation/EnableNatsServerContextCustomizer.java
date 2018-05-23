package berlin.yuna.natsserver.annotation;

import berlin.yuna.natsserver.logic.NatsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.util.Assert;

public class EnableNatsServerContextCustomizer implements ContextCustomizer {

    private final EnableNatsServer enableNatsServer;
    private static final Logger LOG = LoggerFactory.getLogger(EnableNatsServerContextCustomizer.class);

    EnableNatsServerContextCustomizer(EnableNatsServer enableNatsServer) {
        this.enableNatsServer = enableNatsServer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        Assert.isInstanceOf(DefaultSingletonBeanRegistry.class, beanFactory);

        NatsServer natsServerBean = new NatsServer(this.enableNatsServer.natsServerConfig());
        try {
            natsServerBean.getPort();
        } catch (Exception e) {
            natsServerBean.getNatsServerConfig().put("port", String.valueOf(enableNatsServer.port()));
        }

        try {
            natsServerBean.start();
        } catch (Exception e) {
            LOG.error("Failed to initialise [{}]", EnableNatsServer.class, e);
        }

        beanFactory.initializeBean(natsServerBean, NatsServer.name);
        beanFactory.registerSingleton(NatsServer.name, natsServerBean);
        ((DefaultSingletonBeanRegistry) beanFactory).registerDisposableBean(NatsServer.name, natsServerBean);
    }
}
