package managers;

import models.User;
import utils.PasswordUtil;

/**
 * Handles registration, login, logout and remember-me logic.
 */
public class AuthManager {

    private static AuthManager instance;

    private AuthManager() {}

    public static AuthManager getInstance() {
        if (instance == null) instance = new AuthManager();
        return instance;
    }

    // ── Registration ─────────────────────────────────────────────────────────

    public enum RegisterResult { SUCCESS, USERNAME_TAKEN, USERNAME_INVALID,
                                 PASSWORD_TOO_SHORT, PASSWORDS_DO_NOT_MATCH }

    public RegisterResult register(String username, String password, String confirm) {
        if (username == null || username.trim().length() < 3)
            return RegisterResult.USERNAME_INVALID;
        if (!username.matches("[A-Za-z0-9_]+"))
            return RegisterResult.USERNAME_INVALID;
        if (password == null || password.length() < 6)
            return RegisterResult.PASSWORD_TOO_SHORT;
        if (!password.equals(confirm))
            return RegisterResult.PASSWORDS_DO_NOT_MATCH;
        if (UserManager.getInstance().usernameExists(username.trim()))
            return RegisterResult.USERNAME_TAKEN;

        User newUser = new User(username.trim(), PasswordUtil.hash(password));
        UserManager.getInstance().createUser(newUser);
        return RegisterResult.SUCCESS;
    }

    // ── Login ────────────────────────────────────────────────────────────────

    public enum LoginResult { SUCCESS, USER_NOT_FOUND, WRONG_PASSWORD }

    public LoginResult login(String username, String password, boolean rememberMe) {
        User user = UserManager.getInstance().findByUsername(username.trim());
        if (user == null) return LoginResult.USER_NOT_FOUND;
        if (!PasswordUtil.verify(password, user.getPasswordHash()))
            return LoginResult.WRONG_PASSWORD;

        // remember-me
        if (rememberMe) {
            user.setRememberToken(PasswordUtil.generateToken());
        } else {
            user.setRememberToken("");
        }

        UserManager.getInstance().recordLogin(user);
        SessionManager.getInstance().setCurrentUser(user);
        return LoginResult.SUCCESS;
    }

    // ── Logout ───────────────────────────────────────────────────────────────

    public void logout() {
        SessionManager.getInstance().logout();
    }

    // ── Messages ─────────────────────────────────────────────────────────────

    public static String messageFor(RegisterResult r) {
        return switch (r) {
            case SUCCESS              -> "Account created! Please log in.";
            case USERNAME_TAKEN       -> "Username already taken.";
            case USERNAME_INVALID     -> "Username must be 3+ alphanumeric/underscore chars.";
            case PASSWORD_TOO_SHORT   -> "Password must be at least 6 characters.";
            case PASSWORDS_DO_NOT_MATCH -> "Passwords do not match.";
        };
    }

    public static String messageFor(LoginResult r) {
        return switch (r) {
            case SUCCESS       -> "Welcome!";
            case USER_NOT_FOUND -> "Username not found.";
            case WRONG_PASSWORD -> "Incorrect password.";
        };
    }
}
