package berlin.yuna.natsserver.streaming.embedded.annotation;

import berlin.yuna.natsserver.config.NatsStreamingConfig;
import berlin.yuna.natsserver.config.NatsStreamingOptionsBuilder;
import berlin.yuna.natsserver.streaming.embedded.logic.NatsStreamingServer;
import berlin.yuna.natsserver.streaming.embedded.model.exception.NatsStreamingStartException;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.util.Assert;

import static berlin.yuna.natsserver.config.NatsStreamingConfig.NATS_BINARY_PATH;
import static berlin.yuna.natsserver.config.NatsStreamingConfig.NATS_DOWNLOAD_URL;
import static berlin.yuna.natsserver.config.NatsStreamingConfig.NATS_PROPERTY_FILE;
import static berlin.yuna.natsserver.config.NatsStreamingConfig.PORT;
import static berlin.yuna.natsserver.config.NatsStreamingOptions.natsStreamingBuilder;
import static berlin.yuna.natsserver.logic.NatsUtils.isNotEmpty;
import static berlin.yuna.natsserver.streaming.embedded.logic.NatsStreamingServer.BEAN_NAME;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.hasText;

class EnableNatsStreamingServerContextCustomizer implements ContextCustomizer {

    private final EnableNatsStreamingServer enableNatsServer;
    private static final Logger LOG = getLogger(EnableNatsStreamingServerContextCustomizer.class);

    /**
     * Sets the source with parameter {@link EnableNatsStreamingServer} {@link EnableNatsStreamingServerContextCustomizer#customizeContext(ConfigurableApplicationContext, MergedContextConfiguration)}
     *
     * @param enableNatsServer {@link EnableNatsStreamingServer} annotation class
     */
    EnableNatsStreamingServerContextCustomizer(final EnableNatsStreamingServer enableNatsServer) {
        this.enableNatsServer = enableNatsServer;
    }

    /**
     * customizeContext will start register {@link NatsStreamingServer} with bean name {@link NatsStreamingServer#BEAN_NAME} to the spring test context
     *
     * @param context      {@link ConfigurableApplicationContext}
     * @param mergedConfig {@link MergedContextConfiguration} is not in use
     */
    @Override
    public void customizeContext(final ConfigurableApplicationContext context, final MergedContextConfiguration mergedConfig) {
        final ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        Assert.isInstanceOf(DefaultSingletonBeanRegistry.class, beanFactory);
        final ConfigurableEnvironment environment = context.getEnvironment();

        if (enableNatsServer == null) {
            LOG.debug("Skipping [{}] cause its not defined", EnableNatsStreamingServer.class.getSimpleName());
            return;
        }

        NatsStreamingServer natsServer = null;
        final NatsStreamingOptionsBuilder options = natsStreamingBuilder().timeoutMs(enableNatsServer.timeoutMs());
        setEnvConfig(options, environment);
        if (enableNatsServer.port() != (Integer) PORT.defaultValue()) {
            options.port(enableNatsServer.port());
        }
        options.config(enableNatsServer.config()).version(isNotEmpty(enableNatsServer.version()) ? enableNatsServer.version() : options.version());
        configure(options, NATS_PROPERTY_FILE, enableNatsServer.configFile());
        configure(options, NATS_BINARY_PATH, enableNatsServer.binaryFile());
        configure(options, NATS_DOWNLOAD_URL, enableNatsServer.downloadUrl());

        try {
            natsServer = new NatsStreamingServer(options.build());
        } catch (Exception e) {
            ofNullable(natsServer).ifPresent(NatsStreamingServer::close);
            throw new NatsStreamingStartException(
                    "Failed to initialise"
                            + " name [" + EnableNatsStreamingServer.class.getSimpleName() + "]"
                            + " port [" + options.port() + "]"
                            + " timeoutMs [" + options.timeoutMs() + "]"
                            + " logLevel [" + options.logLevel() + "]"
                            + " autostart [" + options.autostart() + "]"
                            + " configFile [" + options.configFile() + "]"
                            + " downloadUrl [" + options.configMap().get(NATS_DOWNLOAD_URL) + "]"
                    , e
            );
        }

        beanFactory.initializeBean(natsServer, BEAN_NAME);
        beanFactory.registerSingleton(BEAN_NAME, natsServer);
        ((DefaultSingletonBeanRegistry) beanFactory).registerDisposableBean(BEAN_NAME, natsServer);

    }

    private void configure(final NatsStreamingOptionsBuilder options, final NatsStreamingConfig key, final String value) {
        if (hasText(value)) {
            options.config(key, value);
        }
    }

    private void setEnvConfig(final NatsStreamingOptionsBuilder options, final ConfigurableEnvironment environment) {
        for (NatsStreamingConfig natsConfig : NatsStreamingConfig.values()) {
            final String key = "nats.streaming.server." + natsConfig.name().toLowerCase();
            final String value = environment.getProperty(key);
            if (hasText(value)) {
                options.config(natsConfig, value);
            }
        }
    }
}
