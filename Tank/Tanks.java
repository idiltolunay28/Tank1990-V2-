package Tank;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract class Tanks implements GameObjects {
    protected static final int width = 832;
    protected static final int height = 832;

    protected int health;
    protected int speed;
    protected int x, y; // konum
    protected int midX, midY; // orta nokta
    protected String direction;
    protected int points;
    protected Player player;
    protected final int size = 56;

    protected boolean isAlive;
    protected boolean isRed;
    protected boolean isFreezed;

    protected List<Boosters> boosters;

    protected List<TankBullet> activeBullets;
    protected List<TankBullet> brokenBullets;
    protected int bulletSpeed;
    protected long lastFiredTime;
    protected final int bulletDuration = 1500;

    protected long lastMove = 0;
    protected long moveDuration = 100;

    protected long shieldDuration;
    protected long shieldStart;
    protected boolean shieldActive;

    protected BufferedImage tankImage;

    protected static final Star tempStar = new Star();

    Tanks(int health, int speed, int x, int y, boolean isRed, int points, String direction) {
        this.health = health;
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.midX = x + 30;
        this.midY = y + 26;
        this.direction = "down";
        this.points = points;
        this.isAlive = true;
        this.isRed = isRed;
        this.isFreezed = false;

        boosters = new ArrayList<>();

        activeBullets = new ArrayList<>();
        brokenBullets = new ArrayList<>();
        bulletSpeed = calculateBulletSpeed();

        lastFiredTime = 0;

        try {
            if (isRed) {
                tankImage = ImageIO.read(getClass().getResource("/Resources/Red_Tank_Alt.png"));
            }
            tankImage = ImageIO.read(getClass().getResource("/Resources/Basic_Tank_Alt.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldnt download Tanks's png!");
            tankImage = null;
        }

        this.midX = (tankImage.getWidth() / 2) + x;
        this.midY = (tankImage.getHeight() / 2) + y;

    }

    public void takeDamage(int damage) {
        int check = health;
        if (!shieldActive) {
            if (check - damage <= 0) {
                health = 0;
                isAlive = false;
            }
            health = health - damage;
        }

        boosters.clear();
    }

    public void fire() {
        if (Tank1990.getGamePanel().getTime() - lastFiredTime > bulletDuration) {
            TankBullet newBullet = new TankBullet(getXBasedOnDirection(), getYBasedOnDirection(),
                    calculateBulletSpeed(),
                    calculateBulletPower(),
                    direction);
            activeBullets.add(newBullet);
            lastFiredTime = Tank1990.getGamePanel().getTime();

            if (calculateStars() >= 2) {
                TankBullet newBullet2 = new TankBullet(getXBasedOnDirection(), getYBasedOnDirection(),
                        calculateBulletSpeed(),
                        calculateBulletPower(),
                        direction);
                activeBullets.add(newBullet2);
                lastFiredTime = Tank1990.getGamePanel().getTime();
            }
        }

    }

    public void move(Map<String, Double> move, StageControl stageControl) {
        double up = move.get("up");
        double down = move.get("down");
        double left = move.get("left");
        double right = move.get("right");

        int nextX = this.x;
        int nextY = this.y;
        String newDirection = this.direction;

        if (up == -10.0 && down != -10.0) {
            nextY += speed;
            newDirection = "down";
        } else if (up != -10 && down == -10) {
            nextY -= speed;
            newDirection = "up";
        } else if (left != -10 && right == -10) {
            nextX -= speed;
            newDirection = "left";
        } else if (left == -10 && right != -10) {
            nextX += speed;
            newDirection = "right";
        }

        if (!stageControl.checkObstacleAt(nextX, nextY, size)) {
            this.x = nextX;
            this.y = nextY;
            setDirection(newDirection);
        } else {
            setLastMove(0);
        }

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
        int result = 1;
        if (calculateStars() >= 1) { // !!
            result = 3;
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

    public void checkCollision(List<Components> comps) {

    }

    public void draw(Graphics g) {

    }

    public void addHealth() {
        health += 1;
    }

    public int getSize() {
        return size;
    }

    public long getLastMove() {
        return lastMove;
    }

    public void setLastMove(long newMove) {
        lastMove = newMove;
    }

    public long getMoveDuration() {
        return moveDuration;
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

    public void setAlive(boolean state) {
        isAlive = state;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int X) {
        x = X;
    }

    public void setY(int Y) {
        y = Y;
    }

    public int getPoint() {
        return points;
    }

    public void setPoint() {

    }

    public boolean getRed() {
        return isRed;
    }

    public void setDirection(String newdirection) {
        direction = newdirection;
        setImage(newdirection);
    }

    public void setImage(String direction) {

    }

    public List<TankBullet> getActiveBullets() {
        return activeBullets;
    }

    public List<TankBullet> getBrokenBullets() {
        return brokenBullets;
    }

    public List<Boosters> getBoosters() {
        return boosters;
    }

    public String getName() {
        return "tank";
    }

    public int getSpeed() {
        return speed;
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

// Speed: 1(Slow), 2(Normal), 3(Fast)

class BasicTank extends Tanks {

    private static String direction;
    // protected BufferedImage tankImage;

    BasicTank(int x, int y, boolean isRed) {
        super(1, 1, x, y, isRed, 100, direction); // Slow
        isAlive = true;
        this.isRed = isRed;

        try {
            if (isRed) {
                tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Red_Tank_Alt.png"));
            }
            tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Basic_Tank_Alt.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldnt download Basic Tank's png!");
            tankImage = null;
        }
    }

    public void draw(Graphics g) {
        if (tankImage != null) {
            g.drawImage(tankImage, x * 64, y * 64, 60, 52, null);

        } else {
            System.out.println("Basic null");
            g.setColor(Color.gray);
            g.fillRect(x, y, 32, 32);
        }
    }

    public void setImage(String newdirection) {
        switch (newdirection) {
            case "up":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Basic_Tank_Ust.png"));
                } catch (Exception e) {
                    System.out.println("Basic Ust null");
                }
                break;
            case "down":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Basic_Tank_Alt.png"));
                } catch (Exception e) {
                    System.out.println("Basic Alt null");
                }
                break;
            case "left":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Basic_Tank_Sol.png"));
                } catch (Exception e) {
                    System.out.println("Basic Sol null");
                }
                break;
            case "right":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Basic_Tank_Sag.png"));
                } catch (Exception e) {
                    System.out.println("Basic Sag null");
                }
                break;
        }

    }

    public String getName() {
        return "basictank";
    }
}

class FastTank extends Tanks {

    private static String direction;
    // protected BufferedImage tankImage;

    FastTank(int x, int y, boolean isRed) {
        super(1, 3, x, y, isRed, 200, direction); // Fast
        isAlive = true;
        this.isRed = isRed;
        try {
            if (isRed) {
                tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Red_Tank_Alt.png"));
            }
            tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Fast_Tank_Alt.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldnt download Fast Tank's png!");
            tankImage = null;
        }
    }

    public void draw(Graphics g) {
        if (tankImage != null) {
            g.drawImage(tankImage, x * 64, y * 64, 60, 52, null);
        } else {
            System.out.println("Fast null");
            g.setColor(Color.white);
            g.fillRect(x, y, 32, 32);
        }
    }

    public void setImage(String newdirection) {
        switch (newdirection) {
            case "up":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Fast_Tank_Ust.png"));
                } catch (Exception e) {
                    System.out.println("Fast Ust null");
                }
                break;
            case "down":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Fast_Tank_Alt.png"));
                } catch (Exception e) {
                    System.out.println("Fast Alt null");
                }
                break;
            case "left":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Fast_Tank_Sol.png"));
                } catch (Exception e) {
                    System.out.println("Fast Sol null");
                }
                break;
            case "right":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Fast_Tank_Sag.png"));
                } catch (Exception e) {
                    System.out.println("Fast Sag null");
                }
                break;
        }
    }

    public String getName() {
        return "fasttank";
    }
}

class PowerTank extends Tanks {

    private static String direction;
    // protected BufferedImage tankImage;

    PowerTank(int x, int y, boolean isRed) {
        super(1, 2, x, y, isRed, 300, direction); // Normal
        isAlive = true;
        this.isRed = isRed;
        try {
            if (isRed) {
                tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Red_Tank_Alt.png"));
            }
            tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Power_Tank_Alt.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldnt download Power Tank's png!");
            tankImage = null;
        }
    }

    public void draw(Graphics g) {
        if (tankImage != null) {
            g.drawImage(tankImage, x * 64, y * 64, 60, 52, null);
        } else {
            System.out.println("Power null");
            g.setColor(Color.yellow);
            g.fillRect(x, y, 32, 32);
        }
    }

    public void setImage(String newdirection) {
        switch (newdirection) {
            case "up":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Power_Tank_Ust.png"));
                } catch (Exception e) {
                    System.out.println("Power Ust null");
                }
                break;
            case "down":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Power_Tank_Alt.png"));
                } catch (Exception e) {
                    System.out.println("Power Alt null");
                }
                break;
            case "left":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Power_Tank_Sol.png"));
                } catch (Exception e) {
                    System.out.println("Power Sol null");
                }
                break;
            case "right":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Power_Tank_Sag.png"));
                } catch (Exception e) {
                    System.out.println("Power Sag null");
                }
                break;
        }

    }

    public String getName() {
        return "powertank";
    }
}

class ArmorTank extends Tanks {

    private static String direction;
    // protected BufferedImage tankImage;

    ArmorTank(int x, int y, boolean isRed) {
        super(4, 2, x, y, isRed, 400, direction);
        isAlive = true;
        this.isRed = isRed;
        try {
            if (isRed) {
                tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Red_Tank_Alt.png"));
            }
            tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Armor_Tank_Alt.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldnt download Armor Tank's png!");
            tankImage = null;
        }
    }

    public void draw(Graphics g) {
        if (tankImage != null) {
            g.drawImage(tankImage, x * 64, y * 64, 60, 52, null);
        } else {
            System.out.println("Armor null");
            g.setColor(Color.green);
            g.fillRect(x, y, 32, 32);
        }
    }

    public void setImage(String newdirection) {
        switch (newdirection) {
            case "up":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Fast_Tank_Ust.png"));
                } catch (Exception e) {
                    System.out.println("Fast Ust null");
                }
                break;
            case "down":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Fast_Tank_Alt.png"));
                } catch (Exception e) {
                    System.out.println("Fast Alt null");
                }
                break;
            case "left":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Fast_Tank_Sol.png"));
                } catch (Exception e) {
                    System.out.println("Fast Sol null");
                }
                break;
            case "right":
                try {
                    tankImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Fast_Tank_Sag.png"));
                } catch (Exception e) {
                    System.out.println("Fast Sag null");
                }
                break;
        }
    }

    public String getName() {
        return "armortank";
    }

}
