package sneps.gui;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.GroupLayout.Alignment;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sneps.exceptions.ContextNameDoesntExistException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.Base;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Variable;
import sneps.snebr.Controller;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;


public class Main extends Application {
	private static boolean isDark;
	private static int windows;
	
	@Override
	public void start(Stage primaryStage) {
		ButtonType dark = new ButtonType("Dark");
		ButtonType light = new ButtonType("Light");
		Alert a = new Alert(AlertType.NONE, "", dark, light);
		a.setTitle("Theme");
		a.setHeaderText("Please choose a theme");
		a.setResizable(false);
		a.setContentText("Please choose a theme");
		a.showAndWait().ifPresent(response -> {
		    if (response == dark) {
		    	try {
					isDark = true;
					Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
				    primaryStage.setTitle("Java SNePS 3.0");
				    primaryStage.setScene(new Scene(root));   
				    primaryStage.show();
				    
				} catch(Exception e) {
					e.printStackTrace();
				}
		    } else if (response == light) {
		    	try {
					isDark = false;
					Parent root = FXMLLoader.load(getClass().getResource("Main2.fxml"));
				    primaryStage.setTitle("Java SNePS 3.0");
				    primaryStage.setScene(new Scene(root));   
				    primaryStage.show();
				    
				} catch(Exception e) {
					e.printStackTrace();
				}
		    }
		});
	}

	public static boolean isDark() {
		return isDark;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public static void visualizeNodes(ArrayList<Node> nodes) throws IOException {
		Stage stage = new Stage();
		WebView wv = new WebView();
		
		//-------------------------------------------------------------------------------------------------
		String data = null;
		if(isDark) {
			data = "document.body.innerHTML += Viz('digraph { nodesep=0.5; ranksep=2.5; bgcolor=gray20;";
		}else {
			data = "document.body.innerHTML += Viz('digraph { nodesep=0.5; ranksep=2.5; bgcolor=none;";
		}
		File f = new File("bin/sneps/gui/displayData.html");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write("<!DOCTYPE html>");
        bw.write("<html>");
        bw.write("<head>");
        bw.write("<title>Test</title>");
        if(isDark) {
        	bw.write("<style>body {background-color: #333333;}</style>");
        }else {
        	bw.write("<style>body {background-color: #ffffff;}</style>");
        }
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
	
	public static void resolveConflicts(ArrayList<NodeSet> propSet) {
		ArrayList<Integer> tempHyps = new ArrayList<Integer>();
		int x = 0;
		int y = 0;
		windows = propSet.size();
		for(int i = 0; i<propSet.size(); i++) {
			NodeSet props = propSet.get(i);
			Stage stage = new Stage();
			Scene scene = new Scene(new VBox()); 
			String styles = null;
			if(isDark) {
				styles = Main.class.getResource("application.css").toExternalForm();
			}else if(!isDark) {
				styles = Main.class.getResource("application2.css").toExternalForm();
			}
			scene.getStylesheets().add(styles);
			AnchorPane pane3 = new AnchorPane();
			ListView<String> list = new ListView<String>();
			pane3.getChildren().add(list);
			
			for(int s = 0; s<props.size(); s++) {
				Node n = props.getNode(s);
				list.getItems().add(n.getIdentifier());
			}
			
			AnchorPane pane1 = new AnchorPane();
			Button btn = new Button("Delete Node");
			btn.getStyleClass().add("tab");
			pane1.getChildren().add(btn);
			btn.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					String identifier = list.getSelectionModel().getSelectedItem();
					Node x = null;
					try {
						x = Network.getNode(identifier);
					} catch (NodeNotFoundInNetworkException e) {
						e.printStackTrace();
					}
					tempHyps.add(x.getId());
					stage.close();
					windows--;
					if(windows == 0) {
						int[] hyps = new int[tempHyps.size()];
						for(int i = 0; i<hyps.length; i++) {
							hyps[i] = tempHyps.get(i);
							System.out.println(tempHyps.get(i));
						}
						PropositionSet propNodes = null;
						try {
							propNodes = new PropositionSet(hyps);
						} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							Controller.handleContradiction(propNodes, false);
						} catch (NodeNotFoundInNetworkException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NotAPropositionNodeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NodeNotFoundInPropSetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (DuplicatePropositionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ContextNameDoesntExistException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
				
			});
			
			AnchorPane pane2 = new AnchorPane();
			Label lbl = new Label("Please delete a node to resolve contradiction");
			lbl.setStyle("-fx-font-size: 24px;");
			lbl.getStyleClass().add("title");
			
			pane2.getChildren().add(lbl);
			
			btn.prefWidthProperty().bind(stage.widthProperty());
			lbl.prefWidthProperty().bind(stage.widthProperty());
			list.prefWidthProperty().bind(stage.widthProperty());
			
			((VBox)scene.getRoot()).getChildren().add(lbl);
			((VBox)scene.getRoot()).getChildren().add(list);
			((VBox)scene.getRoot()).getChildren().add(btn);
			
		    stage.setTitle("Resolve Contradiction");
		    stage.setScene(scene); 
		    stage.setWidth(600);
		    stage.setHeight(360);
			stage.setResizable(false);
			stage.setX(x);
			stage.setY(y);
			x += 100;
			y += 100;
			stage.show();
		}
	}
	
	public static void userAction(ArrayList<NodeSet> propSet) {
		ButtonType ignore = new ButtonType("Ignore");
		ButtonType resolve = new ButtonType("Resolve Conflict");
		ButtonType ca = new ButtonType("Cancel Assertion");
		
		Alert a = new Alert(AlertType.NONE, "", ignore, resolve, ca);
		a.setTitle("Contradiction Detected!");
		a.setHeaderText("Error asserting nodes!");
		a.setResizable(false);
		a.setContentText("Contradiction Detected");
		a.showAndWait().ifPresent(response -> {
		    if (response == resolve) {
		    	resolveConflicts(propSet);
		    }else if (response == ignore) {
		    	try {
					Controller.handleContradiction(null, true);
				} catch (NodeNotFoundInNetworkException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotAPropositionNodeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NodeNotFoundInPropSetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DuplicatePropositionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ContextNameDoesntExistException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }else if(response == ca) {
		    	try {
					Controller.handleContradiction(null, false);
				} catch (NodeNotFoundInNetworkException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotAPropositionNodeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NodeNotFoundInPropSetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DuplicatePropositionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ContextNameDoesntExistException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
	}
}