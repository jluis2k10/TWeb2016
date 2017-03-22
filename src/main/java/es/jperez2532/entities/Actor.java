package es.jperez2532.entities;

import javax.persistence.*;

/**
 * Entidad que modela a un Actor.
 * <p>
 * Se corresponde con la tabla "ACTORS" de la Base de Datos.
 */
@Entity
@Table(name = "Actors")
public class Actor extends AbstractEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, unique = true, nullable = false)
    private String name;

    /**
     * Constructor de la clase.
     */
    public Actor() {}

    /**
     * Constructor de la clase. Establece el nombre del Actor.
     * @param name El nombre del Actor
     */
    public Actor(String name) {
        this.name = name;
    }

    /**
     * Devuelve el ID del Actor.
     * @return el ID del Actor
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID del Actor.
     * @param id el ID del Actor
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre del Actor.
     * @return el nombre del Actor
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del Actor.
     * @param name el nombre del Actor
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}