package es.jperez2532.controllers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.jperez2532.entities.Account;
import es.jperez2532.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

/**
 * Controlador para las páginas que manejan a los usuarios en la administración.
 */
@Controller
@RequestMapping(value = "/admin")
public class AdminUsersController extends MainController {

    private final UserService userService;

    /**
     * Constructor de la clase con las inyecciones de dependencia apropiadas.
     *
     * @param userService inyección {@link UserService}
     */
    @Autowired
    public AdminUsersController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Muestra la lista, paginada, de todos los usuarios registrados en el sistema o de
     * aquellos que coinciden con el término indicado en el parámetro <code>buscar</code>.
     *
     * @param model     Interfaz/contenedor para pasar datos a la Vista
     * @param pageable  Interfaz con información sobre la paginación
     * @param principal Token de autenticación del usuario
     * @param buscar    Nombre del usuario a buscar (opcional)
     * @return La Vista a mostrar
     */
    @RequestMapping(value = "/usuarios")
    public String users(Model model, Pageable pageable, Principal principal,
                        @RequestParam(value = "buscar", required = false) String buscar) {
        Account loggedAccount = userService.findByUserName(principal.getName(), false);
        String url_params = "?";
        Page<Account> page;

        if (buscar != null) {
            page = userService.findUsersByUserName(buscar, pageable);
            url_params = "?buscar=" + buscar + "&";
            model.addAttribute("buscando", buscar);
        }
        else {
            page = userService.findAll(pageable);
        }

        if (page.getTotalElements() != 0) {
            List<Account> accounts = page.getContent();
            model.addAttribute("accounts", accounts);
        }
        else {
            model.addAttribute("infoMsg", "No existen resultados para el término: <em>" + buscar + "</em>");
        }

        model.addAttribute("loggedAccount", loggedAccount);
        model.addAttribute("url_params", url_params);
        model.addAttribute("page", page);
        model.addAttribute("title", "Administrar Usuarios - PelisUNED");
        return "admin/usuarios";
    }

    /**
     * Maneja la petición de editar alguna de las propiedades del usuario dado y devuelve
     * una respuesta acorde en formato JSON.
     *
     * @param accountId ID del usuario a editar
     * @param modify    Parámetro a modificar
     * @param action    Acción a realizar sobre el parámetro <code>modify</code> (add, delete)
     * @param principal Token de autenticación del usuario
     * @return Respuesta con objeto JSON de la operación
     */
    @ResponseBody
    @RequestMapping("/usuarios/edit")
    public ResponseEntity<ObjectNode> addAdmin(@RequestParam("accountId") Long accountId,
                                               @RequestParam("modify") String modify,
                                               @RequestParam("action") String action,
                                               Principal principal) {
        ObjectNode response = JsonNodeFactory.instance.objectNode();
        Account accountToEdit = userService.findOne(accountId);

        // Aunque el checkbox aparece desactivado, comprobamos que el administrador no está tratando
        // de editarse a sí mismo (por seguridad, se podría cambiar el estado del checkbox manualmente)
        Account loggedAccount = userService.findByUserName(principal.getName());
        if (loggedAccount.getId().equals(accountToEdit.getId())) {
            response.put("message", "Error: no puedes editar tu propio usuario!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String message = userService.update(accountToEdit, modify, action);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Maneja la petición de borrar la cuenta de un usuario dado y devuelve
     * una respuesta acorde en formato JSON.
     *
     * @param accountId ID del usuario a borrar
     * @param principal Token de autenticación del usuario
     * @return Respuesta con objeto JSON de la operación
     */
    @ResponseBody
    @RequestMapping(value = "/usuarios/delete")
    public ResponseEntity<ObjectNode> deleteUser(@RequestParam("accountId") Long accountId,
                                                 Principal principal) {
        ObjectNode response = JsonNodeFactory.instance.objectNode();
        Account accountToDelete = userService.findOne(accountId);

        // Comprobamos que el administrador no está intentando borrarse a sí mismo
        Account loggedAccount = userService.findByUserName(principal.getName());
        if (loggedAccount.getId().equals(accountToDelete.getId())) {
            response.put("message", "Error: no puedes borrar tu propia cuenta!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        userService.delete(accountToDelete);
        response.put("message", "Cuenta de " + accountToDelete.getUserName() + " eliminada.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
