package dynamikColorTiles;


import javafx.application.*;

import javafx.event.*;

import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import javafx.stage.Stage;


public class DCTMain extends Application
{

    private int tileNrHorizontal = 200;
    private int tileNrVertical = 150;
    private int colorCount = 14;
    private int rangeBegin = -1;
    private int rangeEnd = 1;
    private float probability = 1.0f;

    HBox root = new HBox();

    Label rangeBeginLbl = new Label("Range begin");
    Label rangeEndLbl = new Label("Range end");
    
    Slider sliderV = new Slider(-5, 5, -1);   
    Slider sliderH = new Slider(-5, 5, 1);
    
    
    Label colorCountLbl = new Label("Nr. of Colors");
    TextField ccTxtField = new TextField(String.valueOf(colorCount));

    HBox ccBox = new HBox();

    Label ratioLbl = new Label("Ratio");
    TextField ratioTxtField = new TextField(String.valueOf(probability));

    HBox ratioHBox = new HBox();

    VBox adjustmentVBox = new VBox();

    HBox rangeHBox = new HBox();

    private static int aniInstCount = 0;

    private NewAnimation na;
    
    private boolean halt = false;
    
    Runnable drawAndCompute =()->
	{
	
	
		na.setConfigData(colorCount, tileNrHorizontal, tileNrVertical, rangeBegin, rangeEnd, 
	        		probability);
		na.setRange();	
		na.setColorGraph();
		na.initTileData();
		
		while(!halt)
		{
				
			Platform.runLater(() ->	na.drawArray());
			
			na.computeTileData();
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
    	
    	sliderV.setShowTickMarks(true);
    	sliderV.setShowTickLabels(true);
    	sliderV.setMajorTickUnit(1);
    	sliderV.setBlockIncrement(1);
    	sliderV.setSnapToTicks(true);
    	sliderV.valueProperty().addListener((obs, oldval, newVal) -> 
        sliderV.setValue(newVal.intValue()));
    	
    	sliderH.setShowTickMarks(true);
    	sliderH.setShowTickLabels(true);
    	sliderH.setMajorTickUnit(1);
    	sliderH.setBlockIncrement(1);
    	sliderH.setSnapToTicks(true);
    	sliderH.valueProperty().addListener((obs, oldval, newVal) -> 
        sliderH.setValue(newVal.intValue()));
    	
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
        	
        	t = new Thread(drawAndCompute);
        	t.start();

            fetchAndDisplayData(finalPrimaryStage);
        };
        startBtn.setOnAction(eventH0);

        rangeHBox.getChildren().addAll(rangeBeginLbl, sliderV, rangeEndLbl, sliderH);

        ccBox.getChildren().addAll(colorCountLbl, ccTxtField);

        ratioHBox.getChildren().addAll(ratioLbl, ratioTxtField);

        adjustmentVBox.getChildren().addAll(rangeHBox, ccBox, ratioHBox, startBtn);
        										//Dummy
       	na = new NewAnimation(finalPrimaryStage, root, tileNrHorizontal, tileNrVertical);

       	root.getChildren().addAll(adjustmentVBox, na.getCanvas());

       	
       	primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchAndDisplayData(Stage stage)
    {
    	    	
		halt = false;

		aniInstCount++;
    	
    	colorCount = Integer.parseInt(ccTxtField.getText());
        rangeBegin = (int) sliderV.getValue();
        rangeEnd = (int) sliderH.getValue();
        probability = Float.parseFloat(ratioTxtField.getText());
        
        String inputState = DCTMain.inputIsOK(colorCount, rangeBegin, rangeEnd, probability);
        
        if(inputState.equals(noError))
        {
        	
           	setTitle(stage, "Colors: " + colorCount
                   	+ " Probability: "
                   	+ probability
                   	+ " Range: ("
                   	+ rangeBegin
                   	+ ", "
                  	+ rangeEnd+")"
           			+ "H: " + tileNrHorizontal
           			+ "W: " + tileNrVertical);
       		        }
        else new InputErrorMsgStage(inputState);
    }

    private void setTitle(Stage stage, String title)
    {
        stage.setTitle(title);
    }
        
    private static String inputIsOK(int colorCount, int rangeBegin, int rangeEnd, float ratio)
    {
    	if(colorCount>256||colorCount<2)return colorCountError;
    	if(Math.abs(rangeBegin)>6)return rangeBeginError;
    	if(Math.abs(rangeEnd)>6)return rangeEndError;
    	if(rangeBegin==rangeEnd)return rangeBeginEqualToRangeEndError;
    	if(-rangeBegin+rangeEnd>10)return rangeSpanTooBigError;
    	
    	if(ratio<=0||ratio>1)return ratioOutOfBounds;
    	
    	return noError;
    }

    public void stop()
    {
    	Platform.exit();
    }
}