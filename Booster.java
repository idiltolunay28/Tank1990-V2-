package Tank;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

abstract class Boosters {
    protected int x, y;
    protected BufferedImage boostImage;
    protected final int size = 10;

    Boosters() {
        this.x = 0;
        this.y = 0;
        setBoosterSpawn();

    }

    public void setBoosterSpawn() {
        Random randomX = new Random();
        int location = randomX.nextInt(15);
        switch (location) {
            case 0:
                setX(3 * 64);
                setY(3 * 64);
                break;
            case 1:
                setX(4 * 64);
                setY(4 * 64);
                break;
            case 2:
                setX(5 * 64);
                setY(5 * 64);
                break;
            case 3:
                setX(6 * 64);
                setY(6 * 64);
                break;
            case 4:
                setX(7 * 64);
                setY(7 * 64);
                break;
            case 5:
                setX(8 * 64);
                setY(8 * 64);
                break;
            case 6:
                setX(9 * 64);
                setY(9 * 64);
                break;
            case 7:
                setX(10 * 64);
                setY(10 * 64);
                break;
            case 8:
                setX(8 * 64);
                setY(4 * 64);
                break;
            case 9:
                setX(7 * 64);
                setY(5 * 64);
                break;
            case 10:
                setX(5 * 64);
                setY(7 * 64);
                break;
            case 11:
                setX(4 * 64);
                setY(8 * 64);
                break;
            case 12:
                setX(3 * 64);
                setY(9 * 64);
                break;
            case 13:
                setX(2 * 64);
                setY(10 * 64);
                break;
            case 14:
                setX(9 * 64);
                setY(3 * 64);
                break;
            case 15:
                setX(6 * 64);
                setY(8 * 64);
                break;

        }
    }

    public void addEffect(Player player) {

    }

    public void addEffect(Tanks tanks) {

    }

    public void draw(Graphics g) {

    }

    public int getSize() {
        return size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int newX) {
        x = newX;
    }

    public void setY(int newY) {
        y = newY;
    }

}

class Grenade extends Boosters {

    Grenade() {
        super();

        try {
            boostImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Grenade.png"));
        } catch (Exception e) {
            System.out.println("Grenade null");
        }

    }

    public void draw(Graphics g) {
        if (boostImage != null) {
            g.drawImage(boostImage, x * 64, y * 64, 64, 64, null);

        } else {
            System.out.println("Greande null");
            g.setColor(Color.gray);
            g.fillRect(x, y, 32, 32);
        }
    }

    public void addEffect(Player player) {
        StageControl.getStage().BOOM();
    }

    public void addEffect(Tanks tanks) {

    }
}

class Helmet extends Boosters {

    Helmet() {
        super();
        try {
            boostImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Helmet.png"));
        } catch (Exception e) {
            System.out.println("Helmet null");
        }
    }

    public void draw(Graphics g) {

        if (boostImage != null) {
            g.drawImage(boostImage, x * 64, y * 64, 64, 64, null);

        } else {
            System.out.println("Helmet null");
            g.setColor(Color.gray);
            g.fillRect(x, y, 32, 32);
        }
    }

    public void addEffect(Player player) {
        player.setShield(true);
        player.setShieldStart(Tank1990.getGamePanel().getTime());
    }

    public void addEffect(Tanks tanks) {
        tanks.setShield(true);
        tanks.setShieldStart(Tank1990.getGamePanel().getTime());
    }
}

class Shovel extends Boosters {

    Shovel() {
        super();
        try {
            boostImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Shovel.png"));
        } catch (Exception e) {
            System.out.println("Shovel null in cons");
        }
    }

    public void draw(Graphics g) {
        if (boostImage != null) {
            g.drawImage(boostImage, x * 64, y * 64, 64, 64, null);

        } else {
            System.out.println("Shovel null");
            g.setColor(Color.gray);
            g.fillRect(x, y, 32, 32);
        }
    }

    public void addEffect(List<Components> map, Player player) {
        List<Components> adding = new ArrayList<>();
        List<Components> removing = new ArrayList<>();

        for (Components comp : map) {
            if (comp instanceof RightStair && !comp.isDestroyed()) {
                removing.add(comp);
                adding.add(new SteelRightStair(comp.getX(), comp.getY()));
            } else if (comp instanceof LeftStair && !comp.isDestroyed()) {
                removing.add(comp);
                adding.add(new SteelLeftStair(comp.getX(), comp.getY()));
            }
        }

        map.removeAll(removing);
        map.addAll(adding);
    }

    public void addEffect(Tanks tanks) {

    }
}

class Star extends Boosters {
    Star() {
        super();
        try {
            boostImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Star.png"));
        } catch (Exception e) {
            System.out.println("Star null");
        }
    }

    public void draw(Graphics g) {
        if (boostImage != null) {
            g.drawImage(boostImage, x * 64, y * 64, 64, 64, null);

        } else {
            System.out.println("Star null");
            g.setColor(Color.gray);
            g.fillRect(x, y, 32, 32);
        }
    }

}

class Tank extends Boosters {
    Tank() {
        super();
        try {
            boostImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Tank.png"));
        } catch (Exception e) {
            System.out.println("Tank null");
        }
    }

    public void draw(Graphics g) {
        if (boostImage != null) {
            g.drawImage(boostImage, x * 64, y * 64, 64, 64, null);

        } else {
            System.out.println("Tank null");
            g.setColor(Color.gray);
            g.fillRect(x, y, 32, 32);
        }
    }

    public void addEffect(Player player) {
        player.addHealth();
    }

    public void addEffect(Tanks tanks) {
        tanks.addHealth();
    }
}

class Timer extends Boosters {
    Timer() {
        super();
        try {
            boostImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Timer.png"));
        } catch (Exception e) {
            System.out.println("Timer is null in cons");
        }
    }

    public void draw(Graphics g) {
        if (boostImage != null) {
            g.drawImage(boostImage, x * 64, y * 64, 64, 64, null);

        } else {
            System.out.println("Timer null");
            g.setColor(Color.gray);
            g.fillRect(x, y, 32, 32);
        }
    }
}