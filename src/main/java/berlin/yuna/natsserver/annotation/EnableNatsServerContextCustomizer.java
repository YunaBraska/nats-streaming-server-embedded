package berlin.yuna.natsserver.annotation;

import berlin.yuna.natsserver.logic.NatsServer;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.util.Assert;

import static org.slf4j.LoggerFactory.getLogger;

public class EnableNatsServerContextCustomizer implements ContextCustomizer {

    private final EnableNatsServer enableNatsServer;
    private static final Logger LOG = getLogger(EnableNatsServerContextCustomizer.class);

    /**
     * Sets the source with parameter {@link EnableNatsServer} {@link EnableNatsServerContextCustomizer#customizeContext(ConfigurableApplicationContext, MergedContextConfiguration)}
     *
     * @param enableNatsServer {@link EnableNatsServer} annotation class
     */
    EnableNatsServerContextCustomizer(EnableNatsServer enableNatsServer) {
        this.enableNatsServer = enableNatsServer;
    }

    /**
     * customizeContext will start register {@link NatsServer} with bean name {@link NatsServer#BEAN_NAME} to the spring test context
     *
     * @param context      {@link ConfigurableApplicationContext}
     * @param mergedConfig {@link MergedContextConfiguration} is not in use
     */
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

        beanFactory.initializeBean(natsServerBean, NatsServer.BEAN_NAME);
        beanFactory.registerSingleton(NatsServer.BEAN_NAME, natsServerBean);
        ((DefaultSingletonBeanRegistry) beanFactory).registerDisposableBean(NatsServer.BEAN_NAME, natsServerBean);
    }
}
