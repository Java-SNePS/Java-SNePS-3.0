package sneps.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import sneps.exceptions.CaseFrameAlreadyExistException;
import sneps.exceptions.CaseFrameCannotBeRemovedException;
import sneps.exceptions.CaseFrameWithSetOfRelationsNotFoundException;
import sneps.exceptions.CustomException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.exceptions.RelationDoesntExistException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.Semantic;
import sneps.network.classes.Wire;
import sneps.network.classes.term.Base;
import sneps.network.classes.term.Variable;

public class FXController implements Initializable {
	Network network = new Network();
	private double curX, curY;
	private ArrayList<Wire> wires = new ArrayList<Wire>();
	private CaseFrame curCF = null;
	
	@FXML
	private TextArea console;
	@FXML
	private Label conOutput, nodeDetails, relationDetails;
	@FXML
	private ScrollPane logScroll;
	@FXML
	private TextField newRN, newRT, newRA, newRL, baseNodeIdentPop, baseNodeSemTyPop,
	caseFrameSTN, baseNodeSemType, baseNodeID;
	@FXML
	private ListView<String> relationSetList, relationSetList1, cfRS, caseFramesList,
	caseFrameRelationList, nodesList, wiresList, variableNodesList, baseNodesList;
	@FXML
	private Group dragBaseNode, dragVarNode;
	@FXML
	private AnchorPane drawArea, mainAnchor, baseNodepopBox, varNodepopBox;
	@FXML
	private ScrollPane drawScroll;
	@FXML
	private MenuButton caseFrameChoice;


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
		
		nodesList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				String nodeName = nodesList.getSelectionModel().getSelectedItem();
				nodeDetails(nodeName);
				
			}
			
		});
		
		relationSetList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				String relationName = relationSetList.getSelectionModel().getSelectedItem();
				relationDetails(relationName);
				
			}
			
		});
		
		load();
		dragBaseNode();
		dragVariableNode();
		drawArea();
		drawScroll.setFitToHeight(true);
		drawScroll.setFitToWidth(true);
		updateNodesList();
		updateRelationSetList();
		updateCaseFramesList();
		
	}
	
	//SNePS Log Scroll down
	public void ScrollDown() {
		Timer timer = new Timer();
		TimerTask titask = new TimerTask()
		{
		        public void run()
		        {
		        	logScroll.setVvalue(1.0);     
		        }

		};
		timer.schedule(titask,20l);
	}
	
	
	//Draw Base Node
	public Group makeBaseNode(String identifier, String semType) {
		Group x = new Group();
		StackPane sp = new StackPane();
		Ellipse nodeBack = new Ellipse();
		nodeBack.setFill(Color.web("#f8ff1f"));
		nodeBack.setRadiusX(50.0);
		nodeBack.setRadiusY(40.0);
		Label txt = new Label(identifier);
		txt.setTextFill(Color.BLACK);
		sp.getChildren().add(nodeBack);
		sp.getChildren().add(txt);
		x.getChildren().add(sp);
		return x;
	}
	
	//Draw Var Node
	public Group makeVarNode(String name) {
		Group x = new Group();
		StackPane sp = new StackPane();
		Ellipse nodeBack = new Ellipse();
		nodeBack.setFill(Color.web("#00ff22"));
		nodeBack.setRadiusX(50.0);
		nodeBack.setRadiusY(40.0);
		Label txt = new Label(name);
		txt.setTextFill(Color.BLACK);
		sp.getChildren().add(nodeBack);
		sp.getChildren().add(txt);
		x.getChildren().add(sp);
		return x;
	}
	
	//Erase all drawn nodes
	public void eraseDrewNetwork() {
		drawArea.getChildren().clear();
	}
	
	
	//Drag Base Node to draw area
	public void dragBaseNode() {
		
		dragBaseNode.setOnMouseEntered(new EventHandler <MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dragBaseNode.setCursor(Cursor.HAND);
			}
		});
		
		dragBaseNode.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
                System.out.println("onDragDetected");
                Dragboard db = dragBaseNode.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString("BaseNode");
                db.setContent(content);
                event.consume();
				
			}
			
		});
		
	}

	
	//Drag Base Node to draw area
	public void dragVariableNode() {
		
		dragVarNode.setOnMouseEntered(new EventHandler <MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dragVarNode.setCursor(Cursor.HAND);
			}
		});
		
		dragVarNode.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
                System.out.println("onDragDetected");
                Dragboard db = dragVarNode.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString("VarNode");
                db.setContent(content);
                event.consume();
				
			}
			
		});
		
	}
	
	//Handling draw area
	public void drawArea() {
		
		drawArea.setOnDragOver(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                event.acceptTransferModes(TransferMode.ANY);
                if(db.getString() == "BaseNode") {
                	
                }
                event.consume();
            }
        });
		
       drawArea.setOnDragDropped(new EventHandler<DragEvent>() {

		@Override
		public void handle(DragEvent event) {
			Dragboard db = event.getDragboard();
            event.acceptTransferModes(TransferMode.ANY);
            if(db.getString() == "BaseNode") {
	            curX = event.getX();
	            curY = event.getY();
	            baseNodepopBox.setVisible(true);
            }
            else if(db.getString() == "VarNode") {
            	curX = event.getX();
	            curY = event.getY();
	            varNodepopBox.setVisible(true);
            }
		}  
       });
       
	}

	
	//Submit base node drawn in draw area
	public void submitExistBaseNode() {
		String identifier = baseNodesList.getSelectionModel().getSelectedItem();
		Node n = null;
		try {
			n = Network.getNode(identifier);
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Semantic s = n.getSemanticType();
		String semType = s.getSemanticType();
        Group x = makeBaseNode(identifier, semType);
        x.setLayoutX(curX-25);
        x.setLayoutY(curY-20);
        baseNodepopBox.setVisible(false);
        drawArea.getChildren().add(x);
        x.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				drawArea.getChildren().remove(x);
			}
        	
        });
	}
	
	//Submit base node drawn in draw area
		public void submitNewBaseNode() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
			String identifier = baseNodeIdentPop.getText();
			String semType = baseNodeSemTyPop.getText();
			Semantic semantic = new Semantic(semType);
			Node node = Network.buildBaseNode(identifier, semantic);
	        Group x = makeBaseNode(identifier, semType);
	        x.setLayoutX(curX-25);
	        x.setLayoutY(curY-20);
	        baseNodepopBox.setVisible(false);
	        baseNodeIdentPop.setText("");
	        baseNodeSemTyPop.setText("");
	        drawArea.getChildren().add(x);
	        updateNodesList();
	        x.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					drawArea.getChildren().remove(x);
				}
	        	
	        });
		}

	//Submit existing variable node drawn in draw area
		public void submitExistVariableNode() {
			String varNode = variableNodesList.getSelectionModel().getSelectedItem();
			Node n = null;
			try {
				n = Network.getNode(varNode);
			} catch (NodeNotFoundInNetworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String name = n.getIdentifier();
	        Group x = makeVarNode(name);
	        x.setLayoutX(curX-25);
	        x.setLayoutY(curY-20);
	        varNodepopBox.setVisible(false);
	        drawArea.getChildren().add(x);
	        updateNodesList();
	        x.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					drawArea.getChildren().remove(x);
				}
	        	
	        });
		}
		
	//Submit new variable node drawn in draw area
		public void submitNewVariableNode() {
			Node n = Network.buildVariableNode();
			String name = n.getIdentifier();
	        Group x = makeVarNode(name);
	        x.setLayoutX(curX-25);
	        x.setLayoutY(curY-20);
	        varNodepopBox.setVisible(false);
	        drawArea.getChildren().add(x);
	        updateNodesList();
	        x.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					drawArea.getChildren().remove(x);
				}
	        	
	        });
		}

	//Define relation menu-based
	public void defineRelation() {
		String name = newRN.getText();
		String type = newRT.getText();
		String adjust = newRA.getText();
		int limit = Integer.parseInt(newRL.getText());
		try {
			Network.defineRelation(name, type, adjust, limit);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Relation created");
			alert.setHeaderText("Relation is created successfully");
			alert.setContentText("The relation " + name + " is created successfully!");
			alert.showAndWait();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Relation NOT created");
			alert.setHeaderText("Relation is NOT created successfully");
			alert.setContentText("The relation " + name + " is NOT created successfully!");
			alert.showAndWait();
			e.printStackTrace();
		}
		
		newRN.setText("");
		newRT.setText("");
		newRA.setText("");
		newRL.setText("");
		
		updateRelationSetList();
		
	}
	
	
	//Undefine relation menu-based
	public void undefineRelation() {
		String selectedRelation = relationSetList.getSelectionModel().getSelectedItem();
		try {
			Network.undefineRelation(selectedRelation);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Relation Deleted");
			alert.setHeaderText("Relation Deleted Successfully");
			alert.setContentText("The relation " + selectedRelation + " is deleted successfully!");
			alert.showAndWait();
			updateRelationSetList();
		} catch (CaseFrameCannotBeRemovedException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Relation is NOT Deleted Successfully");
			alert.setContentText("The relation " + selectedRelation + " is NOT deleted successfully!");
			alert.showAndWait();
			e.printStackTrace();
		}
	}
	
	
	//Updates relation lists when a new one is added or deleted
	public void updateRelationSetList() {
		Hashtable<String, Relation> relations = Network.getRelations();
		relationSetList.getItems().clear();
		relationSetList1.getItems().clear();
		for (Entry<String, Relation> entry : relations.entrySet()) {
		    String key = entry.getKey();
		    relationSetList.getItems().add(key);
		    relationSetList1.getItems().add(key);

		}
	}

	//This method adds selected relations from the network to a create a new caseFrame with these relations/ Menu-based
	public void addToCFRS() {
		String selectedRelation = relationSetList1.getSelectionModel().getSelectedItem();
		if(selectedRelation != null) {
				cfRS.getItems().add(selectedRelation);
				relationSetList1.getItems().remove(selectedRelation);
		}
	}

	//This method removes selected relations from a case frame created from menu-based
	public void removeCFRS() {
		String selectedRelation = cfRS.getSelectionModel().getSelectedItem();
		if(selectedRelation != null) {
			relationSetList1.getItems().add(selectedRelation);
			cfRS.getItems().remove(selectedRelation);
		}
	}

	
	//Adds case frame to the network - menu-based
	public void submitCaseFrame() {
		String semanticType = caseFrameSTN.getText();
		LinkedList<Relation> caseFrameList = new LinkedList<Relation>();
		for(int i=0; i< cfRS.getItems().size(); i++){
			String rName = cfRS.getItems().get(i);
			try {
				Relation r = Network.getRelation(rName);
				caseFrameList.add(r);
			} catch (RelationDoesntExistException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			Network.defineCaseFrame(semanticType, caseFrameList);
			updateCaseFramesList();
			updateRelationSetList();
			cfRS.getItems().clear();
		} catch (CaseFrameAlreadyExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	//Update all list of case frames
	public void updateCaseFramesList() {
		caseFramesList.getItems().clear();
		caseFrameChoice.getItems().clear();
		Hashtable<String, CaseFrame> caseFrames = Network.getCaseFrames();
		for (Entry<String, CaseFrame> entry : caseFrames.entrySet()) {
		    String key = entry.getKey();
		    MenuItem item = new MenuItem(key);
		    caseFramesList.getItems().add(key);
		    item.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					try {
						CaseFrame cf = Network.getCaseFrame(item.getText());
						curCF = cf;
						LinkedList<Relation> relations = cf.getRelations();
						caseFrameRelationList.getItems().clear();
						for(Relation r : relations) {
							caseFrameRelationList.getItems().add(r.getName());
						}
						
					} catch (CaseFrameWithSetOfRelationsNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		    	
		    });
		    caseFrameChoice.getItems().add(item);
		}
	}

	//Undefine case frames - menu-based
	public void undefineCaseFrame() {
		String caseFrame = caseFramesList.getSelectionModel().getSelectedItem();
		Hashtable<String, CaseFrame> cframes = Network.getCaseFrames();
		CaseFrame cf = cframes.get(caseFrame);
		try {
			Network.undefineCaseFrame(cf.getId());
			updateCaseFramesList();
		} catch (CaseFrameCannotBeRemovedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Creates a base node
	public void buildBaseNode() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		String nodeName = baseNodeID.getText();
		String semType = baseNodeSemType.getText();
		Semantic semantic = new Semantic(semType);
		Node node = Network.buildBaseNode(nodeName, semantic);
		if(node == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Node was NOT created successfully!");
			alert.setContentText("ERROR: Acts cannot be base nodes!!!");
			alert.showAndWait();
		}else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Node created!");
			alert.setHeaderText("Node was created successfully!");
			alert.setContentText("The base node " + nodeName + " was created successfully!");
			alert.showAndWait();
			updateNodesList();
			baseNodeID.setText("");
			baseNodeSemType.setText("");
		}
		
	}
	
	public void updateNodesList() {
		Hashtable<String, Node> nodes = Network.getNodes();
		nodesList.getItems().clear();
		variableNodesList.getItems().clear();
		baseNodesList.getItems().clear();
		for (Entry<String, Node> entry : nodes.entrySet()) {
		    String key = entry.getKey();
		    Node n = entry.getValue();
		    String item = key;
		    nodesList.getItems().add(item);
		    if(n.getTerm() instanceof Variable) {
		    	variableNodesList.getItems().add(item);
		    }else if(n.getTerm() instanceof Base) {
		    	baseNodesList.getItems().add(item);
		    }
		}
	}

	public void createWire() {
		
		String rName = caseFrameRelationList.getSelectionModel().getSelectedItem();
		Relation r = null;
		try {
			r = Network.getRelation(rName);
		} catch (RelationDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String nodeName = nodesList.getSelectionModel().getSelectedItem();
		Node node = null;
		try {
			node = Network.getNode(nodeName);
		} catch (NodeNotFoundInNetworkException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Wire w = new Wire(r, node);
		wiresList.getItems().add(r.getName() + " " + nodeName);
		wires.add(w);
		
	}
	
	public void buildVN() {
		Network.buildVariableNode();
		updateNodesList();
	}
	
	public void buildMolecularNode() {
		try {
			Network.buildMolecularNode(wires, curCF);
			updateNodesList();
			wiresList.getItems().clear();
			wires.clear();
		} catch (CustomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void save() {
		try {
			Network.save("relations", "caseFrames", "nodes");
			System.out.println("saved");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void load() {
		try {
			Network.load("relations" , "caseFrames", "nodes");
			updateNodesList();
			updateCaseFramesList();
			updateRelationSetList();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void createDefaults() {
		try {
			Network.defineDefaults();
			updateRelationSetList();
		} catch (CustomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void nodeDetails(String identifier) {
		Node n = null;
		try {
			n = Network.getNode(identifier);
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(n.getTerm() instanceof Variable) {
			String syntactic = n.getSyntacticType();
			int id = n.getId();
			nodeDetails.setText("Node Identifier: " + identifier + "\n" + "Syntactic Type: " + syntactic + "\n" + "ID: " + id);
		}else {
			String semantic = n.getSemantic().getSemanticType();
			String syntactic = n.getSyntacticType();
			int id = n.getId();
			nodeDetails.setText("Node Identifier: " + identifier + "\n" + "Semantic Type: "
			+ semantic + "\n" + "Syntactic Type: " + syntactic + "\n" + "ID: " + id);
		}
	}
	
	public void relationDetails(String rname) {
		Relation r = null;
		try {
			r = Network.getRelation(rname);
		} catch (RelationDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String type = r.getType();
		String adjust = r.getAdjust();
		int limit = r.getLimit();
		boolean quantifier = r.isQuantifier();
		relationDetails.setText("Relation Name: " + rname + "\n" + "Type: " + type
		+ "\n" + "Adjust: " + adjust + "\n" + "Limit: " + limit + "\t" + "Quantifier: " 
		+ quantifier);
	}
	
	public void resetNetwork() {
		Network.getRelations().clear();
		Network.getNodes().clear();
		Network.getCaseFrames().clear();
		save();
		load();
	}
}
