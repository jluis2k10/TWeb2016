package es.jperez2532.config;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import java.io.File;

/**
 * Created by Jose Luis on 17/02/2017.
 */
public class SpringWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { ApplicationContextConfig.class, DBConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    // TODO: estudiar cómo funciona este filtro
    // Sirve para que las vistas tengan acceso a las variables obtenidas del repositorio que
    // sean lazy-loading (se mantienen en la sesión durante la transacción completa, hasta que
    // se genera la vista).
    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{
                new OpenEntityManagerInViewFilter()
        };
    }

    // 10 MB. Luego manejamos el tamaño máximo en el validator correspondiente.
    // Creo que es mejor así, una excepción por superar el tamaño máximo es difícil de manejar.
    private int maxUploadSizeInMb = 10 * 1024 * 1024;
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {

        // upload temp file will put here
        File uploadDirectory = new File(System.getProperty("java.io.tmpdir"));

        // register a MultipartConfigElement
        MultipartConfigElement multipartConfigElement =
                new MultipartConfigElement(uploadDirectory.getAbsolutePath(),
                        maxUploadSizeInMb, maxUploadSizeInMb * 2,
                        maxUploadSizeInMb / 2);

        registration.setMultipartConfig(multipartConfigElement);
    }


}