package ie.ucd.sds.webUI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// todo delete this for final submission
@Deprecated
@Controller
public class HelloController {
    @GetMapping("/hello")
    public String hello(@RequestParam(name = "name", required = false) String name, Model model) {
        String nameForModel = name == null ? "" : name;
        model.addAttribute("name", nameForModel);
        return "hello";
    }
}
