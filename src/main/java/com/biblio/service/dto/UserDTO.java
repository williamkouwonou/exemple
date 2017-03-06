package com.biblio.service.dto;

import com.biblio.config.util.Constants;
import com.biblio.web.rest.util.Utils;
import java.text.ParseException;
import java.util.Date;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.*;
import java.util.Set;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(min = 3, max = 50)
    @NotNull()
    private String nom;

    @Size(min = 3, max = 50)
    @NotNull
    private String prenom;
    
    @NotNull
    private Date dateNaissance;

    @NotNull
    @Size(min = 7, max = 20)
    private String tel;

    @Email
    @Size(min = 5, max = 100)
    @NotNull
    private String email;

    private boolean activated = false;

    private Set<String> roles;

    public UserDTO() {
    }

    public UserDTO(String login, String nom, String prenom, Date dateNaissance, String tel, String email, Set<String> roles) {
        this.login = login;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.tel = tel;
        this.email = email;
        this.roles = roles;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    public void setDateNaissance(String dateNaissance) throws ParseException {
        this.dateNaissance = Utils.convertToDate(dateNaissance);
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

}
