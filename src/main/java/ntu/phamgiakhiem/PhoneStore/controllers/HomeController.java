package ntu.phamgiakhiem.PhoneStore.controllers;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {

	@GetMapping("/")
    public String login() {
        return "/user/homepage";
    }

}
