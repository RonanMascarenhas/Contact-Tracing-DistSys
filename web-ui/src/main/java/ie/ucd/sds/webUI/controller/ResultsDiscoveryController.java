package ie.ucd.sds.webUI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/results")
public class ResultsDiscoveryController {
    @GetMapping("/")
    public String getResultIndex() {
        return "results/index";
    }
}
