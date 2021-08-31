package org.caltech.miniswing.gateway.web;

import lombok.RequiredArgsConstructor;
import org.skcc.team1.legacy.customerclient.CustomerClient;
import org.skcc.team1.legacy.customerclient.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class IndexController {
    @Autowired
    private CustomerClient customerClient;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("custs", customerClient.getCustomers("", LocalDate.now()));
        return "index";
    }

    @GetMapping("/customer/save")
    public String customerSave() {
        return "customer-save";
    }
}
