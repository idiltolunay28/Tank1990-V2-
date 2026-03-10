package Tank;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Eagle extends Components implements UnWalkable, Destructible, GameObjects {
    protected boolean isDestroyed;
    protected BufferedImage eagleImage;
    protected final int size = 64;

    Eagle(int x, int y) {
        super(x, y);
        this.isDestroyed = false;
        try {
            eagleImage = ImageIO.read(getClass().getResourceAsStream("/Resources/Eagle.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Eagle null ");
        }
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void takeDamage() {
        isDestroyed = true;
    }

    public void draw(Graphics g) {
        if (eagleImage != null) {
            g.drawImage(eagleImage, x * 64, y * 64, 64, 64, null);
        } else {
            g.setColor(Color.red);
            g.fillRect(x, y, 16, 16);
        }
    }

    public int getSize() {
        return size;
    }
}
