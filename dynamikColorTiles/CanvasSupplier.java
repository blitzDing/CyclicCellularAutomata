package dynamikColorTiles;




import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import someMath.DirectedWeightedGraph;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;




public class CanvasSupplier
{
        
	float probability;
                
	final HBox root;
        
	private int cycleNr = 0;
	private int tileWidth =3;
	private int tileHeight = tileWidth;
	private int tileNrHorizontal;
	private int tileNrVertical;
	private final int stndrtWeight = 1;
	private ColorSpace colorSpace;
	private int colorCount;
	private DirectedWeightedGraph<Color> dg;
	private boolean [][] nStates;
	private Set<List<Integer>> neighboursDiff;
	private int [][] tileArray;
    private TileCanvas tileCanvas;

    public CanvasSupplier(HBox root, int tileNrHorizontal, int tileNrVertical)
    {

    	this.root = root;
      	
      	this.tileCanvas = new TileCanvas(tileWidth, tileHeight, tileNrHorizontal, tileNrVertical);
    }

    public void setConfigData(int colorCount, int tileNrHorizontal, int tileNrVertical, boolean[][] nStates
    		, float probability)
    {
    	
      	this.colorCount = colorCount;
      	this.colorSpace = new ColorSpace(colorCount, ColorSpace.ColorType.arbitrary);

      	this.tileNrHorizontal = tileNrHorizontal; 
      	this.tileNrVertical = tileNrVertical;
      	this.tileArray = new int[tileNrHorizontal][tileNrVertical];
      	
      	this.nStates = nStates;
      	
      	this.probability = probability;	
    }
    
    public void setupNeighbourDelta()
    {
    	
    	neighboursDiff = new HashSet<List<Integer>>();
    	
    	int xSpan = nStates.length;
    	int ySpan = nStates[0].length;
    	
    	for(int x=0;x<xSpan;x++)
    	{
    		for(int y=0;y<ySpan;y++)
    		{
    			int xx = x-(xSpan-1)/2;
    			int yy = y-(ySpan-1)/2;
    			
    			if(nStates[x][y])
    			{
    				List<Integer> list = new ArrayList<>();
    				list.add(xx);
    				list.add(yy);
    			
    				neighboursDiff.add(list);
    			}
    		}
    	}
    } 
   //neighboursDiff = CollectionManipulation.cartesianProduct(intSetList);
    
    public void setColorGraph()
    {
    	
      	//Circle graph for now
      	Set<Color> colorSet = new HashSet<>();
      	colorSet.addAll(colorSpace.getColorList());
      	this.dg = new DirectedWeightedGraph<Color>();
      	Color lastColor = colorSpace.getColorByNr(colorCount-1);
      	Color firstColor = colorSpace.getColorByNr(0);
      	dg.connect(lastColor,firstColor, probability);
      	for(int n=0;n<colorCount-1;n++)
      	{

      		Color col = colorSpace.getColorByNr(n);
      		Color nxtCol = colorSpace.getColorByNr(n+1);
                
      		dg.connect(col, nxtCol, stndrtWeight);
      	}
    }
    
    public void initTileData()
    {
 
    	
    	BiConsumer<Integer, Integer> bic = (x,y)->tileArray[x][y] = (int)(Math.random()*colorCount);
    	walkThruArray(bic);
    }
    
    public void resetCycleNr()
    {
    	cycleNr = 0;
    }
    
    public void computeTileData()
    {
    
		try 
		{
			Thread.sleep(250);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

    	int[][] copy = new int[tileNrHorizontal][tileNrVertical];
		BiConsumer<Integer, Integer> bic = (x,y)->copy[x][y]=tileArray[x][y];
		walkThruArray(bic);
		

    	Set<Point> removedTilesCoords = new HashSet<>();
   		cycleNr++;
   		
    	bic = (x,y)-> changeColNrOfTile(x,y,copy);
    	walkThruArray(bic);

    	bic = (x,y)->{tileArray[x][y]=copy[x][y];};
    	walkThruArray(bic);
    }

        
    public void changeColNrOfTile(int x, int y, int[][] copy)
    {
    	
    	Color colorCenter = colorSpace.getColorByNr(tileArray[x][y]);
    	Set<Color> incomingConnections = dg.whoPointsToThisVertex(colorCenter);
    	
    	for(Point p: neighbourCoords(x,y))
    	{	
    		int colNrNeighbour = tileArray[p.x][p.y];
    		Color colNeighbour = colorSpace.getColorByNr(colNrNeighbour);


    		//Needs addjustment if i want more then one 'incomming' Vertexes.
        	//Hmmm?
    		if(incomingConnections.contains(colNeighbour))
    		{
    			
    			double probability = dg.getWeightOfConnection(colNeighbour, colorCenter);
    			
            	int b = (int)(1000*probability);
            	
        		int z = (int)(Math.random()*1000);
        		if(z<b)
        		{
        			copy[x][y]=colNrNeighbour;
        			break;//<-important.
        		}
    		}
    	}
    }
    
    public void drawArray()
    {

    	//System.out.println("Thread is on the javaFX Application Thread: "+Platform.isFxApplicationThread());
    	System.out.println("Drawing Cycles Nr.: "+cycleNr);
    	
    	BiConsumer<Integer, Integer> bic = (x,y)->
    	{                        			
    		int idNr = tileArray[x][y];
   			Color c = colorSpace.getColorByNr(idNr);
   			tileCanvas.setColorOnTile(x,y, c);	
    	};
    	walkThruArray(bic);
    }

    public Set<Point> findSusceptiblesCoords(int searchIDNr, int x, int y, int[][] tileArray, Set<Point> removedTilesCoords)
    {

    	Set<Point> susceptibles = new HashSet<>();

    	for(Point p: neighbourCoords(x,y))
    	{

    		int idNr = tileArray[p.x][p.y];
    		if(idNr==searchIDNr&&!removedTilesCoords.contains(p))
    		{
    			susceptibles.add(p);             
    			removedTilesCoords.add(p);
    		}
    	}

    	return susceptibles;
    }
    
    public int getTileWidth() {return tileWidth;}
    
    public int getTileHeight() {return tileHeight;}

    public Set<Point> neighbourCoords(int x, int y)    
    {

    	Set<Point> points = new HashSet<>();
            
    	for(List<Integer> l: neighboursDiff)
    	{

    		int xNeighbour = l.get(0) + x;
    		int yNeighbour = l.get(1) + y;

    		//Torus
    		xNeighbour=Math.floorMod(xNeighbour,tileNrHorizontal);
    		yNeighbour=Math.floorMod(yNeighbour, tileNrVertical);


    		Point p = new Point(xNeighbour,yNeighbour);
    		points.add(p);
    	}

    	return points;
    }

    public void walkThruArray(BiConsumer<Integer, Integer> bic)
    {
    	for(int x = 0; x< tileNrHorizontal; x++)
    	{
    		for(int y = 0; y< tileNrVertical; y++)bic.accept(x, y);;
    	}    
    }
    
    public Canvas getCanvas() {return tileCanvas.getCanvas();}
}