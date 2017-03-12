package es.jperez2532.repositories;

import es.jperez2532.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {

    Country findByName(String name);

}
