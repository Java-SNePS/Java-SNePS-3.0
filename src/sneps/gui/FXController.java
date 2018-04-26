package sneps.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
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
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.cables.UpCable;
import sneps.network.cables.UpCableSet;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.RCFP;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.Semantic;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Base;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Term;
import sneps.network.classes.term.Variable;

public class FXController implements Initializable {
	Network network = new Network();
	private String currentSelectedRelation;
	private double curX, curY, wireXStart, wireXEnd, wireYStart, wireYEnd;
	private ArrayList<Wire> wires = new ArrayList<Wire>();
	private CaseFrame curCF = null;
	private RelationsRestrictedCaseFrame curRRCF = null;
	boolean moveMode = false;
	boolean deleteMode = false;
	boolean wireMode = false;
	Group draggedNode;
	VarNodeShape vns;
	private ArrayList<Line> drawnWiresList = new ArrayList<Line>();
	private ArrayList<Label> drawnRelationsList = new ArrayList<Label>();
	private ArrayList<Circle> arrows = new ArrayList<Circle>();
	private Hashtable<String, BaseNodeShape> tableOfBaseNodesDrawn = new Hashtable<String, BaseNodeShape>();
	private Hashtable<String, VarNodeShape> tableOfVarNodesDrawn = new Hashtable<String, VarNodeShape>();
	private ArrayList<MolNodeShape> listOfMolNodesDrawn = new ArrayList<MolNodeShape>();
	private LinkedList<RCFP> rrcflist = new LinkedList<RCFP>();
	private int ocp;
	private int ncp;
	
	@FXML
	private TextArea console;
	@FXML
	private Label nodeDetails, relationDetails;
	@FXML
	private TextField newRN, newRT, newRA, newRL, baseNodeIdentPop, baseNodeSemTyPop,
	caseFrameSTN, baseNodeSemType, baseNodeID, overrideAdjust,
	overrideLimit, rrcfSem, newNetName;
	@FXML
	private ListView<String> relationSetList, relationSetList1, relationSetList2, cfRS, caseFramesList,
	caseFrameRelationList, nodesList, wiresList, variableNodesList, baseNodesList, caseFramesDrawList,
	relationOfDrawnCF, rrcfrslist;
	@FXML
	private Group dragBaseNode, dragVarNode, wireModeBtn, dragMolNode;
	@FXML
	private AnchorPane drawArea, mainAnchor, baseNodepopBox, varNodepopBox, drawMolCF,
	relationCFDrawSelect, consoleOutput;
	@FXML
	private ScrollPane drawScroll, consoleScroll;
	@FXML
	private MenuButton caseFrameChoice, netChoice1, netChoice2;
	@FXML
	private Button drawModeBtn, deleteModeBtn, moveModeBtn;
	@FXML
	private Rectangle wireBtnRect;
	@FXML
	private WebView webView;
	@FXML
	Tab displayNetTab;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
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
		
		dragMolNode();
		dragBaseNode();
		dragVariableNode();
		drawArea();
		drawScroll.setFitToHeight(true);
		drawScroll.setFitToWidth(true);
		updateNodesList();
		updateRelationSetList();
		updateCaseFramesList();
		normalMode();
		wireMode();
		consoleHandler();
		try {
			Network.loadNetworks();
			updateNetLists();
		} catch (FileNotFoundException e) {
			System.out.println("Files Not Found!");
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Files Not Found!");
			//e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Files Not Found!");
			//e.printStackTrace();
		}
		
		displayNetTab.setOnSelectionChanged(new EventHandler<Event>() {

			@Override
			public void handle(Event e) {
				try {
					generateNetwork();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
	}

	
	
//..........SNePS Log Methods..........................................
	//Controls The SNePS Log
	public void consoleHandler() {
		consoleScroll.setFitToHeight(true);
		consoleScroll.setFitToWidth(true);
		console.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				ArrayList<String> lines = new ArrayList<String>();
				if(event.getCode() == KeyCode.ENTER) {
					for(String line : console.getText().split("\n")) {
						lines.add(line);
					}
					
					String cmd = lines.get(lines.size() - 1);
					String res = "Result will be here";
					console.setText(console.getText() + "\n" + res);
					console.positionCaret(console.getLength());
					ocp = console.getCaretPosition();
					ScrollDown();
					//System.out.println(cmd);
				}
				
				if(event.getCode() == KeyCode.BACK_SPACE) {
					ncp = console.getCaretPosition();
					if(ncp > ocp) {
						console.deletePreviousChar();
					}
					event.consume();
				}
			}
			
		});
	}

	//SNePS Log Scroll down
	public void ScrollDown() {
		Timer timer = new Timer();
		TimerTask titask = new TimerTask()
		{
		        public void run()
		        {
		        	consoleScroll.setVvalue(1.0 );     
		        }

		};
		timer.schedule(titask,20l);
	}
//..........END Of SNePS Log Methods...................................
	
	
	
	
	
	
	
//..........Drawing the Network Methods................................
	//Erase all drawn nodes
	public void eraseDrawnNetwork() {
		drawArea.getChildren().clear();
	}
	
	//Drag Base Node to draw area
	public void dragMolNode() {
			
			dragMolNode.setOnMouseEntered(new EventHandler <MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					dragMolNode.setCursor(Cursor.HAND);
				}
			});
			
			dragMolNode.setOnDragDetected(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
	                //System.out.println("onDragDetected");
	                Dragboard db = dragMolNode.startDragAndDrop(TransferMode.ANY);
	                ClipboardContent content = new ClipboardContent();
	                content.putString("MolNode");
	                db.setContent(content);
	                event.consume();
					
				}
				
			});
			
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
                //System.out.println("onDragDetected");
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
                //System.out.println("onDragDetected");
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
            
            else if(db.getString() == "MoveNode" && moveMode == true) {
            	curX = event.getX();
            	curY = event.getY();
            }
            else if(db.getString() == "MolNode") {
            	curX = event.getX();
            	curY = event.getY();
            	drawMolCF.setVisible(true);
            }
		}  
       });
       
	}

	//Draw the molecular node
	public void submitMolNode() {
		String cfname = caseFramesDrawList.getSelectionModel().getSelectedItem();
		CaseFrame cf = null;
		try {
			cf = Network.getCaseFrame(cfname);
		} catch (CustomException e) {
			e.printStackTrace();
		}
		MolNodeShape mns = new MolNodeShape(curX, curY, cf);
		listOfMolNodesDrawn.add(mns);
		Group shape = mns.drawShape();
		shape.setLayoutX(curX - 50);
		shape.setLayoutY(curY - 40);
		drawArea.getChildren().add(shape);
		drawMolCF.setVisible(false);
		shape.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(deleteMode == true) {
					drawArea.getChildren().remove(shape);
				}
				
				if(wireMode == true) {
					LinkedList<Relation> relations = drawAreaRelations(cfname);
					relationOfDrawnCF.getItems().clear();
					for(Relation r: relations) {
						relationOfDrawnCF.getItems().add(r.getName());
					}
					relationCFDrawSelect.setVisible(true);	
				}
			}
			
		});
        
        shape.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(moveMode == true) {
					Dragboard db = shape.startDragAndDrop(TransferMode.ANY);
			        ClipboardContent content = new ClipboardContent();
			        content.putString("MoveNode");
			        db.setContent(content);
			        event.consume();
				}
				
				if(wireMode == true) {
					Dragboard db = shape.startDragAndDrop(TransferMode.ANY);
			        ClipboardContent content = new ClipboardContent();
			        content.putString("wireMode");
			        wireXStart = shape.getLayoutX() + 50;
			        wireYStart = shape.getLayoutY() + 40;
			        db.setContent(content);
			        event.consume();
				}
			}
        	
        });
        
        // Moving the node
        shape.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
            	if(moveMode == true && wireMode == false) {
            		double oldPosX = shape.getLayoutX() + 50;
            		double oldPosY = shape.getLayoutY() + 40;
	                shape.setLayoutX(curX-50);
	                shape.setLayoutY(curY-40);
	                mns.setX(curX - 50);
	                mns.setY(curY - 40);
	                for(int i = 0; i < drawnWiresList.size(); i++) {
	                	Line l = drawnWiresList.get(i);
	                	Label lbl = drawnRelationsList.get(i);
	                	Circle c = arrows.get(i);
	                	if(l.getStartX() == oldPosX && l.getStartY() == oldPosY) {
	                		l.setStartX(shape.getLayoutX() + 50);
	                		l.setStartY(shape.getLayoutY() + 40);
	                		lbl.setLayoutX((l.getStartX() + l.getEndX()) / 2);
				        	lbl.setLayoutY((l.getStartY() + l.getEndY()) / 2);
				        	double vx = l.getEndX() - l.getStartX();
				        	double vy = l.getEndY() - l.getStartY();
				        	double vn = Math.sqrt((vx*vx) + (vy*vy));
				        	double vnx = vx/vn;
				        	double vny = vy/vn;
				        	double td = (vx*vx) + (vy*vy);
				        	double d = Math.sqrt(td) -50;
				        	double newX = l.getStartX() + (d*vnx);
				        	double newY = l.getStartY() + (d*vny);
				        	c.setCenterX(newX);
				        	c.setCenterY(newY);
				        	
				            double ax = l.getStartX() - l.getEndX();
				            double ay = l.getStartY() - l.getEndY();
				            double bx = l.getStartX() - (l.getStartX() + 5);
				            double by = 0;
				            double a = Math.sqrt((ax*ax) + (ay*ay));
				            double b = Math.sqrt((bx*bx) + (by*by));
				            double ab = (ax*bx) + (ay*by);
				            double abn = a * b;
				            double angle = Math.acos(ab/abn) * (180/Math.PI);
				            if(l.getEndY() < l.getStartY()) {
				            	c.setRotate(-angle);
				            }
				            else {
				            	c.setRotate(angle);
				            }
	                	}
	                	else if(l.getEndX() == oldPosX && l.getEndY() == oldPosY) {
	                		l.setEndX(shape.getLayoutX() + 50);
	                		l.setEndY(shape.getLayoutY() + 40);
	                		lbl.setLayoutX((l.getStartX() + l.getEndX()) / 2);
				        	lbl.setLayoutY((l.getStartY() + l.getEndY()) / 2);
				        	double vx = l.getEndX() - l.getStartX();
				        	double vy = l.getEndY() - l.getStartY();
				        	double vn = Math.sqrt((vx*vx) + (vy*vy));
				        	double vnx = vx/vn;
				        	double vny = vy/vn;
				        	double td = (vx*vx) + (vy*vy);
				        	double d = Math.sqrt(td) -50;
				        	double newX = l.getStartX() + (d*vnx);
				        	double newY = l.getStartY() + (d*vny);
				        	c.setCenterX(newX);
				        	c.setCenterY(newY);
				        	
				        	double ax = l.getStartX() - l.getEndX();
				            double ay = l.getStartY() - l.getEndY();
				            double bx = l.getStartX() - (l.getStartX() + 5);
				            double by = 0;
				            double a = Math.sqrt((ax*ax) + (ay*ay));
				            double b = Math.sqrt((bx*bx) + (by*by));
				            double ab = (ax*bx) + (ay*by);
				            double abn = a * b;
				            double angle = Math.acos(ab/abn) * (180/Math.PI);
				            if(l.getEndY() < l.getStartY()) {
				            	c.setRotate(-angle);
				            }
				            else {
				            	c.setRotate(angle);
				            }
				            
	                	}
	                }
	                event.consume();
	            }
            }
        });
        
        shape.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				if(wireMode == true) {
					Dragboard db = event.getDragboard();
			        if(db.getString() == "wireMode" && currentSelectedRelation != null) {
			        	wireXEnd = shape.getLayoutX() + 50;
			        	wireYEnd = shape.getLayoutY() + 40;
			        	WireRelation wr = new WireRelation(wireXStart, wireYStart, wireXEnd, wireYEnd);
			        	Line l = wr.drawLine();
			        	double vx = wireXEnd - wireXStart;
			        	double vy = wireYEnd - wireYStart;
			        	double vn = Math.sqrt((vx*vx) + (vy*vy));
			        	double vnx = vx/vn;
			        	double vny = vy/vn;
			        	double td = (vx*vx) + (vy*vy);
			        	double d = Math.sqrt(td) -50;
			        	double newX = wireXStart + (d*vnx);
			        	double newY = wireYStart + (d*vny);
			        	Circle cir = new Circle();
			        	cir.setCenterX(newX);
			        	cir.setCenterY(newY);
			        	cir.setRadius(12.5);
			        	Image img = new Image("sneps/gui/AH.png");
			        	cir.setFill(new ImagePattern(img));
			            double ax = wireXStart - wireXEnd;
			            double ay = wireYStart - wireYEnd;
			            double bx = wireXStart - (wireXStart + 5);
			            double by = wireYStart - wireYStart;
			            double a = Math.sqrt((ax*ax) + (ay*ay));
			            double b = Math.sqrt((bx*bx) + (by*by));
			            double ab = (ax*bx) + (ay*by);
			            double abn = a * b;
			            double angle = Math.acos(ab/abn) * (180/Math.PI);
			            if(wireYEnd < wireYStart) {
			            	cir.setRotate(-angle);
			            }
			            else {
			            	cir.setRotate(angle);
			            }
			        	drawArea.getChildren().add(l);
			        	drawArea.getChildren().add(cir);
			        	l.toBack();
			        	Label relationName = new Label(currentSelectedRelation);
			        	relationName.setLayoutX((l.getStartX() + l.getEndX()) / 2);
			        	relationName.setLayoutY((l.getStartY() + l.getEndY()) / 2);
			        	drawArea.getChildren().add(relationName);
			        	l.setOnMouseClicked(new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent arg0) {
								if(deleteMode == true) {
									drawArea.getChildren().remove(l);
									drawArea.getChildren().remove(relationName);
									drawArea.getChildren().remove(cir);
									drawnWiresList.remove(l);
									drawnRelationsList.remove(relationName);
									arrows.remove(cir);
								}
							}
			        		
			        	});
			        	currentSelectedRelation = null;
			        	drawnWiresList.add(l);
			        	drawnRelationsList.add(relationName);
			        	arrows.add(cir);
			        }
			        event.consume();
				}
			}
        	
        });
	}
	
	//Draw the relation
	public void drawRelation() {
		String rname = relationOfDrawnCF.getSelectionModel().getSelectedItem();
		currentSelectedRelation = rname;
		relationCFDrawSelect.setVisible(false);
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
		BaseNodeShape baseNode = new BaseNodeShape(identifier, semType, curX, curY);
		tableOfBaseNodesDrawn.put(identifier, baseNode);
		Group shape = baseNode.makeShape();
		shape.setLayoutX(curX-50);
		shape.setLayoutY(curY-40);
		drawArea.getChildren().add(shape);
		baseNodepopBox.setVisible(false);
		//Delete the node
        shape.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(deleteMode == true) {
					drawArea.getChildren().remove(shape);
				}
				
			}
			
		});
        
        shape.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(moveMode == true) {
					Dragboard db = shape.startDragAndDrop(TransferMode.ANY);
			        ClipboardContent content = new ClipboardContent();
			        content.putString("MoveNode");
			        db.setContent(content);
			        event.consume();
				}
			}
        	
        });
        
        // Moving the node
        shape.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
            	if(moveMode == true && wireMode == false) {
            		double oldPosX = shape.getLayoutX() + 50;
            		double oldPosY = shape.getLayoutY() + 40;
	                shape.setLayoutX(curX-50);
	                shape.setLayoutY(curY-40);
	                baseNode.setXY(curX-50, curY-40);
	                for(int i = 0; i < drawnWiresList.size(); i++) {
	                	Line l = drawnWiresList.get(i);
	                	Label lbl = drawnRelationsList.get(i);
	                	Circle c = arrows.get(i);
	                	if(l.getEndX() == oldPosX && l.getEndY() == oldPosY) {
	                		l.setEndX(shape.getLayoutX() + 50);
	                		l.setEndY(shape.getLayoutY() + 40);
	                		lbl.setLayoutX((l.getStartX() + l.getEndX()) / 2);
				        	lbl.setLayoutY((l.getStartY() + l.getEndY()) / 2);
				        	double vx = l.getEndX() - l.getStartX();
				        	double vy = l.getEndY() - l.getStartY();
				        	double vn = Math.sqrt((vx*vx) + (vy*vy));
				        	double vnx = vx/vn;
				        	double vny = vy/vn;
				        	double td = (vx*vx) + (vy*vy);
				        	double d = Math.sqrt(td) -50;
				        	double newX = l.getStartX() + (d*vnx);
				        	double newY = l.getStartY() + (d*vny);
				        	c.setCenterX(newX);
				        	c.setCenterY(newY);
				        	double ax = l.getStartX() - l.getEndX();
				            double ay = l.getStartY() - l.getEndY();
				            double bx = l.getStartX() - (l.getStartX() + 5);
				            double by = 0;
				            double a = Math.sqrt((ax*ax) + (ay*ay));
				            double b = Math.sqrt((bx*bx) + (by*by));
				            double ab = (ax*bx) + (ay*by);
				            double abn = a * b;
				            double angle = Math.acos(ab/abn) * (180/Math.PI);
				            if(l.getEndY() < l.getStartY()) {
				            	c.setRotate(-angle);
				            }
				            else {
				            	c.setRotate(angle);
				            }
				        	
	                	}
	                }
	                event.consume();
	            }
            }
        });
        
        //Drawing the wires
        shape.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				if(wireMode == true) {
					Dragboard db = event.getDragboard();
			        if(db.getString() == "wireMode" && currentSelectedRelation != null) {
			        	wireXEnd = shape.getLayoutX() + 50;
			        	wireYEnd = shape.getLayoutY() + 40;
			        	WireRelation wr = new WireRelation(wireXStart, wireYStart, wireXEnd, wireYEnd);
			        	Line l = wr.drawLine();
			        	double vx = wireXEnd - wireXStart;
			        	double vy = wireYEnd - wireYStart;
			        	double vn = Math.sqrt((vx*vx) + (vy*vy));
			        	double vnx = vx/vn;
			        	double vny = vy/vn;
			        	double td = (vx*vx) + (vy*vy);
			        	double d = Math.sqrt(td) -50;
			        	double newX = wireXStart + (d*vnx);
			        	double newY = wireYStart + (d*vny);
			        	Circle cir = new Circle();
			        	cir.setCenterX(newX);
			        	cir.setCenterY(newY);
			        	cir.setRadius(12.5);
			        	Image img = new Image("sneps/gui/AH.png");
			        	cir.setFill(new ImagePattern(img));
			            double ax = wireXStart - wireXEnd;
			            double ay = wireYStart - wireYEnd;
			            double bx = wireXStart - (wireXStart + 5);
			            double by = wireYStart - wireYStart;
			            double a = Math.sqrt((ax*ax) + (ay*ay));
			            double b = Math.sqrt((bx*bx) + (by*by));
			            double ab = (ax*bx) + (ay*by);
			            double abn = a * b;
			            double angle = Math.acos(ab/abn) * (180/Math.PI);
			            if(wireYEnd < wireYStart) {
			            	cir.setRotate(-angle);
			            }
			            else {
			            	cir.setRotate(angle);
			            }
			        	drawArea.getChildren().add(l);
			        	drawArea.getChildren().add(cir);
			        	l.toBack();
			        	Label relationName = new Label(currentSelectedRelation);
			        	relationName.setLayoutX((l.getStartX() + l.getEndX()) / 2);
			        	relationName.setLayoutY((l.getStartY() + l.getEndY()) / 2);
			        	drawArea.getChildren().add(relationName);
			        	l.setOnMouseClicked(new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent arg0) {
								if(deleteMode == true) {
									drawArea.getChildren().remove(l);
									drawArea.getChildren().remove(relationName);
									drawArea.getChildren().remove(cir);
									drawnWiresList.remove(l);
									drawnRelationsList.remove(relationName);
									arrows.remove(cir);
								}
							}
			        		
			        	});
			        	currentSelectedRelation = null;
			        	drawnWiresList.add(l);
			        	drawnRelationsList.add(relationName);
			        	arrows.add(cir);
			        }
			        event.consume();
				}
			}
        	
        });
	}
	
		public void submitNewBaseNode() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {

			String identifier = baseNodeIdentPop.getText();
			String semType = baseNodeSemTyPop.getText();
			BaseNodeShape baseNode = new BaseNodeShape(identifier, semType, curX, curY);
			Group shape = baseNode.makeShape();
			shape.setLayoutX(curX-50);
			shape.setLayoutY(curY-40);
			drawArea.getChildren().add(shape);
			tableOfBaseNodesDrawn.put(identifier, baseNode);
			baseNodepopBox.setVisible(false);
			baseNodeIdentPop.setText("");
			baseNodeSemTyPop.setText("");
			//Delete the node
        shape.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(deleteMode == true) {
					drawArea.getChildren().remove(shape);
				}
				
			}
			
		});
        
        shape.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(moveMode == true) {
					Dragboard db = shape.startDragAndDrop(TransferMode.ANY);
			        ClipboardContent content = new ClipboardContent();
			        content.putString("MoveNode");
			        db.setContent(content);
			        event.consume();
				}
			}
        	
        });
        
        // Moving the node
        shape.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
            	if(moveMode == true && wireMode == false) {
            		double oldPosX = shape.getLayoutX() + 50;
            		double oldPosY = shape.getLayoutY() + 40;
	                shape.setLayoutX(curX-50);
	                shape.setLayoutY(curY-40);
	                baseNode.setXY(curX-50, curY-40);
	                for(int i = 0; i < drawnWiresList.size(); i++) {
	                	Line l = drawnWiresList.get(i);
	                	Label lbl = drawnRelationsList.get(i);
	                	Circle c = arrows.get(i);
	                	if(l.getEndX() == oldPosX && l.getEndY() == oldPosY) {
	                		l.setEndX(shape.getLayoutX() + 50);
	                		l.setEndY(shape.getLayoutY() + 40);
	                		lbl.setLayoutX((l.getStartX() + l.getEndX()) / 2);
				        	lbl.setLayoutY((l.getStartY() + l.getEndY()) / 2);
				        	double vx = l.getEndX() - l.getStartX();
				        	double vy = l.getEndY() - l.getStartY();
				        	double vn = Math.sqrt((vx*vx) + (vy*vy));
				        	double vnx = vx/vn;
				        	double vny = vy/vn;
				        	double td = (vx*vx) + (vy*vy);
				        	double d = Math.sqrt(td) -50;
				        	double newX = l.getStartX() + (d*vnx);
				        	double newY = l.getStartY() + (d*vny);
				        	c.setCenterX(newX);
				        	c.setCenterY(newY);
				        	double ax = l.getStartX() - l.getEndX();
				            double ay = l.getStartY() - l.getEndY();
				            double bx = l.getStartX() - (l.getStartX() + 5);
				            double by = 0;
				            double a = Math.sqrt((ax*ax) + (ay*ay));
				            double b = Math.sqrt((bx*bx) + (by*by));
				            double ab = (ax*bx) + (ay*by);
				            double abn = a * b;
				            double angle = Math.acos(ab/abn) * (180/Math.PI);
				            if(l.getEndY() < l.getStartY()) {
				            	c.setRotate(-angle);
				            }
				            else {
				            	c.setRotate(angle);
				            }
	                	}
	                }
	                event.consume();
	            }
            }
        });
        
        shape.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				if(wireMode == true) {
					Dragboard db = event.getDragboard();
			        if(db.getString() == "wireMode" && currentSelectedRelation != null) {
			        	wireXEnd = shape.getLayoutX() + 50;
			        	wireYEnd = shape.getLayoutY() + 40;
			        	WireRelation wr = new WireRelation(wireXStart, wireYStart, wireXEnd, wireYEnd);
			        	Line l = wr.drawLine();
			        	double vx = wireXEnd - wireXStart;
			        	double vy = wireYEnd - wireYStart;
			        	double vn = Math.sqrt((vx*vx) + (vy*vy));
			        	double vnx = vx/vn;
			        	double vny = vy/vn;
			        	double td = (vx*vx) + (vy*vy);
			        	double d = Math.sqrt(td) -50;
			        	double newX = wireXStart + (d*vnx);
			        	double newY = wireYStart + (d*vny);
			        	Circle cir = new Circle();
			        	cir.setCenterX(newX);
			        	cir.setCenterY(newY);
			        	cir.setRadius(12.5);
			        	Image img = new Image("sneps/gui/AH.png");
			        	cir.setFill(new ImagePattern(img));
			            double ax = wireXStart - wireXEnd;
			            double ay = wireYStart - wireYEnd;
			            double bx = wireXStart - (wireXStart + 5);
			            double by = wireYStart - wireYStart;
			            double a = Math.sqrt((ax*ax) + (ay*ay));
			            double b = Math.sqrt((bx*bx) + (by*by));
			            double ab = (ax*bx) + (ay*by);
			            double abn = a * b;
			            double angle = Math.acos(ab/abn) * (180/Math.PI);
			            if(wireYEnd < wireYStart) {
			            	cir.setRotate(-angle);
			            }
			            else {
			            	cir.setRotate(angle);
			            }
			        	drawArea.getChildren().add(l);
			        	drawArea.getChildren().add(cir);
			        	l.toBack();
			        	Label relationName = new Label(currentSelectedRelation);
			        	relationName.setLayoutX((l.getStartX() + l.getEndX()) / 2);
			        	relationName.setLayoutY((l.getStartY() + l.getEndY()) / 2);
			        	drawArea.getChildren().add(relationName);
			        	l.setOnMouseClicked(new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent arg0) {
								if(deleteMode == true) {
									drawArea.getChildren().remove(l);
									drawArea.getChildren().remove(relationName);
									drawArea.getChildren().remove(cir);
									drawnWiresList.remove(l);
									drawnRelationsList.remove(relationName);
									arrows.remove(cir);
									
								}
							}
			        		
			        	});
			        	currentSelectedRelation = null;
			        	drawnWiresList.add(l);
			        	drawnRelationsList.add(relationName);
			        	arrows.add(cir);
			        }
			        event.consume();
				}
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
			String identifier = n.getIdentifier();
			VarNodeShape varNodeS = new VarNodeShape(identifier, curX, curY);
			tableOfVarNodesDrawn.put(identifier, varNodeS);
			Group shape = varNodeS.makeShape();
			shape.setLayoutX(curX-50);
			shape.setLayoutY(curY-40);
			drawArea.getChildren().add(shape);
	        varNodepopBox.setVisible(false);
	     //Delete the node
        shape.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(deleteMode == true) {
					drawArea.getChildren().remove(shape);
				}
				
			}
			
		});
        
        shape.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(moveMode == true) {
					Dragboard db = shape.startDragAndDrop(TransferMode.ANY);
			        ClipboardContent content = new ClipboardContent();
			        content.putString("MoveNode");
			        db.setContent(content);
			        event.consume();
				}
			}
        	
        });
        
        // Moving the node
        shape.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
            	if(moveMode == true && wireMode == false) {
            		double oldPosX = shape.getLayoutX() + 50;
            		double oldPosY = shape.getLayoutY() + 40;
	                shape.setLayoutX(curX-50);
	                shape.setLayoutY(curY-40);
	                varNodeS.setXY(curX-50, curY-40);
	                for(int i = 0; i < drawnWiresList.size(); i++) {
	                	Line l = drawnWiresList.get(i);
	                	Label lbl = drawnRelationsList.get(i);
	                	Circle c = arrows.get(i);
	                	if(l.getEndX() == oldPosX && l.getEndY() == oldPosY) {
	                		l.setEndX(shape.getLayoutX() + 50);
	                		l.setEndY(shape.getLayoutY() + 40);
	                		lbl.setLayoutX((l.getStartX() + l.getEndX()) / 2);
				        	lbl.setLayoutY((l.getStartY() + l.getEndY()) / 2);
				        	double vx = l.getEndX() - l.getStartX();
				        	double vy = l.getEndY() - l.getStartY();
				        	double vn = Math.sqrt((vx*vx) + (vy*vy));
				        	double vnx = vx/vn;
				        	double vny = vy/vn;
				        	double td = (vx*vx) + (vy*vy);
				        	double d = Math.sqrt(td) -50;
				        	double newX = l.getStartX() + (d*vnx);
				        	double newY = l.getStartY() + (d*vny);
				        	c.setCenterX(newX);
				        	c.setCenterY(newY);
				        	double ax = l.getStartX() - l.getEndX();
				            double ay = l.getStartY() - l.getEndY();
				            double bx = l.getStartX() - (l.getStartX() + 5);
				            double by = 0;
				            double a = Math.sqrt((ax*ax) + (ay*ay));
				            double b = Math.sqrt((bx*bx) + (by*by));
				            double ab = (ax*bx) + (ay*by);
				            double abn = a * b;
				            double angle = Math.acos(ab/abn) * (180/Math.PI);
				            if(l.getEndY() < l.getStartY()) {
				            	c.setRotate(-angle);
				            }
				            else {
				            	c.setRotate(angle);
				            }
	                	}
	                }
	                event.consume();
	            }
            }
        });
        
        shape.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				if(wireMode == true) {
					Dragboard db = event.getDragboard();
			        if(db.getString() == "wireMode" && currentSelectedRelation != null) {
			        	wireXEnd = shape.getLayoutX() + 50;
			        	wireYEnd = shape.getLayoutY() + 40;
			        	WireRelation wr = new WireRelation(wireXStart, wireYStart, wireXEnd, wireYEnd);
			        	Line l = wr.drawLine();
			        	double vx = wireXEnd - wireXStart;
			        	double vy = wireYEnd - wireYStart;
			        	double vn = Math.sqrt((vx*vx) + (vy*vy));
			        	double vnx = vx/vn;
			        	double vny = vy/vn;
			        	double td = (vx*vx) + (vy*vy);
			        	double d = Math.sqrt(td) -50;
			        	double newX = wireXStart + (d*vnx);
			        	double newY = wireYStart + (d*vny);
			        	Circle cir = new Circle();
			        	cir.setCenterX(newX);
			        	cir.setCenterY(newY);
			        	cir.setRadius(12.5);
			        	Image img = new Image("sneps/gui/AH.png");
			        	cir.setFill(new ImagePattern(img));
			            double ax = wireXStart - wireXEnd;
			            double ay = wireYStart - wireYEnd;
			            double bx = wireXStart - (wireXStart + 5);
			            double by = wireYStart - wireYStart;
			            double a = Math.sqrt((ax*ax) + (ay*ay));
			            double b = Math.sqrt((bx*bx) + (by*by));
			            double ab = (ax*bx) + (ay*by);
			            double abn = a * b;
			            double angle = Math.acos(ab/abn) * (180/Math.PI);
			            if(wireYEnd < wireYStart) {
			            	cir.setRotate(-angle);
			            }
			            else {
			            	cir.setRotate(angle);
			            }
			        	drawArea.getChildren().add(l);
			        	drawArea.getChildren().add(cir);
			        	l.toBack();
			        	Label relationName = new Label(currentSelectedRelation);
			        	relationName.setLayoutX((l.getStartX() + l.getEndX()) / 2);
			        	relationName.setLayoutY((l.getStartY() + l.getEndY()) / 2);
			        	drawArea.getChildren().add(relationName);
			        	l.setOnMouseClicked(new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent arg0) {
								if(deleteMode == true) {
									drawArea.getChildren().remove(l);
									drawArea.getChildren().remove(relationName);
									drawArea.getChildren().remove(cir);
									drawnWiresList.remove(l);
									drawnRelationsList.remove(relationName);
									arrows.remove(cir);
								}
							}
			        		
			        	});
			        	currentSelectedRelation = null;
			        	drawnWiresList.add(l);
			        	drawnRelationsList.add(relationName);
			        	arrows.add(cir);
			        }
			        event.consume();
				}
			}
        	
        });
	}
		
	//Submit new variable node drawn in draw area
	public void submitNewVariableNode() {
			Node n = Network.buildVariableNode();
			String identifier = n.getIdentifier();
			VarNodeShape varNode = new VarNodeShape(identifier, curX, curY);
			Group shape = varNode.makeShape();
			shape.setLayoutX(curX-50);
			shape.setLayoutY(curY-40);
			drawArea.getChildren().add(shape);
			varNodepopBox.setVisible(false);
			updateNodesList();
			//Delete the node
        shape.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(deleteMode == true) {
					drawArea.getChildren().remove(shape);
				}
				
			}
			
		});
        
        shape.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(moveMode == true) {
					Dragboard db = shape.startDragAndDrop(TransferMode.ANY);
			        ClipboardContent content = new ClipboardContent();
			        content.putString("MoveNode");
			        db.setContent(content);
			        event.consume();
				}
			}
        	
        });
        
        // Moving the node
        shape.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
            	if(moveMode == true && wireMode == false) {
            		double oldPosX = shape.getLayoutX() + 50;
            		double oldPosY = shape.getLayoutY() + 40;
	                shape.setLayoutX(curX-50);
	                shape.setLayoutY(curY-40);
	                varNode.setXY(curX-50, curY-40);
	                for(int i = 0; i < drawnWiresList.size(); i++) {
	                	Line l = drawnWiresList.get(i);
	                	Label lbl = drawnRelationsList.get(i);
	                	Circle c = arrows.get(i);
	                	if(l.getEndX() == oldPosX && l.getEndY() == oldPosY) {
	                		l.setEndX(shape.getLayoutX() + 50);
	                		l.setEndY(shape.getLayoutY() + 40);
	                		lbl.setLayoutX((l.getStartX() + l.getEndX()) / 2);
				        	lbl.setLayoutY((l.getStartY() + l.getEndY()) / 2);
				        	double vx = l.getEndX() - l.getStartX();
				        	double vy = l.getEndY() - l.getStartY();
				        	double vn = Math.sqrt((vx*vx) + (vy*vy));
				        	double vnx = vx/vn;
				        	double vny = vy/vn;
				        	double td = (vx*vx) + (vy*vy);
				        	double d = Math.sqrt(td) -50;
				        	double newX = l.getStartX() + (d*vnx);
				        	double newY = l.getStartY() + (d*vny);
				        	c.setCenterX(newX);
				        	c.setCenterY(newY);
				        	double ax = l.getStartX() - l.getEndX();
				            double ay = l.getStartY() - l.getEndY();
				            double bx = l.getStartX() - (l.getStartX() + 5);
				            double by = 0;
				            double a = Math.sqrt((ax*ax) + (ay*ay));
				            double b = Math.sqrt((bx*bx) + (by*by));
				            double ab = (ax*bx) + (ay*by);
				            double abn = a * b;
				            double angle = Math.acos(ab/abn) * (180/Math.PI);
				            if(l.getEndY() < l.getStartY()) {
				            	c.setRotate(-angle);
				            }
				            else {
				            	c.setRotate(angle);
				            }
	                	}
	                }
	                event.consume();
	            }
            }
        });
        
        shape.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				if(wireMode == true) {
					Dragboard db = event.getDragboard();
			        if(db.getString() == "wireMode" && currentSelectedRelation != null) {
			        	wireXEnd = shape.getLayoutX() + 50;
			        	wireYEnd = shape.getLayoutY() + 40;
			        	WireRelation wr = new WireRelation(wireXStart, wireYStart, wireXEnd, wireYEnd);
			        	Line l = wr.drawLine();
			        	double vx = wireXEnd - wireXStart;
			        	double vy = wireYEnd - wireYStart;
			        	double vn = Math.sqrt((vx*vx) + (vy*vy));
			        	double vnx = vx/vn;
			        	double vny = vy/vn;
			        	double td = (vx*vx) + (vy*vy);
			        	double d = Math.sqrt(td) -50;
			        	double newX = wireXStart + (d*vnx);
			        	double newY = wireYStart + (d*vny);
			        	Circle cir = new Circle();
			        	cir.setCenterX(newX);
			        	cir.setCenterY(newY);
			        	cir.setRadius(12.5);
			        	Image img = new Image("sneps/gui/AH.png");
			        	cir.setFill(new ImagePattern(img));
			            double ax = wireXStart - wireXEnd;
			            double ay = wireYStart - wireYEnd;
			            double bx = wireXStart - (wireXStart + 5);
			            double by = wireYStart - wireYStart;
			            double a = Math.sqrt((ax*ax) + (ay*ay));
			            double b = Math.sqrt((bx*bx) + (by*by));
			            double ab = (ax*bx) + (ay*by);
			            double abn = a * b;
			            double angle = Math.acos(ab/abn) * (180/Math.PI);
			            if(wireYEnd < wireYStart) {
			            	cir.setRotate(-angle);
			            }
			            else {
			            	cir.setRotate(angle);
			            }
			        	drawArea.getChildren().add(l);
			        	drawArea.getChildren().add(cir);
			        	l.toBack();
			        	Label relationName = new Label(currentSelectedRelation);
			        	relationName.setLayoutX((l.getStartX() + l.getEndX()) / 2);
			        	relationName.setLayoutY((l.getStartY() + l.getEndY()) / 2);
			        	drawArea.getChildren().add(relationName);
			        	l.setOnMouseClicked(new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent arg0) {
								if(deleteMode == true) {
									drawArea.getChildren().remove(l);
									drawArea.getChildren().remove(relationName);
									drawArea.getChildren().remove(cir);
									drawnWiresList.remove(l);
									drawnRelationsList.remove(relationName);
									arrows.remove(cir);
								}
							}
			        		
			        	});
			        	currentSelectedRelation = null;
			        	drawnWiresList.add(l);
			        	drawnRelationsList.add(relationName);
			        	arrows.add(cir);
			        }
			        event.consume();
				}
			}
        	
        });
	}

	//Deletes all drawn shapes in the draw area
	public void resetNetwork() {
		Network.getRelations().clear();
		Network.getNodes().clear();
		Network.getCaseFrames().clear();
		save();
		load();
	}

	//Selects the delete node/relation mode
	public void deleteMode() {
		moveMode = false;
		deleteMode = true;
		deleteModeBtn.getStyleClass().clear();
		drawModeBtn.getStyleClass().clear();
		moveModeBtn.getStyleClass().clear();
		drawModeBtn.getStyleClass().add("customBtn");
		moveModeBtn.getStyleClass().add("customBtn");
		deleteModeBtn.getStyleClass().add("customBtnSelected");
	}
	
	//Selects the move node mode
	public void moveMode() {
		moveMode = true;
		deleteMode = false;
		wireMode = false;
		deleteModeBtn.getStyleClass().clear();
		drawModeBtn.getStyleClass().clear();
		moveModeBtn.getStyleClass().clear();
		deleteModeBtn.getStyleClass().add("customBtn");
		drawModeBtn.getStyleClass().add("customBtn");
		moveModeBtn.getStyleClass().add("customBtnSelected");
	}

	//Selects the draw mode
	public void normalMode() {
		moveMode = false;
		deleteMode = false;
		deleteModeBtn.getStyleClass().clear();
		drawModeBtn.getStyleClass().clear();
		moveModeBtn.getStyleClass().clear();
		deleteModeBtn.getStyleClass().add("customBtn");
		moveModeBtn.getStyleClass().add("customBtn");
		drawModeBtn.getStyleClass().add("customBtnSelected");
		
	}

	//Creates the drawn base nodes in the network
	public void submitDrawnBaseNodes() {
		for (Entry<String, BaseNodeShape> entry : tableOfBaseNodesDrawn.entrySet()) {
			String key = entry.getKey();
			BaseNodeShape temp = entry.getValue();
			String semantic = temp.semtantic;
			Semantic semType = new Semantic(semantic);
			Network.buildBaseNode(key, semType);
		}
		updateNodesList();
	}

	//Create the drawn molecular nodes in the network
	public void submitDrawMolNodes() {
		for(int i = 0; i<listOfMolNodesDrawn.size(); i++) {
			MolNodeShape temp = listOfMolNodesDrawn.get(i);
			ArrayList<Wire> wires = new ArrayList<Wire>();
			RelationsRestrictedCaseFrame cf = (RelationsRestrictedCaseFrame) temp.getCf();
			double x = temp.getX();
			double y = temp.getY();
			for(int j = 0; j<drawnWiresList.size(); j++) {
				Line l = drawnWiresList.get(j);
				double xStart = l.getStartX();
				double yStart = l.getStartY();
				double xEnd = l.getEndX();
				double yEnd = l.getEndY();
				if((x == xStart) && (y == yStart)) {
					//System.out.println("Node == line start");
					for (Entry<String, BaseNodeShape> entry : tableOfBaseNodesDrawn.entrySet()) {
						String key = entry.getKey();
						BaseNodeShape tempBase = entry.getValue();
						double xPos = tempBase.x;
						double yPos = tempBase.y;
						if((xPos == xEnd) && (yPos == yEnd)) {
							//System.out.println("There is a base node here!!");
							Label lbl = drawnRelationsList.get(j);
							String rname = lbl.getText();
							System.out.println(rname);
							Relation r = null;
							try {
								r = Network.getRelation(rname);
							} catch (CustomException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Node n = null;
							try {
								n = Network.getNode(key);
							} catch (CustomException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println(n.getIdentifier());
							Wire w = new Wire(r, n);
							wires.add(w);
						}
					}
				}
			}
			try {
				Network.buildMolecularNode(wires, cf);
			} catch (CustomException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//submit drawn Nodes
	public void submitDrawnNodes() {
		submitDrawnBaseNodes();
		submitDrawMolNodes();
		updateNodesList();
	}
	
	//Selects the wire mode
	public void wireMode() {
		wireModeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(wireMode == false) {
					wireMode = true;
					wireBtnRect.setVisible(true);
				}
				else {
					wireMode = false;
					wireBtnRect.setVisible(false);
				}
			}
			
		});
	}
	
	//Returns a list of the relations of a case frame
	public LinkedList<Relation> drawAreaRelations(String caseFrame){
		CaseFrame cf = null;
		try {
			cf = Network.getCaseFrame(caseFrame);
		} catch (CustomException e) {
			e.printStackTrace();
		}
		return cf.getRelations();
	}

//..........END of Drawing the Network Methods..........................
	
	
	
	
	
	
	
	
	
//..........Traditional Menu Methods...................................
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
		caseFramesDrawList.getItems().clear();
		Hashtable<String, CaseFrame> caseFrames = Network.getCaseFrames();
		for (Entry<String, CaseFrame> entry : caseFrames.entrySet()) {
		    String key = entry.getKey();
		    MenuItem item = new MenuItem(key);
		    caseFramesList.getItems().add(key);
		    caseFramesDrawList.getItems().add(key);
		    item.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					try {
						CaseFrame cf = Network.getCaseFrame(item.getText());
						curCF = cf;
						curRRCF = (RelationsRestrictedCaseFrame) cf;
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
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Node created!");
			alert.setHeaderText("Node was created successfully!");
			alert.setContentText("The base node " + nodeName + " was created successfully!");
			alert.showAndWait();
			updateNodesList();
			baseNodeID.setText("");
			baseNodeSemType.setText("");
		}
		
	}
	
	//Updates the nodes list
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
	
	//Creates a wire
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
	
	//Builds a variable node in the network
	public void buildVN() {
		Network.buildVariableNode();
		updateNodesList();
	}
	
	//Builds the molecular node in the network
	public void buildMolecularNode() {
		//CaseFrame cf = (CaseFrame) curCF;
		//System.out.println(cf.getRelations());
		try {
			Network.buildMolecularNode(wires, curRRCF);
			updateNodesList();
			wiresList.getItems().clear();
			wires.clear();
		} catch (CustomException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Displays the details of a selected node
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
	
	//Displays the details of a selected relation
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

	//Adds an overridden a relation to rr case frame
	public void addOverriddenRelationToCaseFrame() {
		String selectedRelation = relationSetList1.getSelectionModel().getSelectedItem();
		if(selectedRelation != null) {
			String adjust = overrideAdjust.getText();
			String limitS = overrideLimit.getText();
			
			if((adjust.length() == 0) && (limitS.length() == 0)) {
				Relation r = null;
				try {
					r = Network.getRelation(selectedRelation);
					
				} catch (CustomException e) {
					e.printStackTrace();
				}
				
				String ra = r.getAdjust();
				int rl = r.getLimit();
				RCFP rcfp = new RCFP(r,ra,rl);
				rrcflist.add(rcfp);
				cfRS.getItems().add(selectedRelation);
				relationSetList1.getItems().remove(selectedRelation);
				overrideAdjust.setText("");
				overrideLimit.setText("");
				
			}else {
				int limit = Integer.parseInt(overrideLimit.getText());
				Relation r = null;
				try {
					r = Network.getRelation(selectedRelation);
				} catch (CustomException e) {
					e.printStackTrace();
				}
				RCFP rcfp = new RCFP(r,adjust,limit);
				rrcflist.add(rcfp);
				cfRS.getItems().add(selectedRelation);
				relationSetList1.getItems().remove(selectedRelation);
				overrideAdjust.setText("");
				overrideLimit.setText("");
			}
		}
	}
	
	//Removes an overridden relation from rr case frame
	public void removeOverridenRelation() {
		String selectedRelation = rrcfrslist.getSelectionModel().getSelectedItem();
		if(selectedRelation != null) {
			for(RCFP rcfp: rrcflist) {
		    	  Relation r = rcfp.getRelation();
		    	  if(r.getName() == selectedRelation) {
		    		  rrcflist.remove(rcfp);
		    		  relationSetList1.getItems().add(selectedRelation);
		    		  cfRS.getItems().remove(selectedRelation);
		    	  }
		    }

		}
	}
	
	//Creates rrcf
	public void submitRRCF() {
		String semType = rrcfSem.getText();
		try {
			Network.defineCaseFrameWithConstraints(semType, rrcflist);
		} catch (CustomException e) {
			e.printStackTrace();
		}
		updateCaseFramesList();
		updateRelationSetList();
		rrcflist.clear();
		cfRS.getItems().clear();
		rrcfSem.setText("");
	}
	
	public void deleteNode() {
		String identifier = nodesList.getSelectionModel().getSelectedItem().toString();
		Node n = null;
		try {
			n = Network.getNode(identifier);
		} catch (CustomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Network.removeNode(n);
			updateNodesList();
		} catch (CustomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//..........END Of Traditional Menu Methods.............................	

	
	
	
	
	
	
//..........Saving & Loading Methods....................................
	
	public void save() {
		String name = netChoice1.getText();
		String relations = name + "relations";
		String caseFrames = name + "caseFrames";
		String nodes = name + "nodes";
		String molnodes = name + "molNodes";
		String mc = name + "mc";
		String pc = name + "pc";
		String vc = name + "vc";
		String pn = name + "pn";
		String ni = name + "ni";
		String udms = name + "udms";
		String udps = name + "udps";
		String udvs = name + "udvs";
		try {
			Network.save(relations, caseFrames, nodes, molnodes, mc, pc, vc, pn, ni, udms, udps, udvs);
			System.out.println("saved");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		String name = netChoice2.getText();
		String relations = name + "relations";
		String caseFrames = name + "caseFrames";
		String nodes = name + "nodes";
		String molnodes = name + "molNodes";
		String mc = name + "mc";
		String pc = name + "pc";
		String vc = name + "vc";
		String pn = name + "pn";
		String ni = name + "ni";
		String udms = name + "udms";
		String udps = name + "udps";
		String udvs = name + "udvs";
		try {
			Network.load(relations , caseFrames, nodes, molnodes, mc, pc, vc, pn, ni, udms, udps, udvs);
			updateNodesList();
			updateCaseFramesList();
			updateRelationSetList();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void submitNewNet() throws IOException {
		String name = newNetName.getText();
		boolean r = Network.addToSavedNetworks(name);
		if(r == true) {
			System.out.println("Network Created Successfully!");
			Network.saveNetworks();
		}else {
			System.out.println("Network Already Exists, Please Type Another Name.");
		}
		updateNetLists();
	}

	public void updateNetList1() {
		netChoice1.getItems().clear();
		ArrayList<String> temp = Network.getSavedNetworks();
		for(int i = 0; i<temp.size(); i++) {
			MenuItem mi = new MenuItem(temp.get(i));
			mi.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					netChoice1.setText(mi.getText());
				}
				
			});
			netChoice1.getItems().add(mi);
		}
	}
	
	public void updateNetList2() {
		netChoice2.getItems().clear();
		ArrayList<String> temp = Network.getSavedNetworks();
		for(int i = 0; i<temp.size(); i++) {
			MenuItem mi = new MenuItem(temp.get(i));
			mi.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					netChoice2.setText(mi.getText());
				}
				
			});
			netChoice2.getItems().add(mi);
		}
	}
	
	public void updateNetLists() {
		updateNetList1();
		updateNetList2();
		
	}

	
//..........End of saving and loading methods...........................
	
	
	
	
	
//..........Displaying the network method...............................
	public void generateNetwork() throws IOException {
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
        
        Hashtable<String, NodeSet> nodes = Network.getMolecularNodes();
		for (Entry<String, NodeSet> entry : nodes.entrySet()) {
			NodeSet ns = entry.getValue();
			for(int i = 0; i<ns.size(); i++) {
				Molecular molNode = (Molecular) ns.getNode(i).getTerm();
				System.out.println(molNode.getIdentifier());
				DownCableSet dcs = molNode.getDownCableSet();
				Hashtable<String, DownCable> downCables = dcs.getDownCables();
				for(Entry<String, DownCable> entry1 : downCables.entrySet()) {
					String rname = entry1.getKey();
					System.out.println(rname);
					DownCable dc = entry1.getValue();
					NodeSet cableNodes = dc.getNodeSet();
					for(int j = 0; j<cableNodes.size(); j++) {
						Node n = cableNodes.getNode(j);
						String nodeShape = " " + n.getIdentifier() + " [style=filled,color=white];";
						if(n.getTerm() instanceof Molecular) {
							nodeShape = " " + n.getIdentifier() + " [style=filled,color=dodgerblue];";
						}
						else if(n.getTerm() instanceof Base) {
							nodeShape = " " + n.getIdentifier() + " [style=filled,color=yellow];";
						}
						else if(n.getTerm() instanceof Variable) {
							nodeShape = " " + n.getIdentifier() + " [style=filled,color=green];";
						}
						System.out.println(n.getIdentifier());
						String nodeName = n.getIdentifier();
						String relation= "[style=filled, color=red, label=\"" + rname + "\", fontcolor=red]";
						String molShape = " " + molNode.getIdentifier() + " [style=filled,color=dodgerblue];";
						String dotSyntax = " " + molNode.getIdentifier() + " -> " + nodeName + relation + ";";
						bw.write(nodeShape);
						bw.write(molShape);
						bw.write(dotSyntax);
					}
					
				}
			}
		}
		bw.write("}');");
		bw.write("</script></body></html>");
		bw.close();
		//System.out.println("Generating Network..");
	}
	
	
	
	public void displayNetwork() {
		String url = this.getClass().getResource("displayData.html").toExternalForm();
		webView.getEngine().load(url);
		//System.out.println(url.toString());
	}
	

	public void createDefaults() {
		try {
			Network.defineDefaults();
			updateRelationSetList();
		} catch (CustomException e) {
			e.printStackTrace();
		}
	}
	

}
