package berlin.yuna.natsserver.annotation;

import berlin.yuna.natsserver.logic.NatsServer;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that can be specified on a test class that runs Nats based tests.
 * Provides the following features over and above the regular <em>Spring TestContext
 * Framework</em>:
 * <ul>
 * <li>Registers a {@link NatsServer} bean with the {@link NatsServer} bean name.
 * </li>
 * </ul>
 * <p>
 * The typical usage of this annotation is like:
 * <pre class="code">
 * &#064;RunWith(SpringRunner.class)
 * &#064;EnableNatsServer
 * public class MyNatsTests {
 *
 *    &#064;Autowired
 *    private NatsServer natsServer;
 *
 * }
 * </pre>
 *
 * @author Yuna Morgenstern
 * @see NatsServer
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
@Inherited
public @interface EnableNatsServer {

    /**
     * @return port number
     */
    int port() default 4222;

    /**
     * Passes the original parameters to the server on startup
     * @see NatsServer#setNatsServerConfig(String...)
     */
    String[] natsServerConfig() default {};

}
