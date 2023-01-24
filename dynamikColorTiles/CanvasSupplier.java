package dynamikColorTiles;

import java.awt.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import javafx.stage.Stage;


import someMath.CollectionManipulation;

import someMath.DirectedWeightedGraph;



public class CanvasSupplier
{

	Stage stage;
        
	int rangeBegin;
	int rangeEnd;
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
	private Set<Integer> cordParts;
	private Set<List<Integer>> neighboursDiff;
	private int [][] tileArray;
    private TileCanvas tileCanvas;

    public CanvasSupplier(Stage stage, HBox root, int tileNrHorizontal, int tileNrVertical)
    {

    	this.stage = stage;    
    	this.root = root;
      	
      	this.tileCanvas = new TileCanvas(tileWidth, tileHeight, tileNrHorizontal, tileNrVertical);
    }

    public void setConfigData(int colorCount, int tileNrHorizontal, int tileNrVertical, int rangeBegin, int rangeEnd
    		, float probability)
    {
    	
      	this.colorCount = colorCount;
      	this.colorSpace = new ColorSpace(colorCount, ColorSpace.ColorType.arbitrary);

      	this.tileNrHorizontal = tileNrHorizontal; 
      	this.tileNrVertical = tileNrVertical;
      	this.tileArray = new int[tileNrHorizontal][tileNrVertical];
      	

      	this.rangeBegin = rangeBegin;
      	this.rangeEnd = rangeEnd;
      	this.probability = probability;	
    }
    
    public void setRange()
    {
      	cordParts = new HashSet<>(
      			IntStream.rangeClosed(rangeBegin, rangeEnd).boxed().collect(Collectors.toList()));
      	
      	List<Set<Integer>> intSetList = new ArrayList<>();
      	intSetList.add(cordParts);
      	intSetList.add(cordParts);
      	neighboursDiff = CollectionManipulation.cartesianProduct(intSetList);

    }
    
    public void setColorGraph()
    {
    	
      	//Circle graph for now
      	Set<Color> colorSet = new HashSet<>();
      	colorSet.addAll(colorSpace.getColorList());
      	this.dg = new DirectedWeightedGraph<Color>(colorSet);
      	Color lastColor = colorSpace.getColorByNr(colorCount-1);
      	Color firstColor = colorSpace.getColorByNr(0);
      	dg.connect(lastColor,firstColor,stndrtWeight);
      	for(int n=0;n<colorCount-1;n++)
      	{

      		Color col = colorSpace.getColorByNr(n);
      		Color nxtCol = colorSpace.getColorByNr(n+1);
                
      		dg.connect(col, nxtCol, stndrtWeight);
      	}
    }
    
    public void initTileData()
    {
    	for(int x = 0; x< tileNrHorizontal; x++)
    	{
    		for(int y = 0; y< tileNrVertical; y++)tileArray[x][y] = (int)(Math.random()*colorCount);
    	}    
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
    		
    	int[][] copy = Arrays.stream(tileArray).map(int[]::clone).toArray(int[][]::new);

    	Set<Point> removedTilesCoords = new HashSet<>();
   		cycleNr++;
    	for (int x = 0; x < tileNrHorizontal; x++)
    	{
   
    		for (int y = 0; y < tileNrVertical; y++)
    		{
     
    		   int idNrSrc = tileArray[x][y];
    		   Color srcCol = colorSpace.getColorByNr(idNrSrc);
   			   //In a Circle every Color has only one Destiny.
   			   //So the next line makes sense. (catchRandom)                        
   			   Color desCol = CollectionManipulation.catchRandomElementOfSet(dg.getDestinysOf(srcCol));     
   			   int idNrDes = colorSpace.getNrOfColor(desCol);     
   			   Set<Point> susceptible = findSusceptiblesCoords(idNrDes, x, y, tileArray, removedTilesCoords); 
    		   //System.out.println(susceptible.size()+" Susceptibles at Position: "+x+","+y);
               changeColNrOfSusceptible(susceptible, idNrSrc, copy);
    	   }    
   	   }

    	   //if(Arrays.deepEquals(copy,tileArray))fin();
       tileArray = Arrays.stream(copy).map(int[]::clone).toArray(int[][]::new); 	
    }

        
    public void drawArray()
    {

    	//System.out.println("Thread is on the javaFX Application Thread: "+Platform.isFxApplicationThread());
    	System.out.println("Drawing Cycles Nr.: "+cycleNr);
    	for(int x=0;x<tileNrHorizontal;x++)
    	{
    		for(int y=0;y<tileNrVertical;y++)            
    		{                    
    			
    			int idNr = tileArray[x][y];
    			Color c = colorSpace.getColorByNr(idNr);
    			tileCanvas.setColorOnTile(x,y, c);
    		}
    	}
    }

    void changeColNrOfSusceptible(Set<Point> susceptibles, int idNr, int[][] tileArray)
    {

    	int b = (int)(1000*probability);

    	for(Point p: susceptibles)
        {
    		int z = (int)(Math.random()*1000);
    		if(z<b)tileArray[p.x][p.y]=idNr;
        }
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
    
    public Canvas getCanvas() {return tileCanvas.getCanvas();}
}