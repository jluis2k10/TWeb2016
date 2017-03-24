package es.jperez2532.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Entidad que modela a una Película.
 * <p>
 * Se corresponde con la tabla "FILMS" de la Base de Datos.
 */
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

    /* Puntuación (estrellas) de la película */
    @Column(name = "score", precision = 3, scale = 2)
    private BigDecimal score;

    /* Cantidad de votos que los usuarios han emitido sobre la película */
    @Column(name = "nvotes")
    private int nvotes;

    /* Cantidad de visualizaciones de la película */
    @Column(name = "views")
    private int views;

    /*
    Géneros de la Película. Asociación Uno-A-Muchos con la entidad Genre. Se utiliza la
    tabla "FILMS_TO_GENRES" para mapear dicha asociación.
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Films_to_Genres",
                joinColumns = {@JoinColumn(name = "film_id", referencedColumnName = "id")},
                inverseJoinColumns = {@JoinColumn(name = "genre_id", referencedColumnName = "id")})
    private List<Genre> filmGenres = new ArrayList<>();

    /*
    Directores de la Película. Asociación Uno-A-Muchos con la entidad Director. Se utiliza la
    tabla "FILMS_TO_DIRECTORS" para mapear dicha asociación.
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Films_to_Directors",
            joinColumns = {@JoinColumn(name = "film_id")},
            inverseJoinColumns = {@JoinColumn(name = "director_id")})
    private List<Director> filmDirectors = new ArrayList<>();

    /*
    Actores principales/estrellas de la Película. Asociación Uno-A-Muchos con la entidad Actor.
    Se utiliza la tabla "FILMS_TO_STARS" para mapear dicha asociación.
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Films_to_Stars",
            joinColumns = {@JoinColumn(name = "film_id")},
            inverseJoinColumns = {@JoinColumn(name = "actor_id")})
    private List<Actor> filmStars = new ArrayList<>();

    /*
    Actores secundarios/reparto de la Película. Asociación Uno-A-Muchos con la entidad Actor.
    Se utiliza la tabla "FILMS_TO_SUPPORTINGS" para mapear dicha asociación.
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Films_to_Supportings",
            joinColumns = {@JoinColumn(name = "film_id")},
            inverseJoinColumns = {@JoinColumn(name = "actor_id")})
    private List<Actor> filmSupportings = new ArrayList<>();

    /*
    Países de la Película. Asociación Uno-A-Muchos con la entidad Country. Se utiliza la
    tabla "FILMS_TO_COUNTRIES" para mapear dicha asociación.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "Films_to_Countries",
            joinColumns = {@JoinColumn(name = "film_id")},
            inverseJoinColumns = {@JoinColumn(name = "country_id")})
    private List<Country> filmCountries = new ArrayList<>();

    /*
    Usuarios que tienen a esta película en su lista de reproducción. Asociación Uno-A-Muchos
    con la entidad Account. Se utiliza la tabla "WATCHLIST" para mapear dicha asociación.
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Watchlist",
            joinColumns = {@JoinColumn(name = "film_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "account_id", referencedColumnName = "id")})
    private List<Account> listedIn = new ArrayList<>();

    /**
     * Constructor de la clase.
     * <p>
     * Necesitamos inicializar el atributo <code>score</code> para tener un valor
     * disponible en el formulario de añadir una nueva película y así poder enviarlo
     * a la Base de Datos.
     */
    public Film() {
        // Se necesita inicializar score para tener un valor disponible
        // en el formulario de añadir una nueva película
        this.score = new BigDecimal(0);
    }

    /**
     * Devuelve el ID de la Película.
     * @return el ID de la Película
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID de la Película.
     * @param id el ID a establecer
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el título de la Película.
     * @return el título de la Película
     */
    public String getTitle() {
        return title;
    }

    /**
     * Establece el título de la Película.
     * @param title el título a establecer
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Devuelve la descripción/sinopsis de la Película.
     * @return la descripción de la Película
     */
    public String getDescription() {
        return description;
    }

    /**
     * Establece la descripción/sinopsis de la Película.
     * @param description la descripción a establecer
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Devuelve la dureación, en minutos, de la Película.
     * @return la duración en minutos de la Película
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Establece la duración, en minutos, de la Película.
     * @param duration la duración en minutos a establecer
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Devuelve el año de estreno de la Película.
     * @return el año de estreno de la Película
     */
    public String getYear() {
        return year;
    }

    /**
     * Establece el año de estreno de la Película.
     * @param year año de estreno de la Película
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Devuelve el nombre del archivo de la imagen con el póster promocional de la Película.
     * @return nombre del archivo del póster de la Película
     */
    public String getPoster() {
        return poster;
    }

    /**
     * Establece el nombre del archivo de la imagen con el póster promocional de la Película.
     * @param poster nombre del archivo del póster de la película
     */
    public void setPoster(String poster) {
        this.poster = poster;
    }

    /**
     * Devuelve el rating (clasificación por edad) de la Película.
     * @return el rating de la Película
     */
    public String getRating() {
        return rating;
    }

    /**
     * Establece el rating (clasificación por edad) de la Película
     * @param rating el rating a establecer
     */
    public void setRating(String rating) {
        this.rating = rating;
    }

    /**
     * Devuelve el ID de un vídeo de Youtube (supuestamente con el tráiler de la película).
     * @return el ID de un vídeo de Youtube
     */
    public String getTrailer() {
        return trailer;
    }

    /**
     * Establece el ID de un vídeo de Youtube (supuestamente con el tráiler de la película).
     * @param trailer ID de un vídeo de Youtube
     */
    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    /**
     * Devuelve la puntuación (estrellas) de la Película.
     * @return la puntuación de la película
     */
    public BigDecimal getScore() {
        return score;
    }

    /**
     * Establece la puntuación (estrellas) de la Película.
     * @param score puntuación de la Película
     */
    public void setScore(BigDecimal score) {
        this.score = score;
    }

    /**
     * Devuelve la cantidad de votos que se han hecho a la Película.
     * @return la cantidad de votos
     */
    public int getNvotes() {
        return nvotes;
    }

    /**
     * Establece la cantidad de votos que tiene la Película.
     * @param nvotes cantidad de votos
     */
    public void setNvotes(int nvotes) {
        this.nvotes = nvotes;
    }

    /**
     * Devuelve la cantidad de visualizaciones que se han hecho a la Película.
     * @return la cantidad de visualizaciones
     */
    public int getViews() {
        return views;
    }

    /**
     * Establece la cantidad de visualizaciones que se han hecho a la Película.
     * @param views cantidad de visualizaciones
     */
    public void setViews(int views) {
        this.views = views;
    }

    /**
     * Devuelve una lista con los géneros de la Película.
     * @return lista con los géneros de la Película
     */
    public List<Genre> getFilmGenres() {
        return filmGenres;
    }

    /**
     * Establece una lista con los géneros de la Película.
     * @param filmGenres lista con los géneros a establecer
     */
    public void setFilmGenres(List<Genre> filmGenres) {
        this.filmGenres = filmGenres;
    }

    /**
     * Devuelve una lista con los directores de la Película.
     * @return lista con los directores de la Película
     */
    public List<Director> getFilmDirectors() {
        return filmDirectors;
    }

    /**
     * Establece una lista con los directores de la Película.
     * @param filmDirectors lista con los directores a establecer
     */
    public void setFilmDirectors(List<Director> filmDirectors) {
        this.filmDirectors = filmDirectors;
    }

    /**
     * Devuelve una lista con los actores principales de la Película.
     * @return lista con los actores principales de la Película
     */
    public List<Actor> getFilmStars() {
        return filmStars;
    }

    /**
     * Establece una lista con los actores principales de la Película.
     * @param filmStars lista con los actores principales a establecer
     */
    public void setFilmStars(List<Actor> filmStars) {
        this.filmStars = filmStars;
    }

    /**
     * Devuelve una lista con los actores secundarios de la Película.
     * @return lista con los actores secundarios de la Película
     */
    public List<Actor> getFilmSupportings() {
        return filmSupportings;
    }

    /**
     * Establece una lista con los actores secundarios de la Película.
     * @param filmSupportings lista con los actores secundarios a establecer
     */
    public void setFilmSupportings(List<Actor> filmSupportings) {
        this.filmSupportings = filmSupportings;
    }

    /**
     * Devuelve una lista con los países de la Película.
     * @return lista con los países de la Película
     */
    public List<Country> getFilmCountries() {
        return filmCountries;
    }

    /**
     * Establece una lista con los países de la Película.
     * @param filmCountries lista con los países a establecer
     */
    public void setFilmCountries(List<Country> filmCountries) {
        this.filmCountries = filmCountries;
    }

    /**
     * Devuelve una lista con las cuentas que tienen a esta Película en su lista de reproducción.
     * @return lista de cuentas con esta película en su lista de reproducción
     */
    public List<Account> getListedIn() {
        return listedIn;
    }

    /**
     * Establece una lista con las cuentas que tienen a esta Película en su lista de reproducción.
     * @param listedIn lista de cuentas con esta película en su lista de reproducción
     */
    public void setListedIn(List<Account> listedIn) {
        this.listedIn = listedIn;
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
                ", year='" + year + '\'' +
                ", score=" + score +
                ", views=" + views +
                ", filmGenres=" + filmGenres +
                '}';
    }

    /* Clases Comparator necesarias para ordenar una colección de objetos Película según
     * ciertos campos (se utilizará al ordenar resultados de búsqueda) */

    /**
     * Comparar Películas por ID
     */
    public static class ComparatorFilmId implements Comparator<Film> {
        /**
         * Devuelve un entero positivo si la película con la que se compara tiene un ID menor.
         *
         * @param film0 Película a comparar
         * @param film1 Película con la que se compara
         * @return entero >0 si ID superior, 0 si IDs iguales, <0 si ID inferior
         */
        @Override
        public int compare(Film film0, Film film1) {
            return film0.getId().intValue() - film1.getId().intValue();
        }
    }

    /**
     * Comparar Películas por Título
     */
    public static class ComparatorFilmTitle implements Comparator<Film> {
        /**
         * Devuelve un entero positivo si la película con la que se compara está alfabéticamente
         * ordenada por debajo.
         *
         * @param film0 Película a comparar
         * @param film1 Película con la que se compara
         * @return entero >0, 0 o <0 en función del orden alfabético de las películas comparadas
         */
        @Override
        public int compare(Film film0, Film film1) {
            return film0.getTitle().compareToIgnoreCase(film1.getTitle());
        }
    }

    /**
     * Comparar Películas por Año de estreno
     */
    public static class ComparatorFilmYear implements Comparator<Film> {
        /**
         * Devuelve un entero positivo si la película con la que se compara tiene un año inferior.
         *
         * @param film0 Película a comparar
         * @param film1 Película con la que se compara
         * @return entero >0 si año superior, 0 si años iguales, <0 si año inferior
         */
        @Override
        public int compare(Film film0, Film film1) {
            return Integer.parseInt(film0.getYear()) - Integer.parseInt(film1.getYear());
        }
    }

    /**
     * Comparar Películas por Puntuación (estrellas)
     */
    public static class ComparatorFilmScore implements Comparator<Film> {
        /**
         * Devuelve un entero positivo si la película con la que se compara tiene una
         * puntuación inferior.
         *
         * @param film0 Película a comparar
         * @param film1 Película con la que se compara
         * @return entero >0 si puntuación superior, 0 si puntuaciones iguales,
         *         <0 si puntuación inferior
         */
        @Override
        public int compare(Film film0, Film film1) {
            return film0.getScore().intValueExact() - film1.getScore().intValueExact();
        }
    }
}
