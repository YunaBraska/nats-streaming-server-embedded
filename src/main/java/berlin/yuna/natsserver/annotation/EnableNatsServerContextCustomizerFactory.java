package berlin.yuna.natsserver.annotation;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

import java.util.List;

public class EnableNatsServerContextCustomizerFactory implements ContextCustomizerFactory {

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
        EnableNatsServer enableNatsServer = AnnotatedElementUtils.findMergedAnnotation(testClass, EnableNatsServer.class);
        return new EnableNatsServerContextCustomizer(enableNatsServer);
    }

}