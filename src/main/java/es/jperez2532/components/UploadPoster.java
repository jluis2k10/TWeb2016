package es.jperez2532.components;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class UploadPoster {

    private MultipartFile posterFile;

    public MultipartFile getPosterFile() {
        return posterFile;
    }

    public void setPosterFile(MultipartFile posterFile) {
        this.posterFile = posterFile;
    }

    public String upload(String fileName, ServletContext servletContext) throws RuntimeException {

        if (posterFile.isEmpty())
            throw new RuntimeException("NotEmpty");

        String contentType = posterFile.getContentType();
        if (!(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/gif")))
            throw new RuntimeException("ImagesOnly.FilmForm.poster");

        // Buscar extensión
        String ext = posterFile.getOriginalFilename().substring(
                posterFile.getOriginalFilename().lastIndexOf('.'), posterFile.getOriginalFilename().length());
        // Guardar la imagen
        try {
            Path imgPath = Paths.get(servletContext.getRealPath("/") +
                     "/WEB-INF/resources/img/posters/" + fileName + ext);
            Files.write(imgPath, posterFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("UploadingError.FilmForm.poster");
        }
        return fileName + ext;
    }
}
