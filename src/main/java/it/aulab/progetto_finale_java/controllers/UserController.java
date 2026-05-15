package it.aulab.progetto_finale_java.controllers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_java.dtos.ArticleDto;
import it.aulab.progetto_finale_java.dtos.UserDTO;
import it.aulab.progetto_finale_java.models.User;
import it.aulab.progetto_finale_java.services.ArticleService;
import it.aulab.progetto_finale_java.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;





@Controller
public class UserController{
    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    //Rotta di home
    @GetMapping("/")
    public String home(Model viewModel){

        List<ArticleDto> articles = articleService.readAll();

        //ordino e invio al temeplate gli articli ordinati in modo decrescente
        Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());

        List<ArticleDto> lastThreeArticles= articles.stream().limit(3).collect(Collectors.toList());

        viewModel.addAttribute("articles", lastThreeArticles);
        
        return "home";
    }

    //rotta di registrazione
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDTO());
        return "auth/register";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    //Rotta per il salvataggio della registrazione
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDTO userDTO,
                                BindingResult result ,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                 HttpServletRequest request, HttpServletResponse response){
        
        User existingUser= userService.findUserByEmail(userDTO.getEmail());

        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
           result.rejectValue("email", null,
            "there is alredy an account registered with the same email"); 
        }

        if(result.hasErrors()){
            model.addAttribute("user", userDTO);
            return "auth/register";
        }
        userService.saveUser(userDTO, redirectAttributes, request, response);

        redirectAttributes.addFlashAttribute("successMessage", "Registratione avvenuta con successo!");

        return "redirect:/";
        
    }

    //rotta per la ricerca degli articoli in base all' utente
    @GetMapping("/search/{id}")
    public String userArticleSearch(@PathVariable("id") Long id, Model viewModel) {
        User user= userService.find(id);
        viewModel.addAttribute("title", "Tutti gli articoli di " + user.getUsername());

        List<ArticleDto> articles = articleService.searchByAuthor(user);
        viewModel.addAttribute("articles", articles);
        return "article/articles";
    }
    

    
}
