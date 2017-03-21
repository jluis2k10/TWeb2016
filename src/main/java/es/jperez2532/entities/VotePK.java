package es.jperez2532.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Primary key compuesta para la clase Vote.class
 * Necesitamos un constructor para generar la clave compuesta cada vez que creamos el objeto,
 * y un constructor vacío que es el que usará Hibernate.
 */
@Embeddable
public class VotePK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Basic
    @Column(name = "film_id")
    public long film;

    @Basic
    @Column(name = "account_id")
    public long account;

    public VotePK() { }

    public VotePK(long filmId, long accountId) {
        this.film = filmId;
        this.account = accountId;
    }

    public long getFilmId() {
        return film;
    }

    public void setFilmId(long filmId) {
        this.film = filmId;
    }

    public long getAccountId() {
        return account;
    }

    public void setAccountId(long accountId) {
        this.account = accountId;
    }

    /** (non-Javadoc)
	 * @see java.lang.Object#equals(Object)
	 */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VotePK)) return false;

        VotePK votePK = (VotePK) o;

        if (film != votePK.film) return false;
        return account == votePK.account;
    }

    /** (non-Javadoc)
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = (int) (film ^ (film >>> 32));
        result = 31 * result + (int) (account ^ (account >>> 32));
        return result;
    }
}
