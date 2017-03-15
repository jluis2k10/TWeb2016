package es.jperez2532.repositories;

import es.jperez2532.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Jose Luis on 18/02/2017.
 */
@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {
    Page<Account> findAll(Pageable pageable);
    Account findByUserName(String userName);
    Account findByEmail(String email);
    Long countByAccountRoles_RoleIgnoreCase(String roleName);
    Long countByActive(boolean status);
}
