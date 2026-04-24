package ui;

import managers.AuthManager;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import utils.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Registration screen with real-time validation feedback.
 */
public class RegisterPanel extends JPanel {

    public interface RegisterSuccessListener { void onRegisterSuccess(); }
    public interface SwitchToLoginListener   { void onSwitchToLogin(); }

    private RegisterSuccessListener successListener;
    private SwitchToLoginListener   loginListener;

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JLabel         errorLabel;
    private JLabel         successLabel;

    public RegisterPanel(RegisterSuccessListener sl, SwitchToLoginListener ll) {
        this.successListener = sl;
        this.loginListener   = ll;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.BG_PRIMARY);

        RoundedPanel card = new RoundedPanel(ThemeManager.BG_CARD, 20);
        card.withBorder(ThemeManager.BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 44, 40, 44));
        card.setMaximumSize(new Dimension(420, 560));

        JLabel logo = new JLabel("⚡ AlgoVerse");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(ThemeManager.ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Create your account");
        subtitle.setFont(ThemeManager.FONT_NORMAL);
        subtitle.setForeground(ThemeManager.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = styledField("At least 3 alphanumeric chars");
        passwordField = styledPass();
        confirmField  = styledPass();

        errorLabel = new JLabel(" ");
        errorLabel.setFont(ThemeManager.FONT_SMALL);
        errorLabel.setForeground(ThemeManager.ERROR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        successLabel = new JLabel(" ");
        successLabel.setFont(ThemeManager.FONT_SMALL);
        successLabel.setForeground(ThemeManager.SUCCESS);
        successLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundedButton registerBtn = new RoundedButton("  Create Account  ");
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> attemptRegister());

        JButton loginLink = new JButton("<html><u>Already have an account? Sign In</u></html>");
        loginLink.setOpaque(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setBorderPainted(false);
        loginLink.setForeground(ThemeManager.ACCENT);
        loginLink.setFont(ThemeManager.FONT_SMALL);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLink.addActionListener(e -> loginListener.onSwitchToLogin());

        card.add(logo);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        card.add(label("Username"));
        card.add(Box.createVerticalStrut(5));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(label("Password (min 6 chars)"));
        card.add(Box.createVerticalStrut(5));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(14));
        card.add(label("Confirm Password"));
        card.add(Box.createVerticalStrut(5));
        card.add(confirmField);
        card.add(Box.createVerticalStrut(10));
        card.add(errorLabel);
        card.add(successLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(loginLink);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    private void attemptRegister() {
        errorLabel.setText(" ");
        successLabel.setText(" ");
        String user    = usernameField.getText().trim();
        String pass    = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        AuthManager.RegisterResult result =
                AuthManager.getInstance().register(user, pass, confirm);

        if (result == AuthManager.RegisterResult.SUCCESS) {
            successLabel.setText("Account created! Redirecting…");
            Timer t = new Timer(1200, e -> successListener.onRegisterSuccess());
            t.setRepeats(false);
            t.start();
        } else {
            errorLabel.setText(AuthManager.messageFor(result));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JTextField styledField(String hint) {
        JTextField f = new JTextField();
        f.setToolTipText(hint);
        style(f);
        return f;
    }

    private JPasswordField styledPass() {
        JPasswordField f = new JPasswordField();
        f.setEchoChar('●');
        style(f);
        return f;
    }

    private void style(javax.swing.text.JTextComponent c) {
        c.setBackground(ThemeManager.BG_SURFACE);
        c.setForeground(ThemeManager.TEXT_PRIMARY);
        c.setCaretColor(ThemeManager.ACCENT);
        c.setFont(ThemeManager.FONT_NORMAL);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.FONT_SMALL);
        l.setForeground(ThemeManager.TEXT_SECONDARY);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(ThemeManager.BG_PRIMARY);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
