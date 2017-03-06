/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblio.web.rest;

import com.biblio.config.util.Constants;
import com.biblio.entity.User;
import com.biblio.repository.UserRepository;
import com.biblio.service.MailService;
import com.biblio.service.UserService;
import com.biblio.web.rest.vm.ManagedUserVM;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author kouwonou
 */
@RestController
@RequestMapping(value = "/api")
public class UserResource {

    @Inject
    private UserService userService;
    @Inject
    private MailService mailService;
    @Inject
    private UserRepository userRepository;
   

    @RequestMapping(value = "/register",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
   
    public Object createUser(@RequestBody @Valid ManagedUserVM managedUserVM, BindingResult bindingResult) {
        Map<String, Object> modele = new HashMap<>();
        if (bindingResult.hasErrors()) {

            modele.put(Constants.ERROR, true);
            modele.put(Constants.MESSAGE, "Enregistrement échoué");
            for (FieldError f : bindingResult.getFieldErrors()) {
                modele.put(f.getField(), f.getDefaultMessage());

            }

            return modele;
        }
        if (userRepository.findOneByLogin(managedUserVM.getLogin()) .isPresent()) {
            modele.put(Constants.ERROR, "true");
            modele.put(Constants.MESSAGE, "Enregistrement échoué");
            modele.put("login", "Ce nom d'utilisateur existe deja");
            return modele;
        }
        if (userRepository.findOneByEmail(managedUserVM.getEmail()).isPresent()) {
            modele.put(Constants.ERROR, "true");
            modele.put(Constants.MESSAGE, "Enregistrement échoué");
            modele.put("email", "Ce email est deja utilisé");
            return modele;
        }
        User u = userService.createUser(managedUserVM);
        modele.put(Constants.MESSAGE, "Enregistrement réussi");
        return modele;
    }
}
