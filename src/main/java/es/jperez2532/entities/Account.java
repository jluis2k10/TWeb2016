package es.jperez2532.entities;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Jose Luis on 18/02/2017.
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Accounts_to_Roles",
                joinColumns = {@JoinColumn(name = "account_id")},
                inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<AccountRole> accountRoles = new HashSet<AccountRole>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Vote> accountVotes = new ArrayList<Vote>();

    // Sólo se utiliza a la hora de registrar una nueva cuenta
    @Transient
    private String passwordConfirm;

    /*@Transient
    private List<AccountRole> accountRolesList;*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<AccountRole> getAccountRoles() {
        return accountRoles;
    }

    public void setAccountRoles(Set<AccountRole> accountRoles) {
        this.accountRoles = accountRoles;
    }

    public String getPasswordConfirm() {
        return this.passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public List<Vote> getAccountVotes() {
        return accountVotes;
    }

    public void setAccountVotes(List<Vote> accountVotes) {
        this.accountVotes = accountVotes;
    }

    @Override
    public String toString() {
        return "[" + this.userName + ", " + this.accountRoles + "]";
    }
}