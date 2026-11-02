import java.awt.Graphics;

public class Plains extends Land {
    @Override
    protected void drawTerrain(Graphics g) {
        TileArt.drawPlains(g, getWidth(), getHeight());
    }
}
