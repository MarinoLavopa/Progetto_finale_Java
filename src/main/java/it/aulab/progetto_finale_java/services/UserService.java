package it.aulab.progetto_finale_java.services;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_java.dtos.UserDTO;
import it.aulab.progetto_finale_java.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    void saveUser(UserDTO userDTO, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response);
    User findUserByEmail(String email);
    User find(Long id);
}
