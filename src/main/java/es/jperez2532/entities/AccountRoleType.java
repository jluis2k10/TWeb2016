package es.jperez2532.entities;

/**
 * Created by Jose Luis on 20/02/2017.
 */
public enum AccountRoleType {
    USER("USER"),
    ADMIN("ADMIN");

    String accountRoleType;

    private AccountRoleType(String accountRoleType) {
        this.accountRoleType = accountRoleType;
    }

    public String getAccountRoleType() {
        return accountRoleType;
    }
}
