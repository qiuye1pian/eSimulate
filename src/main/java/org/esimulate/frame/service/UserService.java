package org.esimulate.frame.service;


import org.esimulate.frame.model.User;
import org.esimulate.frame.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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

    public User login(User user) {
        // 1️⃣ 通过 username 查询数据库中的用户
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        if (foundUser.isPresent()) {
            User existingUser = foundUser.get();
            // 2️⃣ 校验密码（MD5 转换 + 比对）
            // 前端传来的 MD5 加密密码
            String inputPassword = user.getPassword();
            // 数据库中的加密密码
            String storedPassword = existingUser.getPassword();

            if (passwordEncoder.matches(inputPassword, storedPassword)) {
                return existingUser; // ✅ 登录成功，返回用户信息
            }
        }
        // ❌ 登录失败
        return null;
    }
}