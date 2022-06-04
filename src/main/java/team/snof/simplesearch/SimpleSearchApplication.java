package team.snof.simplesearch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import team.snof.simplesearch.user.repository.RoleRepository;
import team.snof.simplesearch.search.model.bo.favorite.Role;
import team.snof.simplesearch.search.model.bo.favorite.RoleName;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableRedisRepositories
@EnableSwagger2
@SpringBootApplication
@MapperScan("team.snof")
@EntityScan(basePackageClasses = {
        SimpleSearchApplication.class,
        Jsr310JpaConverters.class
})
public class SimpleSearchApplication {
    @Autowired
    RoleRepository roleRepository;

    @PostConstruct
    void init() {
        Boolean userRoleExists = roleRepository.findByName(RoleName.ROLE_USER).isPresent();
        Boolean userAdminExists = roleRepository.findByName(RoleName.ROLE_ADMIN).isPresent();
        if(!userRoleExists) {
            Role r=new Role();
            r.setName(RoleName.ROLE_USER);
            roleRepository.save(r);
        }
        if(!userAdminExists) {
            Role r=new Role();
            r.setName(RoleName.ROLE_ADMIN);
            roleRepository.save(r);
        }
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {

        SpringApplication.run(SimpleSearchApplication.class, args);
    }

}
