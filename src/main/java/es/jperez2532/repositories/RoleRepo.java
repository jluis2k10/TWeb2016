package es.jperez2532.repositories;

import es.jperez2532.entities.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created by Jose Luis on 21/02/2017.
 */
@Repository
public interface RoleRepo extends JpaRepository<AccountRole, Long> {

    Set<AccountRole> findByRole(String role);

}
