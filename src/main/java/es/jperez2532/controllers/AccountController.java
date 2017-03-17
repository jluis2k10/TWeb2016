package es.jperez2532.controllers;

import es.jperez2532.components.ChangePassword;
import es.jperez2532.entities.Account;
import es.jperez2532.entities.Film;
import es.jperez2532.repositories.FilmRepo;
import es.jperez2532.services.UserService;
import es.jperez2532.validator.AccountValidator;
import es.jperez2532.validator.EditPasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/micuenta")
public class AccountController extends MainController {

    @Autowired private UserService userService;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private AccountValidator accountValidator;
    @Autowired private EditPasswordValidator editPasswordValidator;
    @Autowired private FilmRepo filmRepo;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String editar(Model model, Principal principal) {
        Account currentAccount = userService.findByUserName(principal.getName());
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
                         RedirectAttributes redirectAttributes, Model model) {

        editPasswordValidator.validate(changePassword, bindingResultPassword);
        accountValidator.validateUpdated(accountForm, bindingResultCuenta);
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
        redirectAttributes.addFlashAttribute("infoMsg", "Cuenta actualizada correctamente.");
        return("redirect:/");
    }

    @RequestMapping(value = "/milista", method = RequestMethod.GET)
    public String miLista(Model model, Principal principal) {
        Account account = userService.findByUserName(principal.getName());
        model.addAttribute("films", account.getWatchlist());
        model.addAttribute("title", "Lista de reproducción - PelisUNED");
        return("micuenta/milista");
    }

    @ResponseBody
    @RequestMapping(value = "/milista/add", method = RequestMethod.GET)
    public ResponseEntity<String> addWatchList (@RequestParam("film-id") Long filmId, Principal principal) {
        Account account = userService.findByUserName(principal.getName());
        Film film = filmRepo.findOne(filmId);
        account.getWatchlist().add(film);
        userService.updateWatchlist(account);
        return ResponseEntity.ok("{}");
    }

    @ResponseBody
    @RequestMapping(value = "/milista/delete", method = RequestMethod.GET)
    public ResponseEntity<String> deleteWatchList (@RequestParam("film-id") Long filmId, Principal principal) {
        Account account = userService.findByUserName(principal.getName());
        Film film = filmRepo.findOne(filmId);
        account.getWatchlist().remove(film);
        userService.updateWatchlist(account);
        return ResponseEntity.ok("{}");
    }
}
