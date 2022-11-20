package dynamikColorTiles;


import javafx.scene.canvas.*;
import javafx.scene.paint.*;


public class TileCanvas
{

    private final int tileNr;

    private final Canvas canvas;

    private final int tileWidth, tileHeight, tileNrHorizontal, tileNrVertical;


    public TileCanvas(int tileWidth, int tileHeight, int tileNrHorizontal, int tileNrVertical)
    {

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileNrHorizontal = tileNrHorizontal;
        this.tileNrVertical = tileNrVertical;

        tileNr = tileNrVertical*tileNrHorizontal;
        canvas = new Canvas(tileWidth*tileNrHorizontal, tileHeight*tileNrVertical);
    }


    public void setColorOnTile(int x, int y, Color c)
    {

        int startX = x*tileWidth;
        int startY = y*tileHeight;

        canvas.getGraphicsContext2D().setFill(c);
        canvas.getGraphicsContext2D().fillRect(startX, startY, startX+tileWidth, startY+tileHeight);
    }

    public Canvas getCanvas(){return canvas;}

    public int getTileNr(){return tileNr;}
}