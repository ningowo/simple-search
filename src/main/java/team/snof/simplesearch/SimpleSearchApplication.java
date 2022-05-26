package team.snof.simplesearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class SimpleSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleSearchApplication.class, args);
    }

}
