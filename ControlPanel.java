package Tank;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {

    protected ImageIcon controls;
    protected ImageIcon returnIcon;
    protected JPanel buttonPanel;

    ControlPanel() {
        setSize(new Dimension(900, 600));
        setBackground(Color.black);

        JPanel temJPanel = new JPanel(new BorderLayout());
        temJPanel.setPreferredSize(new Dimension(900, 600));

        try {
            BufferedImage buffer = ImageIO.read(getClass().getResource("/Resources/Controls.png"));
            controls = new ImageIcon(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Controls (in Panel) null");
        }

        JLabel label = new JLabel(controls);
        temJPanel.add(label, BorderLayout.CENTER);

        buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setPreferredSize(new Dimension(300, 200));

        try {
            BufferedImage buffer2 = ImageIO.read(getClass().getResource("/Resources/Return_Button.png"));
            returnIcon = new ImageIcon(buffer2);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Return button null");
        }
        JButton returnButton = new JButton(returnIcon);
        returnButton.setPreferredSize(new Dimension(300, 200));

        buttonPanel.add(returnButton);

    }

}
