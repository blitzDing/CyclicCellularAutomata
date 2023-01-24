package dynamikColorTiles;


import javafx.scene.canvas.*;
import javafx.scene.paint.*;


public class TileCanvas
{

    private final int tileNr;

    private final Canvas canvas;

    private final int tileWidth, tileHeight;//, tileNrHorizontal, tileNrVertical;


    public TileCanvas(int tileWidth, int tileHeight, int tileNrHorizontal, int tileNrVertical)
    {

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        //this.tileNrHorizontal = tileNrHorizontal;
        //this.tileNrVertical = tileNrVertical;

        tileNr = tileNrVertical*tileNrHorizontal;
        canvas = new Canvas(tileWidth*tileNrHorizontal, tileHeight*tileNrVertical);
    }


    public void setColorOnTile(int x, int y, Color c)
    {

        double startX = x*tileWidth;
        double startY = y*tileHeight;

    	canvas.getGraphicsContext2D().setFill(c);
        canvas.getGraphicsContext2D().fillRect(startX, startY, tileHeight, tileWidth);
    }
    
    public void drawVerticalLine(int x)
    {
    	
    	double startX = x+0.5;
    	double startY = 0.5;
    	
    	double endX = x;
    	double endY = canvas.getHeight();

    	GraphicsContext gc = canvas.getGraphicsContext2D();

    	gc.setLineWidth(2.0f);
    	gc.setFill(Color.BLACK);
    	gc.moveTo(startX, startY);
        gc.lineTo(endX, endY);
        gc.stroke();    
    }

    public void drawHorizontalLine(int y)
    {
    	
    	double startX = 0.5;
    	double startY = y+0.5;
    	
    	double endX = canvas.getWidth();
    	double endY = y;

    	GraphicsContext gc = canvas.getGraphicsContext2D();

    	gc.setLineWidth(2.0f);
    	gc.setFill(Color.BLACK);
    	gc.moveTo(startX, startY);
        gc.lineTo(endX, endY);
        gc.stroke();
    }

    public Canvas getCanvas(){return canvas;}

    public int getTileNr(){return tileNr;}
}