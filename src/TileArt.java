import java.awt.*;

public class TileArt {

    private TileArt() {
    }

    public static void drawForest(Graphics g, int width, int height) {
        g.setColor(new Color(20, 90, 30));
        g.fillRect(0, 0, width, height);
        int treeW = Math.max(6, width / 3);
        int trunkW = Math.max(2, treeW / 5);
        int trunkH = Math.max(2, height / 6);
        drawTree(g, width / 4, height, treeW, trunkW, trunkH);
        drawTree(g, width / 2, height, treeW, trunkW, trunkH);
        drawTree(g, width * 3 / 4, height, treeW, trunkW, trunkH);
    }

    private static void drawTree(Graphics g, int cx, int baseY, int treeW, int trunkW, int trunkH) {
        g.setColor(new Color(80, 50, 25));
        g.fillRect(cx - trunkW / 2, baseY - trunkH, trunkW, trunkH);
        g.setColor(new Color(25, 130, 55));
        int canopyH = baseY / 2;
        int[] xs = {cx - treeW / 2, cx, cx + treeW / 2};
        int[] ys = {baseY - trunkH, baseY - trunkH - canopyH, baseY - trunkH};
        g.fillPolygon(xs, ys, 3);
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
        g.setColor(new Color(105, 105, 105));
        g.fillRect(0, 0, width, height);
        int peakH = height * 2 / 3;
        int[] xs = {0, width / 2, width};
        int[] ys = {height, height - peakH, height};
        g.setColor(new Color(70, 70, 70));
        g.fillPolygon(xs, ys, 3);
        int[] capX = {width / 2 - width / 8, width / 2, width / 2 + width / 8};
        int[] capY = {height - peakH / 2, height - peakH, height - peakH / 2};
        g.setColor(new Color(240, 240, 245));
        g.fillPolygon(capX, capY, 3);
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
