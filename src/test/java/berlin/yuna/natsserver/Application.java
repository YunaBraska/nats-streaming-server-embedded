package berlin.yuna.natsserver;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@EnableAutoConfiguration
public class Application {

    public static void main(final String[] args) {
        run(Application.class, args);
    }
}
