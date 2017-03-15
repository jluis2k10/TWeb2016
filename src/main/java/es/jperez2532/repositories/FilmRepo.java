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

    //@Cacheable(value = "searchFilm")
    Set<Film> findByTitleIgnoreCaseContaining(String title);
    //@Cacheable(value = "searchFilm")
    //@Cacheable(value = "searchFilm")
    Set<Film> findByFilmGenres_NameIgnoreCase(String genres);
    //@Cacheable("searchFilm")
    Set<Film> findByFilmDirectors_NameIgnoreCaseContaining(String name);
    //@Cacheable("searchFilm")
    Set<Film> findByFilmStars_NameIgnoreCaseContaining(String name);
    //@Cacheable("searchFilm")
    Set<Film> findByFilmSupportings_NameIgnoreCaseContaining(String name);
    //@Cacheable("searchFilm")
    Set<Film> findByFilmCountries_NameIgnoreCase(String name);
    //@Cacheable("searchFilm")
    Set<Film> findByDescriptionIgnoreCaseContaining(String description);
    //@Cacheable("searchFilm")
    Set<Film> findByYear(String year);
}
