package es.jperez2532.entities;

import javax.persistence.*;

/**
 * Entidad que modela a un Género (de películas).
 * <p>
 * Se corresponde con la tabla "GENRES" de la Base de Datos.
 */
@Entity
@Table(name = "Genres")
public class Genre extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, unique = true, nullable = false)
    private String name;

    /**
     * Constructor de la clase.
     */
    public Genre() {}

    /**
     * Constructor de la clase. Establece el nombre del género.
     * @param name el nombre a establecer
     */
    public Genre(String name) {
        this.name = name;
    }

    /**
     * Devuelve el ID del Género.
     * @return el ID del Género
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID del Género.
     * @param id el ID a establecer
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre del Género.
     * @return el nombre del Género
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del Género.
     * @param name el nombre a establecer
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
