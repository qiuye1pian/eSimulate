package org.esimulate.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;

@Component
public class RSAUtils {
    private static final int KEY_SIZE = 2048; // 密钥长度
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    public RSAUtils() {
        generateKeyPair();
    }

    // 生成密钥对
    public void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            this.publicKey = (RSAPublicKey) keyPair.getPublic();
            this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        } catch (Exception e) {
            throw new RuntimeException("生成密钥对失败", e);
        }
    }

    // 获取PEM格式的公钥
    public String getPublicKeyPEM() {
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" +
                publicKeyBase64 +
                "\n-----END PUBLIC KEY-----";
    }

    // 解密方法（登陆时使用）
    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }
}