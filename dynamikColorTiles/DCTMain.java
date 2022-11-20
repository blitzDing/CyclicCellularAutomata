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

        //Start new move
        Runnable r2 = new NewAni(stage);
        Platform.runLater(r2);
        aniInstCount++;
    }

    private void setTitle(Stage stage, String title)
    {
        stage.setTitle(title);
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