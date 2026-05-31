package ntu.phamgiakhiem.PhoneStore.controllers;

import org.springframework.web.bind.annotation.GetMapping;

public class AdminController {
	@GetMapping("/admin/dashboard")
    public String login() {
        return "/admin/dashboard";
    }

 
}
