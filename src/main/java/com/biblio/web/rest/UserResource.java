/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblio.web.rest;

import com.biblio.config.util.Constants;
import com.biblio.entity.User;
import com.biblio.repository.UserRepository;
import com.biblio.security.SecurityUtils;
import com.biblio.service.MailService;
import com.biblio.service.UserService;
import com.biblio.service.dto.UserDTO;
import com.biblio.web.rest.vm.KeyAndPasswordVM;
import com.biblio.web.rest.vm.ManagedUserVM;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
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
            bindingResult.getFieldErrors().stream().forEach((f) -> {
                modele.put(f.getField(), f.getDefaultMessage());
            });

            return modele;
        }
        if (userRepository.findOneByLogin(managedUserVM.getLogin()).isPresent()) {
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

    /**
     * POST /account : update the current user information.
     *
     * @param userDTO the current user information
     * @param bindingResult
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
     * Request) or 500 (Internal Server Error) if the user couldn't be updated
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object saveAccount(@Valid @RequestBody UserDTO userDTO,BindingResult bindingResult) {
        Map<String, Object> modele = new HashMap<>();
        Optional<User> existingUser = userRepository.findOneByEmail(userDTO.getEmail());
        if (bindingResult.hasErrors()) {

            modele.put(Constants.ERROR, true);
            modele.put(Constants.MESSAGE, "Enregistrement échoué");
            bindingResult.getFieldErrors().stream().forEach((f) -> {
                modele.put(f.getField(), f.getDefaultMessage());
            });

            return modele;
        }
        
        
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userDTO.getLogin()))) {
            modele.put(Constants.ERROR, "true");
            modele.put(Constants.MESSAGE, "Email existe");
            modele.put(Constants.RESULTAT, "Opération echouée");

            return modele;
        }
        return userRepository
                .findOneByLogin(SecurityUtils.getCurrentUserLogin())
                .map(u -> {
                    userService.updateUser(userDTO.getNom(), userDTO.getPrenom(), userDTO.getDateNaissance(), userDTO.getEmail(),
                            userDTO.getTel());
                    modele.put(Constants.RESULTAT, "Opération réussi");
                    return modele;
                });

    }

    /**
     * POST /account : update  user information by his id.
     *
     * @param userDTO the user information
     * @param id id of account to update
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
     * Request) or 500 (Internal Server Error) if the user couldn't be updated
     */
    @RequestMapping(value = "/account/{id}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Object saveAccount(@Valid @RequestBody UserDTO userDTO, @PathVariable("id") Long id,BindingResult bindingResult) {
        Map<String, Object> modele = new HashMap<>();
        if (bindingResult.hasErrors()) {

            modele.put(Constants.ERROR, true);
            modele.put(Constants.MESSAGE, "Enregistrement échoué");
            bindingResult.getFieldErrors().stream().forEach((f) -> {
                modele.put(f.getField(), f.getDefaultMessage());
            });

            return modele;
        }
        
        Optional<User> existingUser = userRepository.findOneByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userDTO.getLogin()))) {
            modele.put(Constants.ERROR, "true");
            modele.put(Constants.MESSAGE, "Email existe");
            modele.put(Constants.RESULTAT, "Opération echouée");

            return modele;
        }
        Optional<User> user = userRepository.findOneById(id);
        if (user.isPresent()) {
            userService.updateUser(id, userDTO.getLogin(), userDTO.getNom(), userDTO.getPrenom(), userDTO.getDateNaissance(), userDTO.getEmail(), userDTO.isActivated(),
                    userDTO.getTel(), userDTO.getRoles());

            modele.put(Constants.RESULTAT, "Opération réussi");
            return modele;

        }
        modele.put(Constants.ERROR, "true");
        modele.put(Constants.MESSAGE, "Cet utilisateur n'existe pas");
        modele.put(Constants.RESULTAT, "Opération echouée");
        return modele;

    }

    /**
     * POST /account/change_password : changes the current user's password
     *
     * @param password the new password
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
     * Request) if the new password is not strong enough
     */
    @RequestMapping(value = "/account/change_password",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_PLAIN_VALUE)

    public ResponseEntity<?> changePassword(@RequestBody String password) {
        if (!checkPasswordLength(password)) {
            return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
        }
        userService.changePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean checkPasswordLength(String password) {
        return (!StringUtils.isEmpty(password)
                && password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH
                && password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH);
    }

    /**
     * POST /account/reset_password/init : Send an e-mail to reset the password
     * of the user
     *
     * @param mail the mail of the user
     * @param request the HTTP request
     * @return the ResponseEntity with status 200 (OK) if the e-mail was sent,
     * or status 400 (Bad Request) if the e-mail address is not registered
     */
    @RequestMapping(value = "/account/reset_password/init",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_PLAIN_VALUE)

    public ResponseEntity<?> requestPasswordReset(@RequestBody String mail, HttpServletRequest request) {
        return userService.requestPasswordReset(mail)
                .map(user -> {
                    String baseUrl = request.getScheme()
                            + "://"
                            + request.getServerName()
                            + ":"
                            + request.getServerPort()
                            + request.getContextPath();
                    mailService.sendPasswordResetMail(user, baseUrl);
                    return new ResponseEntity<>("e-mail was sent", HttpStatus.OK);
                }).orElse(new ResponseEntity<>("e-mail address not registered", HttpStatus.BAD_REQUEST));
    }

    /**
     * POST /account/reset_password/finish : Finish to reset the password of the
     * user
     *
     * @param keyAndPassword the generated key and the new password
     * @return the ResponseEntity with status 200 (OK) if the password has been
     * reset, or status 400 (Bad Request) or 500 (Internal Server Error) if the
     * password could not be reset
     */
    @RequestMapping(value = "/account/reset_password/finish",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_PLAIN_VALUE)

    public ResponseEntity<String> finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
        }
        return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
                .map(user -> new ResponseEntity<String>(HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
