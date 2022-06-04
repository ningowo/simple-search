package team.snof.simplesearch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableRedisRepositories
@EnableSwagger2
@SpringBootApplication
@MapperScan("team.snof")
public class SimpleSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleSearchApplication.class, args);
    }

}
