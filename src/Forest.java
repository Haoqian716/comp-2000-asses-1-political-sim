import java.awt.Graphics;

public class Forest extends Land implements Improvable {
    private int level;
    public void levelUp() { if (level < 2) level++; }
    public double getBonus() { return 0.2 + 0.4 * level; }
    @Override
    protected void drawTerrain(Graphics g) {
        TileArt.drawForest(g, getWidth(), getHeight());
    }
}
