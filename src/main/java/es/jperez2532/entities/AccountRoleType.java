package es.jperez2532.entities;

/**
 * Tipo enumerado con los roles posibles.
 */
public enum AccountRoleType {
    USER("USER"),
    ADMIN("ADMIN");

    String accountRoleType;

    AccountRoleType(String accountRoleType) {
        this.accountRoleType = accountRoleType;
    }

    /**
     * Devuelve el tipo de Rol.
     * @return el tipo de Rol
     */
    public String getAccountRoleType() {
        return accountRoleType;
    }
}
