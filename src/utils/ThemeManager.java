package utils;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {

    // ── Background ──────────────────────────────────────────────────────────
    public static final Color BG_PRIMARY   = new Color(0x0D0D1A);
    public static final Color BG_SECONDARY = new Color(0x13132A);
    public static final Color BG_SURFACE   = new Color(0x1A1A3E);
    public static final Color BG_CARD      = new Color(0x1E1E45);

    // ── Accents ──────────────────────────────────────────────────────────────
    public static final Color ACCENT        = new Color(0x6C63FF);
    public static final Color ACCENT_HOVER  = new Color(0x8B84FF);
    public static final Color ACCENT_PINK   = new Color(0xFF6584);
    public static final Color ACCENT_GREEN  = new Color(0x43E97B);
    public static final Color ACCENT_YELLOW = new Color(0xFFD166);
    public static final Color ACCENT_CYAN   = new Color(0x38F9D7);
    public static final Color ACCENT_ORANGE = new Color(0xFF9A3C);

    // ── Text ─────────────────────────────────────────────────────────────────
    public static final Color TEXT_PRIMARY   = new Color(0xEEEEFF);
    public static final Color TEXT_SECONDARY = new Color(0x9999BB);
    public static final Color TEXT_MUTED     = new Color(0x55557A);

    // ── Status ───────────────────────────────────────────────────────────────
    public static final Color SUCCESS = new Color(0x2ED573);
    public static final Color WARNING = new Color(0xFFB347);
    public static final Color ERROR   = new Color(0xFF4757);

    // ── Borders ───────────────────────────────────────────────────────────────
    public static final Color BORDER = new Color(0x2A2A5A);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font FONT_LARGE  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_MEDIUM = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 13);

    /** Apply full dark theme to Swing UIManager (no external library needed). */
    public static void applyDarkTheme() {
        // Try FlatLaf if on classpath
        try {
            Class<?> cls = Class.forName("com.formdev.flatlaf.FlatDarkLaf");
            UIManager.setLookAndFeel((LookAndFeel) cls.getDeclaredConstructor().newInstance());
            // FlatLaf extra tweaks
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            return;
        } catch (Exception ignored) {}

        // Manual fallback
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        UIManager.put("Panel.background",              BG_PRIMARY);
        UIManager.put("Frame.background",              BG_PRIMARY);
        UIManager.put("Label.foreground",              TEXT_PRIMARY);
        UIManager.put("Button.background",             ACCENT);
        UIManager.put("Button.foreground",             Color.WHITE);
        UIManager.put("Button.focus",                  new Color(0,0,0,0));
        UIManager.put("TextField.background",          BG_SURFACE);
        UIManager.put("TextField.foreground",          TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",     ACCENT);
        UIManager.put("TextField.border",              BorderFactory.createLineBorder(BORDER, 1));
        UIManager.put("PasswordField.background",      BG_SURFACE);
        UIManager.put("PasswordField.foreground",      TEXT_PRIMARY);
        UIManager.put("PasswordField.caretForeground", ACCENT);
        UIManager.put("PasswordField.border",          BorderFactory.createLineBorder(BORDER, 1));
        UIManager.put("Table.background",              BG_SECONDARY);
        UIManager.put("Table.foreground",              TEXT_PRIMARY);
        UIManager.put("Table.gridColor",               BORDER);
        UIManager.put("Table.selectionBackground",     ACCENT);
        UIManager.put("Table.selectionForeground",     Color.WHITE);
        UIManager.put("TableHeader.background",        BG_SURFACE);
        UIManager.put("TableHeader.foreground",        TEXT_SECONDARY);
        UIManager.put("TableHeader.cellBorder",        BorderFactory.createLineBorder(BORDER));
        UIManager.put("ScrollPane.background",         BG_PRIMARY);
        UIManager.put("Viewport.background",           BG_PRIMARY);
        UIManager.put("ScrollBar.background",          BG_SECONDARY);
        UIManager.put("ScrollBar.thumb",               BG_SURFACE);
        UIManager.put("ScrollBar.thumbHighlight",      ACCENT);
        UIManager.put("ScrollBar.width",               8);
        UIManager.put("ComboBox.background",           BG_SURFACE);
        UIManager.put("ComboBox.foreground",           TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground",  ACCENT);
        UIManager.put("ComboBox.selectionForeground",  Color.WHITE);
        UIManager.put("Slider.background",             BG_PRIMARY);
        UIManager.put("Slider.foreground",             ACCENT);
        UIManager.put("Slider.thumbColor",             ACCENT);
        UIManager.put("TabbedPane.background",         BG_PRIMARY);
        UIManager.put("TabbedPane.foreground",         TEXT_PRIMARY);
        UIManager.put("TabbedPane.selected",           BG_SURFACE);
        UIManager.put("TabbedPane.contentAreaColor",   BG_SURFACE);
        UIManager.put("TabbedPane.tabAreaBackground",  BG_SECONDARY);
        UIManager.put("CheckBox.background",           BG_PRIMARY);
        UIManager.put("CheckBox.foreground",           TEXT_PRIMARY);
        UIManager.put("CheckBox.focus",                new Color(0,0,0,0));
        UIManager.put("ToolTip.background",            BG_CARD);
        UIManager.put("ToolTip.foreground",            TEXT_PRIMARY);
        UIManager.put("OptionPane.background",         BG_SURFACE);
        UIManager.put("OptionPane.messageForeground",  TEXT_PRIMARY);
        UIManager.put("List.background",               BG_SECONDARY);
        UIManager.put("List.foreground",               TEXT_PRIMARY);
        UIManager.put("List.selectionBackground",      ACCENT);
        UIManager.put("TextArea.background",           BG_SURFACE);
        UIManager.put("TextArea.foreground",           TEXT_PRIMARY);
        UIManager.put("TextArea.caretForeground",      ACCENT);
        UIManager.put("EditorPane.background",         BG_SURFACE);
        UIManager.put("EditorPane.foreground",         TEXT_PRIMARY);
        UIManager.put("PopupMenu.background",          BG_SECONDARY);
        UIManager.put("MenuItem.background",           BG_SECONDARY);
        UIManager.put("MenuItem.foreground",           TEXT_PRIMARY);
        UIManager.put("MenuItem.selectionBackground",  ACCENT);
        UIManager.put("SplitPane.background",          BG_PRIMARY);
        UIManager.put("SplitPane.dividerSize",         4);
        UIManager.put("ProgressBar.background",        BG_SURFACE);
        UIManager.put("ProgressBar.foreground",        ACCENT);
        UIManager.put("ProgressBar.selectionBackground", TEXT_PRIMARY);
        UIManager.put("ProgressBar.selectionForeground", TEXT_PRIMARY);
    }

    /** Linear interpolation between two colours. */
    public static Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        return new Color(
            (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t),
            (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
            (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t)
        );
    }

    /** Paint a horizontal gradient rectangle. */
    public static void paintGradient(Graphics2D g2, Color c1, Color c2,
                                     int x, int y, int w, int h) {
        g2.setPaint(new GradientPaint(x, y, c1, x + w, y, c2));
        g2.fillRect(x, y, w, h);
    }
}
