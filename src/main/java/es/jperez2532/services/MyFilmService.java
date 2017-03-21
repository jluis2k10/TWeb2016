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
 *
 * Todos los resultados de consultas a la BBDD se guardan en tres cachés:
 *  - homePageFilms: almacena los resultados que se muestran en la portada
 *  - allFilms: almacena los resultados que se muestran en el catálogo/buscar por término
 *  - film: almacena películas una por una
 *
 * La caché de la portada se regenera con cualquier cambio que se haga a cualquier
 * película, incluyendo votos y reproducciones. Es decir todos los usuarios verán la misma
 * portada hasta que se produzca algún cambio en alguna película.
 * La caché de allFilms que guarda las búsquedas NO SE REGENRA al reproducir una
 * película.
 */
@Service
public class MyFilmService implements FilmService {

    @Autowired private FilmRepo filmRepo;
    @Autowired private GenreRepo genreRepo;
    @Autowired private DirectorRepo directorRepo;
    @Autowired private ActorRepo actorRepo;
    @Autowired private CountryRepo countryRepo;
    @Autowired private VoteRepo voteRepo;
    @Autowired private UploadPoster uploadPoster;

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

    @Caching(evict = {
            @CacheEvict(value = "homePageFilms", allEntries = true),
            @CacheEvict(value = "allFilms", allEntries = true),
            @CacheEvict(value = "film", key = "#film.id")})
    public boolean delete (Film film, ServletContext servletContext) {
        try {
            uploadPoster.delete(film.getPoster(), servletContext);
        } catch (RuntimeException e) {
           return false;
        } finally {
            filmRepo.delete(film);
        }
        return true;
    }

    /**
     * Actualizar película en BBDD.
     * Necesario refrescar todas las cachés para que no contengan información
     * desactualizada.
     * @param film
     */
    @Caching(evict = {
            @CacheEvict(value = "homePageFilms", allEntries = true),
            @CacheEvict(value = "allFilms", allEntries = true),
            @CacheEvict(value = "film", key = "#film.id")})
    public void update(Film film) {
        filmRepo.save(film);
    }

    /**
     * Actualizar película en BBDD.
     * Este método se utiliza únicamente para actualizar la información acerca del
     * número de reproducciones de la película ya que no es necesario refrescar la
     * caché de resultados porque ellos no se da la opción de ordenar por número
     * de reproducciones.
     * Si utilizáramos el método update() en su lugar estaríamos eliminando datos
     * de la caché de forma innecesaria.
     * @param film
     */
    @Caching(evict = {
            @CacheEvict(value = "homePageFilms", allEntries = true),
            @CacheEvict(value = "film", key = "#film.id")})
    public void updateViews(Film film) {
        filmRepo.save(film);
    }

    /**
     * Busca películas que contengan un término dado. Realiza la búsqueda en los campos:
     *   - Género
     *   - Título
     *   - Directores
     *   - Actores (principales y secundarios)
     *   - País
     *   - Descripción
     *   - Año
     * Devuelve una página (Page) con el número preciso de resultados necesarios y
     * ordenados para presentarlos en la página que muestra los resultados de la búsqueda.
     * Como se hacen muchas consultas a BBDD, cacheamos los resultados para no tener que
     * repetir las consultas si se realiza una búsqueda ya realizada con anterior
     *
     * @param term Término de búsqueda
     * @param pageable Información sobre la página actual (nº página, resultados por
     *                 página y modo de ordenación)
     * @return Página con los resultados ordenados
     */
    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> search(String term, Pageable pageable) {
        // Separamos el término en palabras en caso de que sea una frase, para así poder
        // buscar también por cada una de estas palabras (mínimo 4 letras por palabra)
        List<String> terms = getWords(term, 3);
        Set<Film> result = null;

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
        return new PageImpl<Film>(resultsList.subList(startIndex, endIndex),
                pageable, resultsList.size());
    }

    // Si cacheo esto empiezan errores en hibernate
    @Cacheable(value = "film", key = "#id")
    public Film findOne(Long id) {
        return filmRepo.findOne(id);
    }

    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> findAll(Pageable pageable) {
        return filmRepo.findAll(pageable);
    }

    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> findByGenre(String genre, Pageable pageable) {
        return filmRepo.findByFilmGenres_NameIgnoreCase(genre, pageable);
    }

    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> findByDirector(String director, Pageable pageable) {
        return filmRepo.findByFilmDirectors_NameIgnoreCase(director, pageable);
    }

    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> findByActor(String actor, Pageable pageable) {
        return filmRepo.findDistinctByFilmStars_NameIgnoreCaseOrFilmSupportings_NameIgnoreCase(actor, actor, pageable);
    }

    @Cacheable(value = "allFilms", keyGenerator = "filmsKey")
    public Page<Film> findByCountry(String country, Pageable pageable) {
        return filmRepo.findByFilmCountries_NameIgnoreCase(country, pageable);
    }

    /*@Caching(evict = {
            @CacheEvict(value = "homePageFilms", allEntries = true),
            @CacheEvict(value = "allFilms", allEntries = true),
            @CacheEvict(value = "film", key = "#film.id")})
    public BigDecimal reDoVotes(Film film) {
        int count = 0;
        BigDecimal fScore = new BigDecimal(0);
        for(Vote vote: film.getFilmVotes()) {
            fScore = fScore.add(new BigDecimal(vote.getScore()));
            count++;
        }
        if (fScore.compareTo(BigDecimal.ZERO) != 0)
            fScore = fScore.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
        film.setScore(fScore);
        film.setNvotes(count);
        filmRepo.save(film);
        return fScore;
    }*/
    @Caching(evict = {
            @CacheEvict(value = "homePageFilms", allEntries = true),
            @CacheEvict(value = "allFilms", allEntries = true),
            @CacheEvict(value = "film", key = "#film.id")})
    public void calcScore(Film film) {
        int count = 0;
        BigDecimal fScore = new BigDecimal(0);
        List<Vote> votes = voteRepo.findByIdFilm(film.getId());
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

    /*public void reDoVotes(List<Vote> votes) {
        for(Vote vote: votes) {
            reDoVotes(vote.getFilm());
        }
    }*/

    @Cacheable(value = "homePageFilms", keyGenerator = "filmsKey")
    public Set<String> getRandomGenres(int limit) {
        Set<String> results = new HashSet<String>();
        List<Genre> genres = genreRepo.findAllByOrderByNameAsc();
        while (results.size() < limit && results.size() < genres.size())
            results.add(genres.get(ThreadLocalRandom.current().nextInt(genres.size())).getName());
        return results;
    }

    @Cacheable(value = "homePageFilms", keyGenerator = "filmsKey")
    public Map<String, Collection<Film>> findHomePageFilms(int limit, Set<String> genres) {
        Map<String, Collection<Film>> results = new HashMap<String, Collection<Film>>();
        Pageable pageable = new PageRequest(0, limit );

        Page<Film> lastFilms = filmRepo.findAll(new PageRequest(0, 6, Sort.Direction.DESC, "id"));
        Page<Film> valoradas = filmRepo.findAllByOrderByScoreDesc(pageable);
        Page<Film> vistas = filmRepo.findAllByOrderByViewsDesc(pageable);
        results.put("ultimas", lastFilms.getContent());
        results.put("valoradas", valoradas.getContent());
        results.put("vistas", vistas.getContent());

        // Recuperamos películas por género y las desordenamos antes de devolverlas
        Iterator<String> it = genres.iterator();
        while (it.hasNext()) {
            String nextGenre = it.next();
            Page<Film> genre = filmRepo.findByFilmGenres_NameIgnoreCase(nextGenre, pageable);
            List<Film> films = genre.getContent();
            List<Film> modificableFilms = new ArrayList<Film>(films);
            Collections.shuffle(modificableFilms, ThreadLocalRandom.current());
            results.put(nextGenre, modificableFilms);
        }

        return results;
    }

    /**
     * Genera una lista ordenada de películas según los criterios específicados a
     * partir de un conjunto no ordenado de películas.
     *
     * @param results Conjunto no ordenado de películas
     * @param sort Criterios de ordenación
     * @return Lista ordenada de películas
     */
    private static List<Film> orderResultsList(Set<Film> results, Sort sort) {
        List<Film> resultsList = new ArrayList<Film>(results);
        Sort.Order order = null;
        Iterator<Sort.Order> it = sort.iterator();
        // En principio no debería existir más de un Order en sort (a no ser que el usuario
        // haya modificado manualmente la URL), así que en cualquier caso nos quedamos únicamente
        // con el primero.
        if (it.hasNext())
            order = it.next();

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
     * @param text
     * @param min_size
     * @return
     */
    private static List<String> getWords(String text, int min_size) {
        List<String> words = new ArrayList<String>();
        String word = null;
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

    public Map<String, Film> getStats() {
        Map<String, Film> filmStats = new HashMap<String, Film>();
        filmStats.put("masVista", filmRepo.findAll(new PageRequest(0, 1, Sort.Direction.DESC, "views")).getContent().get(0));
        filmStats.put("menosVista", filmRepo.findAll(new PageRequest(0, 1, Sort.Direction.ASC, "views")).getContent().get(0));
        filmStats.put("mejorValorada", filmRepo.findAll(new PageRequest(0, 1, Sort.Direction.DESC, "score")).getContent().get(0));
        filmStats.put("peorValorada", filmRepo.findAll(new PageRequest(0, 1, Sort.Direction.ASC, "score")).getContent().get(0));
        return filmStats;
    }
}
