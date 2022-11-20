package diffGeo;


import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;

import javafx.stage.Stage;



public class Main extends Application
{

    double width = 1500, height = 500;

    HBox root = new HBox();
    AniThread at = new AniThread(width, height);

    @Override
    public void start(Stage primaryStage) throws Exception
    {

        root.getChildren().add(at.getCanvas());

        primaryStage.setTitle("Diff Geo.");
        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();

        /*
            Remember:
                Display always begins after the start-Method exits.
                Before that nothing is shown.
         */

        at.start();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}