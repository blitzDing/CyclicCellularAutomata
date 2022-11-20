package dynamikColorTiles;


import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import someMath.CollectionManupulation;
import someMath.DirectedWeightedGraph;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class AnimationThrd extends Thread
{

    private int cycleNr = 0;
    private final int tileWidth =3;
    private final int tileHeight = tileWidth;
    private final int tileNrHorizontal;
    private final int tileNrVertical;
    private final int stndrtWeight = 1;
    private final ColorSpace colorSpace;
    private final int colorCount;
    private float ratio;
    private final DirectedWeightedGraph<Color> dg;
    			//DirectedWeightedGraph
    private Set<Integer> cordParts;
    private Set<List<Integer>> neighboursDiff;
    private int [][] tileArray;
    private final TileCanvas tileCanvas;
    private boolean exit = false;
    private final String name;

    private final Runnable draw = ()->
    {
        drawArray(tileArray);
    };

    @SuppressWarnings("unchecked")
	public AnimationThrd(String name, int tilesHorizontal, int tilesVertical, int colorCount, int rangeBegin, int rangeEnd, float ratio)
    {

        this.name = name;
        this.ratio = ratio;
        cordParts = new HashSet<>
        (IntStream.rangeClosed(rangeBegin, rangeEnd).boxed().collect(Collectors.toList()));
        neighboursDiff = CollectionManupulation.cartesianProduct(cordParts,cordParts);

        tileNrHorizontal = tilesHorizontal;
        tileNrVertical = tilesVertical;
        this.tileCanvas = new TileCanvas(tileWidth, tileHeight, tileNrHorizontal, tileNrVertical);
        this.colorCount = colorCount;
        this.colorSpace = new ColorSpace(colorCount, ColorSpace.ColorType.arbitrary);
        this.tileArray = new int[tileNrHorizontal][tileNrVertical];


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

        initArray();
    }

    public void initArray()
    {


        for(int x = 0; x< tileNrHorizontal; x++)
        {
            for(int y = 0; y< tileNrVertical; y++)
            {
                tileArray[x][y] = (int)(Math.random()*colorCount);
            }
        }
    }

    @Override
    public void run()
    {

        while(!exit)
        {
            int[][] copy = Arrays.stream(tileArray).map(int[]::clone).toArray(int[][]::new);

            Set<Point> removedTilesCoords = new HashSet<>();

            try
            {
                Platform.runLater(draw);
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            cycleNr++;

            for (int x = 0; x < tileNrHorizontal; x++)
            {
                for (int y = 0; y < tileNrVertical; y++)
                {

                    int idNrSrc = tileArray[x][y];
                    Color srcCol = colorSpace.getColorByNr(idNrSrc);
                    //In a Circle every Color has only one Destiny.
                    //So the next line makes sense.
                    Color desCol = CollectionManupulation.
                    		catchRandomElementOfSet(dg.getDestinysOf(srcCol));
                    int idNrDes = colorSpace.getNrOfColor(desCol);

                    Set<Point> susceptible = findSusceptiblesCoords(idNrDes, x, y, tileArray, removedTilesCoords);
                    //System.out.println(susceptible.size()+" Susceptibles at Position: "+x+","+y);
                    changeSusceptibles(susceptible, idNrSrc, copy);
                }
            }

            //if(Arrays.deepEquals(copy,tileArray))fin();
            tileArray = Arrays.stream(copy).map(int[]::clone).toArray(int[][]::new);
        }
    }

    void drawArray(int[][] tileArray)
    {

        System.out.println("Thread: "+name+". Drawing Cycles Nr.: "+cycleNr);
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

    void changeSusceptibles(Set<Point> susceptibles, int idNr, int[][] tileArray)
    {

        int b = (int)(1000*ratio);


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

    public Canvas getCanvas(){ return tileCanvas.getCanvas(); }

    public void unRun()
    {
        exit = true;
        Toolkit.getDefaultToolkit().beep();
        System.out.println("Fin");
    }


}