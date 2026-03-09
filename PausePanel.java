package Tank;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class PausePanel extends JLabel {

    protected ImageIcon pauseIcon;

    PausePanel() {
        setPreferredSize(new Dimension(832, 832));
        setLayout(new BorderLayout());
        setOpaque(false);

        try {
            BufferedImage buffer = ImageIO.read(getClass().getResource("/Resources/Pause.png"));

            Image scaled = buffer.getScaledInstance(195, 35, Image.SCALE_SMOOTH);
            pauseIcon = new ImageIcon(scaled);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Pause null");
        }

        JLabel label = new JLabel(pauseIcon);
        label.setPreferredSize(new Dimension(195, 35));
        add(label, BorderLayout.CENTER);

    }

}
