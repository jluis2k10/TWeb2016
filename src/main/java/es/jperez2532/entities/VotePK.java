package es.jperez2532.entities;

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

    @Column(name = "film_id")
    public long filmId;

    @Column(name = "account_id")
    public long accountId;

    public VotePK() { }

    public VotePK(long filmId, long accountId) {
        this.filmId = filmId;
        this.accountId = accountId;
    }

    public long getFilmId() {
        return filmId;
    }

    public void setFilmId(long filmId) {
        this.filmId = filmId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VotePK)) return false;

        VotePK votePK = (VotePK) o;

        if (filmId != votePK.filmId) return false;
        return accountId == votePK.accountId;
    }

    @Override
    public int hashCode() {
        int result = (int) (filmId ^ (filmId >>> 32));
        result = 31 * result + (int) (accountId ^ (accountId >>> 32));
        return result;
    }
}
