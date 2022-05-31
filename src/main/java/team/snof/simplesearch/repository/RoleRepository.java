package team.snof.simplesearch.repository;

import team.snof.simplesearch.search.model.bo.favorite.Role;
import team.snof.simplesearch.search.model.bo.favorite.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}