/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblio.repository;

import com.biblio.entity.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author kouwonou
 */
public interface RoleRepository extends JpaRepository<Role, String>{
    @Query("select name From Role")
    List<String> findAuthorities();

    Role findByName(String name);
}
