package com.biblio.web.rest.vm;


import com.biblio.service.dto.UserDTO;
import java.util.Date;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * View Model extending the UserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserVM extends UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 100;

    private Long id;


    @NotNull
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserVM() {
    }

   

    public ManagedUserVM(String login, String nom, String prenom, Date dateNaissance, String tel, String email, Set<String> roles) {
        super(login, nom, prenom, dateNaissance, tel, email, roles);
    }

    public ManagedUserVM(Long id, String password, String login, String nom, String prenom, Date dateNaissance, String tel, String email, Set<String> roles) {
        super(login, nom, prenom, dateNaissance, tel, email, roles);
        this.id = id;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
   
    
}
