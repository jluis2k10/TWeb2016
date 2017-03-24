package es.jperez2532.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Primary key compuesta para la clase {@link Vote}, que necesita un ID "empotrable" para
 * poder modelar la clave compuesta (película, voto) de la que consta.
 * <p>
 * Necesitamos un constructor para generar la clave compuesta cada vez que creamos el objeto,
 * y un constructor vacío que es el que usará Hibernate.
 */
@Embeddable
public class VotePK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Basic
    @Column(name = "film_id")
    private long film;

    @Basic
    @Column(name = "account_id")
    private long account;

    /**
     * Constructor de la clase.
     */
    public VotePK() { }

    /**
     * Constructor de la clase. Establece los ID de la Película y de la Cuenta.
     * @param filmId    ID de la Película
     * @param accountId ID de la Cuenta
     */
    public VotePK(long filmId, long accountId) {
        this.film = filmId;
        this.account = accountId;
    }

    /**
     * Devuelve el ID de la Película.
     * @return el ID de la Película
     */
    public long getFilmId() {
        return film;
    }

    /**
     * Establece el ID de la Película.
     * @param filmId ID a establecer
     */
    public void setFilmId(long filmId) {
        this.film = filmId;
    }

    /**
     * Devuelve el ID de la Cuenta.
     * @return el ID de la Cuenta
     */
    public long getAccountId() {
        return account;
    }

    /**
     * Establece el ID de la Cuenta.
     * @param accountId ID a establecer
     */
    public void setAccountId(long accountId) {
        this.account = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VotePK)) return false;

        VotePK votePK = (VotePK) o;

        if (film != votePK.film) return false;
        return account == votePK.account;
    }

    @Override
    public int hashCode() {
        int result = (int) (film ^ (film >>> 32));
        result = 31 * result + (int) (account ^ (account >>> 32));
        return result;
    }
}
