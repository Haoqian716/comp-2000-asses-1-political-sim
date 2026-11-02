import java.awt.Graphics;

public class Mountain extends Land implements Impassable {
    @Override
    protected void drawTerrain(Graphics g) {
        TileArt.drawMountain(g, getWidth(), getHeight());
    }
}
