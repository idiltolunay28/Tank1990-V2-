package Tank;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Player implements GameObjects {
    protected static final int width = 832;
    protected static final int height = 832;

    protected int health;
    protected static final int speed = 1;
    protected int x, y;
    protected int spawnX, spawnY;
    protected int midX, midY;
    protected int points;
    protected int place; // kaçıncı oyuncu
    protected final int size = 56;

    protected boolean isAlive;

    protected List<Boosters> boosters;
    protected boolean fieldActive;
    protected BufferedImage playerImage;
    protected String direction;

    protected List<PlayerBullet> activeBullets;
    protected List<PlayerBullet> brokenBullets;
    protected int bulletSpeed;
    protected long lastFiredTime;
    protected final int bulletDuration = 1000;

    protected final long shieldDuration = 1000;
    protected long shieldStart;
    protected boolean shieldActive;

    protected static final Star tempStar = new Star();

    Player(int x, int y, int place) {
        health = 3;
        this.x = x;
        this.y = y;
        points = 0;
        this.place = place;
        isAlive = true;

        boosters = new ArrayList<>();
        fieldActive = false; // !!!!!!!!!
        direction = "up";

        activeBullets = new ArrayList<>();
        brokenBullets = new ArrayList<>();
        lastFiredTime = 0;

        shieldStart = 0;
        shieldActive = false;

        try {
            playerImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Player_Ust.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Player null in const");
            playerImage = null;
        }

    }

    public void draw(Graphics g) {
        if (playerImage != null) {
            g.drawImage(playerImage, x * 64, y * 64, 64, 64, null);
        } else {
            g.setColor(Color.gray);
            g.fillRect(x, y, 64, 64);
        }
    }

    public void move(List<Components> map, int Xchange, int Ychange) {
        int newX = x + (Xchange * speed);
        int newY = y + (Ychange * speed);

        if (!checkCollision(map, newX, newY)) {
            x = newX;
            y = newY;
        } else {
            if (Xchange != 0){
                this.y = Math.round(this.y / 64.0f) * 64;
            }
            if (Ychange != 0){
                this.x = Math.round(this.x / 64.0f) * 64;
            }
        }

    }

    public boolean checkCollision(List<Components> map, int Xcord, int Ycord) {
        if (Xcord < 0 || Xcord >= 13 || Ycord < 0 || Ycord >= 13) {
            return true;
        }

        for (Components comp : map) {
            if (comp instanceof UnWalkable) {
                if (comp.getX() == Xcord && comp.getY() == Ycord) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setSpawn(int count) {
        if (count == 1) {
            setX(4);
            setY(12);
            setSpawnX(4);
            setSpawnY(12);
        }
        if (count == 2) {
            setX(8);
            setY(12);
            setSpawnX(8);
            setSpawnY(12);
        }
    }

    public void fire() {
        if (Tank1990.getGamePanel().getTime() - lastFiredTime > bulletDuration) {
            PlayerBullet newBullet = new PlayerBullet(getXBasedOnDirection(), getYBasedOnDirection(),
                    calculateBulletSpeed(),
                    calculateBulletPower(),
                    direction);
            activeBullets.add(newBullet);
            lastFiredTime = Tank1990.getGamePanel().getTime();

            if (calculateStars() >= 2) {
                PlayerBullet newBullet2 = new PlayerBullet(getXBasedOnDirection(), getYBasedOnDirection(),
                        calculateBulletSpeed(),
                        calculateBulletPower(),
                        direction);
                activeBullets.add(newBullet2);
                lastFiredTime = Tank1990.getGamePanel().getTime();
            }
        }

    }

    public void takeDamage(int damage) {
        int check = health;
        if (!shieldActive) {
            if (check - damage <= 0) {
                health = 0;
                isAlive = false;
            }
            health = health - damage;
            x = spawnX;
            y = spawnY;
        }
        boosters.clear();
    }

    public void unlockShield() {

    }

    public int calculateStars() {
        int star = 0;
        if (boosters.contains(tempStar)) {
            for (Boosters boost : boosters) {
                if (boost.equals(tempStar)) {
                    star++;
                }
            }
        }
        return star;
    }

    public int calculateBulletSpeed() {
        int result = 10;
        if (calculateStars() >= 1) { // !!
            result = 30;
        }
        return result;
    }

    public int calculateBulletPower() {
        int result = 1;
        if (calculateStars() >= 3) {
            result = 2;
        }
        return result;
    }

    public boolean getAlive() {
        return isAlive;
    }

    public int getSize() {
        return size;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String newdirection) {
        direction = newdirection;
    }

    public int getHealth() {
        return health;
    }

    public boolean getShield() {
        return shieldActive;
    }

    public void setShield(boolean shieldStatus) {
        shieldActive = shieldStatus;
    }

    public long getShieldStart() {
        return shieldStart;
    }

    public void setShieldStart(long time) {
        shieldStart = time;
    }

    public long getShieldDuration() {
        return shieldDuration;
    }

    public void addHealth() {
        health += 1;
    }

    public int getX() {
        return x;
    }

    public void setX(int X) {
        x = X;
    }

    public void setSpawnX(int x) {
        spawnX = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int Y) {
        y = Y;
    }

    public void setSpawnY(int y) {
        spawnY = y;
    }

    public List<PlayerBullet> getActiveBullets() {
        return activeBullets;
    }

    public List<PlayerBullet> getBrokenBullets() {
        return brokenBullets;
    }

    public List<Boosters> getBoosters() {
        return boosters;
    }

    public int getXBasedOnDirection() {
        switch (direction) {
            case "up":
                return x * 64 + (getSize() / 2);
            case "down":
                return x * 64 + (getSize() / 2);
            case "left":
                return x * 64;
            case "right":
                return x * 64 + getSize();
            default:
                return x * 64;
        }
    }

    public int getYBasedOnDirection() {
        switch (direction) {
            case "up":
                return y * 64;
            case "down":
                return y * 64 + getSize();
            case "left":
                return y * 64 + (getSize() / 2);
            case "right":
                return y * 64 + (getSize() / 2);
            default:
                return y * 64;
        }
    }
}
