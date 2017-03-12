package es.jperez2532.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Films")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 100, unique = true, nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "duration", length = 3, nullable = false)
    private String duration;

    @Column(name = "year", length = 4, nullable = false)
    private String year;

    @Column(name = "poster", length = 100, nullable = false)
    private String poster;

    @Column(name = "rating", length = 5)
    private String rating;

    @Column(name = "trailer", length = 11)
    private String trailer;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Films_to_Genres",
                joinColumns = {@JoinColumn(name = "film_id")},
                inverseJoinColumns = {@JoinColumn(name = "genre_id")})
    private List<Genre> filmGenres = new ArrayList<Genre>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Films_to_Directors",
            joinColumns = {@JoinColumn(name = "film_id")},
            inverseJoinColumns = {@JoinColumn(name = "director_id")})
    private List<Director> filmDirectors = new ArrayList<Director>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Films_to_Stars",
            joinColumns = {@JoinColumn(name = "film_id")},
            inverseJoinColumns = {@JoinColumn(name = "actor_id")})
    private List<Actor> filmStars = new ArrayList<Actor>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Films_to_Supportings",
            joinColumns = {@JoinColumn(name = "film_id")},
            inverseJoinColumns = {@JoinColumn(name = "actor_id")})
    private List<Actor> filmSupportings = new ArrayList<Actor>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "Films_to_Countries",
            joinColumns = {@JoinColumn(name = "film_id")},
            inverseJoinColumns = {@JoinColumn(name = "country_id")})
    private List<Country> filmCountries = new ArrayList<Country>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public List<Genre> getFilmGenres() {
        return filmGenres;
    }

    public void setFilmGenres(List<Genre> filmGenres) {
        this.filmGenres = filmGenres;
    }

    public List<Director> getFilmDirectors() {
        return filmDirectors;
    }

    public void setFilmDirectors(List<Director> filmDirectors) {
        this.filmDirectors = filmDirectors;
    }

    public List<Actor> getFilmStars() {
        return filmStars;
    }

    public void setFilmStars(List<Actor> filmStars) {
        this.filmStars = filmStars;
    }

    public List<Actor> getFilmSupportings() {
        return filmSupportings;
    }

    public void setFilmSupportings(List<Actor> filmSupportings) {
        this.filmSupportings = filmSupportings;
    }

    public List<Country> getFilmCountries() {
        return filmCountries;
    }

    public void setFilmCountries(List<Country> filmCountries) {
        this.filmCountries = filmCountries;
    }

    @Override
    public int hashCode() {
        int result = (getId() != null ? getId().hashCode() : 0);
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getDuration().hashCode();
        result = 31 * result + getYear().hashCode();
        result = 31 * result + getRating().hashCode();
        result = 31 * result + (getTrailer() != null ? getTrailer().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", year=" + year +
                ", poster='" + poster + '\'' +
                ", rating='" + rating + '\'' +
                ", trailer='" + trailer + '\'' +
                ", filmGenres=" + filmGenres +
                ", filmDirectors=" + filmDirectors +
                ", filmStars=" + filmStars +
                ", filmSupportings=" + filmSupportings +
                ", filmCountries=" + filmCountries +
                '}';
    }
}
