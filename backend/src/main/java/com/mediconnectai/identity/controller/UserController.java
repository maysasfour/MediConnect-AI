package com.mediconnectai.identity.controller;
import com.mediconnectai.identity.entity.User;
import com.mediconnectai.identity.service.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService service;
    public UserController(UserService service) { this.service = service; }
    @GetMapping public List<User> findAll() { return service.findAll(); }
}
