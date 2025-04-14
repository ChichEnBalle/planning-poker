package put.com.PLPBackend;



import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
// @PropertySource("classpath:application.properties")
@EnableConfigurationProperties
public class HelloworldApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(HelloworldApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Hello World!");
    }

}