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
    Page<Film> findByFilmGenres_NameIgnoreCase(String genre, Pageable pageable);
    Page<Film> findByFilmDirectors_NameIgnoreCase(String director, Pageable pageable);
    // Distinct o repite resultados (por el left outer join que genera)
    Page<Film> findDistinctByFilmStars_NameIgnoreCaseOrFilmSupportings_NameIgnoreCase(String actor, String actor2, Pageable pageable);
    Page<Film> findByFilmCountries_NameIgnoreCase(String country, Pageable pageable);

    Set<Film> findByTitleIgnoreCaseContaining(String title);
    Set<Film> findByFilmGenres_NameIgnoreCase(String genre);
    Set<Film> findByFilmDirectors_NameIgnoreCaseContaining(String director);
    Set<Film> findByFilmStars_NameIgnoreCaseContaining(String actor);
    Set<Film> findByFilmSupportings_NameIgnoreCaseContaining(String actor);
    Set<Film> findByFilmCountries_NameIgnoreCase(String country);
    Set<Film> findByDescriptionIgnoreCaseContaining(String description);
    Set<Film> findByYear(String year);

    Page<Film> findAllByOrderByScoreDesc(Pageable pageable);
    Page<Film> findAllByOrderByViewsDesc(Pageable pageable);
}
