package es.jperez2532.validator;

import es.jperez2532.entities.Genre;
import es.jperez2532.services.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Clase que se utiliza para validar un Género (de Películas).
 */
@Component
public class GenreValidator implements Validator {

    private final FilmService filmService;

    /**
     * Constructor de la clase con las inyecciones de dependencia apropiadas.
     * @param filmService inyección {@link FilmService}
     */
    @Autowired
    public GenreValidator(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Comprueba que el <em>validator</em> puede trabajar con la clase que se le pasa.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return Genre.class.equals(aClass);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Valida el Género indicado.
     */
    @Override
    public void validate(Object genreForm, Errors errors) {
        Genre genre = (Genre) genreForm;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty");
        if (filmService.findGenreByName(genre.getName()) != null)
            errors.rejectValue("name", "Duplicate.GenreForm.genre");
    }
}
