package es.jperez2532.services;

import es.jperez2532.entities.*;
import es.jperez2532.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.*;

@Service
public class MyFilmService implements FilmService {

    @Autowired private FilmRepo filmRepo;
    @Autowired private GenreRepo genreRepo;
    @Autowired private DirectorRepo directorRepo;
    @Autowired private ActorRepo actorRepo;
    @Autowired private CountryRepo countryRepo;

    @CacheEvict(value = "searchFilm", allEntries = true)
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

    @CacheEvict(value = "searchFilm", allEntries = true)
    public boolean delete (Film film, ServletContext servletContext) {
        try {
            Path path = Paths.get(servletContext.getRealPath("/") +
                    "/WEB-INF/resources/img/posters/" + film.getPoster());
            Files.delete(path);
        } catch (IOException e) {
           return false;
        } finally {
            filmRepo.delete(film);
        }
        return true;
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
     *
     * @param term Término de búsqueda
     * @param pageable Información sobre la página actual (nº página, resultados por
     *                 página y modo de ordenación)
     * @return Página con los resultados ordenados
     */
    @Cacheable(value = "searchFilm")
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

    @CacheEvict(value = "searchFilm", allEntries = true)
    public BigDecimal reDoVotes(Film film) {
        int count = 0;
        BigDecimal fScore = new BigDecimal(0);
        for(Vote vote: film.getFilmVotes()) {
            fScore = fScore.add(new BigDecimal(vote.getScore()));
            count++;
        }
        fScore = fScore.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
        film.setScore(fScore);
        film.setNvotes(count);
        filmRepo.save(film);
        return fScore;
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

}
