package dynamikColorTiles;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class InputError extends Stage 
{

	private int width = 450;
	private int height = 75;
	
	public InputError(String inputState)
	{
		super();
		
		HBox root = new HBox();
		
		Label msgLabel = new Label(inputState);
		msgLabel.setPrefWidth(width);
		msgLabel.setPrefHeight(height);
		msgLabel.setAlignment(Pos.CENTER);;
		
		root.getChildren().add(msgLabel);
		Scene scene = new Scene(root,width,height);
		

	    this.setScene(scene);
	    this.show();
	}
}