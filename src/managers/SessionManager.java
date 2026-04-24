package managers;

import models.User;

/**
 * Singleton that holds the currently authenticated user for the session.
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public User getCurrentUser()           { return currentUser; }
    public void setCurrentUser(User user)  { this.currentUser = user; }
    public boolean isLoggedIn()            { return currentUser != null; }

    public void logout() {
        if (currentUser != null) {
            // Clear remember-me token on explicit logout
            currentUser.setRememberToken("");
            UserManager.getInstance().updateUser(currentUser);
        }
        currentUser = null;
    }

    /**
     * Try to restore a session from the saved remember-me token.
     * Called at startup; sets currentUser if a valid token is found.
     */
    public void loadRememberedSession() {
        User u = UserManager.getInstance().findByRememberToken();
        if (u != null) {
            currentUser = u;
        }
    }
}
