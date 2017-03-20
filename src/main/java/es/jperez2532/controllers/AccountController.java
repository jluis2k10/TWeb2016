package es.jperez2532.controllers;

import es.jperez2532.components.ChangePassword;
import es.jperez2532.entities.Account;
import es.jperez2532.services.UserService;
import es.jperez2532.validator.AccountValidator;
import es.jperez2532.validator.EditPasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Controlador para manejar aspectos relacionados con la cuenta del usuario.
 */
@Controller
@RequestMapping("/micuenta")
public class AccountController extends MainController {

    @Autowired private UserService userService;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private AccountValidator accountValidator;
    @Autowired private EditPasswordValidator editPasswordValidator;

    /**
     * Presenta el formulario para editar los datos y la contraseña del usuario.
     *
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @param principal Token de autenticación del usuario
     * @return Vista a mostrar
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String edit(Model model, Principal principal) {
        Account currentAccount = userService.findByUserName(principal.getName());
        ChangePassword changePassword = new ChangePassword();
        model.addAttribute("editarCuentaForm", currentAccount);
        model.addAttribute("provincias", userService.getProvincias());
        model.addAttribute("changePasswordForm", changePassword);
        model.addAttribute("title", "Mi cuenta - PelisUNED");
        return("micuenta/editar");
    }

    /**
     * Recoge el formulario (<code>POST</code>) con la información actualizada del usuario.
     *
     * @param accountForm Contenedor de los datos introducidos en el formulario para actualizar la cuenta
     * @param bindingResultCuenta Errores en el formulario <code>accountForm</code>
     * @param changePassword Contenedor de los datos introducidos en el formulario para cambiar la contraseña
     * @param bindingResultPassword Errores en el formulario <code>changePassword</code>
     * @param redirectAttributes Interfaz/contenedor para pasar datos a una Redirección
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @return Vista a mostrar o redirección a efectuar
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public String edit(@ModelAttribute("editarCuentaForm") Account accountForm,
                       BindingResult bindingResultCuenta,
                       @ModelAttribute("changePasswordForm") ChangePassword changePassword,
                       BindingResult bindingResultPassword,
                       RedirectAttributes redirectAttributes, Model model) {
        editPasswordValidator.validate(changePassword, bindingResultPassword);
        accountValidator.validateUpdated(accountForm, bindingResultCuenta);
        if(bindingResultCuenta.hasErrors() || bindingResultPassword.hasErrors()) {
            model.addAttribute("provincias", userService.getProvincias());
            model.addAttribute("title", "Registro - PelisUNED");
            return "micuenta/editar";
        }
        userService.updateOwn(accountForm, changePassword);
        redirectAttributes.addFlashAttribute("infoMsg", "Cuenta actualizada correctamente.");
        return("redirect:/");
    }

    /**
     * Presenta la solicitud de confirmación antes de borrar una cuenta de usuario.
     *
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @return Vista a mostrar
     */
    @RequestMapping(value = "/borrar", method = RequestMethod.GET)
    public String delete(Model model) {
        model.addAttribute("title", "Confirmación - PelisUNED");
        return("micuenta/borrar");
    }

    /**
     * Recoge el formulario (<code>POST</code>) de confirmación de borrar cuenta de usuario.
     *
     * @param principal Token de autenticación del usuario
     * @param redirectAttributes Interfaz/contenedor para pasar datos a una Redirección
     * @param confirm Valor del checkbox <code>confirm</code> del formulario recogido
     * @return Redirección a portada
     */
    @RequestMapping(value = "/borrar", method = RequestMethod.POST)
    public String delete(Principal principal, RedirectAttributes redirectAttributes,
                         @RequestParam("confirm") String confirm) {
        if (confirm.equals("on")) {
            Account account = userService.findByUserName(principal.getName());
            if (!userService.deleteOwn(account)) {
                redirectAttributes.addFlashAttribute("infoMsg",
                        "Eres el único usuario administrador, no es posibile eliminar tu cuenta.");
            }
        }
        return "redirect:/";
    }

    /**
     * Presenta la lista de reproducción del usuario.
     *
     * @param model Interfaz/contenedor para pasar datos a la Vista
     * @param principal Token de autenticación del usuario
     * @return Vista a mostrar
     */
    @RequestMapping(value = "/milista", method = RequestMethod.GET)
    public String myWatchList(Model model, Principal principal) {
        Account account = userService.findByUserName(principal.getName());
        model.addAttribute("films", account.getWatchlist());
        model.addAttribute("title", "Lista de reproducción - PelisUNED");
        return("micuenta/milista");
    }

    /**
     * Recoge una petición para añadir una película a la lista de reproducción del usuario
     * y devuelve confirmación JSON de la operación.
     *
     * @param filmId ID de la película a añadir a la lista
     * @param principal Token de autenticación del usuario
     * @return Resupuesta JSON (en forma de text/html)
     */
    @ResponseBody
    @RequestMapping(value = "/milista/add", method = RequestMethod.GET)
    public ResponseEntity<String> addToWatchList(@RequestParam("film-id") Long filmId, Principal principal) {
        userService.addToWatchlist(principal.getName(), filmId);
        return ResponseEntity.ok("{}");
    }

    /**
     * Recoge una petición para eliminar una película de la lista de reproducción del usuario
     * y devuelve confirmación JSON de la operación.
     * @param filmId ID de la película a eliminar de la lista
     * @param principal Token de autenticación del usuario
     * @return Respuesta JSON (en forma de text/html)
     */
    @ResponseBody
    @RequestMapping(value = "/milista/delete", method = RequestMethod.GET)
    public ResponseEntity<String> deleteFromWatchList(@RequestParam("film-id") Long filmId, Principal principal) {
        userService.deleteFromWatchlist(principal.getName(), filmId);
        return ResponseEntity.ok("{}");
    }
}
