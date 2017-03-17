package es.jperez2532.repositories;

import es.jperez2532.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepo extends JpaRepository<Genre, Long> {
    Genre findByName(String name);
    List<Genre> findAllByOrderByNameAsc();
}
