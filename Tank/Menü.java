package Tank;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

class Menü extends JPanel {

    protected JButton singleButton;
    protected ImageIcon singleIcon;

    protected JButton multiButton;
    protected ImageIcon multiIcon;

    protected JButton controls;
    protected ImageIcon controlIcon;

    protected ImageIcon titleIcon;

    Menü(ActionListener singlePlayer, ActionListener multiPlayer, ActionListener Controls) {
        setSize(900, 600);
        setBackground(Color.black);
        setLayout(new BorderLayout());

        try {
            BufferedImage buffer1 = ImageIO.read(getClass().getResource("/Resources/Battle_City.png"));
            titleIcon = new ImageIcon(buffer1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Başlik null");
        }

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setPreferredSize(new Dimension(600, 400));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel(titleIcon);
        titlePanel.add(title, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
        buttonPanel.setPreferredSize(new Dimension(600, 400));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setOpaque(false);

        try {
            BufferedImage buffer2 = ImageIO.read(getClass().getResource("/Resources/Single_Button.png"));
            singleIcon = new ImageIcon(buffer2);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Single null");
        }
        try {
            BufferedImage buffer3 = ImageIO.read(getClass().getResource("/Resources/Multi_Button.png"));
            multiIcon = new ImageIcon(buffer3);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Multi null");
        }
        try {
            BufferedImage buffer4 = ImageIO.read(getClass().getResource("/Resources/Controls.png"));
            controlIcon = new ImageIcon(buffer4);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Controls null");
        }

        // Single Player Button
        singleButton = new JButton(singleIcon);
        singleButton.setPreferredSize(new Dimension(300, 400));
        singleButton.addActionListener(singlePlayer);

        // Multi Player Button
        multiButton = new JButton(multiIcon);
        multiButton.setPreferredSize(new Dimension(300, 400));
        multiButton.addActionListener(multiPlayer);

        // Controls Button
        controls = new JButton(controlIcon);
        controls.setPreferredSize(new Dimension(300, 400));
        controls.addActionListener(Controls);

        singleButton.setBorderPainted(false);
        singleButton.setContentAreaFilled(false);
        singleButton.setFocusPainted(false);
        singleButton.setOpaque(false);

        multiButton.setBorderPainted(false);
        multiButton.setContentAreaFilled(false);
        multiButton.setFocusPainted(false);
        multiButton.setOpaque(false);

        controls.setBorderPainted(false);
        controls.setContentAreaFilled(false);
        controls.setFocusPainted(false);
        controls.setOpaque(false);

        singleButton.addActionListener(singlePlayer);
        multiButton.addActionListener(multiPlayer);
        controls.addActionListener(Controls);

        buttonPanel.add(singleButton);
        buttonPanel.add(multiButton);
        buttonPanel.add(controls);

        JPanel temPanel = new JPanel(new BorderLayout());
        temPanel.setBackground(Color.black);
        temPanel.add(buttonPanel, BorderLayout.CENTER);

        add(temPanel, BorderLayout.CENTER);
    }

}
