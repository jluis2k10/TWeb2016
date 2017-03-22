package es.jperez2532.entities;

import javax.persistence.*;

/**
 * Entidad que modela un Voto hecho por un usuario a una película.
 * <p>
 * Se corresponde con la tabla "VOTES" de la Base de Datos.
 * La tabla Votes tiene una PK compuesta (película, voto). Se necesita utilizar
 * un Objeto {@link VotePK} que modele dicha clave.
 */
@Entity
@Table(name = "Votes")
public class Vote {

    // PK compuesta (film_id, account_id)
    @EmbeddedId
    private VotePK id;

    @Column(name = "score", nullable = false)
    private int score;

    /*
    Mapeamos un objeto Film (una película) utilizando el valor de la columna "film_id"
    de la tabla "VOTES" y asociándolo al valor de la columna "id" de la tabla "FILM".
     */
    @MapsId("film")
    @JoinColumns({ @JoinColumn(name = "film_id", referencedColumnName = "id") })
    @OneToOne(fetch = FetchType.LAZY)
    private Film film;

    /*
    Mapeamos un objeto Account (una cuenta de usuario) utilizando el valor de la columna
    "account_id" de la tabla "VOTES" y asociándolo al valor de la columna "id" de la
    tabla "ACCOUNT".
     */
    @MapsId("account")
    @JoinColumns({ @JoinColumn(name = "account_id", referencedColumnName = "id") })
    @OneToOne(fetch = FetchType.LAZY)
    private Account account;

    /**
     * Devuelve el ID (compuesto, {@link VotePK}) del Voto.
     * @return el ID del Voto
     */
    public VotePK getId() {
        return id;
    }

    /**
     * Establece el ID (compuesto, {@link VotePK}) del Voto.
     * @param id el ID a establecer
     */
    public void setId(VotePK id) {
        this.id = id;
    }

    /**
     * Devuelve el valor del Voto (la puntuación que le ha otorgado el usuario
     * emisor del voto a la película).
     * @return el valor del Voto
     */
    public int getScore() {
        return score;
    }

    /**
     * Establece el valor del Voto (la puntuación que le ha otorgado el usuario
     * emisor del voto a la película).
     * @param score el valor a establecer
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Devuelve la película asociada con el Voto.
     * @return la película asociada
     */
    public Film getFilm() {
        return film;
    }

    /**
     * Establece la película asociada con el Voto.
     * @param film la película a asociar con el Voto
     */
    public void setFilm(Film film) {
        this.film = film;
    }

    /**
     * Devuelve la cuenta asociada con el Voto.
     * @return la cuenta asociada
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Establece la cuenta asociada con el Voto.
     * @param account la cuenta a asociar
     */
    public void setAccount(Account account) {
        this.account = account;
    }
}
