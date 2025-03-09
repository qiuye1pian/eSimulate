package org.esimulate.frame.controller;

import org.esimulate.frame.model.User;
import org.esimulate.frame.pojo.LoginDto;
import org.esimulate.frame.service.UserService;
import org.esimulate.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usercenter")
public class UserCenterController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil JwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@RequestBody User user) {
        User loggedInUser = userService.login(user);
        if (loggedInUser != null) {
            // ✅ 成功，返回 200 OK
            LoginDto loginDto = new LoginDto(loggedInUser, JwtUtil.generateToken(loggedInUser.getUsername()));
            return ResponseEntity.ok(loginDto);
        }
        // ❌ 失败，返回 401 Unauthorized
        return ResponseEntity.status(401).build();
    }
}
