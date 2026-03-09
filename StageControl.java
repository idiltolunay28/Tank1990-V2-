package Tank;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class StageControl {

    protected static StageControl sc;
    protected Player player1;
    protected Player player2;

    protected static final int totalTank = 20;
    protected static final int maxOnFieldTank = 3;

    protected int currentStage;
    protected int totalTanksSpawned;
    protected int tankOnField;

    protected Map<String, Integer> totalScore;

    protected static List<Components> currentMap;
    protected static List<Components> destroyedComponents;

    protected List<Tanks> aliveTanks;
    protected List<Tanks> deadTanks;

    protected List<Boosters> unclaimedBoosters;
    protected List<Boosters> claimedBoosters;

    protected int boosterX;
    protected int boosterY;

    protected boolean waiting;
    protected long nextStage;
    protected final int finalTime = 4000;

    private static final Timer tempTimer = new Timer();

    StageControl(Player player1, Player player2) {
        sc = this;
        this.player1 = player1;
        this.player2 = player2;

        currentStage = 1;
        totalTanksSpawned = 0;
        tankOnField = 0;

        totalScore = new TreeMap<>();

        currentMap = new ArrayList<>();
        destroyedComponents = new ArrayList<>();
        aliveTanks = new ArrayList<>();
        deadTanks = new ArrayList<>();
        unclaimedBoosters = new ArrayList<>();
        claimedBoosters = new ArrayList<>();
    }

    public void draw(Graphics g) {
        synchronized (currentMap) {
            for (Components comp : currentMap) {
                if (!comp.isDestroyed) {
                    comp.draw(g);
                }
            }
        }

        synchronized (aliveTanks) {
            for (Tanks tanks : aliveTanks) {
                if (tanks.isAlive == true) {
                    tanks.draw(g);

                    for (TankBullet tankBullet : tanks.getActiveBullets()) {
                        if (tankBullet.getAlive()) {
                            tankBullet.draw(g);
                        }
                    }
                }

            }
        }
    }

    public boolean isStageComplete() { // tanklar bitti ise
        return aliveTanks.isEmpty();
    }

    public void startStage() {
        if (!waiting) {
            waiting = true;
            nextStage = Tank1990.getGamePanel().getTime() + finalTime;
        }
        if (Tank1990.getGamePanel().getTime() < nextStage) {
            return;
        }
        waiting = false;
        currentStage++;
        aliveTanks.clear();
        for (int i = 0; i < 3; i++) {
            Tanks tank = randomTanks();
            StarterSpawn(tank, i);
            aliveTanks.add(tank);
        }
    }

    public void updateStage() {
        long time = Tank1990.getGamePanel().getTime();

        synchronized (aliveTanks) {
            // Tanklar üzerinde güvenli döngü
            for (int i = aliveTanks.size() - 1; i >= 0; i--) {
                Tanks tank = aliveTanks.get(i);
                if (tank == null) continue;

                if (tank.isAlive) {
                    updateEnemyTankLogic(tank, time);

                    List<TankBullet> bullets = tank.getActiveBullets();
                    synchronized (bullets) {
                        for (int j = bullets.size() - 1; j >= 0; j--) {
                            TankBullet bullet = bullets.get(j);

                            if (bullet == null || !bullet.getAlive()) {
                                bullets.remove(j);
                                continue;
                            }

                            bullet.move(currentMap);
                            checkBullet(bullet);
                            checkBullet(bullet, player1);
                            if (player2 != null) checkBullet(bullet, player2);
                        }
                    }
                } else {
                    addScore(tank.getName(), tank.getPoint());
                    tankOnField--;
                    aliveTanks.remove(i);
                    if (totalTanksSpawned < totalTank) spawnTank();
                }
            }
        }

        synchronized (currentMap) {
            currentMap.removeIf(comp -> comp == null || comp.isDestroyed());
        }
    }

    private void updateEnemyTankLogic(Tanks tank, long time) {
        if (time - tank.getLastMove() >= tank.getMoveDuration()) {
            GameObjects target = calculateTarget(tank, player1, player2, getEagle());
            tank.move(moveChances(tank, player1, player2), this);
            tank.setLastMove(time);
            tank.fire();
        }
    }

    public void checkBullet(Bullets bullets, Player player) {
        if (!bullets.getAlive()) {
            return;
        }

        Rectangle forBullet = new Rectangle(bullets.getX(), bullets.getY(), 8, 8);
        Rectangle forPlayer = new Rectangle(player.getX() * 64, player.getY() * 64, player.getSize(), player.getSize());
        if (forBullet.intersects(forPlayer)) {
            player.takeDamage(bullets.getPower());
            bullets.setAlive(false);
        }
    }

    public void checkBullet(Bullets bullets, Tanks tanks) {
        checkBullet(bullets);
        if (!bullets.getAlive()) {
            return;
        }

        Rectangle forBullet = new Rectangle(bullets.getX(), bullets.getY(), 8, 8);
        Rectangle forTank = new Rectangle(tanks.getX() * 64, tanks.getY() * 64, tanks.getSize(), tanks.getSize());
        if (forBullet.intersects(forTank)) {
            tanks.takeDamage(bullets.getPower());
            bullets.setAlive(false);
        }

    }

    public void checkBullet(Bullets bullets) {
        if (!bullets.getAlive()) return;

        Rectangle forBullet = new Rectangle(bullets.getX(), bullets.getY(), 8, 8);

        synchronized(currentMap) {
            for (Components comp : currentMap) {
                if (comp instanceof Destructible && !comp.isDestroyed()) {
                    Rectangle forcomp = new Rectangle(comp.getX() * 64, comp.getY() * 64, 64, 64);

                    if (forBullet.intersects(forcomp)) {
                        comp.takeDamage(bullets.getPower());
                        bullets.setAlive(false);
                        return;
                    }
                }
            }
        }
    }

    public boolean checkObstacleAt(int nextX, int nextY, int size) {
        Rectangle nextPos = new Rectangle(nextX, nextY, size, size);

        if (nextX < 0 || nextX > 832 - size || nextY < 0 || nextY > 832 - size) {
            return true;
        }

        synchronized(currentMap) {
            for (Components comp : currentMap) {
                if (comp instanceof UnWalkable && !comp.isDestroyed()) {
                    Rectangle compRect = new Rectangle(comp.getX() * 64, comp.getY() * 64, 64, 64);

                    if (nextPos.intersects(compRect)) {
                        return true;
                    }
                }
            }
        }

        Eagle eagle = getEagle();
        if (eagle != null && !eagle.isDestroyed()) {
            Rectangle eagleRect = new Rectangle(eagle.getX() * 64, eagle.getY() * 64, 64, 64);
            if (nextPos.intersects(eagleRect)) {
                return true;
            }
        }

        return false;
    }

    public Tanks randomTanks() {
        Random random = new Random();
        int tankType = random.nextInt(4);

        switch (tankType) {
            case 0:
                totalTanksSpawned++;
                tankOnField++;
                BasicTank newBasic = new BasicTank(0, 0, checkRed());
                return newBasic;
            case 1:
                totalTanksSpawned++;
                tankOnField++;
                FastTank newFast = new FastTank(0, 0, checkRed());
                return newFast;
            case 2:
                totalTanksSpawned++;
                tankOnField++;
                PowerTank newPower = new PowerTank(0, 0, checkRed());
                return newPower;
            case 3:
                totalTanksSpawned++;
                tankOnField++;
                ArmorTank newArmor = new ArmorTank(0, 0, checkRed());
                return newArmor;
            default:
                totalTanksSpawned++;
                tankOnField++;
                BasicTank newD = new BasicTank(0, 0, checkRed());
                return newD;
        }
    }

    public void spawnTank() {
        Random random = new Random();
        int tankType = random.nextInt(3);
        int[] point = getRandomSpawnPoint();
        switch (tankType) {
            case 0:
                totalTanksSpawned++;
                tankOnField++;
                BasicTank newBasic = new BasicTank(point[0], point[1], checkRed());
                aliveTanks.add(newBasic);
                break;
            case 1:
                totalTanksSpawned++;
                tankOnField++;
                FastTank newFast = new FastTank(point[0], point[1], checkRed());
                aliveTanks.add(newFast);
                break;
            case 2:
                totalTanksSpawned++;
                tankOnField++;
                PowerTank newPower = new PowerTank(point[0], point[1], checkRed());
                aliveTanks.add(newPower);
                break;
            case 3:
                totalTanksSpawned++;
                tankOnField++;
                ArmorTank newArmor = new ArmorTank(point[0], point[1], checkRed());
                aliveTanks.add(newArmor);
                break;
        }
    }

    public boolean checkRed() {
        if (totalTanksSpawned == 4 || totalTanksSpawned == 11 || totalTanksSpawned == 18) {
            return true;
        } else {
            Random random1 = new Random();
            int redchance = random1.nextInt(10);
            if (redchance < 3) {
                return true;
            }
            return false;
        }
    }

    public String getDirection(Tanks tanks, GameObjects target) {
        int dx = target.getX() - tanks.getX();
        int dy = target.getY() - tanks.getY();

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                return "right";
            } else {
                return "left";
            }
        } else {
            if (dy > 0) {
                return "down";
            } else {
                return "up";
            }
        }
    }

    public Map<String, Double> moveChances(Tanks tanks, Player player1, Player player2) {
        Map<String, Double> moveChance = new HashMap<>();
        double up = 0.0;
        double down = 0.0;
        double left = 0.0;
        double right = 0.0;

        Random random = new Random();

        GameObjects target = calculateTarget(tanks, player1, player2, getEagle());
        if (target.getX() > tanks.getX()) {
            if (!checkObstacle(tanks, "right")) {
                right += random.nextDouble() * 2.0;
            } else {
                right -= 10.0;
            }
        } else if (target.getX() < tanks.getX()) {
            if (!checkObstacle(tanks, "left")) {
                left += random.nextDouble() * 2.0;
            } else {
                left -= 10.0;
            }
        } else if (target.getX() == tanks.getX()) {
            if (target.getY() > tanks.getY()) {
                if (!checkObstacle(tanks, "down")) {
                    down += random.nextDouble() * 2.0;
                } else {
                    down -= 10.0;
                }
            } else if (target.getY() < tanks.getY()) {
                if (!checkObstacle(tanks, "up")) {
                    up += random.nextDouble() * 2.0;
                } else {
                    up -= 10.0;
                }
            }
        }

        moveChance.put("up", up);
        moveChance.put("down", down);
        moveChance.put("left", left);
        moveChance.put("right", right);
        return moveChance;
    }

    public Boosters randomBooster() {
        Random random = new Random();
        int chance = random.nextInt(6);
        Boosters newboost = null;

        switch (chance) {
            case 0:
                newboost = new Grenade();
                return newboost;
            case 1:
                newboost = new Helmet();
                return newboost;
            case 2:
                newboost = new Shovel();
                return newboost;
            case 3:
                newboost = new Star();
                return newboost;
            case 4:
                newboost = new Tank();
                return newboost;
            case 5:
                newboost = new Timer();
                return newboost;
            default:
                newboost = null;
                return null;

        }
    }

    public void StarterSpawn(Tanks tanks, int position) {
        if (position == 0) {
            tanks.setX(0);
            tanks.setY(0);
        } else if (position == 1) {
            tanks.setX(6);
            tanks.setY(0);
        } else if (position == 2) {
            tanks.setX(12);
            tanks.setY(0);
        } else {
            return;
        }

    }

    public void checkBoosters() {

        Rectangle forPlayer1 = new Rectangle(player1.getX() * 64, player1.getY() * 64, player1.getSize(),
                player1.getSize());
        for (Boosters boost : unclaimedBoosters) {
            boolean taken = false;
            System.out.println(boost.getX() + " " + boost.getY() + " " + boost.getSize());
            Rectangle forBoost = new Rectangle(boost.getX(), boost.getY(), boost.getSize(), boost.getSize());

            if (forBoost.intersects(forPlayer1)) {
                player1.getBoosters().add(boost);
                taken = true;
            } else if (player2 != null) {
                Rectangle forPlayer2 = new Rectangle(player2.getX() * 64, player2.getY() * 64, player2.getSize(),
                        player2.getSize());
                if (forBoost.intersects(forPlayer2)) {
                    player2.getBoosters().add(boost);
                    taken = true;
                }
            } else {
                for (Tanks tanks : aliveTanks) {
                    Rectangle forTank = new Rectangle(tanks.getX() * 64, tanks.getY() * 64, tanks.getSize(),
                            tanks.getSize());
                    if (forBoost.intersects(forTank)) {
                        tanks.getBoosters().add(boost);
                        taken = true;
                        break;
                    }
                }
            }

            if (taken) {
                claimedBoosters.add(boost);
                if (player1.getBoosters().contains(boost)) {
                    boost.addEffect(player1);
                } else if (player2 != null && player2.getBoosters().contains(boost)) {
                    boost.addEffect(player2);
                } else {
                    for (Tanks tanks : aliveTanks) {
                        if (tanks.getBoosters().contains(boost)) {
                            boost.addEffect(tanks);
                            break;
                        }
                    }
                }
            }

        }
    }

    public void BOOM() {
        for (Tanks tanks : aliveTanks) {
            tanks.setAlive(false);
        }
    }

    public int[] getRandomSpawnPoint() {
        Random random = new Random();
        int[][] spawnPoints = { { 0, 0 }, { 6, 0 }, { 12, 0 } };
        int[] point = spawnPoints[random.nextInt(spawnPoints.length)];
        return point;
    }

    public void nextStage() {
        currentStage++;
        tankOnField = 0;
        aliveTanks.clear();
        unclaimedBoosters.clear();
        loadStage(currentStage);
    }

    public static Components createComponents(char c, int x, int y) {
        switch (c) {
            case 'T':
                return new TankSpawn(x, y);
            case 'P':
                return new PlayerSpawn(x, y);
            case 'b':
                return new BrickWall(x, y);
            case 's':
                return new SteelWall(x, y);
            case 't':
                return new Trees(x, y);
            case 'w':
                return new Water(x, y);
            case 'i':
                return new Ice(x, y);
            case 'l':
                return new LeftStair(x, y);
            case 'r':
                return new RightStair(x, y);
            case 'e':
                return new Eagle(x, y);
            default:
                return null;
        }
    }

    public List<Components> firstMap() { // CHECK MATRIX
        List<Components> tempMap = new ArrayList<>();
        String[] matrix = {
                "000s000s00000", // 1
                "tb0s000b0b0b0", // 2
                "tb0tttbb0bsb0", // 3
                "t00b00bb0s00i", // 4
                "t00b0bs00btbs", // 5
                "tt0bbb00sbt00", // 6
                "bbbbbttsbbtb0", // 7
                "000stb0b0b0b0", // 8
                "sbws0b0b0wwbw", // 9
                "wb0b0bbb0bsbw", // 10
                "tb0b0bbb0000w", // 11
                "wb0000000b0bw", // 12
                "wb0b0rel0bbb0", // 13
        };

        for (int i = 0; i < matrix.length; i++) { // Y
            String row = matrix[i];
            for (int j = 0; j < row.length(); j++) { // X
                char c = row.charAt(j);
                Components newComp = createComponents(c, j, i);
                if (newComp != null) {
                    tempMap.add(newComp);
                }
            }
        }
        return tempMap;
    }

    public Components randomComp(int x, int y) {
        Random random = new Random();
        int chance = random.nextInt(100);
        if (chance < 50) {
            return null;
        } else if (chance < 60) {
            return new BrickWall(x, y);
        } else if (chance < 70) {
            return new SteelWall(x, y);
        } else if (chance < 80) {
            return new Trees(x, y);
        } else if (chance < 90) {
            return new Water(x, y);
        } else {
            return new Ice(x, y);
        }
    }

    public List<Components> randomizeMap() {
        List<Components> tempMap = new ArrayList<>();
        int width = 13;
        int height = 13;

        Components newcomp = null;
        for (int i = 0; i < height; i++) { // Y
            for (int j = 0; j < width; j++) { // X
                if (j == 5 && i == 12) {
                    newcomp = new RightStair(j, i);
                } else if (j == 6 && i == 12) {
                    newcomp = new Eagle(j, i);
                } else if (j == 7 && i == 12) {
                    newcomp = new LeftStair(j, i);
                } else if (i == 0 && (j == 0 || j == 6 || j == 12)) {
                    newcomp = new TankSpawn(j, i);
                } else if (i == 12 && (j == 4 || j == 8)) {
                    newcomp = new PlayerSpawn(j, i);
                } else {
                    newcomp = randomComp(j, i);
                }

                if (newcomp != null) {
                    tempMap.add(newcomp);
                }
            }
        }
        return tempMap;
    }

    public Eagle getEagle() {
        for (Components comp : currentMap) {
            if (comp instanceof Eagle) {
                return (Eagle) comp;
            }
        }
        return null;
    }

    public void loadStage() {
        if (currentStage == 1) {
            currentMap = firstMap();
        } else {
            currentMap = randomizeMap();
        }
    }

    public void loadStage(int stage) {
        if (!currentMap.isEmpty()) {
            currentMap.clear();
        }
        if (stage == 1) {
            currentMap = firstMap();
        } else {
            currentMap = randomizeMap();
        }
    }

    public GameObjects calculateTarget(Tanks tanks, Player player1, Player player2, Eagle eagle) {
        double distPlayer1 = calculateDistance(tanks, player1);
        double distPlayer2 = 100000;
        if (player2 != null) {
            distPlayer2 = calculateDistance(tanks, player2);
        }
        double distEagle = calculateDistance(tanks, eagle);

        double min = 1000;
        GameObjects target = eagle;

        if (distPlayer1 < min) {
            min = distPlayer1;
            target = player1;
        }
        if (distPlayer2 < min) {
            min = distPlayer2;
            target = player2;
        }
        if (distEagle < min) {
            min = distEagle;
            target = eagle;
        }

        return target;
    }

    public double calculateDistance(Tanks tanks, Player player) {
        double tankMidX = tanks.getX() + tanks.getSize() / 2.0;
        double tankMidY = tanks.getY() + tanks.getSize() / 2.0;

        double playerMidX = player.getX() + player.getSize() / 2.0;
        double playerMidY = player.getY() + player.getSize() / 2.0;

        double distx = tankMidX - playerMidX;
        double disty = tankMidY - playerMidY;
        return Math.sqrt(distx * distx + disty * disty);
    }

    public double calculateDistance(Tanks tanks, Eagle eagle) {
        double tankMidX = tanks.getX() + tanks.getSize() / 2.0;
        double tankMidY = tanks.getY() + tanks.getSize() / 2.0;

        double eagleMidX = eagle.getX() + eagle.getSize() / 2.0;
        double eagleMidY = eagle.getY() + eagle.getSize() / 2.0;

        double distx = tankMidX - eagleMidX;
        double disty = tankMidY - eagleMidY;
        return Math.sqrt(distx * distx + disty * disty);
    }

    public double calculateDistance(Bullets bullets, Player player) {
        double bulletMidX = bullets.getX() + bullets.getSize() / 2.0;
        double bulletMidY = bullets.getY() + bullets.getSize() / 2.0;

        double playerMidX = player.getX() + player.getSize() / 2.0;
        double playerMidY = player.getY() + player.getSize() / 2.0;

        double distx = bulletMidX - playerMidX;
        double disty = bulletMidY - playerMidY;
        return Math.sqrt(distx * distx + disty * disty);
    }

    public double calculateDistance(Bullets bullets, Tanks tanks) {
        double bulletMidX = bullets.getX() + bullets.getSize() / 2.0;
        double bulletMidY = bullets.getY() + bullets.getSize() / 2.0;

        double tankMidX = tanks.getX() + tanks.getSize() / 2.0;
        double tankMidY = tanks.getY() + tanks.getSize() / 2.0;

        double distx = bulletMidX - tankMidX;
        double disty = bulletMidY - tankMidY;
        return Math.sqrt(distx * distx + disty * disty);
    }

    public double calculateDistance(Bullets bullets, Components comp) {
        double bulletMidX = bullets.getX() + bullets.getSize() / 2.0;
        double bulletMidY = bullets.getY() + bullets.getSize() / 2.0;

        double compMidX = comp.getX() + comp.getSize() / 2.0;
        double compMidY = comp.getY() + comp.getSize() / 2.0;

        double distx = bulletMidX - compMidX;
        double disty = bulletMidY - compMidY;
        return Math.sqrt(distx * distx + disty * disty);
    }

    public double calculateDistance(Boosters boosters, Player player) {
        double boosterMidX = boosters.getX() + boosters.getSize() / 2;
        double boosterMidY = boosters.getY() + boosters.getSize() / 2;

        double playerMidX = player.getX() + player.getSize() / 2.0;
        double playerMidY = player.getY() + player.getSize() / 2.0;

        double distx = boosterMidX - playerMidX;
        double disty = boosterMidY - playerMidY;
        return Math.sqrt(distx * distx + disty * disty);
    }

    public double calculateDistance(Boosters boosters, Tanks tanks) {
        double boosterMidX = boosters.getX() + boosters.getSize() / 2;
        double boosterMidY = boosters.getY() + boosters.getSize() / 2;

        double tankMidX = tanks.getX() + tanks.getSize() / 2.0;
        double tankMidY = tanks.getY() + tanks.getSize() / 2.0;

        double distx = boosterMidX - tankMidX;
        double disty = boosterMidY - tankMidY;
        return Math.sqrt(distx * distx + disty * disty);
    }

    public List<Tanks> getAliveTanks() {
        return aliveTanks;
    }

    public List<Components> getComponents() {
        return currentMap;
    }

    public boolean checkObstacle(Player player, String direction) {
        int mapX = player.getX() / 64;
        int mapY = player.getY() / 64;

        switch (direction) {
            case "up":
                return containsComp(mapX, mapY - 1);
            case "down":
                return containsComp(mapX, mapY + 1);
            case "left":
                return containsComp(mapX - 1, mapY);
            case "right":
                return containsComp(mapX + 1, mapY);
            default:
                return true;
        }
    }

    public boolean checkObstacle(Tanks tanks, String direction) {
        int mapX = tanks.getX();
        int mapY = tanks.getY();

        switch (direction) {
            case "up":
                return containsComp(mapX, mapY - 1);
            case "down":
                return containsComp(mapX, mapY + 1);
            case "left":
                return containsComp(mapX - 1, mapY);
            case "right":
                return containsComp(mapX + 1, mapY);
            default:
                return true;
        }
    }

    public boolean containsComp(int xcord, int ycord) {
        if (xcord < 0 || xcord >= 13 || ycord < 0 || ycord >= 13) {
            return true;
        }

        for (Components comp : currentMap) {
            if (comp instanceof UnWalkable) {
                if (comp.getX() == xcord && comp.getY() == ycord) {
                    return true;
                }
            }

        }

        return false;
    }

    public void addScore(String name, int scoretoAdd) {
        int currentScore = totalScore.getOrDefault(name, 0);
        totalScore.put(name, currentScore + scoretoAdd);
    }

    public Map<String, Integer> getScore() {
        return totalScore;
    }

    public static StageControl getStage() {
        return sc;
    }
}