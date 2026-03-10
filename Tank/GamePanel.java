package Tank;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements KeyListener, MouseListener, Runnable {

    protected static final int width = 208;
    protected static final int height = 208;

    protected static long time;

    protected boolean Paused;
    protected boolean Won;
    protected boolean Ended;
    protected boolean Running;

    protected PausePanel pausePanel;
    // protected StageFinalPanel finalPanel;

    protected int playerCount;
    protected Player player1;
    protected Player player2;

    protected Eagle eagle;

    protected StageControl stageControl;
    protected int stage;

    protected long gameTime;
    protected long pauseStart;
    protected long totalPauseTime;

    GamePanel(int playerCount) {
        time = 0;
        this.playerCount = playerCount;
        Paused = false;
        Won = false;
        Ended = false;
        Running = true;

        player1 = new Player(width / 2, height / 2, 1);
        player1.setSpawn(1);
        if (playerCount == 2) {
            player2 = new Player(width / 2, height / 2, 2);
            player2.setSpawn(2);

        } else {
            player2 = null;
        }

        stageControl = new StageControl(player1, player2);
        gameTime = 0;
        pauseStart = 0;
        totalPauseTime = 0;

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);

        pausePanel = new PausePanel();
        pausePanel.setVisible(false);
        add(pausePanel);
        setVisible(true);

        // finalPanel = new StageFinalPanel(stageControl.getScore());
        // add(finalPanel);
        // finalPanel.setVisible(false);

        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
    }

    @Override
    public void run() {
        stageControl.loadStage();

        while (Running) {

            if (!Paused && !Ended) {
                gameTime = System.currentTimeMillis() - totalPauseTime;
                updatePlayers();

                if (stageControl.isStageComplete() && playerStatus() && eagleStatus()) {
                    stageControl.startStage();
                }
                stageControl.updateStage();

                for (PlayerBullet bullet : player1.getActiveBullets()) {
                    if (bullet.getAlive()) {
                        bullet.move(stageControl.getComponents());
                        stageControl.checkBullet(bullet);
                        checkOnetank(bullet);

                    } else {
                        player1.brokenBullets.add(bullet);
                    }
                }

                if (player2 != null) {
                    for (PlayerBullet bullet : player2.getActiveBullets()) {
                        if (bullet.getAlive()) {
                            bullet.move(stageControl.getComponents());
                            stageControl.checkBullet(bullet);
                            checkOnetank(bullet);

                        } else {
                            player2.brokenBullets.add(bullet);
                        }
                    }
                    player2.getActiveBullets().removeAll(player2.getBrokenBullets());
                    player2.getBrokenBullets().clear();
                }

                player1.getActiveBullets().removeAll(player1.getBrokenBullets());
                player1.getBrokenBullets().clear();

                repaint();
                try {
                    Thread.sleep(15);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }

    public boolean playerStatus() {
        if (player2 != null) {
            return player1.getAlive() || player2.getAlive();
        } else {
            return player1.getAlive();
        }
    }

    public boolean eagleStatus() {
        if (stageControl.getEagle().isDestroyed()) {
            return false;
        }
        return true;
    }

    public void updatePlayers() {
        if (player2 != null) {
            if (!player1.getAlive() && !player2.getAlive()) {
                Ended = true;
                Won = false;
            }
        } else {
            if (!player1.getAlive()) {
                Ended = true;
                Won = false;
            }
        }

        if (stageControl.getEagle().isDestroyed()) {
            Ended = true;
            Won = false;
        }

        if (gameTime - player1.getShieldStart() >= player1.getShieldDuration()) {
            player1.setShield(false);
        }

        if (player2 != null) {
            if (gameTime - player2.getShieldStart() >= player2.getShieldDuration()) {
                player2.setShield(false);
            }
        }
    }

    public void checkOnetank(Bullets bullets) {
        for (Tanks tanks : stageControl.getAliveTanks()) {
            stageControl.checkBullet(bullets, tanks);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized(stageControl.getComponents()) {
            stageControl.draw(g);
        }

        synchronized(stageControl.getAliveTanks()) {
            if (player1 != null && player1.getAlive()) {
                player1.draw(g);
                synchronized(player1.getActiveBullets()) {
                    for (Bullets bullet : player1.getActiveBullets()) {
                        if (bullet.getAlive()) {
                            bullet.draw(g);
                        }
                    }
                }
            }

            if (player2 != null && player2.getAlive()) {
                player2.draw(g);
                synchronized(player2.getActiveBullets()) {
                    for (Bullets bullet : player2.getActiveBullets()) {
                        if (bullet.getAlive()) {
                            bullet.draw(g);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                player1.setDirection("up");
                player1.move(stageControl.getComponents(), 0, -1);
                try {
                    player1.playerImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Player_Ust.png"));
                    player1.setDirection("up");
                } catch (Exception u) {
                    u.printStackTrace();
                    System.out.println("Player_Ust null ");
                }
                break;
            case KeyEvent.VK_LEFT:
                player1.setDirection("left");
                player1.move(stageControl.getComponents(), -1, 0);
                try {
                    player1.playerImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Player_Sol.png"));
                    player1.setDirection("left");
                } catch (Exception l) {
                    l.printStackTrace();
                    System.out.println("Player_Sol null ");
                }
                break;
            case KeyEvent.VK_DOWN:
                player1.setDirection("down");
                player1.move(stageControl.getComponents(), 0, 1);
                try {
                    player1.playerImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Player_Alt.png"));
                    player1.setDirection("down");
                } catch (Exception a) {
                    a.printStackTrace();
                    System.out.println("Player_Alt null ");
                }
                break;
            case KeyEvent.VK_RIGHT:
                player1.setDirection("right");
                player1.move(stageControl.getComponents(), 1, 0);
                try {
                    player1.playerImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Player_Sag.png"));
                    player1.setDirection("right");
                } catch (Exception r) {
                    r.printStackTrace();
                    System.out.println("Player_Sag null ");
                }
                break;
            case KeyEvent.VK_W:
                if (player2 != null) {
                    player2.setDirection("up");
                    player2.move(stageControl.getComponents(), 0, -1);
                    try {
                        player2.playerImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Player_Ust.png"));
                        player2.setDirection("up");
                    } catch (Exception u) {
                        u.printStackTrace();
                        System.out.println("Player_Ust null ");
                    }
                }

                break;
            case KeyEvent.VK_A:
                if (player2 != null) {
                    player2.move(stageControl.getComponents(), -1, 0);
                    try {
                        player2.playerImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Player_Sol.png"));
                        player2.setDirection("left");
                    } catch (Exception l) {
                        l.printStackTrace();
                        System.out.println("Player_Sol null ");
                    }
                }
                break;
            case KeyEvent.VK_S:
                if (player2 != null) {
                    player2.move(stageControl.getComponents(), 0, 1);
                    try {
                        player2.playerImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Player_Alt.png"));
                        player2.setDirection("down");
                    } catch (Exception a) {
                        a.printStackTrace();
                        System.out.println("Player_Alt null ");
                    }
                }
                break;
            case KeyEvent.VK_D:
                if (player2 != null) {
                    player2.move(stageControl.getComponents(), 1, 0);
                    try {
                        player2.playerImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Player_Sag.png"));
                        player2.setDirection("right");
                    } catch (Exception r) {
                        r.printStackTrace();
                        System.out.println("Player_Sag null ");
                    }
                }
                break;
            case KeyEvent.VK_Z:
                player1.fire();
                break;
            case KeyEvent.VK_X:
                if (player2 != null) {
                    player2.fire();
                }
                break;
            case KeyEvent.VK_ENTER:
                Paused = !Paused;
                if (Paused) {
                    pausePanel.setVisible(true);
                    pauseStart = System.currentTimeMillis();
                } else if (!Paused) {
                    pausePanel.setVisible(false);
                    totalPauseTime += System.currentTimeMillis() - pauseStart;
                }
                break;

        }
    }

    public long getTime() {
        return gameTime;
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public StageControl getStageControl() {
        return stageControl;
    }
}

interface GameObjects {
    public int getX();

    public int getY();
}