package sneps.gui;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Base;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Variable;
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
	
	public static void visualizeNodes(ArrayList<Node> nodes) throws IOException {
		Stage stage = new Stage();
		WebView wv = new WebView();
		
		//-------------------------------------------------------------------------------------------------
		String data = "document.body.innerHTML += Viz('digraph { nodesep=0.5; ranksep=2.5; bgcolor=gray20;";
		File f = new File("bin/sneps/gui/displayData.html");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write("<!DOCTYPE html>");
        bw.write("<html>");
        bw.write("<head>");
        bw.write("<title>Test</title>");
        bw.write("<style>body {background-color: #333333;}</style>");
        bw.write("<script src='viz.js'></script>");
        bw.write("</head>");
        bw.write("<body>");
        bw.write("<script>");
        bw.write(data);
        
		for(int i = 0; i<nodes.size(); i++) {
			Node n = nodes.get(i);
			if(n.getTerm() instanceof Molecular) {
				Molecular molNode = (Molecular) n.getTerm();
				DownCableSet dcs = molNode.getDownCableSet();
				Hashtable<String, DownCable> downCables = dcs.getDownCables();
				for(Entry<String, DownCable> entry : downCables.entrySet()) {
					String rname = entry.getKey();
					//System.out.println(rname);
					DownCable dc = entry.getValue();
					NodeSet cableNodes = dc.getNodeSet();
					for(int j = 0; j<cableNodes.size(); j++) {
						Node x = cableNodes.getNode(j);
						String nodeShape = null;
						if(x.getTerm() instanceof Molecular) {
							nodeShape = " " + x.getIdentifier() + " [style=filled,color=dodgerblue];";
						}
						else if(x.getTerm() instanceof Base) {
							nodeShape = " " + x.getIdentifier() + " [style=filled,color=yellow];";
						}
						else if(x.getTerm() instanceof Variable) {
							nodeShape = " " + x.getIdentifier() + " [style=filled,color=green];";
						}
						
						String nodeName = x.getIdentifier();
						String relation= "[style=filled, color=red, label=\"" + rname + "\", fontcolor=red]";
						String molShape = " " + molNode.getIdentifier() + " [style=filled,color=dodgerblue];";
						String dotSyntax = " " + molNode.getIdentifier() + " -> " + nodeName + relation + ";";
						bw.write(nodeShape);
						bw.write(molShape);
						bw.write(dotSyntax);
					}
				}
			}else if(n.getTerm() instanceof Base) {
				String nodeShape = " " + n.getIdentifier() + " [style=filled,color=yellow];";
				bw.write(nodeShape);
			}else if(n.getTerm() instanceof Variable) {
				String nodeShape = " " + n.getIdentifier() + " [style=filled,color=green];";
				bw.write(nodeShape);
			}
		}
		
		bw.write("}');");
		bw.write("</script></body></html>");
		bw.close();
		
		//-----------------------------------------------------------------------------------------------------------
		String url = Main.class.getResource("displayData.html").toExternalForm();
		
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());
        
		wv.getEngine().load(url);
		wv.prefWidthProperty().bind(stage.widthProperty());
		wv.prefHeightProperty().bind(stage.heightProperty());
	    Group root = new Group(wv);
	    Scene scene = new Scene(root, 600, 300);  
	    stage.setTitle("Node Set"); 
	    stage.setScene(scene); 
	    stage.show(); 
	    
	}
}