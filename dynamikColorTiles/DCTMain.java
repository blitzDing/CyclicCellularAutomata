package dynamikColorTiles;


import javafx.application.*;

import javafx.event.*;

import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.stage.Stage;


public class DCTMain extends Application
{

    private int tileNrHorizontal = 200;
    private int tileNrVertical = 150;
    private int colorCount = 15;
    private int rangeBegin = -1;
    private int rangeEnd = 1;
    private float ratio = 1.0f;

    HBox root = new HBox();

    Label rangeBeginLbl = new Label("Range begin");
    Label rangeEndLbl = new Label("Range end");
    TextField rBTxtField = new TextField("-1");
    TextField rETxtField = new TextField("1");

    Label colorCountLbl = new Label("Nr. of Colors");
    TextField ccTxtField = new TextField("14");

    HBox ccBox = new HBox();

    Label ratioLbl = new Label("Ratio");
    TextField ratioTxtField = new TextField(String.valueOf(ratio));

    HBox ratioBox = new HBox();

    VBox adjustmentBox = new VBox();

    HBox rangeBox = new HBox();

    private static int aniInstCount = 0;

    AnimationThrd move = null;

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
            startBtn.setText("Session: "+(aniInstCount-1));

            alterGUIAndNewAni(finalPrimaryStage);
        };
        startBtn.setOnAction(eventH0);

        rangeBox.getChildren().addAll(rangeBeginLbl,rBTxtField,rangeEndLbl,rETxtField);

        ccBox.getChildren().addAll(colorCountLbl, ccTxtField);

        ratioBox.getChildren().addAll(ratioLbl, ratioTxtField);

        adjustmentBox.getChildren().addAll(rangeBox, ccBox, ratioBox, startBtn);

        root.getChildren().addAll(adjustmentBox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void alterGUIAndNewAni(Stage stage)
    {

        //Stop old move if possible
        if(aniInstCount>0)
        {
            Runnable r = new AlterGUI();
            Platform.runLater(r);
        }

        colorCount = Integer.parseInt(ccTxtField.getText());
        rangeBegin = Integer.parseInt(rBTxtField.getText());
        rangeEnd = Integer.parseInt(rETxtField.getText());
        ratio = Float.parseFloat(ratioTxtField.getText());
        
        String inputState = DCTMain.inputIsOK(colorCount, rangeBegin, rangeEnd, ratio);
        if(inputState.equals(noError))
        {
        	Runnable r2 = new NewAni(stage);
        	Platform.runLater(r2);
        	aniInstCount++;
        }
        else new InputError(inputState);
    }

    private void setTitle(Stage stage, String title)
    {
        stage.setTitle(title);
    }
    
    private static final String colorCountError = "colorCount too big or too small.";
    private static final String rangeBeginError = "Range-begin amount too big.";
    private static final String rangeEndError = "Range-end amount too big.";
    private static final String rangeBeginEqualToRangeEndError = "range-Begin can not be equal to range-End";    
    private static final String rangeSpanTooBigError = "The span of Range is too Big";
    private static final String ratioOutOfBounds = "Ratio must be bigger then Zero and equal or smaller the One.";
    private static final String noError = "OK";
    
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

    private class NewAni implements Runnable
    {

        Stage stage;

        public NewAni(Stage stage)
        {
            this.stage = stage;
        }

        @Override
        public void run()
        {

            colorCount = Integer.parseInt(ccTxtField.getText());
            rangeBegin = Integer.parseInt(rBTxtField.getText());
            rangeEnd = Integer.parseInt(rETxtField.getText());
            ratio = Float.parseFloat(ratioTxtField.getText());
            
            
           	setTitle(stage,"Colors: "+colorCount
                   	+" Ratio: "
                   	+ratio
                   	+" Range: ("
                   	+rangeBegin
                   	+", "
                  	+rangeEnd+")");

            String name = String.valueOf(aniInstCount);
           	move = new 
           			AnimationThrd(name, tileNrHorizontal, tileNrVertical, colorCount, rangeBegin, rangeEnd, ratio);

            root.getChildren().add(move.getCanvas());
            move.start();
        }
    }

    public void stop()
    {
    	Platform.exit();
    }
    
    private class AlterGUI implements Runnable
    {

        @Override
        public void run()
        {

            Canvas canvas = move.getCanvas();
            root.getChildren().remove(canvas);
            move.unRun();
        }
    }
}