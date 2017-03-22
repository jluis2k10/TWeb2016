package es.jperez2532.entities;

/**
 * Clase abstracta para agrupar algunas de las entidades definidas.
 *
 * Es necesario para un método privado del controlador
 * {@link org.springframework.web.bind.annotation.RestController}
 */
public abstract class AbstractEntity {
    /**
     * Devuelve el nombre de la entidad.
     * @return El nombre de la entidad
     */
    public abstract String getName();
}
