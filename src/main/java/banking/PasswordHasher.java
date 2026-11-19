package banking;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    public static String hash(String password) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt, 100_000, 256
            );

            byte[] key = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .getEncoded();

            return Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(key);
        } catch (Exception e) {
            throw new RuntimeException("Could not hash password.", e);
        }
    }

    public static boolean matches(String password, String savedHash) {
        try {
            String[] parts = savedHash.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);

            PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt, 100_000, 256
            );

            byte[] enteredHash = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .getEncoded();

            byte[] saved = Base64.getDecoder().decode(parts[1]);
            return MessageDigest.isEqual(enteredHash, saved);
        } catch (Exception e) {
            return false;
        }
    }
}