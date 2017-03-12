package es.jperez2532.validator;

import es.jperez2532.entities.Account;
import es.jperez2532.repositories.AccountRepo;
import es.jperez2532.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

/**
 * Created by Jose Luis on 05/03/2017.
 */
@Component
public class EditAccountValidator implements Validator {

    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private AccountValidations accountValidations;

    @Override
    public boolean supports(Class<?> aClass) {
        return Account.class.equals(aClass);
    }

    @Override
    public void validate(Object accountForm, Errors errors) {
        Account newAccount = (Account) accountForm;
        Account currentAccount = accountRepo.findByUserName(userService.getPrincipal());

        // Validar nombre de usuario
        if (!currentAccount.getUserName().equals(newAccount.getUserName())) {
            accountValidations.validateUserName(newAccount, errors);
        }

        // Validar Dirección Email
        if(!currentAccount.getEmail().equals(newAccount.getEmail())) {
            accountValidations.validateEmail(newAccount, errors);
        }
    }
}
