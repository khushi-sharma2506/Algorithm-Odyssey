package ui.components;

import utils.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A JPanel with rounded corners, optional gradient background, and optional border.
 */
public class RoundedPanel extends JPanel {

    private int    arc          = 16;
    private Color  bgColor;
    private Color  bgColor2;        // null = solid, non-null = gradient
    private Color  borderColor  = null;
    private float  borderWidth  = 1.5f;
    private boolean gradientHoriz = false;

    public RoundedPanel() {
        this(ThemeManager.BG_CARD, 16);
    }

    public RoundedPanel(Color bg, int arc) {
        this.bgColor = bg;
        this.arc     = arc;
        setOpaque(false);
    }

    // ── Fluent setters ────────────────────────────────────────────────────────

    public RoundedPanel withGradient(Color c1, Color c2, boolean horizontal) {
        this.bgColor       = c1;
        this.bgColor2      = c2;
        this.gradientHoriz = horizontal;
        return this;
    }

    public RoundedPanel withBorder(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public RoundedPanel withBorder(Color borderColor, float width) {
        this.borderColor = borderColor;
        this.borderWidth = width;
        return this;
    }

    public void setArc(int arc)          { this.arc = arc; repaint(); }
    public void setBgColor(Color c)      { this.bgColor = c; repaint(); }
    public void setBorderColor(Color c)  { this.borderColor = c; repaint(); }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);

        if (bgColor2 != null) {
            if (gradientHoriz)
                g2.setPaint(new GradientPaint(0, 0, bgColor, w, 0, bgColor2));
            else
                g2.setPaint(new GradientPaint(0, 0, bgColor, 0, h, bgColor2));
        } else {
            g2.setColor(bgColor);
        }
        g2.fill(shape);

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.draw(shape);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) { /* handled above */ }
}
