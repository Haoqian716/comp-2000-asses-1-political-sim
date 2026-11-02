import java.awt.*;

public class TileArt {

    private TileArt() {
    }

    public static void drawForest(Graphics g, int width, int height) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        // Team artwork uses coordinates on a 100 by 100 tile.
        // Scale fresh coordinates for each paint so resizing cannot accumulate.
        g.setColor(new Color(100, 65, 23));
        int trunkWidth = Math.max(1, width * 10 / 100);
        int trunkHeight = Math.max(1, height * 25 / 100);
        g.fillRect(width * 25 / 100, height * 75 / 100, trunkWidth, trunkHeight);
        g.fillRect(width * 65 / 100, height * 75 / 100, trunkWidth, trunkHeight);
        int[] xs = {width * 5 / 100, width * 30 / 100, width * 50 / 100,
            width * 70 / 100, width * 95 / 100};
        int[] ys = {height * 75 / 100, height * 15 / 100, height * 60 / 100,
            height * 15 / 100, height * 75 / 100};
        g.setColor(new Color(31, 80, 44));
        g.fillPolygon(xs, ys, xs.length);
    }

    public static void drawPlains(Graphics g, int width, int height) {
        g.setColor(new Color(110, 170, 70));
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(70, 130, 45));
        int bladeH = Math.max(2, height / 6);
        for (int i = 1; i <= 4; i++) {
            int bx = width * i / 5;
            g.drawLine(bx, height - 1, bx, height - 1 - bladeH);
        }
    }

    public static void drawMountain(Graphics g, int width, int height) {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, width, height);
        int[] xs = {width * 10 / 100, width * 35 / 100, width * 48 / 100,
            width * 60 / 100, width * 90 / 100};
        int[] ys = {height * 95 / 100, height * 20 / 100, height * 50 / 100,
            height * 10 / 100, height * 95 / 100};
        g.setColor(Color.BLACK);
        g.drawPolyline(xs, ys, xs.length);
    }

    public static void drawFlag(Graphics g, int width, int height, Color factionColour) {
        g.setColor(new Color(80, 50, 25));
        int poleX = width / 8;
        g.fillRect(poleX, height / 8, Math.max(2, width / 16), height * 3 / 4);
        g.setColor(factionColour);
        int flagW = width * 5 / 8;
        int flagH = height / 3;
        g.fillRect(poleX + width / 16, height / 8, flagW, flagH);
    }

    public static void drawUnit(Graphics g, int width, int height, Color factionColour) {
        int head = Math.max(3, height / 5);
        g.setColor(new Color(230, 190, 150));
        g.fillOval(width / 2 - head / 2, height / 10, head, head);
        g.setColor(factionColour);
        g.fillRect(width / 4, height / 10 + head, width / 2, height / 3);
        g.setColor(new Color(60, 45, 35));
        int legW = Math.max(2, width / 8);
        int legH = height / 5;
        g.fillRect(width / 2 - legW, height * 3 / 4, legW, legH);
        g.fillRect(width / 2, height * 3 / 4, legW, legH);
    }
}
