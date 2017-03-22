package es.jperez2532.entities;

import javax.persistence.*;

/**
 * Entidad que modela a un País.
 * <p>
 * Se corresponde con la tabla "COUNTRIES" de la Base de Datos.
 */
@Entity
@Table(name = "Countries")
public class Country extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, unique = true, nullable = false)
    private String name;

    /**
     * Constructor de la clase.
     */
    public Country() {}

    /**
     * Constructor de la clase. Establece el nombre del País.
     * @param name el nombre del País
     */
    public Country(String name) {
        this.name = name;
    }

    /**
     * Devuelve el ID del País.
     * @return el ID del País
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID del País.
     * @param id el ID del País
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre del País.
     * @return el nombre del País
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del País.
     * @param name el nombre del País
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}