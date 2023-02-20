package dynamikColorTiles;




import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import someMath.CollectionManipulation;
import someMath.DirectedWeightedGraph;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;



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
   		
    	bic = (x,y)->
    	{

    		int idNrSrc = tileArray[x][y];
    		Color srcCol = colorSpace.getColorByNr(idNrSrc);

    		/*TODO:
   			 * In a Circle every Color has only one Destiny.
   			 * So the next line makes sense. (catchRandom)
   			 * I need to adjust if it ever want to have the possibility of many destinies.
    		 */

    		Set<Pair<Double, Color>> set = dg.getDestiniesOf(srcCol);
    		Pair<Double,Color> desCol = CollectionManipulation.catchRandomElementOfSet(set);
    		int idNrDes = colorSpace.getNrOfColor(desCol.getValue());
    		Set<Point> susceptibles = findSusceptiblesCoords(idNrDes, x, y, tileArray, removedTilesCoords); 
    		//System.out.println(susceptible.size()+" Susceptibles at Position: "+x+","+y);
    		changeColNrOfSusceptible(susceptibles, idNrSrc, copy);
    	};
    	walkThruArray(bic);

    	bic = (x,y)->{tileArray[x][y]=copy[x][y];};
    	walkThruArray(bic);
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

    void changeColNrOfSusceptible(Set<Point> susceptibles, int idNr, int[][] tileArray)
    {

    	Color c = colorSpace.getColorByNr(idNr);
    	Set<Pair<Double, Color>> destinysOfC = dg.getDestiniesOf(c);
    	Pair<Double, Color> oneDestiny = CollectionManipulation.catchRandomElementOfSet(destinysOfC);
    	double probability = oneDestiny.getKey();
    	/* TODO:
    	 * The above must be adjusted if there gone be many Destinies.
    	 * Also other lines of Code might be not working well then.
    	*/
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