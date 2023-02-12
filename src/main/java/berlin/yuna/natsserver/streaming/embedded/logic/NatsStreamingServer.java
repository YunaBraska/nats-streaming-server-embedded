package berlin.yuna.natsserver.streaming.embedded.logic;

import berlin.yuna.natsserver.config.NatsStreamingOptions;
import berlin.yuna.natsserver.logic.NatsStreaming;
import org.springframework.beans.factory.DisposableBean;

/**
 * {@link NatsStreamingServer}
 */
public class NatsStreamingServer extends NatsStreaming implements DisposableBean {

    public static final String BEAN_NAME = NatsStreamingServer.class.getSimpleName();

    /**
     * Create {@link NatsStreaming} with the simplest start able configuration
     *
     * @param options nats options / config
     */
    public NatsStreamingServer(final NatsStreamingOptions options) {
        super(options);
    }

    /**
     * Simply stops the {@link NatsStreaming}
     *
     * @see NatsStreaming#close()
     */
    @Override
    public void destroy() {
        close();
    }
}
