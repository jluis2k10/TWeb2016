package es.jperez2532.components;

import org.apache.commons.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(value = MultipartException.class)
    public RedirectView handleMultipartException(Exception ex, HttpServletRequest request){
        RedirectView model = new RedirectView("/error", true);
        FlashMap flash = RequestContextUtils.getOutputFlashMap(request);
        if (ex instanceof MultipartException) {
            MultipartException mEx = (MultipartException)ex;

            if (ex.getCause() instanceof FileUploadBase.FileSizeLimitExceededException){
                FileUploadBase.FileSizeLimitExceededException flEx = (FileUploadBase.FileSizeLimitExceededException)mEx.getCause();
                float permittedSize = flEx.getPermittedSize() / 1024;
                String message = "El tamaño máximo permitido de un archivo es de " + permittedSize + ".";
                flash.put("errors", message);
            } else {
                flash.put("errors", "Error irrecuperable: " + ex.getMessage());
            }
        } else {
            flash.put("errors", "Error irrecuperable: " + ex.getMessage());
        }
        return model;
    }

    @ExceptionHandler(value = IOException.class)
    public RedirectView handleIOException(Exception ex, HttpServletRequest request){
        RedirectView model = new RedirectView("/error", true);
        FlashMap flash = RequestContextUtils.getOutputFlashMap(request);
        flash.put("errors", "Error irrecuperable: " + ex.getMessage());
        return model;
    }
}