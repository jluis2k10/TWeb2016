package es.jperez2532.repositories;

import es.jperez2532.entities.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectorRepo extends JpaRepository<Director, Long> {
    Director findByName(String name);
    List<Director> findAllByOrderByNameAsc();
}
