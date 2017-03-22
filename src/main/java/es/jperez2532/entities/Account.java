package es.jperez2532.entities;

import javax.persistence.*;
import java.util.*;

/**
 * Entidad Cuenta de usuario.
 * <p>
 * Se corresponde con la tabla "ACCOUNTS" de la Base de Datos.
 */
@Entity
@Table(name = "Accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "User_Name", length = 20, unique = true, nullable = false)
    private String userName;

    @Column(name = "Email", length = 100, nullable = false)
    private String email;

    @Column(name = "Password", length = 100, nullable = false)
    private String password;

    @Column(name = "Active", length = 1, nullable = false)
    private boolean active;

    @Column(name = "Provincia", length = 25, nullable = false)
    private String provincia;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Accounts_to_Roles",
                joinColumns = {@JoinColumn(name = "account_id", referencedColumnName = "id")},
                inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<AccountRole> accountRoles = new ArrayList<AccountRole>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Watchlist",
            joinColumns = {@JoinColumn(name = "account_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "film_id", referencedColumnName = "id")})
    private List<Film> watchlist = new ArrayList<Film>();

    // Sólo se utiliza a la hora de registrar una nueva cuenta
    @Transient
    private String passwordConfirm;

    /**
     * ¿Cuenta de un usuario administrador?
     *
     * @return <code>true</code> si la cuenta es de un administrador
     */
    public boolean isAdmin() {
        Iterator<AccountRole> it = accountRoles.iterator();
        while (it.hasNext()) {
            if (it.next().getRole().equals("ADMIN"))
                return true;
        }
        return false;
    }

    /**
     * Devuelve el ID de la cuenta.
     * @return ID de la cuenta
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID de la cuenta.
     * @param id ID de la cuenta
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre de usuario.
     * @return el nombre de usuario
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Establece el nombre de usuario.
     * @param userName el nombre de usuario
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Devuelve el email asociado a la cuenta.
     * @return el email asociado a la cuenta
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email asociado a la cuenta.
     * @param email email asociado a la cuenta
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve la contraseña de la cuenta.
     * @return la contraseña de la cuenta
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña de la cuenta.
     * @param password la contraseña de la cuenta
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Devuelve la provincia asociada a la cuenta.
     * @return la provincia asociada a la cuenta
     */
    public String getProvincia() {
        return provincia;
    }

    /**
     * Establece la provincia asociada a la cuenta.
     * @param provincia provincia asociada a la cuenta
     */
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    /**
     * Devuelve cierto si la cuenta está activa.
     * @return <code>true</code> si la cuenta está activa
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Establece el estado de cuenta activada/desactivada.
     * @param active el estado de la cuenta
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Devuelve los roles asociados a la cuenta.
     * @return los roles asociados a la cuenta
     */
    public List<AccountRole> getAccountRoles() {
        return accountRoles;
    }

    /**
     * Establece los roles asociados a la cuenta.
     * @param accountRoles Conjunto de roles que se quieren asociar a la cuenta
     */
    public void setAccountRoles(List<AccountRole> accountRoles) {
        this.accountRoles = accountRoles;
    }

    /**
     * Devuelve la confirmación de la contraseña.
     * <p>
     * Durante el registro de una nueva cuenta se necesita que este parámetro sea idéntico al
     * introducido en <em>password</em>.
     * @return la confirmación de la contraseña
     */
    public String getPasswordConfirm() {
        return this.passwordConfirm;
    }

    /**
     * Establece la confirmación de la contraseña.
     * @param passwordConfirm confirmación de la contraseña
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    /**
     * Devuelve la lista de reproducción del usuario.
     * @return lista de películas
     */
    public List<Film> getWatchlist() {
        return watchlist;
    }

    /**
     * Establece la lista de reproducción del usuario.
     * @param watchlist lista de películas
     */
    public void setWatchlist(List<Film> watchlist) {
        this.watchlist = watchlist;
    }

    @Override
    public String toString() {
        return "[" + this.userName + ", " + this.accountRoles + "]";
    }
}