package es.jperez2532.validator;

import es.jperez2532.components.UploadPoster;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UploadPosterValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return UploadPoster.class.equals(aClass);
    }

    @Override
    public void validate(Object uploadPoster, Errors errors) {
        UploadPoster poster = (UploadPoster) uploadPoster;

        if (poster.getPosterFile() != null && poster.getPosterFile().isEmpty())
            errors.rejectValue("poster", "NotEmpty");
        else if (poster.getPosterFile().getSize() > 1 * 1024 * 1024)
            errors.rejectValue("poster", "Size.FilmForm.poster");
    }
}
