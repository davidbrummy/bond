package org.davidbrummy.bond.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class HelloWorldController {
    private static final Logger LOGGER = Logger.getLogger(HelloWorldController.class);
    
    /**
     * Request admin home page.
     *
     * @return The admin home page.
     */
    @RequestMapping("/home")
    public String homePage() {
        return "home";
    }
    
    @RequestMapping("/mobilehome")
    public String mobileHomePage() {
        return "mobileHome";
    }
}
