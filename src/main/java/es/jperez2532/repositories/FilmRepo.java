package es.jperez2532.repositories;

import es.jperez2532.entities.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FilmRepo extends JpaRepository<Film, Long> {
    Film findByTitle(String title);
    Page<Film> findAll(Pageable pageable);
    Page<Film> findByTitleIgnoreCaseContaining(String title, Pageable pageable);
    Set<Film> findByTitleIgnoreCaseContaining(String title);
    Set<Film> findByFilmGenres_NameIgnoreCase(String genres);
    Set<Film> findByFilmDirectors_NameIgnoreCaseContaining(String name);
    Set<Film> findByFilmStars_NameIgnoreCaseContaining(String name);
    Set<Film> findByFilmSupportings_NameIgnoreCaseContaining(String name);
    Set<Film> findByFilmCountries_NameIgnoreCase(String name);
    Set<Film> findByDescriptionIgnoreCaseContaining(String description);
    Set<Film> findByYear(String year);
}
