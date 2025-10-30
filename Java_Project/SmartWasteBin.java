package Java_Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

class SmartWasteBin extends JFrame implements ActionListener {
    private JProgressBar binLevel;
    private JButton addWasteBtn, emptyBinBtn;
    private JLabel statusLabel;
    private BinPanel binPanel;
    private int level = 0;

    SmartWasteBin() {
        setTitle("Smart Waste Bin Monitoring System");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Animated Bin Panel
        binPanel = new BinPanel();
        binPanel.setPreferredSize(new Dimension(300, 300));
        mainPanel.add(binPanel, BorderLayout.CENTER);
        
        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        
        binLevel = new JProgressBar(0, 100);
        binLevel.setValue(level);
        binLevel.setStringPainted(true);
        binLevel.setPreferredSize(new Dimension(200, 30));
        
        JPanel progressPanel = new JPanel();
        progressPanel.add(new JLabel("Bin Capacity:"));
        progressPanel.add(binLevel);
        
        JPanel buttonPanel = new JPanel();
        addWasteBtn = new JButton("Add Waste");
        emptyBinBtn = new JButton("Empty Bin");
        buttonPanel.add(addWasteBtn);
        buttonPanel.add(emptyBinBtn);
        
        statusLabel = new JLabel("Bin is Empty.");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        controlPanel.add(progressPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(buttonPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(statusLabel);
        
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        add(mainPanel);
        
        addWasteBtn.addActionListener(this);
        emptyBinBtn.addActionListener(this);
        
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addWasteBtn) {
            if (level < 100) {
                level += 10;
                binLevel.setValue(level);
                binPanel.setLevel(level);
                statusLabel.setText("Bin Level: " + level + "%");
                if (level >= 80)
                    JOptionPane.showMessageDialog(this, "⚠ Bin almost full! Please empty soon.");
            }
        } else if (e.getSource() == emptyBinBtn) {
            level = 0;
            binLevel.setValue(level);
            binPanel.setLevel(level);
            statusLabel.setText("Bin Emptied.");
        }
    }

    public static void main(String[] args) {
        new SmartWasteBin();
    }
}

class BinPanel extends JPanel {
    private int level = 0;
    private final int MAX_LEVEL = 100;
    private float animationProgress = 0;
    private Timer animationTimer;

    BinPanel() {
        setBackground(new Color(240, 240, 240));
        animationTimer = new Timer(20, e -> {
            if (animationProgress < 1) {
                animationProgress += 0.05f;
                repaint();
            } else {
                animationTimer.stop();
            }
        });
    }

    public void setLevel(int newLevel) {
        level = newLevel;
        animationProgress = 1;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int binWidth = 150;
        int binHeight = 200;
        int binX = (width - binWidth) / 2;
        int binY = (height - binHeight) / 2;

        // Draw bin body (outer border)
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(50, 50, 50));
        
        // Bin main body (rectangular)
        g2d.drawRect(binX, binY, binWidth, binHeight);
        
        // Bin lid
        g2d.fillRect(binX - 5, binY - 15, binWidth + 10, 15);
        g2d.drawRect(binX - 5, binY - 15, binWidth + 10, 15);

        // Draw waste inside bin
        float currentLevel = level;
        int wasteHeight = (int) ((binHeight - 10) * currentLevel / MAX_LEVEL);
        int wasteY = binY + binHeight - 10 - wasteHeight;

        // Gradient waste fill
        GradientPaint gradientPaint = new GradientPaint(
            binX, wasteY, new Color(139, 90, 43),
            binX, binY + binHeight - 10, new Color(101, 67, 33)
        );
        g2d.setPaint(gradientPaint);
        g2d.fillRect(binX + 3, wasteY, binWidth - 6, wasteHeight);

        // Draw waste level indicator line
        g2d.setColor(new Color(200, 200, 200));
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                1, new float[]{5}, 0));
        for (int i = 0; i <= 10; i++) {
            int y = binY + (binHeight * i / 10);
            g2d.drawLine(binX - 10, y, binX - 5, y);
            g2d.drawString((i * 10) + "%", binX - 35, y + 5);
        }

        // Draw percentage text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String percentText = (int) currentLevel + "%";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = binX + (binWidth - fm.stringWidth(percentText)) / 2;
        int textY = binY + binHeight + 40;
        g2d.drawString(percentText, textX, textY);

        // Draw status indicator
        if (currentLevel >= 80) {
            g2d.setColor(new Color(255, 0, 0));
            g2d.fillOval(binX + binWidth + 20, binY + 10, 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString("FULL", binX + binWidth + 25, binY + 60);
        } else if (currentLevel > 0) {
            g2d.setColor(new Color(255, 165, 0));
            g2d.fillOval(binX + binWidth + 20, binY + 10, 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString("HALF", binX + binWidth + 20, binY + 60);
        } else {
            g2d.setColor(new Color(0, 128, 0));
            g2d.fillOval(binX + binWidth + 20, binY + 10, 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString("EMPTY", binX + binWidth + 10, binY + 60);
        }
    }
}