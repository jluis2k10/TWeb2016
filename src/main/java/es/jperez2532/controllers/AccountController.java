package es.jperez2532.controllers;

import es.jperez2532.components.ChangePassword;
import es.jperez2532.entities.Account;
import es.jperez2532.repositories.AccountRepo;
import es.jperez2532.services.UserService;
import es.jperez2532.validator.EditAccountValidator;
import es.jperez2532.validator.EditPasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Controller
@EnableWebMvc
@RequestMapping("/micuenta")
public class AccountController extends MainController {

    @Autowired private AccountRepo accountRepo;
    @Autowired private UserService userService;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private EditAccountValidator editAccountValidator;
    @Autowired private EditPasswordValidator editPasswordValidator;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String editar(Model model) {
        String accountUsername = userService.getPrincipal();
        Account currentAccount = accountRepo.findByUserName(accountUsername);
        ChangePassword changePassword = new ChangePassword();
        model.addAttribute("editarCuentaForm", currentAccount);
        model.addAttribute("provincias", userService.getProvincias());
        model.addAttribute("changePasswordForm", changePassword);
        model.addAttribute("title", "PelisUNED - Mi Cuenta");
        return("micuenta/editar");
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String editar(@ModelAttribute("editarCuentaForm") Account accountForm,
                         BindingResult bindingResultCuenta,
                         @ModelAttribute("changePasswordForm") ChangePassword changePassword,
                         BindingResult bindingResultPassword,
                         Model model) {

        editPasswordValidator.validate(changePassword, bindingResultPassword);
        editAccountValidator.validate(accountForm, bindingResultCuenta);
        if(bindingResultCuenta.hasErrors() || bindingResultPassword.hasErrors()) {
            model.addAttribute("provincias", userService.getProvincias());
            model.addAttribute("title", "PelisUNED - Registro");
            return "micuenta/editar";
        }
        userService.update(accountForm, changePassword);
        // Una vez se ha actualizado el usuario en la BBDD hay que recargar el contexto de la seguridad
        // con los nuevos valores (es como si autentificáramos de manera manual con las nuevas credenciales)
        UserDetails userDetails = userDetailsService.loadUserByUsername(accountForm.getUserName());
        Authentication auth = new PreAuthenticatedAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return("redirect:/");
    }

    @RequestMapping("/listadereproduccion")
    public String listaRepro(Model model) {
        return ("micuenta/listaRepro");
    }

}
