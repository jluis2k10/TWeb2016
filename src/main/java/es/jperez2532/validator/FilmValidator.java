package es.jperez2532.validator;

import es.jperez2532.entities.Film;
import es.jperez2532.repositories.FilmRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Calendar;

@Component
public class FilmValidator implements Validator {

    @Autowired private FilmRepo filmRepo;

    @Override
    public boolean supports(Class<?> aClass) {
        return Film.class.equals(aClass);
    }

    @Override
    public void validate(Object filmForm, Errors errors) {
        Film film = (Film) filmForm;

        // Título
        film.setTitle(film.getTitle().trim());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "NotEmpty");
        if (filmRepo.findByTitle(film.getTitle()) != null)
            errors.rejectValue("title", "Duplicate.FilmForm.title");

        // Año
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "year", "NotEmpty");
        if (!org.apache.commons.lang3.StringUtils.isNumeric(film.getYear()))
            errors.rejectValue("year", "Numeric.FilmForm");
        else if (film.getYear().length() != 4)
            errors.rejectValue("year", "Invalid.FilmForm.year");
        else if (Integer.valueOf(film.getYear()) > Calendar.getInstance().get(Calendar.YEAR))
            errors.rejectValue("year", "Future.FilmForm.year");

        // Duración
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "duration", "NotEmpty");
        if (!org.apache.commons.lang3.StringUtils.isNumeric(film.getDuration()))
            errors.rejectValue("duration", "Numeric.FilmForm");
        else if (film.getDuration().length() > 3)
            errors.rejectValue("duration", "Long.FilmForm.duration");

        // Sinopsis
        film.setDescription(film.getDescription().trim());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "NotEmpty");

        // Géneros
        if (film.getFilmGenres().size() == 0)
            errors.rejectValue("filmGenres", "Size.FilmForm.genres");

        // Directores
        if (film.getFilmDirectors().size() == 0)
            errors.rejectValue("filmDirectors", "Size.FilmForm.directors");

        // Actores principales
        if (film.getFilmStars().size() == 0)
            errors.rejectValue("filmStars", "Size.FilmForm.stars");

        // País
        if (film.getFilmCountries().size() == 0)
            errors.rejectValue("filmCountries", "Size.FilmForm.countries");

        // Youtube ID
        film.setTrailer(film.getTrailer().trim());
        if (!film.getTrailer().isEmpty())
            if (film.getTrailer().length() != 11)
                errors.rejectValue("trailer", "Invalid.FilmForm.trailer");

    }
}
