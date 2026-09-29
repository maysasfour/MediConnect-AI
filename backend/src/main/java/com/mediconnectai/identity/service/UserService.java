package com.mediconnectai.identity.service;
import com.mediconnectai.identity.entity.User;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public class UserService {
    private final AuthService authService;
    public UserService(AuthService authService) { this.authService = authService; }
    public List<User> findAll() { return authService.users(); }
}
