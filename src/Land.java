import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.*;
abstract class  Land  extends JPanel  {
    private int ID;
    private Land left,right,above,below;
    private Empire owner;

    public Land(){
        ID=0;
        left=null;
        right=null;
        above=null;
        below=null;
        owner = null;
    }
    public Land(int identifier){
        if (identifier < 0) throw new IllegalArgumentException("Tile ID must be non-negative");
        ID=identifier;
        left=null;
        right=null;
        above=null;
        below=null;
        owner = null;
    }

    public void setRight(Land land){
        right=land;
    }

    public void setLeft(Land land){
        left=land;
    }

    public void setAbove(Land land){
        above=land;
    }

    public void setBelow(Land land){
        below=land;
    }

    public int getID(){
        return ID; 
    }
    public void setID(int num){
        if (num < 0) throw new IllegalArgumentException("Tile ID must be non-negative");
        ID=num;
    }

    public ArrayList<Land> getAdjTiles(){
        ArrayList<Land> list = new ArrayList<>();
        if (above != null) list.add(above);
        if (right != null) list.add(right);
        if (below != null) list.add(below);
        if (left != null) list.add(left);
        return list;
    }

    public Empire getOwner() 
    {
        return owner;
    }

    public void setOwner(Empire owner)
    {
        this.owner = owner;
    }

    @Override
    protected final void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTerrain(g);
        if (owner != null) {
            TileArt.drawFlag(g, getWidth(), getHeight(), owner.getColor());
            g.setColor(owner.getColor());
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
        }
        if (this instanceof Improvable && ((Improvable) this).getBonus() >= 1.0) {
            g.setColor(Color.YELLOW);
            g.fillOval(getWidth() - 9, getHeight() - 9, 7, 7);
        }
    }

    // Subclasses supply terrain art; the parent retains ownership and overlays.
    protected abstract void drawTerrain(Graphics g);
}
