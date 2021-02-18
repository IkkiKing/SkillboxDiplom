package com.ikkiking.controller;


import com.ikkiking.api.response.InitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class DefaultController {

    private final InitResponse initResponse;

    @Autowired
    public DefaultController(InitResponse initResponse) {
        this.initResponse = initResponse;
    }

    @GetMapping(value = "/**/{path:[^\\.]*}")
    public String redirectToIndex() {
        return "forward:/"; //делаем перенаправление
    }

    /*@RequestMapping("/")
    public String index(Model model) {
        return "index";
    }*/
}
