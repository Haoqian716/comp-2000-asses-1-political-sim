import java.util.ArrayList;

import javax.swing.*;
abstract class  Land  extends JPanel  {
    int ID;
    Land left,right,above,below;
    public Land(){
        ID=0;
        left=null;
        right=null;
        above=null;
        below=null;
    }
    public Land(int identifier){
        if(identifier < 0){
            throw new IllegalArgumentException("Tile ID must be non-negative: " + identifier);
        }
        ID=identifier;
        left=null;
        right=null;
        above=null;
        below=null;
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

    public ArrayList<Land> getAdjTiles(){
        ArrayList<Land> list = new ArrayList<>();
        list.add(above);
        list.add(right);
        list.add(below);
        list.add(left);
        return list;
    }
}
