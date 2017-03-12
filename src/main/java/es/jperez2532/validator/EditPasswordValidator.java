package es.jperez2532.validator;

import es.jperez2532.components.ChangePassword;
import es.jperez2532.entities.Account;
import es.jperez2532.repositories.AccountRepo;
import es.jperez2532.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by Jose Luis on 05/03/2017.
 */
@Component
public class EditPasswordValidator implements Validator {

    @Autowired private UserService userService;
    @Autowired private AccountRepo accountRepo;
    @Autowired private BCryptPasswordEncoder encoder;

    @Override
    public boolean supports(Class<?> aClass) {
        return ChangePassword.class.equals(aClass);
    }

    @Override
    public void validate(Object changePasswordForm, Errors errors) {
        ChangePassword changePassword = (ChangePassword) changePasswordForm;
        Account currentAccount = accountRepo.findByUserName(userService.getPrincipal());

        if (!changePassword.getNewPassword().isEmpty()) {
            // Primero comprobamos que el password nuevo es correcto
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "NotEmpty");
            if (changePassword.getNewPassword().length() < 5 || changePassword.getNewPassword().length() > 30)
                errors.rejectValue("newPassword", "Size.registroForm.password");

            // Segundo comprobamos que se ha introducido correctamente el password antiguo
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "oldPassword", "NotEmpty");
            if (!encoder.matches(changePassword.getOldPassword(), currentAccount.getPassword()))
                errors.rejectValue("oldPassword", "NoMatch.changePasswordForm.oldPassword");
        }
    }
}
