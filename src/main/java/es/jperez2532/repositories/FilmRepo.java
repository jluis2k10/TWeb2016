package es.jperez2532.repositories;

import es.jperez2532.entities.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Repositorio de la entidad {@link Film}.
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre la
 * aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen, Spring Data se encarga
 * de procesarlos y de generar las consultas necesarias.
 */
@Repository
public interface FilmRepo extends JpaRepository<Film, Long> {
    /**
     * Busca ua Película por su título.
     * @param title el título a buscar
     * @return la Película encontrada
     */
    Film findByTitle(String title);

    /**
     * Devuelve una página de entre todas las películas disponibles.
     * @param pageable información sobre la página que se tiene que generar
     * @return Página con las películas encontradas
     */
    Page<Film> findAll(Pageable pageable);

    /**
     * Devuelve una página de entre todas las películas que contengan
     * en su título el término indicado.
     * @param title    el título a buscar
     * @param pageable información sobre la página que se tiene que generar
     * @return Página con las películas encontradas
     */
    Page<Film> findByTitleIgnoreCaseContaining(String title, Pageable pageable);

    /**
     * Devuelve una página de entre todas las películas que sean del Género
     * indicado.
     * @param genre    el nombre del Género a buscar
     * @param pageable información sobre la página que se tiene que generar
     * @return Página con las películas encontradas
     */
    Page<Film> findByFilmGenres_NameIgnoreCase(String genre, Pageable pageable);

    /**
     * Devuelve una página de entre todas las películas que contengan
     * al Director indicado.
     * @param director el nombre del Director a buscar
     * @param pageable información sobre la página que se tiene que generar
     * @return Página con las películas encontradas
     */
    Page<Film> findByFilmDirectors_NameIgnoreCase(String director, Pageable pageable);

    /**
     * Devuelve una página de entre todas las películas que sean del País
     * indicado.
     * @param country el nombre del País a buscar
     * @param pageable información sobre la página que se tiene que generar
     * @return Página con las películas encontradas
     */
    Page<Film> findByFilmCountries_NameIgnoreCase(String country, Pageable pageable);


    /**
     * Devuelve una página de entre todas las películas que contengan
     * al Actor indicado. Busca en las dos relaciones de actores disponibles
     * (FILMS_TO_STARS y FILMS_TO_SUPPORTINGS).
     * <p>
     * <code>Distinct</code> o repite resultados (por el left outer join que genera).
     * @param actor    el nombre del Actor a buscar (en FILMS_TO_STARS)
     * @param actor2   el nombre del Actor a buscar (en FILMS_TO_SUPPORTINGS)
     * @param pageable información sobre la página que se tiene que generar
     * @return Página con las películas encontradas
     */
    Page<Film> findDistinctByFilmStars_NameIgnoreCaseOrFilmSupportings_NameIgnoreCase(String actor, String actor2, Pageable pageable);

    /**
     * Devuelve un conjunto de películas que contienen el título indicado.
     * Búsqueda no exacta.
     * @param title el título a buscar
     * @return conjunto de Películas
     */
    Set<Film> findByTitleIgnoreCaseContaining(String title);

    /**
     * Devuelve un conjunto de películas que sean del Género indicado.
     * Búsqueda no exacta.
     * @param genre el nombre del Género a buscar
     * @return conjunto de Películas
     */
    Set<Film> findByFilmGenres_NameIgnoreCase(String genre);

    /**
     * Devuelve un conjunto de películas que contienen el nombre del Director
     * indicado. Búsqueda no exacta.
     * @param director el nombre del Director a buscar
     * @return conjunto de Películas
     */
    Set<Film> findByFilmDirectors_NameIgnoreCaseContaining(String director);

    /**
     * Devuelve un conjunto de películas que contienen el nombre del Actor
     * (como actor estrella) indicado. Búsqueda no exacta.
     * @param actor el nombre del Actor a buscar
     * @return conjunto de Películas
     */
    Set<Film> findByFilmStars_NameIgnoreCaseContaining(String actor);

    /**
     * Devuelve un conjunto de películas que contienen el nombre del Actor
     * (como actor secundario) indicado. Búsqueda no exacta.
     * @param actor el nombre del Actor a buscar
     * @return conjunto de Películas
     */
    Set<Film> findByFilmSupportings_NameIgnoreCaseContaining(String actor);

    /**
     * Devuelve un conjunto de películas que sean del País indicado.
     * Búsqueda no exacta.
     * @param country el nombre del País a buscar
     * @return conjunto de Películas
     */
    Set<Film> findByFilmCountries_NameIgnoreCase(String country);

    /**
     * Devuelve un conjunto de películas que contienen en su descripción
     * o sinopsis parte del término indicado. Búsqueda no exacta.
     * @param description el término a buscar
     * @return conjunto de Películas
     */
    Set<Film> findByDescriptionIgnoreCaseContaining(String description);

    /**
     * Devuelve un conjunto de películas según el año.
     * @param year año de las películas
     * @return conjunto de Películas
     */
    Set<Film> findByYear(String year);

    /**
     * Devuelve una página de entre todas las películas ordenadas por
     * su puntuación (estrellas) de forma desdendente.
     * @param pageable información sobre la página que se tiene que generar
     * @return Página con las películas encontradas
     */
    Page<Film> findAllByOrderByScoreDesc(Pageable pageable);

    /**
     * Devuelve una página de entre todas las películas ordenadas por
     * su número de visualizaciones de forma desdendente.
     * @param pageable información sobre la página que se tiene que generar
     * @return Página con las películas encontradas
     */
    Page<Film> findAllByOrderByViewsDesc(Pageable pageable);
}
