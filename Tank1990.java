package Tank;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class Tank1990 {
    protected static int width;
    protected static int height;

    protected static JFrame game;
    protected static Menü menüPanel;
    protected static ControlPanel controlPanel;
    protected static GamePanel gamePanel;

    public static void main(String[] args) {

        width = 832;
        height = 832;

        game = new JFrame("Tank 1990");
        game.setBackground(Color.black);
        game.setPreferredSize(new Dimension(width, height));
        game.setLayout(new BorderLayout());
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menüPanel = new Menü(new ActionListener() {
            public void actionPerformed(ActionEvent e) { // SinglePlayer
                gamePanel = new GamePanel(1);
                game.add(gamePanel, BorderLayout.CENTER);
                menüPanel.setVisible(false);
                gamePanel.setVisible(true);
                gamePanel.requestFocusInWindow();

                new Thread(gamePanel).start();
            }
        }, new ActionListener() {
            public void actionPerformed(ActionEvent e) { // MultiPlayer
                gamePanel = new GamePanel(2);
                game.add(gamePanel);
                menüPanel.setVisible(false);
                gamePanel.setVisible(true);
                gamePanel.requestFocusInWindow();

                new Thread(gamePanel).start();
            }
        }, new ActionListener() {
            public void actionPerformed(ActionEvent e) { // Controls
                menüPanel.setVisible(false);
                controlPanel.setVisible(true);
            }

        });

        menüPanel.setPreferredSize(new Dimension(width, height));
        game.add(menüPanel);

        game.pack();
        game.setVisible(true);

    }

    public static GamePanel getGamePanel() {
        return gamePanel;
    }
}
