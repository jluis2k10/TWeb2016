package es.jperez2532.entities;

import javax.persistence.*;

/**
 * Created by Jose Luis on 20/02/2017.
 */
@Entity
@Table(name = "Account_Roles")
public class AccountRole {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "role", length = 15, unique = true, nullable = false)
    private String role = AccountRoleType.USER.getAccountRoleType();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "AccountRole [id=" + id + ", role=" + role + "]";
    }
}
