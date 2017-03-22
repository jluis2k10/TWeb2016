package es.jperez2532.services;

import es.jperez2532.entities.Film;
import es.jperez2532.entities.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Proporciona servicio para manejar una Película ({@link Film}).
 * <p>
 * Se incluyen también operaciones de servicio para otras entidades asociadas
 * con las Películas para ahorrar en inyecciones de dependencia en los controladores.
 * Operaciones para:
 * <p><ul>
 *     <li> Géneros ({@link Genre})
 *     <li>  ({@link Genre})
 * </ul>
 * <p>
 * Forma parte de la capa de servicio de la aplicación, es decir se encarga
 * de tratar los datos antes de enviarlos (o después de recibirlos) a la capa
 * de persistencia (interfaces en {@link es.jperez2532.repositories}.
 * <p>
 * Los métodos "redundantes" con aquellos ya presentes en el repositorio de
 * las Películas, son necesarios para manejar correctamente la caché del sistema.
 */
public interface FilmService {
    /**
     * Persiste una Película.
     * @param film Película a persistir
     */
    void save(Film film);

    /**
     * Elimina una Película de la Base de Datos.
     * @param film           Película a eliminar
     * @param servletContext Contexto del servlet (para acceder al path real)
     * @return <code>true</code> si la Película se elimina con éxito
     */
    boolean delete(Film film, ServletContext servletContext);

    /**
     * Persiste una Película que ya existía en la Base de Datos a la cual se
     * le han actualizado los votos.
     * <p>
     * No se utiliza el método {@link FilmService#save(Film)} por ser éste último
     * más complejo en su implementación sin ser necesario en este caso.
     * @param film Película a Persistir
     */
    void updateVotes(Film film);

    /**
     * Persiste una Película que ya existía en la Base de Datos a la cual se
     * le han actualizado los votos.
     * <p>
     * No se utiliza el método {@link FilmService#save(Film)} por ser éste último
     * más complejo en su implementación sin ser necesario en este caso.
     * <p>
     * Se diferencia de {@link FilmService#updateVotes(Film)} en que en la implementación
     * no se eliminará una de las cachés de Películas.
     * @param film Película a Persistir
     */
    void updateViews(Film film);

    /**
     * Devuelve el número total de películas disponibles en la Base de Datos.
     * @return el número de películas
     */
    Long count();

    /**
     * Busca una Película por su ID.
     * @param id ID de la Película a buscar
     * @return la Película que se haya encontrado
     */
    Film findOne(Long id);

    /**
     * Devuelve una página {@link Page} con un conjunto de Películas.
     * Busca entre todas las películas disponibles en la Base de Datos
     * sin hacer distinciones.
     * @param pageable información sobre la página actual (nº página, resultados por
     *                 página y modo de ordenación)
     * @return la página construida
     */
    Page<Film> findAll(Pageable pageable);

    /**
     * Busca Películas por Título. Devuelve una página {@link Page} con
     * un conjunto de Películas que se correspondan con el Título indicado.
     * @param title    Título de las Películas
     * @param pageable información sobre la página actual (nº página, resultados por
     *                 página y modo de ordenación)
     * @return la página construida
     */
    Page<Film> findByTitle(String title, Pageable pageable);

    /**
     * Busca Películas por Género. Devuelve una página {@link Page} con
     * un conjunto de Películas que se correspondan con el Género indicado.
     * @param genre    Género de las Películas
     * @param pageable información sobre la página actual (nº página, resultados por
     *                 página y modo de ordenación)
     * @return la página construida
     */
    Page<Film> findByGenre(String genre, Pageable pageable);

    /**
     * Busca Películas por Director. Devuelve una página {@link Page} con
     * un conjunto de Películas que contengan al Director indicado.
     * @param director Director de las Películas
     * @param pageable información sobre la página actual (nº página, resultados por
     *                 página y modo de ordenación)
     * @return la página construida
     */
    Page<Film> findByDirector(String director, Pageable pageable);

    /**
     * Busca Películas por Actor. Devuelve una página {@link Page} con
     * un conjunto de Películas que contengan al Actor indicado.
     * @param actor    Actor de las Películas
     * @param pageable información sobre la página actual (nº página, resultados por
     *                 página y modo de ordenación)
     * @return la página construida
     */
    Page<Film> findByActor(String actor, Pageable pageable);

    /**
     * Busca Películas por País. Devuelve una página {@link Page} con
     * un conjunto de Películas que se correspondan con el país indicado.
     * @param country  País de las Películas
     * @param pageable información sobre la página actual (nº página, resultados por
     *                 página y modo de ordenación)
     * @return la página construida
     */
    Page<Film> findByCountry(String country, Pageable pageable);

    /**
     * Busca Películas por un término dado. Devuelve una página {@link Page}
     * con un conjunto de Películas que se contengan al término indicado en
     * alguno de sus campos.
     * @param term     término de búsqueda
     * @param pageable información sobre la página actual (nº página, resultados por
     *                 página y modo de ordenación)
     * @return la página construida
     */
    Page<Film> search(String term, Pageable pageable);

    /**
     * Calcula la puntuación (estrellas) de una película en función de los
     * votos que tenga.
     * @param film Película sobre la que se quiere realizar el cálculo
     */
    void calcScore(Film film);

    /**
     * Genera un conjunto con nombres de Géneros. El conjunto tiene un máximo
     * de <code>limit</code> elementos y son seleccionados de forma aleatoria
     * de la Base de Datos.
     * @param limit máximo número de elementos en el conjunto
     * @return el conjunto generado
     */
    Set<String> getRandomGenres(int limit);

    /**
     * Busca las películas que se mostrarán en la portada del sitio. Cada uno de
     * los "carruseles" que se muestran en portada contendrá un máximo de
     * <code>limit</code> películas cada uno.
     * @param limit  máximo número de elementos por cada carrusel de películas
     *               que se muestra en portada
     * @param genres géneros sobre los que buscar películas
     * @return conjunto de listas de Películas
     */
    Map<String, Collection<Film>> findHomePageFilms(int limit, Set<String> genres);

    /**
     * Obtiene un conjunto con las películas "top" del catálogo. La mejor y la peor
     * valorada, la más y la menos vista.
     * @return conjunto de películas top
     */
    Map<String, Film> getTopFilms();

    /**
     * Persiste un Género.
     * @param genre Género a persistir
     */
    void saveGenre(Genre genre);

    /**
     * Devuelve una lista ordenada alfabéticamente de todos los Géneros disponibles.
     * @return lista de Géneros ordenados alfabéticamente
     */
    List<Genre> findGenresAll();
}
