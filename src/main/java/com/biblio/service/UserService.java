package com.biblio.service;


import com.biblio.entity.Role;
import com.biblio.entity.User;
import com.biblio.repository.RoleRepository;
import com.biblio.repository.UserRepository;
import com.biblio.security.SecurityUtils;
import com.biblio.security.util.ConstantRole;
import com.biblio.service.util.RandomUtil;
import com.biblio.web.rest.vm.ManagedUserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import javax.inject.Inject;
import java.util.*;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;

   

    @Inject
    private RoleRepository roleRepository;

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
                .map(user -> {
                    // activate given user for the registration key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    userRepository.save(user);
                    log.debug("Activated user: {}", user);
                    return user;
                });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);

        return userRepository.findOneByResetKey(key)
                .filter(user -> {
                    ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
                    return user.getResetDate().isAfter(oneDayAgo);
                })
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    userRepository.save(user);
                    return user;
                });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmail(mail)
                .filter(User::getActivated)
                .map(user -> {
                    user.setResetKey(RandomUtil.generateResetKey());
                    user.setResetDate(ZonedDateTime.now());
                    userRepository.save(user);
                    return user;
                });
    }

    public User createUser(String login, String password, String nom, String prenom, String email,
            String tel) {

        User newUser = new User();
        Role authority = roleRepository.findOne(ConstantRole.USER_ROLE);
        Set<Role> roles = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setNom(nom);
        newUser.setPrenom(prenom);
        newUser.setEmail(email);
        
        newUser.setTel(tel);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        roles.add(authority);
        newUser.setRoles(roles);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User createUser(ManagedUserVM managedUserVM) {

        User user = new User();
        user.setLogin(managedUserVM.getLogin());
        user.setNom(managedUserVM.getNom());
        user.setPrenom(managedUserVM.getPrenom());
        user.setEmail(managedUserVM.getEmail());
        user.setTel(managedUserVM.getTel());
        user.setDateNaissance(managedUserVM.getDateNaissance());
        if (managedUserVM.getRoles()!= null) {
            Set<Role> roles = new HashSet<>();
            managedUserVM.getRoles().stream().forEach(
                    role -> roles.add(roleRepository.findOne(role))
            );
            user.setRoles(roles);
        }

        user.setCreatedBy(SecurityUtils.getCurrentUserLogin());

//        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        String encryptedPassword = passwordEncoder.encode(managedUserVM.getPassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(ZonedDateTime.now());
        user.setActivated(true);

       
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public void updateUser(String nom, String prenom,Date dateNaissance, String email,  String tel) {
       
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
            u.setNom(nom);
            u.setPrenom(prenom);
            u.setEmail(email);
            u.setDateNaissance(dateNaissance);
            u.setTel(tel);
            userRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

    public void updateUser(Long id, String login, String nom, String prenom,Date dateNaissance, String email,
            boolean activated,  String tel, Set<String> roles) {
       
        userRepository
                .findOneById(id)
                .ifPresent(u -> {
                    u.setLogin(login);
                    u.setNom(nom);
                    u.setPrenom(prenom);
                    u.setEmail(email);
                    u.setActivated(activated);
                    u.setDateNaissance(dateNaissance);
                    u.setTel(tel);
 
                   
                    Set<Role> managedRoles = u.getRoles();
                    managedRoles.clear();
                    roles.stream().forEach(
                            role-> managedRoles.add(roleRepository.findOne(role))
                    );
                    log.debug("Changed Information for User: {}", u);
                });

    }

    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(u -> {
            userRepository.delete(u);
            log.debug("Deleted User: {}", u);
        });
    }

    public void changePassword(String password) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            userRepository.save(u);
            log.debug("Changed password for User: {}", u);
        });
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithRolesByLogin(String login) {
        return userRepository.findOneByLogin(login).map(u -> {
            u.getRoles().size();
            return u;
        });
    }

    @Transactional(readOnly = true)
    public User getUserWithRols(Long id) {
        User user = userRepository.findOne(id);
        user.getRoles().size(); // eagerly load the association
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserRoles() {
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        user.getRoles().size(); // eagerly load the association
        return user;
    }

    
    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        ZonedDateTime now = ZonedDateTime.now();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }
}
