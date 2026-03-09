package Tank;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

abstract public class Bullets {
    protected int x, y;
    protected int speed;
    protected boolean isAlive;
    protected String direction;
    protected int power;
    protected final int size = 8;

    Bullets(int x, int y, int speed, int power, String direction) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.isAlive = true;
        this.direction = direction;
        this.power = power;
    }

    public void move(List<Components> map) {
        if (!isAlive) return;
        int nextX = x;
        int nextY = y;

        switch (direction) {
            case "up":    nextY -= speed; break;
            case "down":  nextY += speed; break;
            case "left":  nextX -= speed; break;
            case "right": nextX += speed; break;
        }

        if (nextX < 0 || nextX > 832 || nextY < 0 || nextY > 832) {
            isAlive = false;
            return;
        }

        if (checkMapCollision(map, nextX, nextY)) {
            isAlive = false;
        } else {
            this.x = nextX;
            this.y = nextY;
        }
    }

    private boolean checkMapCollision(List<Components> map, int nextX, int nextY) {
        Rectangle bulletRect = new Rectangle(nextX, nextY, size, size);

        synchronized (map){
            for (Components comp : map) {
                if (comp instanceof UnWalkable && comp instanceof Destructible && !comp.isDestroyed()) {
                    Rectangle compRect = new Rectangle(comp.getX() * 64, comp.getY() * 64, 64, 64);

                    if (bulletRect.intersects(compRect)) {
                        comp.takeDamage(this.power);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, size, size);
    }

    // Getters
    public boolean getAlive() { return isAlive; }
    public void setAlive(boolean state) { this.isAlive = state; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getPower() { return power; }
    public int getSize() { return size; }
}

class PlayerBullet extends Bullets {
    PlayerBullet(int x, int y, int speed, int power, String direction) {
        super(x, y, speed, power, direction);
    }
}

class TankBullet extends Bullets {
    TankBullet(int x, int y, int speed, int power, String direction) {
        super(x, y, speed, power, direction);
    }
}