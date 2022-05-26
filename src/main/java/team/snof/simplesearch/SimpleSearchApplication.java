package team.snof.simplesearch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("team.snof.simplesearch.search.mapper")
@SpringBootApplication
public class SimpleSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleSearchApplication.class, args);
    }

}
