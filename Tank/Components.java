package Tank;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.imageio.ImageIO;

interface Walkable {

}

interface UnWalkable {

}

interface Destructible {

}

public abstract class Components implements Serializable { // Bileşenler
    protected boolean isDestroyed;
    protected int x, y;
    protected int health;
    protected final int size = 64;

    Components(int x, int y) {
        this.x = x;
        this.y = y;
        isDestroyed = false;
        this.health = 3;
    }

    public void takeDamage(int damage) {

        if (health > 0) {
            health = health - damage;
            if (health <= 0) {
                isDestroyed = true;
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, 32, 32);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public int getSize() {
        return size;
    }

}

abstract class Wall extends Components implements UnWalkable, Destructible {
    protected int health;

    Wall(int x, int y, int health) {
        super(x, y);
        this.health = health;
    }

    public void takeDamage(int damage) {
        if (health > 0) {
            health = health - damage;
            if (health <= 0) {
                isDestroyed = true;
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, 32, 32);
    }
}

class BrickWall extends Wall {
    protected BufferedImage brickImage;

    BrickWall(int x, int y) {
        super(x, y, 4);
        try {
            brickImage = ImageIO.read(getClass().getResource("/Resources/Brick_Simple.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Brick Wall null");
        }
    }

    public void draw(Graphics g) {
        if (brickImage != null) {
            g.drawImage(brickImage, x * 64, y * 64, 64, 64, null);
        } else {
            g.setColor(Color.red);
            g.fillRect(x, y, 32, 32);
        }
    }

}

class SteelWall extends Wall {
    protected BufferedImage steelImage;

    SteelWall(int x, int y) {
        super(x, y, 8); // !!!!!!!!!!!!!!!!!!!
        try {
            steelImage = ImageIO.read(getClass().getResource("/Resources/Steel_Simple.png"));
        } catch (Exception e) {
            System.out.println("Steel Wall null");
        }
    }

    public void takeDamage(int damage) { // SILAHA GÖRE DAMAGE ALMA ALMAMA
        if (health > 0) {
            health = health - damage;
            if (health <= 0) {
                isDestroyed = true;
            }
        }
    }

    public void draw(Graphics g) {
        if (steelImage != null) {
            g.drawImage(steelImage, x * 64, y * 64, 64, 64, null);
        } else {
            g.setColor(Color.gray);
            g.fillRect(x, y, 16, 16);
        }
    }

}

class LeftStair extends Components implements UnWalkable, Destructible {
    protected BufferedImage leftImage;

    LeftStair(int x, int y) {
        super(x, y);
        this.health = 4;
        try {
            leftImage = ImageIO.read(getClass().getResource("/Resources/Brick_Stair_Left.png"));
        } catch (Exception e) {
            System.out.println("Left Stair null");
        }
    }

    public void draw(Graphics g) {
        if (leftImage != null) {
            g.drawImage(leftImage, x * 64 - 32, y * 64 - 32, 64, 96, null);
        } else {
            g.setColor(Color.red);
            g.fillRect(x, y, 16, 16);
        }
    }
}

class RightStair extends Components implements UnWalkable, Destructible {
    protected BufferedImage rightImage;

    RightStair(int x, int y) {
        super(x, y);
        this.health = 4;
        try {
            rightImage = ImageIO.read(getClass().getResource("/Resources/Brick_Stair_Right.png"));
        } catch (Exception e) {
            System.out.println("Right Stair null");
        }
    }

    public void draw(Graphics g) {
        if (rightImage != null) {
            g.drawImage(rightImage, x * 64 + 32, y * 64 - 32, 64, 96, null);
        } else {
            g.setColor(Color.red);
            g.fillRect(x, y, 16, 16);
        }
    }
}

class SteelLeftStair extends Components implements UnWalkable, Destructible {
    protected BufferedImage leftImage;

    SteelLeftStair(int x, int y) {
        super(x, y);
        this.health = 8;
        try {
            leftImage = ImageIO.read(getClass().getResource("/Resources/Steel_Stair_Left.png"));
        } catch (Exception e) {
            System.out.println("Steel Left Stair null");
        }
    }

    public void draw(Graphics g) {
        if (leftImage != null) {
            g.drawImage(leftImage, x * 64 - 32, y * 64 - 32, 64, 96, null);
        } else {
            g.setColor(Color.red);
            g.fillRect(x, y, 16, 16);
        }
    }

}

class SteelRightStair extends Components implements UnWalkable, Destructible {
    protected BufferedImage rightImage;

    SteelRightStair(int x, int y) {
        super(x, y);
        this.health = 8;
        try {
            rightImage = ImageIO.read(getClass().getResource("/Resources/Steel_Stair_Right.png"));
        } catch (Exception e) {
            System.out.println("Steel Left Stair null");
        }
    }

    public void draw(Graphics g) {
        if (rightImage != null) {
            g.drawImage(rightImage, x * 64 + 32, y * 64 - 32, 64, 96, null);
        } else {
            g.setColor(Color.red);
            g.fillRect(x, y, 16, 16);
        }
    }
}

class Trees extends Components implements Walkable {
    protected BufferedImage treeImage;

    Trees(int x, int y) {
        super(x, y);
        try {
            treeImage = ImageIO.read(getClass().getResource("/Resources/Trees.png"));
        } catch (Exception e) {
            System.out.println("Trees null");
        }
    }

    public void draw(Graphics g) {
        if (treeImage != null) {
            g.drawImage(treeImage, x * 64, y * 64, 64, 64, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(x, y, 16, 16);
        }
    }
}

class Water extends Components implements UnWalkable {
    protected BufferedImage waterImage;

    Water(int x, int y) {
        super(x, y);
        try {
            waterImage = ImageIO.read(getClass().getResource("/Resources/Water.png"));
        } catch (Exception e) {
            System.out.println("Water null");
        }
    }

    public void draw(Graphics g) {
        if (waterImage != null) {
            g.drawImage(waterImage, x * 64, y * 64, 64, 64, null);
        } else {
            g.setColor(Color.blue);
            g.fillRect(x, y, 16, 16);
        }
    }
}

class Ice extends Components implements Walkable {
    protected BufferedImage IceImage;

    Ice(int x, int y) {
        super(x, y);
        try {
            IceImage = ImageIO.read(getClass().getResource("/Resources/Ice.png"));
        } catch (Exception e) {
            System.out.println("Ice null");
        }
    }

    public void draw(Graphics g) {
        if (IceImage != null) {
            g.drawImage(IceImage, x * 64, y * 64, 64, 64, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(x, y, 16, 16);
        }
    }
}

abstract class Spawn extends Components implements Walkable {
    protected boolean occupied;

    Spawn(int x, int y) {
        super(x, y);
        this.occupied = false;

    }
}

class PlayerSpawn extends Spawn {
    protected BufferedImage pSpawnImage;

    PlayerSpawn(int x, int y) {
        super(x, y);
        try {
            pSpawnImage = ImageIO.read(getClass().getResource("/Resources/Player_Spawn.png"));
        } catch (Exception e) {
            System.out.println("Player Spawn null");
        }
    }

    public void draw(Graphics g) {
        if (pSpawnImage != null) {
            g.drawImage(pSpawnImage, x * 64, y * 64, 64, 64, null);
        } else {
            g.setColor(Color.white);
            g.fillOval(x, y, 16, 16);
        }
    }
}

class TankSpawn extends Spawn {
    protected BufferedImage tSpawnImage;

    TankSpawn(int x, int y) {
        super(x, y);
        try {
            tSpawnImage = ImageIO.read(getClass().getResource("/Resources/Tank_Spawn.png"));
        } catch (Exception e) {
            System.out.println("Tank Spawn null");
        }
    }

    public void draw(Graphics g) {
        if (tSpawnImage != null) {
            g.drawImage(tSpawnImage, x * 64, y * 64, 64, 64, null);
        } else {
            g.setColor(Color.white);
            g.fillOval(x, y, 16, 16);
        }
    }
}