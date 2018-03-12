package sneps.gui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FXController implements Initializable {
	
	@FXML
	private TextArea console;
	
	@FXML
	private Label conOutput;
	
	@FXML
	private ScrollPane logScroll;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		console.setOnKeyPressed(new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent keyEvent) {
		        if (keyEvent.getCode() == KeyCode.ENTER)  {
		            String text = console.getText();
		            
		            conOutput.setText(conOutput.getText() + text);
		            
		            // clear text
		            console.setText("");
		            ScrollDown();
		            
		        }
		    }
		});
		
	}
	
	public void ScrollDown() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask()
		{
		        public void run()
		        {
		        	logScroll.setVvalue(1.0);     
		        }

		};
		timer.schedule(task,20l);
	}
	
	
	
	
}
