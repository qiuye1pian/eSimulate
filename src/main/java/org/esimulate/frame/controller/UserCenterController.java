package org.esimulate.frame.controller;

import org.esimulate.frame.model.User;
import org.esimulate.frame.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usercenter")
public class UserCenterController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        User loggedInUser = userService.login(user);
        if (loggedInUser != null) {
            // ✅ 成功，返回 200 OK
            return ResponseEntity.ok(loggedInUser);
        }
        // ❌ 失败，返回 401 Unauthorized
        return ResponseEntity.status(401).build();
    }
}
