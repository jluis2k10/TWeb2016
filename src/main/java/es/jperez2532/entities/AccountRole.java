package es.jperez2532.entities;

import javax.persistence.*;

/**
 * Entidad Rol.
 * <p>
 * Se corresponde con la tabla "ACCOUNT_ROLES" de la Base de Datos. Modela
 * un Rol (administrador o usuario) asociado a una cuenta de usuario.
 */
@Entity
@Table(name = "Account_Roles")
public class AccountRole {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "role", length = 15, unique = true, nullable = false)
    private String role = AccountRoleType.USER.getAccountRoleType();

    /**
     * Devuelve el ID del Rol.
     * @return ID del Rol
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del Rol.
     * @param id ID del Rol
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Devuelve el Rol en forma de String.
     * @return el Rol en forma de String
     */
    public String getRole() {
        return role;
    }

    /**
     * Establece el Rol.
     * @param role Rol a establecer
     */
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "AccountRole [id=" + id + ", role=" + role + "]";
    }
}
