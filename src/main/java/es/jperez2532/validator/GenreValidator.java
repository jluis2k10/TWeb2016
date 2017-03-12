package es.jperez2532.validator;

import es.jperez2532.entities.Genre;
import es.jperez2532.repositories.GenreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by Jose Luis on 09/03/2017.
 */
@Component
public class GenreValidator implements Validator {

    @Autowired private GenreRepo genreRepo;

    @Override
    public boolean supports(Class<?> aClass) {
        return Genre.class.equals(aClass);
    }

    @Override
    public void validate(Object genreForm, Errors errors) {
        Genre genre = (Genre) genreForm;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty");
        if (genreRepo.findByName(genre.getName()) != null)
            errors.rejectValue("name", "Duplicate.GenreForm.genre");
    }
}
