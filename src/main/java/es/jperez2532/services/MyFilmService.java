package es.jperez2532.services;

import es.jperez2532.components.UploadPoster;
import es.jperez2532.entities.*;
import es.jperez2532.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.math.BigDecimal;
import java.text.BreakIterator;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Servicios para operaciones con películas.
 * <p>
 * Todos los resultados de consultas a la BBDD se guardan en tres cachés:
 * <ul>
 *   <li> homePageFilms: almacena los resultados que se muestran en la portada
 *   <li> allFilms: almacena los resultados que se muestran en el catálogo/buscar por término
 *   <li> film: almacena películas una por una
 * </ul><p>
 * La caché de la portada se regenera con cualquier cambio que se haga a cualquier
 * película, incluyendo votos y reproducciones. Es decir todos los usuarios verán la misma
 * portada hasta que se produzca algún cambio en alguna película.
 * <p>
 * La caché de allFilms que guarda las búsquedas NO SE REGENRA al reproducir una
 * película.
 */
@Service
public class MyFilmService implements FilmService {

    @Autowired private UserService userService;
    @Autowired private VotesService votesService;
    @Autowired private FilmRepo filmRepo;
    @Autowired private GenreRepo genreRepo;
    @Autowired private DirectorRepo directorRepo;
    @Autowired private ActorRepo actorRepo;
    @Autowired private CountryRepo countryRepo;
    @Autowired private UploadPoster uploadPoster;

    /**
     * {@inheritDoc}
     * <p>
     * Para persistir una película nueva (que provenga del formulario de crear
     * una nueva película), debemos previamente persistir cada una de las entidades
     * que tenga asociadas (Géneros, Países, Directores, etc).
     * <p>
     * Si estas entidades asociadas ya existían en la Base de Datos, hay que
     * recuperar su identificador para que no se produzcan errores al intentar
     * persisitr la entidad y que Hibernate genere automáticamente las asociaciones
     * adecuadas en las distintas tablas "join" entre la Película y sus entidades
     * asociadas.
     */
    @Caching(evict = {
            @CacheEvict(value = "homePageFilms", allEntries = true),
            @CacheEvict(value = "allFilms", allEntries = true),
            @CacheEvict(value = "film", key = "#film.id")})
    public void save(Film film) {
        // Comprobar Géneros
        if (!film.getFilmGenres().isEmpty()) {
            for (Genre genre: film.getFilmGenres()) {
                if (genreRepo.findByName(genre.getName()) != null)
                    genre.setId(genreRepo.findByName(genre.getName()).getId());
            }
            genreRepo.save(film.getFilmGenres());
        }

        // Comprobar Directores
        if (!film.getFilmDirectors().isEmpty()) {
            for (Director director: film.getFilmDirectors()) {
                if (directorRepo.findByName(director.getName()) != null)
                    director.setId(directorRepo.findByName(director.getName()).getId());
            }
            directorRepo.save(film.getFilmDirectors());
        }

        // Comprobar Actores Principales
        if (!film.getFilmStars().isEmpty()) {
            for (Actor actor: film.getFilmStars()) {
                if (actorRepo.findByName(actor.getName()) != null)
                    actor.setId(actorRepo.findByName(actor.getName()).getId());
            }
            actorRepo.save(film.getFilmStars());
        }

        // Comprobar Actores Secundarios
        if (!film.getFilmSupportings().isEmpty()) {
            for (Actor actor: film.getFilmSupportings()) {
                if (actorRepo.findByName(actor.getName()) != null)
                    actor.setId(actorRepo.findByName(actor.getName()).getId());
            }
            actorRepo.save(film.getFilmSupportings());
        }

        // Comprobar Países
        if (!film.getFilmCountries().isEmpty()) {
            for (Country country: film.getFilmCountries()) {
                if (countryRepo.findByName(country.getName()) != null)
                    country.setId((countryRepo.findByName(country.getName()).getId()));
            }
            countryRepo.save(film.getFilmCountries());
        }
        filmRepo.save(film);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Antes de poder borrar una película debemos eliminar todos los votos emitidos
     * hacia esa película y borrar las posibles cachés de cuentas de usuarios que
     * la pudieran tener en su lista de reproducción para que no se produzcan
     * inconsistencias en la Base de Datos.
     */
    @Caching(evict = {
            @CacheEvict(value = "homePageFilms", allEntries = true),
            @CacheEvict(value = "allFilms", allEntries = true),
            @CacheEvict(value = "film", key = "#film.id")})
    public boolean delete (Film film, ServletContext servletContext) {
        /* Asumimos que el posible fallo al borrar una película sólo
        puede darse al intentar borrar el archivo con la imagen del póster */
        try {
            uploadPoster.delete(film.getPoster(), servletContext);
        } catch (RuntimeException e) {
           return false;
        } finally {
            votesService.deleteVotesFromFilm(film.getId());
            for(Account account: film.getListedIn())
                userService.clearCache(account);
            film.getListedIn().clear();
            filmRepo.delete(film);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Caching(evict = {
            @CacheEvict(value = "homePageFilms", allEntries = true),
            @CacheEvict(value = "allFilms", allEntries = true),
            @CacheEvict(value = "film", key = "#film.id")})
    public void updateVotes(Film film) {
        filmRepo.save(film);
    }

    /**
     * {@inheritDoc}
     */
    @Caching(evict = {
            @CacheEvict(value = "homePageFilms", allEntries = true),
            @CacheEvict(value = "film", key = "#film.id")})
    public void updateViews(Film film) {
        filmRepo.save(film);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Busca películas que contengan un término dado. Realiza la búsqueda en los campos:
     * <ul>
     * <li> Género
     * <li> Título
     * <li> Directores
     * <li> Actores (principales y secundarios)
     * <li> País
     * <li> Descripción
     * <li> Año
     * </ul><p>
     * Devuelve una página (Page) manualmente que contiene el número preciso de resultados
     * necesarios, y ordenados para presentarlos en la página que muestra los resultados
     * de la búsqueda.
     * <p>
     * Como se hacen muchas consultas a BBDD, cacheamos los resultados para no tener que
     * repetir las consultas si se realiza una búsqueda ya realizada con anterior.
     */
    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> search(String term, Pageable pageable) {
        /* Separamos el término en palabras en caso de que sea una frase, para así poder
         buscar también por cada una de estas palabras (mínimo 4 letras por palabra)*/
        List<String> terms = getWords(term, 3);
        Set<Film> result;

        // Buscamos por género (término exacto)
        result = filmRepo.findByFilmGenres_NameIgnoreCase(term);
        if (terms.size() > 1) {
            for (String word: terms)
                result.addAll(filmRepo.findByFilmGenres_NameIgnoreCase(word));
        }

        // Buscamos por título
        result.addAll(filmRepo.findByTitleIgnoreCaseContaining(term));
        if (terms.size() > 1) {
            for (String word: terms)
                result.addAll(filmRepo.findByTitleIgnoreCaseContaining(word));
        }

        // Buscamos por directores
        result.addAll(filmRepo.findByFilmDirectors_NameIgnoreCaseContaining(term));
        if (terms.size() > 1) {
            for (String word: terms)
                result.addAll(filmRepo.findByFilmDirectors_NameIgnoreCaseContaining(word));
        }

        // Buscamos por actores principales
        result.addAll(filmRepo.findByFilmStars_NameIgnoreCaseContaining(term));
        if (terms.size() > 1) {
            for (String word: terms)
                result.addAll(filmRepo.findByFilmStars_NameIgnoreCaseContaining(word));
        }

        // Buscamos por actores secundarios
        result.addAll(filmRepo.findByFilmSupportings_NameIgnoreCaseContaining(term));
        if (terms.size() > 1) {
            for (String word: terms)
                result.addAll(filmRepo.findByFilmSupportings_NameIgnoreCaseContaining(word));
        }

        // Buscamos por países (término exacto)
        result.addAll(filmRepo.findByFilmCountries_NameIgnoreCase(term));
        if (terms.size() > 1) {
            for (String word: terms)
                result.addAll(filmRepo.findByFilmCountries_NameIgnoreCase(word));
        }

        // Buscamos por descripción
        result.addAll(filmRepo.findByDescriptionIgnoreCaseContaining(term));
        if (terms.size() > 1) {
            for (String word: terms)
                result.addAll(filmRepo.findByDescriptionIgnoreCaseContaining(word));
        }

        // Buscamos por año (término exacto)
        result.addAll(filmRepo.findByYear(term));
        if (terms.size() > 1) {
            for (String word: terms)
                result.addAll(filmRepo.findByYear(word));
        }

        // Generamos la lista ordenada según el criterio...
        List<Film> resultsList = orderResultsList(result, pageable.getSort());
        // ... y devolvemos la porción necesaria de ella para la página en la que estemos
        int startIndex = pageable.getOffset();
        int endIndex = Math.min(pageable.getOffset() + pageable.getPageSize(), resultsList.size());
        return new PageImpl<>(resultsList.subList(startIndex, endIndex),
                pageable, resultsList.size());
    }

    /**
     * {@inheritDoc}
     */
    public Long count() {
        return filmRepo.count();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "film", key = "#id")
    public Film findOne(Long id) {
        return filmRepo.findOne(id);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> findAll(Pageable pageable) {
        return filmRepo.findAll(pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> findByTitle(String title, Pageable pageable) {
        return filmRepo.findByTitleIgnoreCaseContaining(title, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> findByGenre(String genre, Pageable pageable) {
        return filmRepo.findByFilmGenres_NameIgnoreCase(genre, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> findByDirector(String director, Pageable pageable) {
        return filmRepo.findByFilmDirectors_NameIgnoreCase(director, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> findByActor(String actor, Pageable pageable) {
        return filmRepo.findDistinctByFilmStars_NameIgnoreCaseOrFilmSupportings_NameIgnoreCase(actor, actor, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> findByCountry(String country, Pageable pageable) {
        return filmRepo.findByFilmCountries_NameIgnoreCase(country, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Caching(evict = {
            @CacheEvict(value = "homePageFilms", allEntries = true),
            @CacheEvict(value = "allFilms", allEntries = true),
            @CacheEvict(value = "film", key = "#film.id")})
    public void calcScore(Film film) {
        int count = 0;
        BigDecimal fScore = new BigDecimal(0);
        List<Vote> votes = votesService.findFilmVotes(film.getId());
        for (Vote vote: votes) {
            fScore = fScore.add(new BigDecimal(vote.getScore()));
            count++;
        }
        if (fScore.compareTo(BigDecimal.ZERO) != 0)
            fScore = fScore.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
        film.setScore(fScore);
        film.setNvotes(count);
        filmRepo.save(film);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "homePageFilms", keyGenerator = "filmsKey")
    public Set<String> getRandomGenres(int limit) {
        Set<String> results = new HashSet<>();
        List<Genre> genres = genreRepo.findAllByOrderByNameAsc();
        while (results.size() < limit && results.size() < genres.size())
            results.add(genres.get(ThreadLocalRandom.current().nextInt(genres.size())).getName());
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "homePageFilms", keyGenerator = "filmsKey")
    public Map<String, Collection<Film>> findHomePageFilms(int limit, Set<String> genres) {
        Map<String, Collection<Film>> results = new HashMap<>();
        Pageable pageable = new PageRequest(0, limit );

        Page<Film> lastFilms = filmRepo.findAll(new PageRequest(0, 6, Sort.Direction.DESC, "id"));
        Page<Film> valoradas = filmRepo.findAllByOrderByScoreDesc(pageable);
        Page<Film> vistas = filmRepo.findAllByOrderByViewsDesc(pageable);
        results.put("ultimas", lastFilms.getContent());
        results.put("valoradas", valoradas.getContent());
        results.put("vistas", vistas.getContent());

        // Recuperamos películas por género y las desordenamos antes de devolverlas
        for (String nextGenre : genres) {
            Set<Film> films = filmRepo.findByFilmGenres_NameIgnoreCase(nextGenre);
            List<Film> modificableFilms = new ArrayList<>(films);
            // Desordenamos y dejamos la lista dentro del límite dado
            Collections.shuffle(modificableFilms, ThreadLocalRandom.current());
            if (modificableFilms.size() > limit)
                modificableFilms.subList(limit, modificableFilms.size()).clear();
            results.put(nextGenre, modificableFilms);
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Film> getTopFilms() {
        Map<String, Film> topFilms = new HashMap<>();
        topFilms.put("masVista", filmRepo.findAll(new PageRequest(0, 1, Sort.Direction.DESC, "views")).getContent().get(0));
        topFilms.put("menosVista", filmRepo.findAll(new PageRequest(0, 1, Sort.Direction.ASC, "views")).getContent().get(0));
        topFilms.put("mejorValorada", filmRepo.findAll(new PageRequest(0, 1, Sort.Direction.DESC, "score")).getContent().get(0));
        topFilms.put("peorValorada", filmRepo.findAll(new PageRequest(0, 1, Sort.Direction.ASC, "score")).getContent().get(0));
        return topFilms;
    }

    /**
     * {@inheritDoc}
     */
    public void saveGenre(Genre genre) {
        genreRepo.save(genre);
    }

    /**
     * {@inheritDoc}
     */
    public List<Genre> findGenresAll() {
        return genreRepo.findAllByOrderByNameAsc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Director> findDirectorsAll() {
        return directorRepo.findAllByOrderByNameAsc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Actor> findActorsAll() {
        return actorRepo.findAllByOrderByNameAsc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Country> findCountriesAll() {
        return countryRepo.findAllByOrderByNameAsc();
    }

    /**
     * Genera una lista ordenada de películas según los criterios específicados a
     * partir de un conjunto no ordenado de películas.
     *
     * @param results Conjunto no ordenado de películas
     * @param sort    Criterios de ordenación
     * @return Lista ordenada de películas
     */
    private static List<Film> orderResultsList(Set<Film> results, Sort sort) {
        List<Film> resultsList = new ArrayList<>(results);
        Sort.Order order = null;
        Iterator<Sort.Order> it = sort.iterator();
        // En principio no debería existir más de un Order en sort (a no ser que el usuario
        // haya modificado manualmente la URL), así que en cualquier caso nos quedamos únicamente
        // con el primero.
        if (it.hasNext())
            order = it.next();

        assert order != null;
        switch (order.getProperty()) {
            case "id":
                Collections.sort(resultsList, new Film.ComparatorFilmId());
                break;
            case "title":
                Collections.sort(resultsList, new Film.ComparatorFilmTitle());
                break;
            case "year":
                Collections.sort(resultsList, new Film.ComparatorFilmYear());
                break;
            case "score":
                Collections.sort(resultsList, new Film.ComparatorFilmScore());
                break;
            default:
                // No debería existir otra posibilidad a no ser que se modifique manualmente,
                // y en este caso devolvemos la lista ordenada por ID.
                Collections.sort(resultsList, new Film.ComparatorFilmId());
        }

        if (order.getDirection() == Sort.Direction.DESC)
            Collections.reverse(resultsList);

        return resultsList;
    }

    /**
     * Convierte una cadena de texto en un array con cada palabra de la cadena de texto.
     * Seleccionamos sólo palabras de tamaño mayor que @min_size
     * @param text     cadena de texto con (posiblemente) más de una palabra
     * @param min_size tamaño mínimo de una palabra para ser incluída en la lista
     * @return Lista de palabras
     */
    private static List<String> getWords(String text, int min_size) {
        List<String> words = new ArrayList<>();
        String word;
        BreakIterator breakIterator = BreakIterator.getWordInstance();
        breakIterator.setText(text);
        int lastIndex = breakIterator.first();
        while (BreakIterator.DONE != lastIndex) {
            int firstIndex = lastIndex;
            lastIndex = breakIterator.next();
            if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
                word = text.substring(firstIndex, lastIndex);
                if (word.length() > min_size)
                    words.add(word);
            }
        }
        return words;
    }
}
