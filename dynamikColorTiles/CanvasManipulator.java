package dynamikColorTiles;




import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import someMath.CollectionManipulation;
import someMath.DirectedWeightedGraph;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;



public class CanvasManipulator
{
        
	int tileChangeRate = 0;
	
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
	private Set<Point> neighboursDiff;
	private int [][] tileArray;
    private TileCanvas tileCanvas;

    public CanvasManipulator(HBox root, int tileNrHorizontal, int tileNrVertical)
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
    	
    	neighboursDiff = new HashSet<Point>();
    	
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
    				Point p = new Point(xx, yy);
    			
    				neighboursDiff.add(p);
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

   		cycleNr++;

   		for (int x = 0; x < tileNrHorizontal; x++)
    	{
   			for (int y = 0; y < tileNrVertical; y++)
    		{
               changeColNrOfPoint(x,y, copy);
    		}
    	}
   		
   		System.out.println("Change Rate: " + tileChangeRate);
   		tileChangeRate = 0;
   		tileArray = Arrays.stream(copy).map(int[]::clone).toArray(int[][]::new);
    }

    public void drawArray()
    {

    	//System.out.println("Thread is on the javaFX Application Thread: "+Platform.isFxApplicationThread());
    	//System.out.println("Drawing Cycles Nr.: "+cycleNr);
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

    //Complicated Heart of the Algorithm.
    private void changeColNrOfPoint(int x, int y, int[][] tileArray)
    {
    	
    	//Map<Color, Double> sumOfPointersOfColor = new HashMap<>();
    	
    	int colorNrOfCenter = tileArray[x][y];
    	Color colorOfCenter = colorSpace.getColorByNr(colorNrOfCenter);
    	Set<Color> destiny = dg.whoPointsToThisVertex(colorOfCenter);
    	
    	for(Point neighbour: neighbourPoints(x,y))
    	{
    		    		
    		int colorNrOfNeighbour = tileArray[neighbour.x][neighbour.y];
    		Color colorOfNeighbour = colorSpace.getColorByNr(colorNrOfNeighbour);
    		boolean isDestiny = destiny.contains(colorOfNeighbour);
    	
    		if(isDestiny)
    		{
    			
    			tileChangeRate++;
    			tileArray[x][y] = colorNrOfNeighbour;
    			break;
    		}
    	}
    			
    		
    	/*
    	{    		
   			double weight = dg.getWeightOfConnection(colorOfNeighbour, colorOfCenter);
    			
    		if(sumOfPointersOfColor.containsKey(colorOfNeighbour))
    		{
    			double newSumWeight = sumOfPointersOfColor.get(colorOfNeighbour)+weight;
    			sumOfPointersOfColor.remove(colorOfNeighbour);
   				sumOfPointersOfColor.put(colorOfNeighbour, newSumWeight);
   			}
   			else sumOfPointersOfColor.put(colorOfNeighbour, weight);
   		}

    	
    	if(sumOfPointersOfColor.isEmpty())return;

    	List<Pair<Color, Double>> layers = new ArrayList<>();

    	for(Color color: sumOfPointersOfColor.keySet())
    	{
    		
    		double sumWeight = sumOfPointersOfColor.get(color);
    		layers.add(new Pair<Color, Double>(color, sumWeight));
    	}

    	List<Double> stripedLayers = CollectionManipulation.getRidOfTheGeneric(layers);
    	double z = CollectionManipulation.randomNrBoundBetween(stripedLayers);
    	
    	int n = CollectionManipulation.betweenWhichElements(z, layers);
    	int k = colorSpace.getNrOfColor(layers.get(n).getKey());
    	tileArray[x][y] = k;
    	*/
    }

    public int getTileWidth() {return tileWidth;}

    public int getTileHeight() {return tileHeight;}

    public Set<Point> neighbourPoints(int x, int y)
    {

    	Set<Point> points = new HashSet<>();

    	for(Point p: neighboursDiff)
    	{

    		int xNeighbour = p.x + x;
    		int yNeighbour = p.y + y;

    		//Torus
    		xNeighbour = Math.floorMod(xNeighbour, tileNrHorizontal);
    		yNeighbour = Math.floorMod(yNeighbour, tileNrVertical);

    		Point neighbour = new Point(xNeighbour,yNeighbour);
    		points.add(neighbour);
    	}

    	if(points.size()>neighboursDiff.size())Platform.exit();
    	
    	return points;
    }
    
    public Canvas getCanvas() {return tileCanvas.getCanvas();}
}