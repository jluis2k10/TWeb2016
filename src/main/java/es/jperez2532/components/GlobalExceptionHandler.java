package es.jperez2532.components;

import org.apache.commons.fileupload.FileUploadBase;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Manejador global de excepciones. Es un "filtro" que indica qué hacer en caso de
 * producirse cierto tipo de excepciones.
 */
@ControllerAdvice("es.jperez2532.controllers")
public class GlobalExceptionHandler {
    /**
     * Maneja excepciones del tipo MultipartException (errores al subir un archivo)
     * @param ex      excepción manejada
     * @param request detalles de la petición http
     * @return parámetros de redirección
     */
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
                flash.put("exceptionMsg", message);
            } else {
                flash.put("exceptionMsg", "Error irrecuperable: " + ex.getMessage());
            }
        } else {
            flash.put("exceptionMsg", "Error irrecuperable: " + ex.getMessage());
        }
        return model;
    }

    /**
     * Maneja excepciones del tipo IOException (errores de E/S)
     * @param ex      excepción manejada
     * @param request detalles de la petición http
     * @return parámetros de redirección
     */
    @ExceptionHandler(value = IOException.class)
    public RedirectView handleIOException(Exception ex, HttpServletRequest request) {
        RedirectView model = new RedirectView("/error", true);
        FlashMap flash = RequestContextUtils.getOutputFlashMap(request);
        flash.put("exceptionMsg", "Error irrecuperable: " + ex.getMessage());
        return model;
    }
}