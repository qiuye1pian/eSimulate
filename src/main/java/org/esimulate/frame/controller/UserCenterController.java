package org.esimulate.frame.controller;

import lombok.extern.log4j.Log4j2;
import org.esimulate.frame.model.User;
import org.esimulate.frame.pojo.LoginDto;
import org.esimulate.frame.service.UserService;
import org.esimulate.util.JwtUtil;
import org.esimulate.util.RSAUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/usercenter")
public class UserCenterController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil JwtUtil;

    @Autowired
    private RSAUtils rsaUtils;

    @PostMapping("/login")
    public LoginDto login(@RequestBody User user) {
        String decryptedPassword = rsaUtils.decrypt(user.getPassword());
        user.setPassword(decryptedPassword);

        User loggedInUser = userService.login(user.getUsername(), user.getPassword());

        if (loggedInUser != null) {
            // ✅ 成功，返回 200 OK
            return LoginDto.success(loggedInUser, JwtUtil.generateToken(loggedInUser.getUsername()));
        }

        return LoginDto.fail();
    }

    @PostMapping("/getPublicKey")
    public ResponseEntity<String> getPublicKey() {
        String publicKey = rsaUtils.getPublicKeyPEM();
        return ResponseEntity.ok(publicKey);
    }

}
