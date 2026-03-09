package Tank;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class StageFinalPanel extends JPanel {
    protected long finalDuration;
    protected ImageIcon basicIcon;
    protected ImageIcon fastIcon;
    protected ImageIcon powerIcon;
    protected ImageIcon armorIcon;

    protected Map<String, Integer> scoreMap;

    StageFinalPanel(Map<String, Integer> scoreMap) {
        this.scoreMap = scoreMap;
        setPreferredSize(new Dimension(832, 832));

        setBackground(Color.BLACK);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        loadPNGs();

        JLabel title = new JLabel("Stage Completed");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.white);
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(30));
        add(title);
        add(Box.createVerticalStrut(30));

        for (Map.Entry<String, Integer> score : scoreMap.entrySet()) {
            add(setScores(score.getKey(), score.getValue()));
            add(Box.createVerticalStrut(20));
        }

    }

    public void loadPNGs() {
        try {
            basicIcon = new ImageIcon(ImageIO.read(getClass().getResource("/Resources/Basic_Tank_Sol.png")));
            fastIcon = new ImageIcon(ImageIO.read(getClass().getResource("/Resources/Fast_Tank_Sol.png")));
            powerIcon = new ImageIcon(ImageIO.read(getClass().getResource("/Resources/Power_Tank_Sol.png")));
            armorIcon = new ImageIcon(ImageIO.read(getClass().getResource("/Resources/Armor_Tank_Sol.png")));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Stage Icons null");
        }
    }

    public JPanel setScores(String image, int score) {
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setOpaque(false);
        scorePanel.setMaximumSize(new Dimension(300, 50));

        JLabel scoreLabel = new JLabel(String.valueOf(score));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setForeground(Color.white);
        scoreLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel icon = new JLabel(getTank(image));
        icon.setHorizontalAlignment(SwingConstants.RIGHT);

        scorePanel.add(scoreLabel, BorderLayout.WEST);
        scorePanel.add(icon, BorderLayout.EAST);

        return scorePanel;
    }

    public ImageIcon getTank(String tank) {
        switch (tank) {
            case "basictank":
                return scale(basicIcon);
            case "fasttank":
                return scale(fastIcon);
            case "powertank":
                return scale(powerIcon);
            case "armortank":
                return scale(armorIcon);
            default:
                return null;
        }
    }

    public ImageIcon scale(ImageIcon icon) {
        if (icon == null) {
            return null;
        }
        Image image = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

}
