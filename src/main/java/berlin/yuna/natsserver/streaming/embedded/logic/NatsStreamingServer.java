package berlin.yuna.natsserver.streaming.embedded.logic;

import berlin.yuna.natsserver.logic.NatsStreaming;
import org.springframework.beans.factory.DisposableBean;

/**
 * {@link NatsStreamingServer}
 */
public class NatsStreamingServer extends NatsStreaming implements DisposableBean {

    public static final String BEAN_NAME = NatsStreamingServer.class.getSimpleName();
    private final long timeoutMs;

    /**
     * Create {@link NatsStreaming} with simplest start able configuration
     *
     * @param timeoutMs tear down timeout
     */
    public NatsStreamingServer(final long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    /**
     * Simply stops the {@link NatsStreaming}
     *
     * @see NatsStreaming#stop()
     */
    @Override
    public void destroy() {
        stop(timeoutMs);
    }
}