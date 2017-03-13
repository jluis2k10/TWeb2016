package es.jperez2532.entities;

import javax.persistence.*;

/**
 * La tabla Votes tiene una PK compuesta. Se necesita utilizar un Objeto (VotePK) que
 * modele dicha clave.
 */
@Entity
@Table(name = "Votes")
public class Vote {

    // PK compuesta (film_id, account_id)
    @EmbeddedId
    private VotePK id;

    @Column(name = "score", nullable = false)
    private int score;

    @ManyToOne
    @JoinColumn(name = "film_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Film film;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Account account;

    public VotePK getId() {
        return id;
    }

    public void setId(VotePK id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
