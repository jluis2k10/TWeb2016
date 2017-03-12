package es.jperez2532.controllers;

import es.jperez2532.entities.Account;
import es.jperez2532.services.UserService;
import es.jperez2532.validator.AccountValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Jose Luis on 18/02/2017.
 */
@Controller
@EnableWebMvc
public class HomeController extends MainController {

    @Autowired private UserService userService;
    @Autowired private AccountValidator accountValidator;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "PelisUNED");
        return "index";
    }

    @RequestMapping(value = "/login",  method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model) {
        String ref = request.getHeader("Referer");
        request.getSession().setAttribute("url_prior_login", ref);
        model.addAttribute("title", "PelisUNED - Acceso");
        return "login";
    }

    @RequestMapping(value = "/registro", method = RequestMethod.GET)
    public String registro(Model model) {
        model.addAttribute("registroForm", new Account());
        model.addAttribute("provincias", userService.getProvincias());
        model.addAttribute("title", "PelisUNED - Registro");
        return "registro";
    }

    @RequestMapping(value = "/registro", method = RequestMethod.POST)
    public String registro(@ModelAttribute("registroForm") Account accountForm,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes,
                           Model model) {
        accountValidator.validate(accountForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("provincias", userService.getProvincias());
            model.addAttribute("title", "PelisUNED - Registro");
            return "registro";
        }
        userService.save(accountForm);
        redirectAttributes.addFlashAttribute("infoMsg",
                "Cuenta registrada. Ya puedes iniciar sesión con ella.");
        return "redirect:/";
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String error(Model model) {
        return "_errors";
    }

}
