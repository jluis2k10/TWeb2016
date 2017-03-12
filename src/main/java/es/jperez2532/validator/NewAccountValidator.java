package es.jperez2532.validator;

import es.jperez2532.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validamos los datos introducidos en el formulario /registro
 * donde se registran los nuevos usuarios
 */
@Component
public class NewAccountValidator implements Validator {

    @Autowired
    private AccountValidations accountValidations;

    @Override
    public boolean supports(Class<?> aClass) {
        return Account.class.equals(aClass); // TODO: ¿implementar equals() propio en entidad Account?
    }

    @Override
    public void validate(Object accountForm, Errors errors) {
        Account account = (Account) accountForm;
        accountValidations.validateUserName(account, errors);
        accountValidations.validateEmail(account, errors);
        accountValidations.validatePassword(account, errors);
        accountValidations.validateProvincia(account, errors);
    }
}
