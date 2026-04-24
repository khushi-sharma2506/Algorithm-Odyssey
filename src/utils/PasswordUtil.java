package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility for SHA-256 password hashing with a random salt.
 * Format stored: base64(salt) + ":" + base64(sha256(salt + password))
 */
public class PasswordUtil {

    private static final int SALT_LENGTH = 16;

    /** Hash a plain-text password. Returns storable hash string. */
    public static String hash(String password) {
        try {
            SecureRandom rng  = new SecureRandom();
            byte[] salt       = new byte[SALT_LENGTH];
            rng.nextBytes(salt);
            byte[] digest     = sha256(concat(salt, password.getBytes("UTF-8")));
            return Base64.getEncoder().encodeToString(salt)
                   + ":" +
                   Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    /** Verify plain text password against a stored hash string. */
    public static boolean verify(String password, String stored) {
        try {
            String[] parts = stored.split(":", 2);
            if (parts.length != 2) return false;
            byte[] salt   = Base64.getDecoder().decode(parts[0]);
            byte[] stored256 = Base64.getDecoder().decode(parts[1]);
            byte[] attempt   = sha256(concat(salt, password.getBytes("UTF-8")));
            return MessageDigest.isEqual(stored256, attempt);
        } catch (Exception e) {
            return false;
        }
    }

    /** Generate a random remember-me token (URL-safe base64). */
    public static String generateToken() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private static byte[] sha256(byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(data);
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] out = new byte[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }
}
