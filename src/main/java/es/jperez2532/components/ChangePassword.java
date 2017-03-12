package es.jperez2532.components;

import org.springframework.stereotype.Component;

@Component
public class ChangePassword {

    private String newPassword;
    private String oldPassword;

    public ChangePassword() {
        this.newPassword = "";
        this.oldPassword = "";
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
