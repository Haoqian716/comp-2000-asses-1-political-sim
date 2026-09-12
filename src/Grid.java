import java.awt.*;
import java.util.*;
import javax.swing.*;
public class Grid extends JPanel{
    TreeMap<Integer,Land> gameMap;
    int gridSize;
    public <E extends Land> Grid(int width,int height, int gridSize){
        gameMap = new TreeMap<Integer,Land>();
        this.gridSize = gridSize;
        setSize(width,height);
        setLayout(null);;
        //System.out.println("grid tile Width: " + tileSizeWidth + " Height: " + tileSizeHeight);
        createGrid();

    }
    public void createGrid(){
        int tileSizeHeight,tileSizeWidth, tileID;
        tileSizeWidth=getWidth()/gridSize;
        tileSizeHeight=getHeight()/gridSize;
        tileID=1;
        Forest placeHolder;
        for(int y = 0;y<gridSize;y++){
            for(int x = 0;x<gridSize;x++){
                placeHolder=new Forest();
                placeHolder.ID=tileID;
                placeHolder.setSize(tileSizeWidth-1, tileSizeHeight-1);
                placeHolder.setLocation(x*tileSizeWidth , y*tileSizeHeight);
                add(placeHolder);
                addTile(tileID,placeHolder);

                if(tileExist(tileID-1)&& tileID%gridSize!=1){
                    getTile(tileID-1).setRight(placeHolder);
                    placeHolder.setLeft(getTile(tileID-1));
                }
                if(tileExist(tileID-gridSize)){
                    getTile(tileID-gridSize).setBelow(placeHolder);
                    placeHolder.setAbove(getTile(tileID-gridSize));
                }
                tileID++;
            }
        }
        Empire test=new Empire(Color.GREEN);
        test.addTile(getTile(1));
        test.updateTiles();
        for(int x=0;x<10;x++){
            claimRandomTile(test);
        }
        test.updateTiles();

    }

    public Land getTile(int ID){
        return gameMap.get(ID);
    }

    public void addTile(int identifier, Land land){
        gameMap.put(identifier, land);
    }

    public boolean tileExist(int ID){
		return gameMap.containsKey(ID);
	}
    
    public void update(){

    }
    public void claimRandomTile(Empire faction){
        ArrayList<Land> list = faction.getPlayableBorderTiles();
        if(list == null || list.isEmpty()){
            return;
        }
        Land borderTile, tileToclaim;
        //int num2 =0;
        borderTile=list.get((int)(Math.random()*list.size()));
        list=borderTile.getAdjTiles();
        int num=(int)(Math.random()*list.size());
        tileToclaim=null;

        while(faction.isOwner(tileToclaim)||!faction.isClaimable(tileToclaim)){
            // System.out.println("tile i'm chcking from: "+ borderTile.ID+" adjTile trying to take = " +num+ " is Owner = "+ faction.isOwner(tileToclaim) + " is claimable = "+ faction.isClaimable(tileToclaim));
            // if(tileToclaim!=null){
            //     System.out.println(tileToclaim.ID);
            // }
            num=(int)(Math.random()*list.size());
            try{
                tileToclaim=list.get(num);
            }catch(IndexOutOfBoundsException e){
                return;
            }
            // if(num2>=25){
            //     System.out.println("loop occured");
            //     return;
            // }
            // num2++;
        }
        //System.out.println("\ntile taken\n");
        faction.addTile(tileToclaim);
    }

    
}
