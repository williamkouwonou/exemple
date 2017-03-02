/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oaut2.simple.spring.boot.app;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author kouwonou
 */
@SpringBootApplication
@RestController
public class MainApplication {
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        SpringApplication.run(MainApplication.class, args);
    }
    
    @RequestMapping("/hello")
    public Object hello(){
        Map<String, Object> modele = new HashMap<>();
        
        modele.put("mot", "Bonjour");
        modele.put("nom", "AMEGAYIBOR");
        modele.put("prenom", "Essi Linda");
        modele.put("age", "23 ans");
        return modele;
        
        
    }
}
