package es.jperez2532.services;

import es.jperez2532.entities.*;
import es.jperez2532.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MyFilmService implements FilmService {

    @Autowired private FilmRepo filmRepo;
    @Autowired private GenreRepo genreRepo;
    @Autowired private DirectorRepo directorRepo;
    @Autowired private ActorRepo actorRepo;
    @Autowired private CountryRepo countryRepo;

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

}
