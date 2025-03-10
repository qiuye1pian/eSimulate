package org.esimulate.frame.service;


import lombok.extern.log4j.Log4j2;
import org.esimulate.frame.model.User;
import org.esimulate.frame.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 加密密码
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User login(String username, String password) {
        // 1️⃣ 通过 username 查询数据库中的用户
        Optional<User> foundUser = userRepository.findByUsername(username);

        log.info("查找用户: {}, 结果为:{}", username, foundUser.isPresent());

        if (foundUser.isPresent()) {
            User existingUser = foundUser.get();
            // 2️⃣ 校验密码（MD5 转换 + 比对）

            // 数据库中的加密密码
            String storedPassword = existingUser.getPassword();

            // 校验密码
            if (passwordEncoder.matches(password, storedPassword)) {
                return existingUser; // ✅ 登陆成功，返回用户信息
            }
        }
        // ❌ 登陆失败
        return null;
    }
}