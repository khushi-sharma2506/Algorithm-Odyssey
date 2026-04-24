package ui.components;

import utils.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Smooth animated progress bar that tweens to a target value.
 */
public class AnimatedProgressBar extends JComponent {

    private float current = 0f;   // 0.0 – 1.0 (rendered value)
    private float target  = 0f;   // 0.0 – 1.0 (goal value)
    private Color trackColor = ThemeManager.BG_SURFACE;
    private Color fillColor1 = ThemeManager.ACCENT;
    private Color fillColor2 = ThemeManager.ACCENT_CYAN;
    private int   arc = 8;
    private String label = null;
    private Timer timer;

    public AnimatedProgressBar() {
        setPreferredSize(new Dimension(200, 18));
        timer = new Timer(16, e -> {
            float diff = target - current;
            if (Math.abs(diff) < 0.001f) {
                current = target;
                timer.stop();
            } else {
                current += diff * 0.08f;
            }
            repaint();
        });
    }

    /** Set progress (0.0–1.0) with animation. */
    public void setProgress(float value) {
        target = Math.max(0f, Math.min(1f, value));
        if (!timer.isRunning()) timer.start();
    }

    /** Immediately jump to value (no animation). */
    public void setProgressImmediate(float value) {
        timer.stop();
        current = target = Math.max(0f, Math.min(1f, value));
        repaint();
    }

    public void setLabel(String label) { this.label = label; repaint(); }
    public void setFillColors(Color c1, Color c2) { fillColor1=c1; fillColor2=c2; repaint(); }
    public void setTrackColor(Color c) { trackColor = c; repaint(); }
    public void setArc(int arc)        { this.arc = arc; repaint(); }
    public float getProgress()         { return target; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Track
        g2.setColor(trackColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

        // Fill
        int fillW = (int)(w * current);
        if (fillW > 0) {
            Shape clip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
            g2.setPaint(new GradientPaint(0, 0, fillColor1, fillW, 0, fillColor2));
            g2.fillRect(0, 0, fillW, h);
            g2.setClip(clip);
        }

        // Label
        if (label != null) {
            g2.setFont(ThemeManager.FONT_SMALL);
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(label)) / 2;
            int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(label, tx, ty);
        }

        g2.dispose();
    }
}
