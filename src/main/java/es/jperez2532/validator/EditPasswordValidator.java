package es.jperez2532.validator;

import es.jperez2532.components.ChangePassword;
import es.jperez2532.entities.Account;
import es.jperez2532.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Clase que se utiliza para validar un cambio en la contraseña de una Cuenta
 * de usuario.
 */
@Component
public class EditPasswordValidator implements Validator {

    private final UserService userService;
    private final BCryptPasswordEncoder encoder;

    /**
     * Constructor de la clase con las inyecciones de dependencia apropiadas.
     * @param userService inyección {@link UserService}
     * @param encoder     inyección {@link BCryptPasswordEncoder}
     */
    @Autowired
    public EditPasswordValidator(UserService userService, BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Comprueba que el <em>validator</em> puede trabajar con la clase que se le pasa.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return ChangePassword.class.equals(aClass);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Valida una nueva contraseña.
     */
    @Override
    public void validate(Object changePasswordForm, Errors errors) {
        ChangePassword changePassword = (ChangePassword) changePasswordForm;
        Account currentAccount = userService.findByUserName(userService.getPrincipal(), false);

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
