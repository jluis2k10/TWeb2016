package es.jperez2532.components;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Clase auxiliar que contiene un objeto Multipart ({@link MultipartFile}) y los métodos
 * necesarios para guardarlo y borrarlo en el servidor.
 */
@Component
public class UploadPoster {

    private MultipartFile posterFile;

    /**
     * Constructor de la clase.
     */
    public UploadPoster() {}

    /**
     * Devuelve el objeto Multipart que modela el archivo subido.
     * @return objeto Multipart
     */
    public MultipartFile getPosterFile() {
        return posterFile;
    }

    /**
     * Establece el objeto Multipart que modela el archivo subido.
     * @param posterFile objeto Multipart
     */
    public void setPosterFile(MultipartFile posterFile) {
        this.posterFile = posterFile;
    }

    /**
     * Guarda el archivo subido en el servidor.
     * @param fileName       nombre del archivo
     * @param servletContext contexto del servidor, necesario para obtener el path real del mismo
     * @return el nombre del archivo con el que se ha guardado el archivo subido en el servidor
     *         (distinto del original)
     * @throws RuntimeException en caso de producirse un error
     */
    public String upload(String fileName, ServletContext servletContext) throws RuntimeException {
        if (posterFile.isEmpty())
            throw new RuntimeException("NotEmpty");

        // Buscar extensión
        String ext = posterFile.getOriginalFilename().substring(
                posterFile.getOriginalFilename().lastIndexOf('.'), posterFile.getOriginalFilename().length());
        // Guardar la imagen
        try {
            Path path = Paths.get(servletContext.getRealPath("/") +
                    "/WEB-INF/resources/img/posters/" + fileName + ext);
            Files.write(path, posterFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("UploadingError.FilmForm.poster");
        }
        return fileName + ext;
    }

    /**
     * Borra un archivo ya presente en el servidor.
     * @param fileName       nombre del archivo a borrar
     * @param servletContext contexto del servidor, necesario para obtener el path real del mismo
     * @throws RuntimeException en caso de producirse un error
     */
    public void delete(String fileName, ServletContext servletContext) throws RuntimeException {
        try {
            Path path = Paths.get(servletContext.getRealPath("/") +
                    "/WEB-INF/resources/img/posters/" + fileName);
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("DeletionError.FilmForm.poster");
        }
    }
}
