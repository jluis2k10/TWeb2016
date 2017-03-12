package es.jperez2532.repositories;

import es.jperez2532.entities.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepo extends JpaRepository<Film, Long> {

    Film findByTitle(String title);
    Page<Film> findAll(Pageable pageable);
    Page<Film> findByTitleIgnoreCaseContaining(String title, Pageable pageable);
}
