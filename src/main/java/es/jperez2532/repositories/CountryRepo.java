package es.jperez2532.repositories;

import es.jperez2532.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {
    Country findByName(String name);
    List<Country> findAllByOrderByNameAsc();
}
