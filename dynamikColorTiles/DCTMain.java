package dynamikColorTiles;


import javafx.application.*;

import javafx.event.*;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javafx.stage.Stage;



public class DCTMain extends Application
{

    private int tileNrHorizontal = 200;
    private int tileNrVertical = 150;

    private final int rangeMax = 5;
    private final int rangeSpan = 2*rangeMax+1;
    
    private final int colorCountMax = 256;
    private final int colorCountMin = 2;
    
    private int colorCount = 14;
    private int rangeBegin = -1;
    private int rangeEnd = 1;
    private float probability = 1.0f;

    HBox root = new HBox();

    Label rangeBeginLbl = new Label("Range begin");
    Label rangeEndLbl = new Label("Range end");
        
    Label colorCountLbl = new Label("Nr. of Colors");
    TextField ccTxtField = new TextField(String.valueOf(colorCount));

    HBox ccBox = new HBox();

    Label ratioLbl = new Label("Ratio");
    TextField ratioTxtField = new TextField(String.valueOf(probability));

    int ntcTileWidth = 20;
    int ntcTileHeight = ntcTileWidth;
    TileCanvas neighbourTC = new TileCanvas(ntcTileWidth, ntcTileHeight, rangeSpan, rangeSpan);
    Color alphaColorNTC = Color.YELLOW;
    Color betaColorNTC = Color.BLUE;
    Color centerColorNTC = Color.RED;
    
    boolean [][] ntcStates = new boolean[rangeSpan][rangeSpan];
    
    HBox ratioHBox = new HBox();
    VBox adjustmentVBox = new VBox();

    private static int aniInstCount = 0;

    private CanvasSupplier cs;
    
    private boolean halt = false;
    
    Runnable drawAndCompute =()->
	{
	
	
		cs.setConfigData(colorCount, tileNrHorizontal, tileNrVertical, ntcStates, probability);
		cs.setupNeighbourDelta();	
		cs.setColorGraph();
		cs.initTileData();
		cs.resetCycleNr();
		
		while(!halt)
		{
				
			Thread dd = new Thread(()-> cs.drawArray());
			
			//Platform.runLater(dd) is on 
			//the JavaFX Application Thread.
			//It is executed in x Time. x < 17 ms.
			Platform.runLater(dd);

			while(dd.isAlive());//<-Semicolon.
			
   			cs.computeTileData();
   			
		}
	};
		
   	Thread t;
    
    private static final String colorCountError = "colorCount too big or too small.";
    private static final String rangeBeginError = "Range-begin amount too big.";
    private static final String rangeEndError = "Range-end amount too big.";
    private static final String rangeBeginEqualToRangeEndError = "range-Begin can not be equal to range-End";    
    private static final String rangeSpanTooBigError = "The span of Range is too Big";
    private static final String ratioOutOfBounds = "Ratio must be bigger then Zero and equal or smaller the One.";
    private static final String noError = "OK";

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
    	        	
        Scene scene = new Scene(root,1100,600);
        primaryStage = new Stage();
        Button startBtn = new Button("Start");
        Stage finalPrimaryStage = primaryStage;
        
        EventHandler<ActionEvent> eventH0 = (event)->
        {
        	
        	System.out.println("Start event!");
            startBtn.setText("Session: " + aniInstCount);
            
            //No need to ask for aliveness.
        	if(t!=null)
        	{
        		
        		new Thread(() -> halt = true).start();//It needs to be killed by another Thread.

        		//This loop Blocks the Thread 
        		//until t is really Dead.
        		//Then later t starts again.
        		//If t is null t initializes 
        		//and starts any way.
        		while(t.isAlive());//<-Semicolon.					
        	}
        	
            fetchAndDisplayData(finalPrimaryStage);
        	t = new Thread(drawAndCompute);
        	t.start();
        };
        startBtn.setOnAction(eventH0);

        neighbourTC.getCanvas().setOnMouseClicked(event -> 
        {
        	

            int x = ((int)(event.getX()))/(ntcTileWidth);
            int y = ((int)(event.getY()))/(ntcTileHeight);

            System.out.println("(x, y) = ("+x +", "+y+")");
            if(!(x==5&&y==5))
            {

            	ntcStates[x][y] = !ntcStates[x][y];

            	if(ntcStates[x][y])neighbourTC.setColorOnTile(x, y, alphaColorNTC);
            	else neighbourTC.setColorOnTile(x, y, betaColorNTC);
            }
            gridLines();
        });    	

        ccBox.getChildren().addAll(colorCountLbl, ccTxtField);

        ratioHBox.getChildren().addAll(ratioLbl, ratioTxtField);

        
        adjustmentVBox.getChildren().addAll(ccBox, ratioHBox, startBtn, neighbourTC.getCanvas());
        setupStates(ntcStates);
        setupGrid(ntcStates);
        
       	cs = new CanvasSupplier(root, tileNrHorizontal, tileNrVertical);

       	root.getChildren().addAll(adjustmentVBox, cs.getCanvas());

       	primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void setupStates(boolean[][]states)
    {
    	
		for(int n=0;n<rangeSpan;n++)
    	{
    		for(int m=0;m<rangeSpan;m++)
    		{
    			int x = n-(rangeSpan-1)/2;
    			int y = m-(rangeSpan-1)/2;
		
    			states[n][m]=false;
    			if(x*x<=1&&y*y<=1)states[n][m]= true;//Ring.
    			if(x==0&&y==0)states[n][m]=false;//middle.
    		}
    	}
    }
    
    private void setupGrid(boolean[][]states)
    {
    	// Fills Canvas complete and over paints everything.	
		for(int n=0;n<rangeSpan;n++)
    	{
    		for(int m=0;m<rangeSpan;m++)
    		{
    			    	
    			boolean isCenter = (m==5)&&(n==5);
    			if(states[n][m])neighbourTC.setColorOnTile(n, m, alphaColorNTC);
    			else neighbourTC.setColorOnTile(n, m, betaColorNTC);
    			
    			if(isCenter)neighbourTC.setColorOnTile(n, m, centerColorNTC);
    		}
    	}
		gridLines();
    }
    
    private void gridLines()
    {
    	
		for(int n=0;n<=(rangeSpan+1)*ntcTileWidth;n+=ntcTileWidth)neighbourTC.drawVerticalLine(n);
		for(int n=0;n<=(rangeSpan+1)*ntcTileHeight;n+=ntcTileHeight)neighbourTC.drawHorizontalLine(n);
    }
    
    private void fetchAndDisplayData(Stage stage)
    {
    	    	
		halt = false;

		aniInstCount++;
    	
    	colorCount = Integer.parseInt(ccTxtField.getText());
        probability = Float.parseFloat(ratioTxtField.getText());
        
        String inputState = inputIsOK(colorCount, probability);
        
        if(inputState.equals(noError))
        {
        	
           	setTitle(stage, "Colors: " + colorCount
                   	+ " Probability: "
                   	+ probability
           			+ "H: " + tileNrHorizontal
           			+ "W: " + tileNrVertical);
       		        }
        else new InputErrorMsgStage(inputState);
    }

    private void setTitle(Stage stage, String title)
    {
        stage.setTitle(title);
    }
        
    private String inputIsOK(int colorCount, float ratio)
    {
    	if(colorCount>colorCountMax||colorCount<colorCountMin)return colorCountError;
    	if(Math.abs(rangeBegin)>rangeMax)return rangeBeginError;
    	if(Math.abs(rangeEnd)>rangeMax)return rangeEndError;
    	if(rangeBegin==rangeEnd)return rangeBeginEqualToRangeEndError;
    	if(-rangeBegin+rangeEnd>(rangeSpan-1))return rangeSpanTooBigError;
    	
    	if(ratio<=0||ratio>1)return ratioOutOfBounds;
    	
    	return noError;
    }

    public void stop()
    {
    	Platform.exit();
    }
}