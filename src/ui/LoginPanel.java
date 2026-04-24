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
 * Login screen with animated gradient background, card layout, and shake-on-error effect.
 */
public class LoginPanel extends JPanel {

    public interface LoginSuccessListener  { void onLoginSuccess(); }
    public interface SwitchToRegisterListener { void onSwitchToRegister(); }

    private final LoginSuccessListener      successListener;
    private final SwitchToRegisterListener  registerListener;

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JCheckBox      rememberMe;
    private JLabel         errorLabel;
    private RoundedButton  loginBtn;

    private float animOffset = 0f;
    private javax.swing.Timer bgTimer;

    public LoginPanel(LoginSuccessListener sl, SwitchToRegisterListener rl) {
        this.successListener  = sl;
        this.registerListener = rl;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.BG_PRIMARY);

        bgTimer = new javax.swing.Timer(30, e -> { animOffset += 0.4f; repaint(); });
        bgTimer.start();

        RoundedPanel card = new RoundedPanel(ThemeManager.BG_CARD, 20);
        card.withBorder(ThemeManager.BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 44, 40, 44));
        card.setMaximumSize(new Dimension(420, 520));

        JLabel logo = new JLabel("⚡ AlgoVerse");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(ThemeManager.ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to continue learning");
        subtitle.setFont(ThemeManager.FONT_NORMAL);
        subtitle.setForeground(ThemeManager.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = field("Username");
        passwordField = new JPasswordField();
        passwordField.setEchoChar('●');
        styleComp(passwordField);

        rememberMe = new JCheckBox("Remember me");
        rememberMe.setOpaque(false);
        rememberMe.setForeground(ThemeManager.TEXT_SECONDARY);
        rememberMe.setFont(ThemeManager.FONT_SMALL);
        rememberMe.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(ThemeManager.FONT_SMALL);
        errorLabel.setForeground(ThemeManager.ERROR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn = new RoundedButton("  Sign In  ");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> attemptLogin());

        JButton regLink = new JButton("<html><u>Don't have an account? Register</u></html>");
        regLink.setOpaque(false); regLink.setContentAreaFilled(false);
        regLink.setBorderPainted(false);
        regLink.setForeground(ThemeManager.ACCENT);
        regLink.setFont(ThemeManager.FONT_SMALL);
        regLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        regLink.addActionListener(e -> registerListener.onSwitchToRegister());

        usernameField.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());

        card.add(logo);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(32));
        card.add(lbl("Username"));
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(16));
        card.add(lbl("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(rememberMe);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(regLink);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    private void attemptLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        errorLabel.setText(" ");
        if (user.isEmpty() || pass.isEmpty()) { showError("Please fill in all fields."); return; }
        AuthManager.LoginResult res = AuthManager.getInstance().login(user, pass, rememberMe.isSelected());
        if (res == AuthManager.LoginResult.SUCCESS) {
            passwordField.setText("");
            successListener.onLoginSuccess();
        } else {
            showError(AuthManager.messageFor(res));
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        Point orig = loginBtn.getLocation();
        int[] offsets = {-8, 8, -6, 6, -4, 4, -2, 2, 0};
        int[] idx = {0};
        javax.swing.Timer shake = new javax.swing.Timer(30, null);
        shake.addActionListener(e -> {
            if (idx[0] < offsets.length) loginBtn.setLocation(orig.x + offsets[idx[0]++], orig.y);
            else { loginBtn.setLocation(orig); shake.stop(); }
        });
        shake.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.setColor(ThemeManager.BG_PRIMARY);
        g2.fillRect(0, 0, w, h);
        float s = (float) Math.sin(Math.toRadians(animOffset));
        float c = (float) Math.cos(Math.toRadians(animOffset * 0.7f));
        drawOrb(g2, (int)(w*0.15f+s*40), (int)(h*0.2f+c*30), 280, new Color(108,99,255,30));
        drawOrb(g2, (int)(w*0.85f+c*50), (int)(h*0.8f+s*40), 220, new Color(255,101,132,25));
        drawOrb(g2, w/2, h/2, 160, new Color(56,249,215,15));
        g2.dispose();
    }

    private void drawOrb(Graphics2D g2, int cx, int cy, int r, Color col) {
        RadialGradientPaint rp = new RadialGradientPaint(cx, cy, r,
                new float[]{0f,1f}, new Color[]{col, new Color(0,0,0,0)});
        g2.setPaint(rp);
        g2.fillOval(cx-r, cy-r, r*2, r*2);
    }

    private JTextField field(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(ThemeManager.TEXT_MUTED);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, 10, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                    g2.dispose();
                }
            }
        };
        styleComp(f);
        return f;
    }

    private void styleComp(javax.swing.text.JTextComponent c) {
        c.setBackground(ThemeManager.BG_SURFACE);
        c.setForeground(ThemeManager.TEXT_PRIMARY);
        c.setCaretColor(ThemeManager.ACCENT);
        c.setFont(ThemeManager.FONT_NORMAL);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER, 1, true),
                BorderFactory.createEmptyBorder(8,12,8,12)));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.FONT_SMALL);
        l.setForeground(ThemeManager.TEXT_SECONDARY);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }
}
