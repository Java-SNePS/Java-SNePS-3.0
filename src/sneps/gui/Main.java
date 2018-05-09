package sneps.gui;
import javafx.fxml.FXMLLoader;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		    primaryStage.setTitle("Java SNePS 3.0");
		    primaryStage.setScene(new Scene(root));   
		    primaryStage.show();
		    
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public static void visualizeNodes() {
		Stage stage = new Stage();
		WebView wv = new WebView();
		wv.getEngine().load("http://google.com");
	    Group root = new Group(wv);
	    Scene scene = new Scene(root, 600, 300);  
	    stage.setTitle("Node Set"); 
	    stage.setScene(scene); 
	    stage.show(); 
	}
}