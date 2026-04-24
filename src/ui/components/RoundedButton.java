package ui.components;

import utils.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A beautiful rounded gradient button with hover + press animations.
 */
public class RoundedButton extends JButton {

    public enum Style { PRIMARY, SECONDARY, DANGER, GHOST }

    private Style style;
    private float hoverAlpha = 0f;
    private Timer hoverTimer;
    private boolean hovered = false;
    private int arc = 12;

    public RoundedButton(String text) {
        this(text, Style.PRIMARY);
    }

    public RoundedButton(String text, Style style) {
        super(text);
        this.style = style;
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFont(ThemeManager.FONT_MEDIUM);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setForeground(Color.WHITE);

        hoverTimer = new Timer(16, e -> {
            if (hovered) {
                hoverAlpha = Math.min(1f, hoverAlpha + 0.12f);
            } else {
                hoverAlpha = Math.max(0f, hoverAlpha - 0.12f);
            }
            repaint();
            if ((!hovered && hoverAlpha <= 0f) || (hovered && hoverAlpha >= 1f)) {
                hoverTimer.stop();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                hovered = true;
                hoverTimer.start();
            }
            @Override public void mouseExited(MouseEvent e) {
                hovered = false;
                hoverTimer.start();
            }
        });
    }

    public void setArc(int arc) { this.arc = arc; }
    public void setStyle(Style style) { this.style = style; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);

        Color base, hover;
        switch (style) {
            case SECONDARY -> { base = ThemeManager.BG_SURFACE; hover = ThemeManager.BG_CARD; }
            case DANGER    -> { base = ThemeManager.ERROR; hover = ThemeManager.ERROR.brighter(); }
            case GHOST     -> { base = new Color(0,0,0,0); hover = new Color(108,99,255,40); }
            default        -> { base = ThemeManager.ACCENT; hover = ThemeManager.ACCENT_HOVER; }
        }

        // Fill
        if (style == Style.PRIMARY) {
            Color c1 = ThemeManager.lerp(ThemeManager.ACCENT, ThemeManager.ACCENT_HOVER, hoverAlpha);
            Color c2 = ThemeManager.lerp(new Color(0x9C63FF), new Color(0xBF84FF), hoverAlpha);
            g2.setPaint(new GradientPaint(0, 0, c1, w, h, c2));
        } else {
            g2.setColor(ThemeManager.lerp(base, hover, hoverAlpha));
        }
        g2.fill(shape);

        // Border for ghost/secondary
        if (style == Style.SECONDARY || style == Style.GHOST) {
            g2.setColor(ThemeManager.lerp(ThemeManager.BORDER, ThemeManager.ACCENT, hoverAlpha));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(shape);
        }

        // Glow overlay when hovered (primary)
        if (style == Style.PRIMARY && hoverAlpha > 0) {
            g2.setColor(new Color(255, 255, 255, (int)(20 * hoverAlpha)));
            g2.fill(shape);
        }

        // Press effect
        ButtonModel m = getModel();
        if (m.isPressed()) {
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fill(shape);
        }

        g2.dispose();

        // Let super paint text/icon
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.width  = Math.max(d.width  + 24, 100);
        d.height = Math.max(d.height + 10, 38);
        return d;
    }
}
