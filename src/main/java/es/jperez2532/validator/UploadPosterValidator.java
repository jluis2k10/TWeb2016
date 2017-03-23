package es.jperez2532.validator;

import es.jperez2532.components.UploadPoster;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Clase que se utiliza para validar la imagen subida como póster de una Película.
 */
@Component
public class UploadPosterValidator implements Validator {
    /**
     * {@inheritDoc}
     * <p>
     * Comprueba que el <em>validator</em> puede trabajar con la clase que se le pasa.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return UploadPoster.class.equals(aClass);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Valida la imagen subida.
     */
    @Override
    public void validate(Object uploadPoster, Errors errors) {
        UploadPoster poster = (UploadPoster) uploadPoster;
        String contentType = poster.getPosterFile().getContentType();

        if (poster.getPosterFile() != null && poster.getPosterFile().isEmpty())
            errors.rejectValue("poster", "NotEmpty");
        else if (poster.getPosterFile().getSize() > 1 * 1024 * 1024)
            errors.rejectValue("poster", "Size.FilmForm.poster");
        else if (!(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/gif")))
            errors.rejectValue("poster", "ImagesOnly.FilmForm.poster");
    }
}
