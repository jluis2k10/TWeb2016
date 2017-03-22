package es.jperez2532.entities;

import javax.persistence.*;

/**
 * Entidad que modela a un Director.
 * <p>
 * Se corresponde con la tabla "DIRECTORS" en la Base de Datos.
 */
@Entity
@Table(name = "Directors")
public class Director extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, unique = true, nullable = false)
    private String name;

    /**
     * Constructor de la clase.
     */
    public Director() {}

    /**
     * Constructor de la clase. Establece el nombre del Director.
     * @param name el nombre del Director
     */
    public Director(String name) {
        this.name = name;
    }

    /**
     * Devuelve el ID del Director.
     * @return el ID del Director
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID del Director.
     * @param id el ID del Director
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre del Director.
     * @return el nombre del Director
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del Director.
     * @param name el nombre del Director
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Director{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
