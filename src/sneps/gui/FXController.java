package sneps.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
import javafx.util.Duration;
import sneps.exceptions.CaseFrameCannotBeRemovedException;
import sneps.exceptions.CaseFrameWithSetOfRelationsNotFoundException;
import sneps.exceptions.ContextNameDoesntExistException;
import sneps.exceptions.ContradictionFoundException;
import sneps.exceptions.CustomException;
import sneps.exceptions.DuplicateContextNameException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.IllegalIdentifierException;
import sneps.exceptions.NodeCannotBeRemovedException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.exceptions.RelationDoesntExistException;
import sneps.exceptions.SemanticNotFoundInNetworkException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.VariableNode;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.CFSignature;
import sneps.network.classes.CableTypeConstraint;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.RCFP;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.Semantic;
import sneps.network.classes.SemanticHierarchy;
import sneps.network.classes.SubDomainConstraint;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.ReportSet;
import sneps.network.classes.term.Base;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Variable;
import sneps.network.paths.AndPath;
import sneps.network.paths.BUnitPath;
import sneps.network.paths.BangPath;
import sneps.network.paths.CFResBUnitPath;
import sneps.network.paths.CFResFUnitPath;
import sneps.network.paths.ComposePath;
import sneps.network.paths.ConversePath;
import sneps.network.paths.DomainRestrictPath;
import sneps.network.paths.EmptyPath;
import sneps.network.paths.FUnitPath;
import sneps.network.paths.IrreflexiveRestrictPath;
import sneps.network.paths.KPlusPath;
import sneps.network.paths.KStarPath;
import sneps.network.paths.OrPath;
import sneps.network.paths.RangeRestrictPath;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snepslog.AP;
import sneps.snip.Report;
import sneps.snip.KnownInstances;

public class FXController implements Initializable {
	Network network = new Network();
	private String currentSelectedRelation;
	private double curX, curY, wireXStart, wireXEnd, wireYStart, wireYEnd;
	private ArrayList<Wire> wires = new ArrayList<Wire>();
	private Hashtable<String, Wire> wiresTable = new Hashtable<String, Wire>();
	private LinkedList<CableTypeConstraint> cables = new LinkedList<CableTypeConstraint>();
	private LinkedList<SubDomainConstraint> sdcs = new LinkedList<SubDomainConstraint>();
	private ArrayList<CFSignature> cfSignsArray = new ArrayList<CFSignature>();
	private Hashtable<String, sneps.network.paths.Path> paths = new Hashtable<String, sneps.network.paths.Path>();

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
	private ArrayList<BaseNodeShape> listOfBaseNodesDrawn = new ArrayList<BaseNodeShape>();
	private ArrayList<VarNodeShape> listOfVarNodesDrawn = new ArrayList<VarNodeShape>();
	private ArrayList<MolNodeShape> listOfMolNodesDrawn = new ArrayList<MolNodeShape>();
	private LinkedList<RCFP> rrcflist = new LinkedList<RCFP>();
	private int bpcounter = 0;

	@FXML
	private TextArea console;
	@FXML
	private Label nodeDetails, relationDetails, ppath, qpath, ppath1, qpath1;
	@FXML
	private TextField newRN, newRL, baseNodeIdentPop, caseFrameSTN, baseNodeID, overrideLimit, newNetName,
			cableMinNodes, cableMaxNodes, signPriority, contextName, semanticName, searchNodes;
	@FXML
	private ListView<String> relationSetList, relationSetList1, relationSetList2, cfRS, caseFramesList,
			caseFrameRelationList, nodesList, wiresList, variableNodesList, baseNodesList, caseFramesDrawList,
			relationOfDrawnCF, rrcfrslist, selectCFForSign, selectCFRelForSign, cablesList, sdcsList, listOfSigns,
			cfListForSign, cfSignsList, propoNodesList, propSet, pathsList, pathRelations, contextList, propoNodesList1,
			propSet1, composeList, propNodesListOfSelCont;
	@FXML
	private Group dragBaseNode, dragVarNode, wireModeBtn, dragMolNode;
	@FXML
	private AnchorPane drawArea, baseNodepopBox, varNodepopBox, drawMolCF, relationCFDrawSelect, consoleOutput;
	@FXML
	private ScrollPane drawScroll, consoleScroll;
	@FXML
	private MenuButton caseFrameChoice, netChoice1, netChoice2, netChoice3, baseNodeSemType, newRT, rrcfSem,
			resultSemType, cableSem, baseNodeSemTyPop, newRA, overrideAdjust, pathNodes1, pathNodes2, selectedPath,
			pathCF, definePathRelations, chooseContext, chooseContext1, parentSemChoice;
	@FXML
	private Button drawModeBtn, deleteModeBtn, moveModeBtn, createPathBTN, assertBTN, deduceBTN;
	@FXML
	private Rectangle wireBtnRect;
	@FXML
	private WebView webView;
	@FXML
	private Tab displayNetTab;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		nodesList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				String nodeName = nodesList.getSelectionModel().getSelectedItem();
				nodeDetails(nodeName);
				Node n = null;
				try {
					n = Network.getNode(nodeName);
				} catch (NodeNotFoundInNetworkException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!(n.getTerm() instanceof Variable)
						&& n.getSemantic().getSemanticType().equalsIgnoreCase("proposition")) {
					assertBTN.setDisable(false);
					deduceBTN.setDisable(false);
				} else {
					assertBTN.setDisable(true);
					deduceBTN.setDisable(true);
				}
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
		addRelsToListSign();
		createAdjusts();
		searchNodes();
		try {
			Network.loadNetworks();
			updateNetLists();
		} catch (FileNotFoundException e) {
			System.out.println("Files Not Found!");
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Files Not Found!");
			// e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Files Not Found!");
			// e.printStackTrace();
		}

		pathsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

//..........SNePS Log Methods..........................................
	// Controls The SNePS Log
	public void consoleHandler() {
		consoleScroll.setFitToHeight(true);
		consoleScroll.setFitToWidth(true);
		console.setText(":");
		console.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				ArrayList<String> lines = new ArrayList<String>();
				ArrayList<String> resLines = new ArrayList<String>();
				if (event.getCode() == KeyCode.ENTER) {
					for (String line : console.getText().split("\n")) {
						String tempLine = line.substring(1, line.length());
						lines.add(tempLine);
					}

					String cmd = lines.get(lines.size() - 1);

					String res = AP.executeSnepslogCommand(cmd);
					for (String rl : res.split("\n")) {
						resLines.add(rl);
					}
					for (int i = 0; i < resLines.size(); i++) {
						console.setText(console.getText() + "\n" + " -" + resLines.get(i));
					}
					console.setText(console.getText() + "\n" + ":");
					console.positionCaret(console.getLength());
					event.consume();
					ScrollDown();
					updateNodesList();
					updateCaseFramesList();
					updateRelationSetList();
					updatePathsList();
					updateSemanticLists();
					updateNetLists();
					updateListOfContexts();
					// System.out.println(cmd);
				}

				if (event.getCode() == KeyCode.BACK_SPACE) {
					if (!(console.getText().substring(console.getText().length() - 1, console.getText().length())
							.equalsIgnoreCase(":"))) {
						console.deletePreviousChar();
					}
					event.consume();

				}

				if (event.getCode() == KeyCode.DELETE) {
					event.consume();
				}

				if (event.getCode() == KeyCode.UP) {
					event.consume();
				}
			}

		});
	}

	// SNePS Log Scroll down
	public void ScrollDown() {
		Timer timer = new Timer();
		TimerTask titask = new TimerTask() {
			public void run() {
				consoleScroll.setVvalue(1.0);
			}

		};
		timer.schedule(titask, 20l);
	}
//..........END Of SNePS Log Methods...................................

//..........Drawing the Network Methods................................
	// Erase all drawn nodes
	public void eraseDrawnNetwork() {
		ButtonType yes = new ButtonType("Yes");
		ButtonType cancel = new ButtonType("Cancel");
		Alert a = new Alert(AlertType.NONE, "", yes, cancel);
		a.setTitle("Delete Drawn Objects");
		a.setHeaderText("Are you sure you want to delete all drawn objects?");
		a.setResizable(false);
		a.setContentText("You can't undo this action!!");
		a.showAndWait().ifPresent(response -> {
			if (response == yes) {
				drawArea.getChildren().clear();
				drawnWiresList.clear();
				drawnRelationsList.clear();
				arrows.clear();
				listOfBaseNodesDrawn.clear();
				listOfVarNodesDrawn.clear();
				listOfMolNodesDrawn.clear();
			} else if (response == cancel) {

			}
		});
	}

	// Drag Base Node to draw area
	public void dragMolNode() {

		dragMolNode.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dragMolNode.setCursor(Cursor.HAND);
			}
		});

		dragMolNode.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// System.out.println("onDragDetected");
				if (Network.getCaseFrames().size() != 0) {
					Dragboard db = dragMolNode.startDragAndDrop(TransferMode.ANY);
					ClipboardContent content = new ClipboardContent();
					content.putString("MolNode");
					db.setContent(content);
					event.consume();
				} else {
					Alert a = new Alert(AlertType.ERROR);
					a.setTitle("Error!");
					a.setContentText("There are no case frames in the network, please create a case frame.");
					a.showAndWait();
				}

			}

		});

	}

	// Drag Base Node to draw area
	public void dragBaseNode() {

		dragBaseNode.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dragBaseNode.setCursor(Cursor.HAND);
			}
		});

		dragBaseNode.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// System.out.println("onDragDetected");
				Dragboard db = dragBaseNode.startDragAndDrop(TransferMode.ANY);
				ClipboardContent content = new ClipboardContent();
				content.putString("BaseNode");
				db.setContent(content);
				event.consume();

			}

		});

	}

	// Drag Base Node to draw area
	public void dragVariableNode() {

		dragVarNode.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dragVarNode.setCursor(Cursor.HAND);
			}
		});

		dragVarNode.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// System.out.println("onDragDetected");
				Dragboard db = dragVarNode.startDragAndDrop(TransferMode.ANY);
				ClipboardContent content = new ClipboardContent();
				content.putString("VarNode");
				db.setContent(content);
				event.consume();

			}

		});

	}

	// Handling draw area
	public void drawArea() {

		drawArea.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				event.acceptTransferModes(TransferMode.ANY);
				if (db.getString() == "BaseNode") {

				}
				event.consume();
			}
		});

		drawArea.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				event.acceptTransferModes(TransferMode.ANY);
				if (db.getString() == "BaseNode") {
					curX = event.getX();
					curY = event.getY();
					baseNodepopBox.setVisible(true);
				} else if (db.getString() == "VarNode") {
					curX = event.getX();
					curY = event.getY();
					varNodepopBox.setVisible(true);
				}

				else if (db.getString() == "MoveNode" && moveMode == true) {
					curX = event.getX();
					curY = event.getY();
				} else if (db.getString() == "MolNode") {
					curX = event.getX();
					curY = event.getY();
					drawMolCF.setVisible(true);
				}
			}
		});

	}

	// Draw the molecular node
	public void submitMolNode() {
		String cfname = caseFramesDrawList.getSelectionModel().getSelectedItem();
		CaseFrame cf = null;
		try {
			cf = Network.getCaseFrame(cfname);
		} catch (CaseFrameWithSetOfRelationsNotFoundException e) {
			// TODO Auto-generated catch block
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
				if (deleteMode == true && wireMode == false) {
					double xPos = shape.getLayoutX() + 50;
					double yPos = shape.getLayoutY() + 40;
					for (int i = 0; i < listOfMolNodesDrawn.size(); i++) {
						MolNodeShape temp = listOfMolNodesDrawn.get(i);
						if (temp != null) {
							if (temp.getX() == xPos && temp.getY() == yPos) {
								listOfMolNodesDrawn.set(i, null);
								drawArea.getChildren().remove(shape);
							}
						}
					}

					for (int i = 0; i < drawnWiresList.size(); i++) {
						Line l = drawnWiresList.get(i);
						if (l != null) {
							Label lbl = drawnRelationsList.get(i);
							Circle cir = arrows.get(i);
							double xStart = l.getStartX();
							double yStart = l.getStartY();
							double xEnd = l.getEndX();
							double yEnd = l.getEndY();
							if (xStart == xPos && yStart == yPos) {
								drawnWiresList.set(i, null);
								drawnRelationsList.set(i, null);
								arrows.set(i, null);
								drawArea.getChildren().remove(l);
								drawArea.getChildren().remove(lbl);
								drawArea.getChildren().remove(cir);
							}
							if (xEnd == xPos && yEnd == yPos) {
								drawnWiresList.set(i, null);
								drawnRelationsList.set(i, null);
								arrows.set(i, null);
								drawArea.getChildren().remove(l);
								drawArea.getChildren().remove(lbl);
								drawArea.getChildren().remove(cir);
							}
						}
					}

				}

				if (wireMode == true) {
					LinkedList<Relation> relations = drawAreaRelations(cfname);
					relationOfDrawnCF.getItems().clear();
					for (Relation r : relations) {
						relationOfDrawnCF.getItems().add(r.getName());
					}
					relationCFDrawSelect.setVisible(true);
				}
			}

		});

		shape.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (moveMode == true) {
					Dragboard db = shape.startDragAndDrop(TransferMode.ANY);
					ClipboardContent content = new ClipboardContent();
					content.putString("MoveNode");
					db.setContent(content);
					event.consume();
				}

				if (wireMode == true) {
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
				if (moveMode == true && wireMode == false) {
					double oldPosX = shape.getLayoutX() + 50;
					double oldPosY = shape.getLayoutY() + 40;
					shape.setLayoutX(curX - 50);
					shape.setLayoutY(curY - 40);

					for (int x = 0; x < listOfMolNodesDrawn.size(); x++) {
						MolNodeShape temp = listOfMolNodesDrawn.get(x);
						if (temp == mns) {
							temp.setX(curX);
							temp.setY(curY);
							listOfMolNodesDrawn.set(x, temp);
						}
					}

					for (int i = 0; i < drawnWiresList.size(); i++) {
						Line l = drawnWiresList.get(i);
						if (l != null) {
							Label lbl = drawnRelationsList.get(i);
							Circle c = arrows.get(i);
							if (l.getStartX() == oldPosX && l.getStartY() == oldPosY) {
								drawnWiresList.get(i).setStartX(curX);
								drawnWiresList.get(i).setStartY(curY);

								l.setStartX(curX);
								l.setStartY(curY);

								lbl.setLayoutX((l.getStartX() + l.getEndX()) / 2);
								lbl.setLayoutY((l.getStartY() + l.getEndY()) / 2);
								drawnRelationsList.get(i).setLayoutX((l.getStartX() + l.getEndX()) / 2);
								drawnRelationsList.get(i).setLayoutY((l.getStartY() + l.getEndY()) / 2);

								double vx = l.getEndX() - l.getStartX();
								double vy = l.getEndY() - l.getStartY();
								double vn = Math.sqrt((vx * vx) + (vy * vy));
								double vnx = vx / vn;
								double vny = vy / vn;
								double td = (vx * vx) + (vy * vy);
								double d = Math.sqrt(td) - 50;
								double newX = l.getStartX() + (d * vnx);
								double newY = l.getStartY() + (d * vny);
								c.setCenterX(newX);
								c.setCenterY(newY);

								double ax = l.getStartX() - l.getEndX();
								double ay = l.getStartY() - l.getEndY();
								double bx = l.getStartX() - (l.getStartX() + 5);
								double by = 0;
								double a = Math.sqrt((ax * ax) + (ay * ay));
								double b = Math.sqrt((bx * bx) + (by * by));
								double ab = (ax * bx) + (ay * by);
								double abn = a * b;
								double angle = Math.acos(ab / abn) * (180 / Math.PI);
								if (l.getEndY() < l.getStartY()) {
									c.setRotate(-angle);
								} else {
									c.setRotate(angle);
								}
							} else if (l.getEndX() == oldPosX && l.getEndY() == oldPosY) {
								drawnWiresList.get(i).setEndX(curX);
								drawnWiresList.get(i).setEndY(curY);

								l.setEndX(curX);
								l.setEndY(curY);

								lbl.setLayoutX((l.getStartX() + l.getEndX()) / 2);
								lbl.setLayoutY((l.getStartY() + l.getEndY()) / 2);
								drawnRelationsList.get(i).setLayoutX((l.getStartX() + l.getEndX()) / 2);
								drawnRelationsList.get(i).setLayoutY((l.getStartY() + l.getEndY()) / 2);

								double vx = l.getEndX() - l.getStartX();
								double vy = l.getEndY() - l.getStartY();
								double vn = Math.sqrt((vx * vx) + (vy * vy));
								double vnx = vx / vn;
								double vny = vy / vn;
								double td = (vx * vx) + (vy * vy);
								double d = Math.sqrt(td) - 50;
								double newX = l.getStartX() + (d * vnx);
								double newY = l.getStartY() + (d * vny);
								c.setCenterX(newX);
								c.setCenterY(newY);

								double ax = l.getStartX() - l.getEndX();
								double ay = l.getStartY() - l.getEndY();
								double bx = l.getStartX() - (l.getStartX() + 5);
								double by = 0;
								double a = Math.sqrt((ax * ax) + (ay * ay));
								double b = Math.sqrt((bx * bx) + (by * by));
								double ab = (ax * bx) + (ay * by);
								double abn = a * b;
								double angle = Math.acos(ab / abn) * (180 / Math.PI);
								if (l.getEndY() < l.getStartY()) {
									c.setRotate(-angle);
								} else {
									c.setRotate(angle);
								}

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
				if (wireMode == true) {
					Dragboard db = event.getDragboard();
					if (db.getString() == "wireMode" && currentSelectedRelation != null) {
						wireXEnd = shape.getLayoutX() + 50;
						wireYEnd = shape.getLayoutY() + 40;
						WireRelation wr = new WireRelation(wireXStart, wireYStart, wireXEnd, wireYEnd);
						Line l = wr.drawLine();
						double vx = wireXEnd - wireXStart;
						double vy = wireYEnd - wireYStart;
						double vn = Math.sqrt((vx * vx) + (vy * vy));
						double vnx = vx / vn;
						double vny = vy / vn;
						double td = (vx * vx) + (vy * vy);
						double d = Math.sqrt(td) - 50;
						double newX = wireXStart + (d * vnx);
						double newY = wireYStart + (d * vny);
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
						double a = Math.sqrt((ax * ax) + (ay * ay));
						double b = Math.sqrt((bx * bx) + (by * by));
						double ab = (ax * bx) + (ay * by);
						double abn = a * b;
						double angle = Math.acos(ab / abn) * (180 / Math.PI);
						if (wireYEnd < wireYStart) {
							cir.setRotate(-angle);
						} else {
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
								if (deleteMode == true) {
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

	// Draw the relation
	public void drawRelation() {
		String rname = relationOfDrawnCF.getSelectionModel().getSelectedItem();
		currentSelectedRelation = rname;
		relationCFDrawSelect.setVisible(false);
	}

	// Submit base node drawn in draw area
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
		listOfBaseNodesDrawn.add(baseNode);
		Group shape = baseNode.makeShape();
		shape.setLayoutX(curX - 50);
		shape.setLayoutY(curY - 40);
		drawArea.getChildren().add(shape);
		baseNodepopBox.setVisible(false);
		// Delete the node
		shape.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (deleteMode == true && wireMode == false) {
					double xPos = shape.getLayoutX() + 50;
					double yPos = shape.getLayoutY() + 40;

					for (int i = 0; i < listOfBaseNodesDrawn.size(); i++) {
						BaseNodeShape temp = listOfBaseNodesDrawn.get(i);
						if (temp != null) {
							if (temp.getX() == xPos && temp.getY() == yPos) {
								listOfBaseNodesDrawn.set(i, null);
								drawArea.getChildren().remove(shape);
							}
						}
					}

					for (int i = 0; i < drawnWiresList.size(); i++) {
						Line l = drawnWiresList.get(i);
						Label lbl = drawnRelationsList.get(i);
						Circle cir = arrows.get(i);
						if (l != null) {
							double xEnd = l.getEndX();
							double yEnd = l.getEndY();
							if (xEnd == xPos && yEnd == yPos) {
								drawnWiresList.set(i, null);
								drawnRelationsList.set(i, null);
								arrows.set(i, null);
								drawArea.getChildren().remove(l);
								drawArea.getChildren().remove(lbl);
								drawArea.getChildren().remove(cir);
							}
						}
					}
				}

			}

		});

		shape.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (moveMode == true) {
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
				if (moveMode == true && wireMode == false) {
					double oldPosX = shape.getLayoutX() + 50;
					double oldPosY = shape.getLayoutY() + 40;
					shape.setLayoutX(curX - 50);
					shape.setLayoutY(curY - 40);

					for (int x = 0; x < listOfBaseNodesDrawn.size(); x++) {
						BaseNodeShape temp = listOfBaseNodesDrawn.get(x);
						if (temp == baseNode) {
							temp.setX(curX);
							temp.setY(curY);
							listOfBaseNodesDrawn.set(x, temp);
						}
					}

					for (int i = 0; i < drawnWiresList.size(); i++) {
						Line l = drawnWiresList.get(i);
						if (l != null) {
							Label lbl = drawnRelationsList.get(i);
							Circle c = arrows.get(i);
							if (l.getEndX() == oldPosX && l.getEndY() == oldPosY) {
								drawnWiresList.get(i).setEndX(curX);
								drawnWiresList.get(i).setEndY(curY);

								l.setEndX(curX);
								l.setEndY(curY);

								lbl.setLayoutX((l.getStartX() + l.getEndX()) / 2);
								lbl.setLayoutY((l.getStartY() + l.getEndY()) / 2);
								drawnRelationsList.get(i).setLayoutX((l.getStartX() + l.getEndX()) / 2);
								drawnRelationsList.get(i).setLayoutY((l.getStartY() + l.getEndY()) / 2);

								double vx = l.getEndX() - l.getStartX();
								double vy = l.getEndY() - l.getStartY();
								double vn = Math.sqrt((vx * vx) + (vy * vy));
								double vnx = vx / vn;
								double vny = vy / vn;
								double td = (vx * vx) + (vy * vy);
								double d = Math.sqrt(td) - 50;
								double newX = l.getStartX() + (d * vnx);
								double newY = l.getStartY() + (d * vny);
								c.setCenterX(newX);
								c.setCenterY(newY);
								double ax = l.getStartX() - l.getEndX();
								double ay = l.getStartY() - l.getEndY();
								double bx = l.getStartX() - (l.getStartX() + 5);
								double by = 0;
								double a = Math.sqrt((ax * ax) + (ay * ay));
								double b = Math.sqrt((bx * bx) + (by * by));
								double ab = (ax * bx) + (ay * by);
								double abn = a * b;
								double angle = Math.acos(ab / abn) * (180 / Math.PI);
								if (l.getEndY() < l.getStartY()) {
									c.setRotate(-angle);
								} else {
									c.setRotate(angle);
								}
							}
						}
					}
					event.consume();
				}
			}
		});

		// Drawing the wires
		shape.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				if (wireMode == true) {
					Dragboard db = event.getDragboard();
					if (db.getString() == "wireMode" && currentSelectedRelation != null) {
						wireXEnd = shape.getLayoutX() + 50;
						wireYEnd = shape.getLayoutY() + 40;
						WireRelation wr = new WireRelation(wireXStart, wireYStart, wireXEnd, wireYEnd);
						Line l = wr.drawLine();
						double vx = wireXEnd - wireXStart;
						double vy = wireYEnd - wireYStart;
						double vn = Math.sqrt((vx * vx) + (vy * vy));
						double vnx = vx / vn;
						double vny = vy / vn;
						double td = (vx * vx) + (vy * vy);
						double d = Math.sqrt(td) - 50;
						double newX = wireXStart + (d * vnx);
						double newY = wireYStart + (d * vny);
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
						double a = Math.sqrt((ax * ax) + (ay * ay));
						double b = Math.sqrt((bx * bx) + (by * by));
						double ab = (ax * bx) + (ay * by);
						double abn = a * b;
						double angle = Math.acos(ab / abn) * (180 / Math.PI);
						if (wireYEnd < wireYStart) {
							cir.setRotate(-angle);
						} else {
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
								if (deleteMode == true) {
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

	// Submit base node drawn in draw area
	public void submitNewBaseNode() {
		String identifier = baseNodeIdentPop.getText();
		String semType = baseNodeSemTyPop.getText();
		BaseNodeShape baseNode = new BaseNodeShape(identifier, semType, curX, curY);
		Group shape = baseNode.makeShape();
		shape.setLayoutX(curX - 50);
		shape.setLayoutY(curY - 40);
		drawArea.getChildren().add(shape);
		listOfBaseNodesDrawn.add(baseNode);
		baseNodepopBox.setVisible(false);
		baseNodeIdentPop.setText("");
		baseNodeSemTyPop.setText("");
		// Delete the node
		shape.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (deleteMode == true && wireMode == false) {
					double xPos = shape.getLayoutX() + 50;
					double yPos = shape.getLayoutY() + 40;

					for (int i = 0; i < listOfBaseNodesDrawn.size(); i++) {
						BaseNodeShape temp = listOfBaseNodesDrawn.get(i);
						if (temp != null) {
							if (temp.getX() == xPos && temp.getY() == yPos) {
								listOfBaseNodesDrawn.set(i, null);
								drawArea.getChildren().remove(shape);
							}
						}
					}

					for (int i = 0; i < drawnWiresList.size(); i++) {
						Line l = drawnWiresList.get(i);
						Label lbl = drawnRelationsList.get(i);
						Circle cir = arrows.get(i);
						if (l != null) {
							double xEnd = l.getEndX();
							double yEnd = l.getEndY();
							if (xEnd == xPos && yEnd == yPos) {
								drawnWiresList.set(i, null);
								drawnRelationsList.set(i, null);
								arrows.set(i, null);
								drawArea.getChildren().remove(l);
								drawArea.getChildren().remove(lbl);
								drawArea.getChildren().remove(cir);
							}
						}
					}
				}

			}

		});

		shape.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (moveMode == true) {
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
				if (moveMode == true && wireMode == false) {
					double oldPosX = shape.getLayoutX() + 50;
					double oldPosY = shape.getLayoutY() + 40;
					shape.setLayoutX(curX - 50);
					shape.setLayoutY(curY - 40);

					for (int x = 0; x < listOfBaseNodesDrawn.size(); x++) {
						BaseNodeShape temp = listOfBaseNodesDrawn.get(x);
						if (temp == baseNode) {
							temp.setX(curX);
							temp.setY(curY);
							listOfBaseNodesDrawn.set(x, temp);
						}
					}

					for (int i = 0; i < drawnWiresList.size(); i++) {
						Line l = drawnWiresList.get(i);
						if (l != null) {
							Label lbl = drawnRelationsList.get(i);
							Circle c = arrows.get(i);
							if (l.getEndX() == oldPosX && l.getEndY() == oldPosY) {
								drawnWiresList.get(i).setEndX(curX);
								drawnWiresList.get(i).setEndY(curY);

								l.setEndX(curX);
								l.setEndY(curY);

								lbl.setLayoutX((l.getStartX() + l.getEndX()) / 2);
								lbl.setLayoutY((l.getStartY() + l.getEndY()) / 2);
								drawnRelationsList.get(i).setLayoutX((l.getStartX() + l.getEndX()) / 2);
								drawnRelationsList.get(i).setLayoutY((l.getStartY() + l.getEndY()) / 2);

								double vx = l.getEndX() - l.getStartX();
								double vy = l.getEndY() - l.getStartY();
								double vn = Math.sqrt((vx * vx) + (vy * vy));
								double vnx = vx / vn;
								double vny = vy / vn;
								double td = (vx * vx) + (vy * vy);
								double d = Math.sqrt(td) - 50;
								double newX = l.getStartX() + (d * vnx);
								double newY = l.getStartY() + (d * vny);
								c.setCenterX(newX);
								c.setCenterY(newY);
								double ax = l.getStartX() - l.getEndX();
								double ay = l.getStartY() - l.getEndY();
								double bx = l.getStartX() - (l.getStartX() + 5);
								double by = 0;
								double a = Math.sqrt((ax * ax) + (ay * ay));
								double b = Math.sqrt((bx * bx) + (by * by));
								double ab = (ax * bx) + (ay * by);
								double abn = a * b;
								double angle = Math.acos(ab / abn) * (180 / Math.PI);
								if (l.getEndY() < l.getStartY()) {
									c.setRotate(-angle);
								} else {
									c.setRotate(angle);
								}
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
				if (wireMode == true) {
					Dragboard db = event.getDragboard();
					if (db.getString() == "wireMode" && currentSelectedRelation != null) {
						wireXEnd = shape.getLayoutX() + 50;
						wireYEnd = shape.getLayoutY() + 40;
						WireRelation wr = new WireRelation(wireXStart, wireYStart, wireXEnd, wireYEnd);
						Line l = wr.drawLine();
						double vx = wireXEnd - wireXStart;
						double vy = wireYEnd - wireYStart;
						double vn = Math.sqrt((vx * vx) + (vy * vy));
						double vnx = vx / vn;
						double vny = vy / vn;
						double td = (vx * vx) + (vy * vy);
						double d = Math.sqrt(td) - 50;
						double newX = wireXStart + (d * vnx);
						double newY = wireYStart + (d * vny);
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
						double a = Math.sqrt((ax * ax) + (ay * ay));
						double b = Math.sqrt((bx * bx) + (by * by));
						double ab = (ax * bx) + (ay * by);
						double abn = a * b;
						double angle = Math.acos(ab / abn) * (180 / Math.PI);
						if (wireYEnd < wireYStart) {
							cir.setRotate(-angle);
						} else {
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
								if (deleteMode == true) {
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

	// Submit existing variable node drawn in draw area
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
		listOfVarNodesDrawn.add(varNodeS);
		Group shape = varNodeS.makeShape();
		shape.setLayoutX(curX - 50);
		shape.setLayoutY(curY - 40);
		drawArea.getChildren().add(shape);
		varNodepopBox.setVisible(false);
		// Delete the node
		shape.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (deleteMode == true && wireMode == false) {
					double xPos = shape.getLayoutX() + 50;
					double yPos = shape.getLayoutY() + 40;

					for (int i = 0; i < listOfVarNodesDrawn.size(); i++) {
						VarNodeShape temp = listOfVarNodesDrawn.get(i);
						if (temp != null) {
							if (temp.getX() == xPos && temp.getY() == yPos) {
								listOfVarNodesDrawn.set(i, null);
								drawArea.getChildren().remove(shape);
							}
						}
					}

					for (int i = 0; i < drawnWiresList.size(); i++) {
						Line l = drawnWiresList.get(i);
						Label lbl = drawnRelationsList.get(i);
						Circle cir = arrows.get(i);
						if (l != null) {
							double xEnd = l.getEndX();
							double yEnd = l.getEndY();
							if (xEnd == xPos && yEnd == yPos) {
								drawnWiresList.set(i, null);
								drawnRelationsList.set(i, null);
								arrows.set(i, null);
								drawArea.getChildren().remove(l);
								drawArea.getChildren().remove(lbl);
								drawArea.getChildren().remove(cir);
							}
						}
					}
				}

			}

		});

		shape.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (moveMode == true) {
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
				if (moveMode == true && wireMode == false) {
					double oldPosX = shape.getLayoutX() + 50;
					double oldPosY = shape.getLayoutY() + 40;
					shape.setLayoutX(curX - 50);
					shape.setLayoutY(curY - 40);

					for (int x = 0; x < listOfVarNodesDrawn.size(); x++) {
						VarNodeShape temp = listOfVarNodesDrawn.get(x);
						if (temp == varNodeS) {
							temp.setX(curX);
							temp.setY(curY);
							listOfVarNodesDrawn.set(x, temp);
						}
					}

					for (int i = 0; i < drawnWiresList.size(); i++) {
						Line l = drawnWiresList.get(i);
						if (l != null) {
							Label lbl = drawnRelationsList.get(i);
							Circle c = arrows.get(i);
							if (l.getEndX() == oldPosX && l.getEndY() == oldPosY) {
								drawnWiresList.get(i).setEndX(curX);
								drawnWiresList.get(i).setEndY(curY);

								l.setEndX(curX);
								l.setEndY(curY);

								lbl.setLayoutX((l.getStartX() + l.getEndX()) / 2);
								lbl.setLayoutY((l.getStartY() + l.getEndY()) / 2);
								drawnRelationsList.get(i).setLayoutX((l.getStartX() + l.getEndX()) / 2);
								drawnRelationsList.get(i).setLayoutY((l.getStartY() + l.getEndY()) / 2);

								double vx = l.getEndX() - l.getStartX();
								double vy = l.getEndY() - l.getStartY();
								double vn = Math.sqrt((vx * vx) + (vy * vy));
								double vnx = vx / vn;
								double vny = vy / vn;
								double td = (vx * vx) + (vy * vy);
								double d = Math.sqrt(td) - 50;
								double newX = l.getStartX() + (d * vnx);
								double newY = l.getStartY() + (d * vny);
								c.setCenterX(newX);
								c.setCenterY(newY);
								double ax = l.getStartX() - l.getEndX();
								double ay = l.getStartY() - l.getEndY();
								double bx = l.getStartX() - (l.getStartX() + 5);
								double by = 0;
								double a = Math.sqrt((ax * ax) + (ay * ay));
								double b = Math.sqrt((bx * bx) + (by * by));
								double ab = (ax * bx) + (ay * by);
								double abn = a * b;
								double angle = Math.acos(ab / abn) * (180 / Math.PI);
								if (l.getEndY() < l.getStartY()) {
									c.setRotate(-angle);
								} else {
									c.setRotate(angle);
								}
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
				if (wireMode == true) {
					Dragboard db = event.getDragboard();
					if (db.getString() == "wireMode" && currentSelectedRelation != null) {
						wireXEnd = shape.getLayoutX() + 50;
						wireYEnd = shape.getLayoutY() + 40;
						WireRelation wr = new WireRelation(wireXStart, wireYStart, wireXEnd, wireYEnd);
						Line l = wr.drawLine();
						double vx = wireXEnd - wireXStart;
						double vy = wireYEnd - wireYStart;
						double vn = Math.sqrt((vx * vx) + (vy * vy));
						double vnx = vx / vn;
						double vny = vy / vn;
						double td = (vx * vx) + (vy * vy);
						double d = Math.sqrt(td) - 50;
						double newX = wireXStart + (d * vnx);
						double newY = wireYStart + (d * vny);
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
						double a = Math.sqrt((ax * ax) + (ay * ay));
						double b = Math.sqrt((bx * bx) + (by * by));
						double ab = (ax * bx) + (ay * by);
						double abn = a * b;
						double angle = Math.acos(ab / abn) * (180 / Math.PI);
						if (wireYEnd < wireYStart) {
							cir.setRotate(-angle);
						} else {
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
								if (deleteMode == true) {
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

	// Submit new variable node drawn in draw area
	public void submitNewVariableNode() {
		Node n = Network.buildVariableNode();
		String identifier = n.getIdentifier();
		VarNodeShape varNode = new VarNodeShape(identifier, curX, curY);
		Group shape = varNode.makeShape();
		shape.setLayoutX(curX - 50);
		shape.setLayoutY(curY - 40);
		drawArea.getChildren().add(shape);
		listOfVarNodesDrawn.add(varNode);
		varNodepopBox.setVisible(false);
		updateNodesList();
		// Delete the node
		shape.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (deleteMode == true && wireMode == false) {
					double xPos = shape.getLayoutX() + 50;
					double yPos = shape.getLayoutY() + 40;

					for (int i = 0; i < listOfVarNodesDrawn.size(); i++) {
						VarNodeShape temp = listOfVarNodesDrawn.get(i);
						if (temp != null) {
							if (temp.getX() == xPos && temp.getY() == yPos) {
								listOfVarNodesDrawn.set(i, null);
								drawArea.getChildren().remove(shape);
							}
						}
					}

					for (int i = 0; i < drawnWiresList.size(); i++) {
						Line l = drawnWiresList.get(i);
						Label lbl = drawnRelationsList.get(i);
						Circle cir = arrows.get(i);
						if (l != null) {
							double xEnd = l.getEndX();
							double yEnd = l.getEndY();
							if (xEnd == xPos && yEnd == yPos) {
								drawnWiresList.set(i, null);
								drawnRelationsList.set(i, null);
								arrows.set(i, null);
								drawArea.getChildren().remove(l);
								drawArea.getChildren().remove(lbl);
								drawArea.getChildren().remove(cir);
							}
						}
					}
				}

			}

		});

		shape.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (moveMode == true) {
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
				if (moveMode == true && wireMode == false) {
					double oldPosX = shape.getLayoutX() + 50;
					double oldPosY = shape.getLayoutY() + 40;
					shape.setLayoutX(curX - 50);
					shape.setLayoutY(curY - 40);

					for (int x = 0; x < listOfVarNodesDrawn.size(); x++) {
						VarNodeShape temp = listOfVarNodesDrawn.get(x);
						if (temp == varNode) {
							temp.setX(curX);
							temp.setY(curY);
							listOfVarNodesDrawn.set(x, temp);
						}
					}

					for (int i = 0; i < drawnWiresList.size(); i++) {
						Line l = drawnWiresList.get(i);
						if (l != null) {
							Label lbl = drawnRelationsList.get(i);
							Circle c = arrows.get(i);
							if (l.getEndX() == oldPosX && l.getEndY() == oldPosY) {
								drawnWiresList.get(i).setEndX(curX);
								drawnWiresList.get(i).setEndY(curY);

								l.setEndX(curX);
								l.setEndY(curY);

								lbl.setLayoutX((l.getStartX() + l.getEndX()) / 2);
								lbl.setLayoutY((l.getStartY() + l.getEndY()) / 2);
								drawnRelationsList.get(i).setLayoutX((l.getStartX() + l.getEndX()) / 2);
								drawnRelationsList.get(i).setLayoutY((l.getStartY() + l.getEndY()) / 2);

								double vx = l.getEndX() - l.getStartX();
								double vy = l.getEndY() - l.getStartY();
								double vn = Math.sqrt((vx * vx) + (vy * vy));
								double vnx = vx / vn;
								double vny = vy / vn;
								double td = (vx * vx) + (vy * vy);
								double d = Math.sqrt(td) - 50;
								double newX = l.getStartX() + (d * vnx);
								double newY = l.getStartY() + (d * vny);
								c.setCenterX(newX);
								c.setCenterY(newY);
								double ax = l.getStartX() - l.getEndX();
								double ay = l.getStartY() - l.getEndY();
								double bx = l.getStartX() - (l.getStartX() + 5);
								double by = 0;
								double a = Math.sqrt((ax * ax) + (ay * ay));
								double b = Math.sqrt((bx * bx) + (by * by));
								double ab = (ax * bx) + (ay * by);
								double abn = a * b;
								double angle = Math.acos(ab / abn) * (180 / Math.PI);
								if (l.getEndY() < l.getStartY()) {
									c.setRotate(-angle);
								} else {
									c.setRotate(angle);
								}
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
				if (wireMode == true) {
					Dragboard db = event.getDragboard();
					if (db.getString() == "wireMode" && currentSelectedRelation != null) {
						wireXEnd = shape.getLayoutX() + 50;
						wireYEnd = shape.getLayoutY() + 40;
						WireRelation wr = new WireRelation(wireXStart, wireYStart, wireXEnd, wireYEnd);
						Line l = wr.drawLine();
						double vx = wireXEnd - wireXStart;
						double vy = wireYEnd - wireYStart;
						double vn = Math.sqrt((vx * vx) + (vy * vy));
						double vnx = vx / vn;
						double vny = vy / vn;
						double td = (vx * vx) + (vy * vy);
						double d = Math.sqrt(td) - 50;
						double newX = wireXStart + (d * vnx);
						double newY = wireYStart + (d * vny);
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
						double a = Math.sqrt((ax * ax) + (ay * ay));
						double b = Math.sqrt((bx * bx) + (by * by));
						double ab = (ax * bx) + (ay * by);
						double abn = a * b;
						double angle = Math.acos(ab / abn) * (180 / Math.PI);
						if (wireYEnd < wireYStart) {
							cir.setRotate(-angle);
						} else {
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
								if (deleteMode == true) {
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

	// Selects the delete node/relation mode
	public void deleteMode() {
		moveMode = false;
		deleteMode = true;
		deleteModeBtn.getStyleClass().clear();
		drawModeBtn.getStyleClass().clear();
		moveModeBtn.getStyleClass().clear();
		drawModeBtn.getStyleClass().add("customBtn");
		moveModeBtn.getStyleClass().add("customBtn");
		deleteModeBtn.getStyleClass().add("customBtnSelected");
		popUpNotification("Delete Mode", "Delete Node", "Delete Node Mode Selected", 1);
	}

	// Selects the move node mode
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
		popUpNotification("Move Mode", "Move Node", "Move Node Mode Selected", 1);
	}

	// Selects the draw mode
	public void normalMode() {
		moveMode = false;
		deleteMode = false;
		deleteModeBtn.getStyleClass().clear();
		drawModeBtn.getStyleClass().clear();
		moveModeBtn.getStyleClass().clear();
		deleteModeBtn.getStyleClass().add("customBtn");
		moveModeBtn.getStyleClass().add("customBtn");
		drawModeBtn.getStyleClass().add("customBtnSelected");
		// popUpNotification("Draw Mode", "Draw Nodes", "Draw Nodes & Wires Mode
		// Selected", 1);

	}

	// Creates the drawn base nodes in the network
	public void submitDrawnBaseNodes() {
		for (int i = 0; i < listOfBaseNodesDrawn.size(); i++) {
			BaseNodeShape temp = listOfBaseNodesDrawn.get(i);
			if (temp != null) {
				String semantic = temp.semtantic;
				Semantic semType = new Semantic(semantic);
				String identifier = temp.identifier;
				try {
					Network.buildBaseNode(identifier, semType);
				} catch (NotAPropositionNodeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NodeNotFoundInNetworkException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalIdentifierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		updateNodesList();
	}

	// Create the drawn molecular nodes in the network
	public void submitDrawnMolNodes() {
		for (int i = 0; i < listOfMolNodesDrawn.size(); i++) {
			MolNodeShape temp = listOfMolNodesDrawn.get(i);
			if (temp != null) {
				String nodeID = temp.getIdentifier();
				if (nodeID == null) {
					ArrayList<Wire> wires = new ArrayList<Wire>();
					RelationsRestrictedCaseFrame cf = (RelationsRestrictedCaseFrame) temp.getCf();
					double x = temp.getX();
					double y = temp.getY();
					for (int j = 0; j < drawnWiresList.size(); j++) {
						Line l = drawnWiresList.get(j);
						if (l != null) {
							double xStart = l.getStartX();
							double yStart = l.getStartY();
							double xEnd = l.getEndX();
							double yEnd = l.getEndY();

							if ((x == xStart) && (y == yStart)) {
								// Check if connected to the wire is a base node and create it
								for (int s = 0; s < listOfBaseNodesDrawn.size(); s++) {
									BaseNodeShape tempBase = listOfBaseNodesDrawn.get(s);
									if (tempBase != null) {
										String identifier = tempBase.identifier;
										double xPos = tempBase.getX();
										double yPos = tempBase.getY();
										// System.out.println(xPos + " " + yPos);
										if ((xPos == xEnd) && (yPos == yEnd)) {
											// System.out.println("There is a base node here!!");
											Label lbl = drawnRelationsList.get(j);
											String rname = lbl.getText();
											System.out.println(rname);
											Relation r = null;
											try {
												r = Network.getRelation(rname);
											} catch (RelationDoesntExistException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
											Node n = null;
											try {
												n = Network.getNode(identifier);
											} catch (NodeNotFoundInNetworkException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											System.out.println(n.getIdentifier());
											Wire w = new Wire(r, n);
											wires.add(w);
										}
									}
								}
								// Check if variable node
								for (int s = 0; s < listOfVarNodesDrawn.size(); s++) {
									VarNodeShape tempVar = listOfVarNodesDrawn.get(s);
									if (tempVar != null) {
										String identifier = tempVar.identifier;
										double xPos = tempVar.getX();
										double yPos = tempVar.getY();
										// System.out.println(xPos + " " + yPos);
										if ((xPos == xEnd) && (yPos == yEnd)) {
											// System.out.println("There is a base node here!!");
											Label lbl = drawnRelationsList.get(j);
											String rname = lbl.getText();
											System.out.println(rname);
											Relation r = null;
											try {
												r = Network.getRelation(rname);
											} catch (RelationDoesntExistException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
											Node n = null;
											try {
												n = Network.getNode(identifier);
											} catch (NodeNotFoundInNetworkException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											if (n != null) {
												Wire w = new Wire(r, n);
												wires.add(w);
												System.out.println(n.getIdentifier());
											}
										}
									}
								}
								// Check if mol Node
								for (int k = 0; k < listOfMolNodesDrawn.size(); k++) {
									MolNodeShape molTemp = listOfMolNodesDrawn.get(k);
									if (molTemp != null) {
										double xPos = molTemp.getX();
										double yPos = molTemp.getY();

										if ((xPos == xEnd) && (yPos == yEnd)) {
											// System.out.println("There is a base node here!!");
											Label lbl = drawnRelationsList.get(j);
											String rname = lbl.getText();
											System.out.println(rname);
											Node n = null;
											n = submitDrawnMolHelper(molTemp);

											Relation r = null;
											try {
												r = Network.getRelation(rname);
											} catch (RelationDoesntExistException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											if (n != null) {
												Wire w = new Wire(r, n);
												wires.add(w);
												System.out.println(n.getIdentifier());
											}
										}
									}
								}
							}
						}
					}
					try {
						Node newMol = Network.buildMolecularNode(wires, cf);
						listOfMolNodesDrawn.get(i).setIdentifier(newMol.getIdentifier());
						popUpNotification("Build Nodes", "Build Nodes Successful", "Build Node Successful", 2);
					} catch (Exception e) {
						popUpNotification("Build Nodes", "Build Nodes NOT Successful", "Build Nodes NOT Successful!!",
								2);
						e.printStackTrace();
					}
				}
			}
		}
	}

	public Node submitDrawnMolHelper(MolNodeShape mns) {
		Node newMol = null;
		if (mns.getIdentifier() != null) {
			try {
				newMol = Network.getNode(mns.getIdentifier());
			} catch (NodeNotFoundInNetworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			ArrayList<Wire> wires = new ArrayList<Wire>();
			RelationsRestrictedCaseFrame cf = (RelationsRestrictedCaseFrame) mns.getCf();
			double x = mns.getX();
			double y = mns.getY();
			for (int j = 0; j < drawnWiresList.size(); j++) {
				Line l = drawnWiresList.get(j);
				if (l != null) {
					double xStart = l.getStartX();
					double yStart = l.getStartY();
					double xEnd = l.getEndX();
					double yEnd = l.getEndY();

					if ((x == xStart) && (y == yStart)) {
						// Check if connected to the wire is a base node and create it
						for (int s = 0; s < listOfBaseNodesDrawn.size(); s++) {
							BaseNodeShape tempBase = listOfBaseNodesDrawn.get(s);
							if (tempBase != null) {
								String identifier = tempBase.identifier;
								double xPos = tempBase.getX();
								double yPos = tempBase.getY();
								// System.out.println(xPos + " " + yPos);
								if ((xPos == xEnd) && (yPos == yEnd)) {
									// System.out.println("There is a base node here!!");
									Label lbl = drawnRelationsList.get(j);
									String rname = lbl.getText();
									System.out.println(rname);
									Relation r = null;
									try {
										r = Network.getRelation(rname);
									} catch (RelationDoesntExistException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Node n = null;
									try {
										n = Network.getNode(identifier);
									} catch (NodeNotFoundInNetworkException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									System.out.println(n.getIdentifier());
									Wire w = new Wire(r, n);
									wires.add(w);
								}
							}
						}
						// Check if variable node
						for (int i = 0; i < listOfVarNodesDrawn.size(); i++) {
							VarNodeShape tempVar = listOfVarNodesDrawn.get(i);
							if (tempVar != null) {
								String identifier = tempVar.identifier;
								double xPos = tempVar.x;
								double yPos = tempVar.y;
								// System.out.println(xPos + " " + yPos);
								if ((xPos == xEnd) && (yPos == yEnd)) {
									// System.out.println("There is a base node here!!");
									Label lbl = drawnRelationsList.get(j);
									String rname = lbl.getText();
									System.out.println(rname);
									Relation r = null;
									try {
										r = Network.getRelation(rname);
									} catch (RelationDoesntExistException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Node n = null;
									try {
										n = Network.getNode(identifier);
									} catch (NodeNotFoundInNetworkException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									if (n != null) {
										Wire w = new Wire(r, n);
										wires.add(w);
										System.out.println(n.getIdentifier());
									}
								}
							}
						}
						// Check if mol Node
						for (int k = 0; k < listOfMolNodesDrawn.size(); k++) {
							MolNodeShape molTemp = listOfMolNodesDrawn.get(k);
							if (molTemp != null) {
								double xPos = molTemp.getX();
								double yPos = molTemp.getY();

								if ((xPos == xEnd) && (yPos == yEnd)) {
									// System.out.println("There is a base node here!!");
									Label lbl = drawnRelationsList.get(j);
									String rname = lbl.getText();
									System.out.println(rname);
									Node n = null;
									n = submitDrawnMolHelper(molTemp);

									Relation r = null;
									try {
										r = Network.getRelation(rname);
									} catch (RelationDoesntExistException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									if (n != null) {
										Wire w = new Wire(r, n);
										wires.add(w);
										System.out.println(n.getIdentifier());
									}
								}
							}
						}
					}
				}
			}
			try {
				newMol = Network.buildMolecularNode(wires, cf);
				for (int g = 0; g < listOfMolNodesDrawn.size(); g++) {
					MolNodeShape tempMNS = listOfMolNodesDrawn.get(g);
					if (tempMNS == mns) {
						listOfMolNodesDrawn.get(g).setIdentifier(newMol.getIdentifier());
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return newMol;
	}

	// submit drawn Nodes
	public void submitDrawnNodes() {
		submitDrawnBaseNodes();
		submitDrawnMolNodes();
		updateNodesList();
	}

	// Selects the wire mode
	public void wireMode() {
		wireModeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (wireMode == false && moveMode == false && deleteMode == false) {
					wireMode = true;
					wireBtnRect.setVisible(true);
					popUpNotification("Wire Mode", "Draw Wire", "Draw Wire Mode Selected", 1);
				} else {
					wireMode = false;
					wireBtnRect.setVisible(false);
				}
			}

		});
	}

	public void popUpNotification(String title, String header, String content, int duration) {
		Alert alert = new Alert(Alert.AlertType.NONE);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		Timeline idlestage = new Timeline(new KeyFrame(Duration.seconds(duration), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				alert.setResult(ButtonType.CANCEL);
				alert.hide();
			}
		}));
		idlestage.setCycleCount(1);
		idlestage.play();
		alert.showAndWait();
	}

	// Returns a list of the relations of a case frame
	public LinkedList<Relation> drawAreaRelations(String caseFrame) {
		CaseFrame cf = null;
		try {
			cf = Network.getCaseFrame(caseFrame);
		} catch (CaseFrameWithSetOfRelationsNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cf.getRelations();
	}

//..........END of Drawing the Network Methods..........................

//..........Traditional Menu Methods...................................
	// Update Semantic Type Lists

	public void updateSemanticLists() {
		Hashtable<String, Semantic> sems = SemanticHierarchy.getSemantics();
		baseNodeSemType.getItems().clear();
		newRT.getItems().clear();
		rrcfSem.getItems().clear();
		resultSemType.getItems().clear();
		cableSem.getItems().clear();
		baseNodeSemTyPop.getItems().clear();
		for (Entry<String, Semantic> entry : sems.entrySet()) {
			String semantic = entry.getKey();
			MenuItem mi1 = new MenuItem(semantic);
			mi1.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					baseNodeSemType.setText(mi1.getText());
				}

			});

			MenuItem mi2 = new MenuItem(semantic);
			mi2.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					newRT.setText(mi2.getText());
				}

			});

			MenuItem mi3 = new MenuItem(semantic);
			mi3.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					rrcfSem.setText(mi3.getText());
				}

			});

			MenuItem mi4 = new MenuItem(semantic);
			mi4.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					resultSemType.setText(mi4.getText());
				}

			});

			MenuItem mi5 = new MenuItem(semantic);
			mi5.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					cableSem.setText(mi5.getText());
				}

			});

			MenuItem mi6 = new MenuItem(semantic);
			mi6.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					baseNodeSemTyPop.setText(mi6.getText());
				}

			});

			baseNodeSemType.getItems().add(mi1);
			newRT.getItems().add(mi2);
			rrcfSem.getItems().add(mi3);
			resultSemType.getItems().add(mi4);
			cableSem.getItems().add(mi5);
			baseNodeSemTyPop.getItems().add(mi6);
		}
	}

	// Parent Semantics
	public void parentSemantics() {
		MenuItem individual = new MenuItem(Semantic.individual.getSemanticType());
		MenuItem infimum = new MenuItem(Semantic.infimum.getSemanticType());
		individual.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				parentSemChoice.setText(individual.getText());
			}

		});

		infimum.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				parentSemChoice.setText(infimum.getText());
			}

		});

		parentSemChoice.getItems().add(individual);
		parentSemChoice.getItems().add(infimum);

	}

	// Create new semantic type
	public void createSemanticType() {
		String semName = semanticName.getText();
		SemanticHierarchy.createSemanticType(semName);
		updateSemanticLists();
		popUpNotification("Create Semantic Type", "Semantic Created Successfully",
				"Semantic: " + semName + " Created Successfully", 2);
	}

	// Adjusts
	public void createAdjusts() {
		MenuItem none = new MenuItem("None");
		none.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				newRA.setText("None");
				// System.out.println("None");
			}

		});

		MenuItem expand = new MenuItem("Expand");
		expand.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				newRA.setText("Expand");
				// System.out.println("Expand");
			}

		});

		MenuItem reduce = new MenuItem("Reduce");
		reduce.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				newRA.setText("Reduce");
				// System.out.println("Reduce");
			}

		});

		MenuItem none1 = new MenuItem("None");
		none1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				overrideAdjust.setText("None");
				// System.out.println("None");
			}

		});

		MenuItem expand1 = new MenuItem("Expand");
		expand1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				overrideAdjust.setText("Expand");
				// System.out.println("Expand");
			}

		});

		MenuItem reduce1 = new MenuItem("Reduce");
		reduce1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				overrideAdjust.setText("Reduce");
				// System.out.println("Reduce");
			}

		});

		newRA.getItems().add(none);
		newRA.getItems().add(expand);
		newRA.getItems().add(reduce);
		overrideAdjust.getItems().add(none1);
		overrideAdjust.getItems().add(expand1);
		overrideAdjust.getItems().add(reduce1);
	}

	// Define relation menu-based
	public void defineRelation() {
		String name = newRN.getText();
		String type = newRT.getText();
		String adjust = newRA.getText();
		int limit = Integer.parseInt(newRL.getText());
		if (limit > 0) {
			Network.defineRelation(name, type, adjust, limit);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Relation created");
			alert.setHeaderText("Relation is created successfully");
			alert.setContentText("The relation " + name + " is created successfully!");
			alert.showAndWait();
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Relation NOT created");
			alert.setHeaderText("Relation is NOT created successfully");
			alert.setContentText("Limit should be greater than 0!");
			alert.showAndWait();
		}

		newRN.setText("");
		newRT.setText("");
		newRA.setText("");
		newRL.setText("");

		updateRelationSetList();

	}

	// Undefine relation menu-based
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
			updateNodesList();
			updateCaseFramesList();
		} catch (CaseFrameCannotBeRemovedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Updates all relations list
	public void updateRelationSetList() {
		Hashtable<String, Relation> relations = Network.getRelations();
		ArrayList<String> sortedRelations = new ArrayList<String>();
		relationSetList.getItems().clear();
		relationSetList1.getItems().clear();
		pathRelations.getItems().clear();
		definePathRelations.getItems().clear();
		for (Entry<String, Relation> entry : relations.entrySet()) {
			String key = entry.getKey();
			sortedRelations.add(key);
		}
		Collections.sort(sortedRelations);
		for (int i = 0; i < sortedRelations.size(); i++) {
			relationSetList.getItems().add(sortedRelations.get(i));
			relationSetList1.getItems().add(sortedRelations.get(i));
			pathRelations.getItems().add(sortedRelations.get(i));
			MenuItem mi = new MenuItem(sortedRelations.get(i));
			mi.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					definePathRelations.setText(mi.getText());
				}

			});
			definePathRelations.getItems().add(mi);
		}
	}

	// This method adds selected relations from the network to a create a new
	// caseFrame with these relations/ Menu-based
	public void addToCFRS() {
		String selectedRelation = relationSetList1.getSelectionModel().getSelectedItem();
		if (selectedRelation != null) {
			cfRS.getItems().add(selectedRelation);
			relationSetList1.getItems().remove(selectedRelation);
		}
	}

	// This method removes selected relations from a case frame created from
	// menu-based
	public void removeCFRS() {
		String selectedRelation = cfRS.getSelectionModel().getSelectedItem();
		if (selectedRelation != null) {
			relationSetList1.getItems().add(selectedRelation);
			cfRS.getItems().remove(selectedRelation);
		}
	}

	// Adds case frame to the network - menu-based
	public void submitCaseFrame() {
		String semanticType = caseFrameSTN.getText();
		LinkedList<Relation> caseFrameList = new LinkedList<Relation>();
		for (int i = 0; i < cfRS.getItems().size(); i++) {
			String rName = cfRS.getItems().get(i);
			try {
				Relation r = Network.getRelation(rName);
				caseFrameList.add(r);
			} catch (RelationDoesntExistException e) {
				e.printStackTrace();
			}
		}

		CaseFrame cf = Network.defineCaseFrame(semanticType, caseFrameList);
		String name = cf.getId();
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("Case Frame");
		a.setHeaderText("Case frame has been created successfully");
		a.setResizable(false);
		a.setContentText("Case frame: " + name + " has been created successfully.");
		a.showAndWait();
		updateCaseFramesList();
		updateRelationSetList();
		cfRS.getItems().clear();
	}

	// Update all list of case frames
	public void updateCaseFramesList() {
		caseFramesList.getItems().clear();
		caseFrameChoice.getItems().clear();
		caseFramesDrawList.getItems().clear();
		selectCFForSign.getItems().clear();
		cfListForSign.getItems().clear();
		pathCF.getItems().clear();
		Hashtable<String, CaseFrame> caseFrames = Network.getCaseFrames();
		ArrayList<String> sortedCFs = new ArrayList<String>();
		for (Entry<String, CaseFrame> entry : caseFrames.entrySet()) {
			String key = entry.getKey();
			sortedCFs.add(key);
		}
		Collections.sort(sortedCFs);

		for (int i = 0; i < sortedCFs.size(); i++) {
			String key = sortedCFs.get(i);
			MenuItem item = new MenuItem(key);
			MenuItem item2 = new MenuItem(key);
			MenuItem item3 = new MenuItem(key);
			caseFramesList.getItems().add(key);
			caseFramesDrawList.getItems().add(key);
			selectCFForSign.getItems().add(key);
			cfListForSign.getItems().add(key);
			item.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					try {
						CaseFrame cf = Network.getCaseFrame(item.getText());
						curCF = cf;
						curRRCF = (RelationsRestrictedCaseFrame) cf;
						LinkedList<Relation> relations = cf.getRelations();
						caseFrameRelationList.getItems().clear();
						for (Relation r : relations) {
							caseFrameRelationList.getItems().add(r.getName());
						}

					} catch (CaseFrameWithSetOfRelationsNotFoundException e) {
						e.printStackTrace();
					}
				}

			});
			caseFrameChoice.getItems().add(item);
			item2.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					pathCF.setText(key);
					pathRelations.getItems().clear();
					try {
						CaseFrame cf = Network.getCaseFrame(item2.getText());
						curCF = cf;
						curRRCF = (RelationsRestrictedCaseFrame) cf;
						LinkedList<Relation> relations = cf.getRelations();
						caseFrameRelationList.getItems().clear();
						for (Relation r : relations) {
							pathRelations.getItems().add(r.getName());
						}

					} catch (CaseFrameWithSetOfRelationsNotFoundException e) {
						e.printStackTrace();
					}
					pathRelations.setDisable(false);
					createPathBTN.setDisable(false);
				}

			});
			pathCF.getItems().add(item2);
		}
	}

	public void addRelsToListSign() {
		selectCFForSign.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				String cfName = selectCFForSign.getSelectionModel().getSelectedItem();
				CaseFrame cf = null;
				try {
					cf = Network.getCaseFrame(cfName);
				} catch (CaseFrameWithSetOfRelationsNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				RelationsRestrictedCaseFrame rrcf = (RelationsRestrictedCaseFrame) cf;
				LinkedList<Relation> relations = rrcf.getRelations();
				selectCFRelForSign.getItems().clear();
				for (Relation r : relations) {
					selectCFRelForSign.getItems().add(r.getName());
				}

			}

		});
	}

	// Undefine case frames - menu-based
	public void undefineCaseFrame() {
		String caseFrame = caseFramesList.getSelectionModel().getSelectedItem();
		Hashtable<String, CaseFrame> cframes = Network.getCaseFrames();
		CaseFrame cf = cframes.get(caseFrame);

		ButtonType yes = new ButtonType("Yes");
		ButtonType cancel = new ButtonType("Cancel");
		Alert a = new Alert(AlertType.NONE, "Promote pawn to:", yes, cancel);
		a.setTitle("Delete Case Frame");
		a.setHeaderText("Are you sure you want to delete this case frame?");
		a.setResizable(false);
		a.setContentText("Are you sure you want to delete this case frame: " + cf.getId() + "?");
		a.showAndWait().ifPresent(response -> {
			if (response == yes) {
				try {
					Network.undefineCaseFrame(cf.getId());
				} catch (CaseFrameCannotBeRemovedException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Delete Case Frame");
					alert.setHeaderText("Error Deleting Case Frame");
					alert.setResizable(false);
					alert.setContentText(
							"Case frame can not be removed, remove the nodes implementing this case frame first");
					alert.showAndWait();
				}
				updateCaseFramesList();
			} else if (response == cancel) {

			}
		});

	}

	// Creates a base node
	public void buildBaseNode() {
		String nodeName = baseNodeID.getText();
		String semType = baseNodeSemType.getText();
		Semantic semantic = null;
		try {
			semantic = SemanticHierarchy.getSemantic(semType);
		} catch (SemanticNotFoundInNetworkException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Node node = null;
		try {
			node = Network.buildBaseNode(nodeName, semantic);
		} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException | IllegalIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (node == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Node was NOT created successfully!");
			alert.setContentText("ERROR: Acts cannot be base nodes!!!");
			alert.showAndWait();
		} else {
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

	// Updates the nodes list
	public void updateNodesList() {
		Hashtable<String, Node> nodes = Network.getNodes();
		ArrayList<String> sorted = new ArrayList<String>();
		nodesList.getItems().clear();
		variableNodesList.getItems().clear();
		baseNodesList.getItems().clear();
		propoNodesList.getItems().clear();
		pathNodes1.getItems().clear();
		pathNodes2.getItems().clear();
		for (Entry<String, Node> entry : nodes.entrySet()) {
			String key = entry.getKey();
			sorted.add(key);
		}

		Collections.sort(sorted);
		for (int i = 0; i < sorted.size(); i++) {
			String key = sorted.get(i);
			MenuItem mi = new MenuItem(key);
			mi.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					pathNodes1.setText(mi.getText());

				}

			});
			pathNodes1.getItems().add(mi);

			MenuItem mi1 = new MenuItem(key);
			mi1.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					pathNodes2.setText(mi1.getText());

				}

			});
			pathNodes2.getItems().add(mi1);
			Node n = nodes.get(key);
			nodesList.getItems().add(key);
			if (n.getTerm() instanceof Variable) {
				variableNodesList.getItems().add(key);
			} else if (n.getTerm() instanceof Base) {
				baseNodesList.getItems().add(key);
				String semantic = n.getSemantic().getSemanticType();
				if (semantic.equalsIgnoreCase("proposition")) {
					propoNodesList.getItems().add(key);
				}
			} else if (n.getTerm() instanceof Molecular) {
				String semantic = n.getSemantic().getSemanticType();
				if (semantic.equalsIgnoreCase("proposition")) {
					propoNodesList.getItems().add(key);
				}
			}
		}
	}

	// Creates a wire
	public void createWire() {

		String rName = caseFrameRelationList.getSelectionModel().getSelectedItem();
		Relation r = null;
		try {
			r = Network.getRelation(rName);
		} catch (RelationDoesntExistException e) {
			e.printStackTrace();
		}

		String nodeName = nodesList.getSelectionModel().getSelectedItem();
		Node node = null;
		try {
			node = Network.getNode(nodeName);
		} catch (NodeNotFoundInNetworkException e1) {
			e1.printStackTrace();
		}

		Wire w = new Wire(r, node);
		wiresList.getItems().add(r.getName() + " " + nodeName);
		wires.add(w);
		wiresTable.put(r.getName() + " " + nodeName, w);
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("Wire Created!");
		a.setHeaderText("Wire has been created successfully");
		a.setResizable(false);
		a.setContentText("Wire name is: " + r.getName() + " " + nodeName);
		a.showAndWait();

	}

	// Delete a wire()
	public void deleteWire() {
		String wireName = wiresList.getSelectionModel().getSelectedItem();
		Wire w = wiresTable.get(wireName);
		wires.remove(w);
		wiresList.getItems().remove(wireName);
		wiresTable.remove(wireName, w);
	}

	// Builds a variable node in the network
	public void buildVN() {
		VariableNode n = Network.buildVariableNode();
		String name = n.getIdentifier();
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("Node Created!");
		a.setHeaderText("Variable node has been created successfully");
		a.setResizable(false);
		a.setContentText("Node name is: " + name);
		a.showAndWait();
		updateNodesList();
	}

	// Builds the molecular node in the network
	public void buildMolecularNode() {
		try {
			Node n = Network.buildMolecularNode(wires, curRRCF);
			String name = n.getIdentifier();
			updateNodesList();
			wiresList.getItems().clear();
			wires.clear();
			Alert a = new Alert(AlertType.INFORMATION);
			a.setTitle("Node Created!");
			a.setHeaderText("Molecular node has been created successfully");
			a.setResizable(false);
			a.setContentText("Node name is: " + name);
			a.showAndWait();
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Node NOT Created!");
			a.setHeaderText("Molecular node was not created successfully");
			a.setResizable(false);
			a.setContentText("Cannot build the node .. the relation node pairs are not valid");
			a.showAndWait();
		}
	}

	// Displays the details of a selected node
	public void nodeDetails(String identifier) {
		Node n = null;
		try {
			n = Network.getNode(identifier);
		} catch (NodeNotFoundInNetworkException e) {
			e.printStackTrace();
		}
		if (n.getTerm() instanceof Variable) {
			String syntactic = n.getSyntacticType();
			int id = n.getId();
			nodeDetails.setText(
					"Node Identifier: " + identifier + "\n" + "Syntactic Type: " + syntactic + "\n" + "ID: " + id);
		} else {
			String semantic = n.getSemantic().getSemanticType();
			String syntactic = n.getSyntacticType();
			int id = n.getId();
			nodeDetails.setText("Node Identifier: " + identifier + "\n" + "Semantic Type: " + semantic + "\n"
					+ "Syntactic Type: " + syntactic + "\n" + "ID: " + id);
		}
	}

	// Displays the details of a selected relation
	public void relationDetails(String rname) {
		Relation r = null;
		try {
			r = Network.getRelation(rname);
		} catch (RelationDoesntExistException e) {
			e.printStackTrace();
		}
		String type = r.getType();
		String adjust = r.getAdjust();
		int limit = r.getLimit();
		boolean quantifier = r.isQuantifier();
		relationDetails.setText("Relation Name: " + rname + "\n" + "Type: " + type + "\n" + "Adjust: " + adjust + "\n"
				+ "Limit: " + limit + "\t" + "Quantifier: " + quantifier);
	}

	// Adds an overridden a relation to rr case frame
	public void addOverriddenRelationToCaseFrame() {
		String selectedRelation = relationSetList1.getSelectionModel().getSelectedItem();
		if (selectedRelation != null) {
			String adjust = overrideAdjust.getText();
			String limitS = overrideLimit.getText();

			if (limitS.length() == 0) {
				Relation r = null;
				try {
					r = Network.getRelation(selectedRelation);

				} catch (RelationDoesntExistException e) {
					e.printStackTrace();
				}

				String ra = r.getAdjust();
				int rl = r.getLimit();
				RCFP rcfp = new RCFP(r, ra, rl);
				rrcflist.add(rcfp);
				cfRS.getItems().add(selectedRelation);
				relationSetList1.getItems().remove(selectedRelation);
				overrideAdjust.setText("");
				overrideLimit.setText("");

			} else {
				int limit = Integer.parseInt(overrideLimit.getText());
				Relation r = null;
				try {
					r = Network.getRelation(selectedRelation);
				} catch (RelationDoesntExistException e) {
					e.printStackTrace();
				}
				RCFP rcfp = new RCFP(r, adjust, limit);
				rrcflist.add(rcfp);
				cfRS.getItems().add(selectedRelation);
				relationSetList1.getItems().remove(selectedRelation);
				overrideAdjust.setText("");
				overrideLimit.setText("");
			}
		}
	}

	// Removes an overridden relation from rr case frame
	public void removeOverridenRelation() {
		String selectedRelation = rrcfrslist.getSelectionModel().getSelectedItem();
		if (selectedRelation != null) {
			for (RCFP rcfp : rrcflist) {
				Relation r = rcfp.getRelation();
				if (r.getName() == selectedRelation) {
					rrcflist.remove(rcfp);
					relationSetList1.getItems().add(selectedRelation);
					cfRS.getItems().remove(selectedRelation);
				}
			}

		}
	}

	// Creates rrcf
	public void submitRRCF() {
		String semType = rrcfSem.getText();
		CaseFrame cf = Network.defineCaseFrameWithConstraints(semType, rrcflist);
		String name = cf.getId();
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("Case Frame");
		a.setHeaderText("Case frame has been created successfully");
		a.setResizable(false);
		a.setContentText("Case frame: " + name + " has been created successfully.");
		a.showAndWait();
		updateCaseFramesList();
		updateRelationSetList();
		rrcflist.clear();
		cfRS.getItems().clear();
		rrcfSem.setText("");
	}

	public void deleteNode() {
		String identifier = nodesList.getSelectionModel().getSelectedItem().toString();
		ButtonType yes = new ButtonType("Yes");
		ButtonType cancel = new ButtonType("Cancel");
		Alert a = new Alert(AlertType.NONE, "Promote pawn to:", yes, cancel);
		a.setTitle("Delete Node");
		a.setHeaderText("Are you sure you want to delete this node?");
		a.setResizable(false);
		a.setContentText("Are you sure you want to delete this node: " + identifier + "?");
		a.showAndWait().ifPresent(response -> {
			if (response == yes) {
				Node n = null;
				try {
					n = Network.getNode(identifier);
				} catch (NodeNotFoundInNetworkException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					Network.removeNode(n);
					updateNodesList();
				} catch (NodeCannotBeRemovedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotAPropositionNodeException e) {
					e.printStackTrace();
				} catch (NodeNotFoundInNetworkException e) {
					e.printStackTrace();
				} catch (NodeNotFoundInPropSetException e) {
					e.printStackTrace();
				}
			} else if (response == cancel) {

			}
		});

	}

	public void createCableTypeConst() {
		String semantic = cableSem.getText();
		Integer min = Integer.parseInt(cableMinNodes.getText());
		Integer max = Integer.parseInt(cableMaxNodes.getText());
		CableTypeConstraint ctc = new CableTypeConstraint(semantic, min, max);
		cables.add(ctc);
		cablesList.getItems().add(ctc.getId());
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("Cabel");
		a.setHeaderText("Cable Type Constraint has been created successfully");
		a.setResizable(false);
		a.setContentText("Cable: " + ctc.getId() + " has been created successfully.");
		a.showAndWait();
		cableMinNodes.clear();
		cableMaxNodes.clear();
	}

	public void deleteCTC() {
		String ctcID = cablesList.getSelectionModel().getSelectedItem();
		for (int i = 0; i < cables.size(); i++) {
			CableTypeConstraint tempCTC = cables.get(i);
			if (tempCTC.getId() == ctcID) {
				cables.remove(i);
				cablesList.getItems().remove(ctcID);
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("Cable");
				a.setHeaderText("Cable Type Constraint has been deleted successfully");
				a.setResizable(false);
				a.setContentText("Cable: " + ctcID + " has been deleted successfully.");
				a.showAndWait();
			}
		}
	}

	public void createSubDomConst() {
		String rname = selectCFRelForSign.getSelectionModel().getSelectedItem();
		SubDomainConstraint sdc = new SubDomainConstraint(rname, cables);
		sdcs.add(sdc);
		sdcsList.getItems().add(sdc.getId());
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("SubDomain Constraint");
		a.setHeaderText("SubDomain Constraint has been created successfully");
		a.setResizable(false);
		a.setContentText("SubDomain Constraint: " + sdc.getId() + " has been created successfully.");
		a.showAndWait();
	}

	public void deleteSubDomConst() {
		String sdcID = sdcsList.getSelectionModel().getSelectedItem();
		for (int i = 0; i < sdcs.size(); i++) {
			SubDomainConstraint tempSDC = sdcs.get(i);
			if (tempSDC.getId() == sdcID) {
				sdcs.remove(i);
				sdcsList.getItems().remove(sdcID);
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("SubDomain Constarin");
				a.setHeaderText("SubDomain Constraint has been deleted successfully");
				a.setResizable(false);
				a.setContentText("SubDomain Constraint: " + sdcID + " has been deleted successfully.");
				a.showAndWait();
			}
		}
	}

	public void createCFSign() {
		String sem = resultSemType.getText();
		String cfID = selectCFForSign.getSelectionModel().getSelectedItem();
		CFSignature cfS = network.createCFSignature(sem, sdcs, cfID);
		listOfSigns.getItems().add(cfS.getId());
		cfSignsArray.add(cfS);
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("Case Frame Signature");
		a.setHeaderText("Case Frame Signature has been created successfully");
		a.setResizable(false);
		a.setContentText("Case Frame Signature: " + cfS.getId() + " has been created successfully.");
		a.showAndWait();
	}

	public void addSignToCF() {
		String cfsID = listOfSigns.getSelectionModel().getSelectedItem();
		String cfID = cfListForSign.getSelectionModel().getSelectedItem();
		Integer priority = Integer.parseInt(signPriority.getText());
		CFSignature rule = null;
		for (int i = 0; i < cfSignsArray.size(); i++) {
			CFSignature temp = cfSignsArray.get(i);
			if (temp.getId() == cfsID) {
				rule = temp;
			}
		}
		RelationsRestrictedCaseFrame cf = null;
		try {
			cf = (RelationsRestrictedCaseFrame) Network.getCaseFrame(cfID);
		} catch (CaseFrameWithSetOfRelationsNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (priority == cf.getSignatureIDs().size()) {
			priority = null;
		} else if (priority > cf.getSignatureIDs().size()) {
			priority = cf.getSignatureIDs().size();
		}

		network.addSignatureToCaseFrame(rule, priority, cf);
		popUpNotification("Case Frame Signature", "Case frame signature has been added successfully",
				"Case frame signature has been added successfully", 2);
	}

	public void addNodeToPropSet() {
		String identifier = propoNodesList.getSelectionModel().getSelectedItem();
		propSet.getItems().add(identifier);
		propoNodesList.getItems().remove(identifier);
	}

	public void removeNodeFromPropSet() {
		String identifier = propSet.getSelectionModel().getSelectedItem();
		propoNodesList.getItems().add(identifier);
		propSet.getItems().remove(identifier);
	}

	public void createContext() {
		String name = contextName.getText();
		int size = propSet.getItems().size();
		int[] prop = new int[size];
		for (int i = 0; i < size; i++) {
			String identifier = propSet.getItems().get(i);
			Node n = null;
			try {
				n = Network.getNode(identifier);
			} catch (NodeNotFoundInNetworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			prop[i] = n.getId();
		}
		PropositionSet hyps = null;
		try {
			hyps = new PropositionSet(prop);
		} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Controller.createContext(name, hyps);
			popUpNotification("Create Context", "Context Created Successfully",
					"Context: " + name + " Created Successfully!", 2);
		} catch (DuplicateContextNameException e) {
			popUpNotification("Create Context", "Context Name Already Exists", "Context: " + name + " Already Exists!",
					2);
			e.printStackTrace();
		} catch (NotAPropositionNodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ContradictionFoundException e) {
			Main.userAction(e.getContradictoryHyps());
		} catch (DuplicatePropositionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeNotFoundInPropSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ContextNameDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateListOfContexts();
		propSet.getItems().clear();
		updateNodesList();
	}

	public void createBUnitPath() {
		String rname = pathRelations.getSelectionModel().getSelectedItem();
		Relation r = null;
		try {
			r = Network.getRelation(rname);
		} catch (RelationDoesntExistException e) {
			e.printStackTrace();
		}

		BUnitPath bup = new BUnitPath(r);
		String pathName = "BUnitPath: " + rname;
		paths.put(pathName, bup);
		updatePathsList();
		popUpNotification("Backward Unit Path", "Backward Unit Path has been created successfully", pathName, 1);
	}

	public void createFUnitPath() {
		String rname = pathRelations.getSelectionModel().getSelectedItem();
		Relation r = null;
		try {
			r = Network.getRelation(rname);
		} catch (RelationDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FUnitPath fup = new FUnitPath(r);
		String pathName = "FUnitPath: " + rname;
		paths.put(pathName, fup);
		updatePathsList();
		popUpNotification("Forward Unit Path", "Forward Unit Path has been created successfully", pathName, 1);
	}

	public void createCFRBUP() {
		String rname = pathRelations.getSelectionModel().getSelectedItem();
		Relation r = null;
		String cfName = pathCF.getText();
		try {
			r = Network.getRelation(rname);
		} catch (RelationDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RelationsRestrictedCaseFrame cf = null;
		try {
			cf = (RelationsRestrictedCaseFrame) Network.getCaseFrame(cfName);
		} catch (CaseFrameWithSetOfRelationsNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CFResBUnitPath cfRBUP = new CFResBUnitPath(r, cf);
		String cfRBUPName = "Case Frame Restricted Backward Unit Path: " + rname + " " + cfName;
		paths.put(cfRBUPName, cfRBUP);
		updatePathsList();
		popUpNotification("Case Frame Restricted Backward Unit Path",
				"Case Frame Restricted Backwardward Unit Path has been created successfully", cfRBUPName, 1);

	}

	public void createCFRFUP() {
		String rname = pathRelations.getSelectionModel().getSelectedItem();
		Relation r = null;
		String cfName = pathCF.getText();
		try {
			r = Network.getRelation(rname);
		} catch (RelationDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RelationsRestrictedCaseFrame cf = null;
		try {
			cf = (RelationsRestrictedCaseFrame) Network.getCaseFrame(cfName);
		} catch (CaseFrameWithSetOfRelationsNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CFResFUnitPath cfRFUP = new CFResFUnitPath(r, cf);
		String cfRFUPName = "Case Frame Restricted Forward Unit Path: " + rname + " " + cfName;
		paths.put(cfRFUPName, cfRFUP);
		updatePathsList();
		popUpNotification("Case Frame Restricted Forward Unit Path",
				"Case Frame Restricted Forward Unit Path has been created successfully", cfRFUPName, 1);
	}

	public void createPath() {
		String x = selectedPath.getText();
		if (x == "Backward Unit Path") {
			createBUnitPath();
		} else if (x == "Forward Unit Path") {
			createFUnitPath();
		} else if (x == "Case Frame Restricted Backward Unit Path") {
			createCFRBUP();
		} else if (x == "Case Frame Restricted Forward Unit Path") {
			createCFRFUP();
		}
	}

	public void setBUP() {
		selectedPath.setText("Backward Unit Path");
		pathCF.setDisable(true);
		createPathBTN.setText("Create Backward Unit Path");
		createPathBTN.setDisable(false);
		pathRelations.setDisable(false);
		updateRelationSetList();
	}

	public void setFUP() {
		selectedPath.setText("Forward Unit Path");
		pathCF.setDisable(true);
		createPathBTN.setText("Create Forward Unit Path");
		createPathBTN.setDisable(false);
		pathRelations.setDisable(false);
		updateRelationSetList();
	}

	public void setCFRBUP() {
		selectedPath.setText("Case Frame Restricted Backward Unit Path");
		pathCF.setDisable(false);
		createPathBTN.setText("Create Case Frame Restricted Backward Unit Path");
		createPathBTN.setDisable(true);
		pathRelations.setDisable(true);
	}

	public void setCFRFUP() {
		selectedPath.setText("Case Frame Restricted Forward Unit Path");
		pathCF.setDisable(false);
		createPathBTN.setText("Create Case Frame Restricted Forward Unit Path");
		createPathBTN.setDisable(true);
		pathRelations.setDisable(true);
	}

	public void updatePathsList() {
		ArrayList<String> sortedPaths = new ArrayList<String>();
		for (Entry<String, sneps.network.paths.Path> entry : paths.entrySet()) {
			String pname = entry.getKey();
			sortedPaths.add(pname);
		}
		Collections.sort(sortedPaths);

		pathsList.getItems().clear();
		for (int i = 0; i < sortedPaths.size(); i++) {
			pathsList.getItems().add(sortedPaths.get(i));
		}
	}

	public void deletePath() {
		String pname = pathsList.getSelectionModel().getSelectedItem();
		paths.remove(pname);
		updatePathsList();
		popUpNotification("Delete Path", "Path Deleted Successfully", "Path: " + pname, 1);
	}

	public void createConversePath() {
		String pname = pathsList.getSelectionModel().getSelectedItem();
		sneps.network.paths.Path p = (sneps.network.paths.Path) paths.get(pname);
		ConversePath cp = new ConversePath(p);
		String cpname = "Converse Path " + pname;
		paths.put(cpname, cp);
		updatePathsList();
		popUpNotification("Converse Path", "Converse Path Created Successfully", "Path: " + pname, 1);
	}

	public void createIrreflexiveRestrictPath() {
		String pname = pathsList.getSelectionModel().getSelectedItem();
		sneps.network.paths.Path p = (sneps.network.paths.Path) paths.get(pname);
		IrreflexiveRestrictPath irp = new IrreflexiveRestrictPath(p);
		String cpname = "Irreflexive Restrict Path " + pname;
		paths.put(cpname, irp);
		updatePathsList();
		popUpNotification("Irreflexive Restrict Path", "Irreflexive Restrict Path Created Successfully",
				"Path: " + pname, 1);
	}

	public void createKPlusPath() {
		String pname = pathsList.getSelectionModel().getSelectedItem();
		sneps.network.paths.Path p = (sneps.network.paths.Path) paths.get(pname);
		KPlusPath kpp = new KPlusPath(p);
		String cpname = "K-Plus Path " + pname;
		paths.put(cpname, kpp);
		updatePathsList();
		popUpNotification("K-Plus Path", "K-Plus Path Created Successfully", "Path: " + pname, 1);
	}

	public void createKStarPath() {
		String pname = pathsList.getSelectionModel().getSelectedItem();
		sneps.network.paths.Path p = (sneps.network.paths.Path) paths.get(pname);
		KStarPath ksp = new KStarPath(p);
		String cpname = "K-Star Path " + pname;
		paths.put(cpname, ksp);
		updatePathsList();
		popUpNotification("K-Star Path", "K-Star Path Created Successfully", "Path: " + pname, 1);
	}

	public void createAndPath() {
		LinkedList<sneps.network.paths.Path> tempPaths = new LinkedList<sneps.network.paths.Path>();
		ObservableList selectedIndices = pathsList.getSelectionModel().getSelectedItems();
		String name = "";
		for (Object o : selectedIndices) {
			tempPaths.add(paths.get(o));
			name += o + " ";
		}
		AndPath p = new AndPath(tempPaths);
		String pname = "And Path " + name;
		paths.put(pname, p);
		updatePathsList();
		popUpNotification("And Path", "And Path Created Successfully", "Path: " + pname, 1);
	}

	public void createOrPath() {
		LinkedList<sneps.network.paths.Path> tempPaths = new LinkedList<sneps.network.paths.Path>();
		ObservableList selectedIndices = pathsList.getSelectionModel().getSelectedItems();
		String name = "";
		for (Object o : selectedIndices) {
			tempPaths.add(paths.get(o));
			name += o + " ";
		}
		OrPath p = new OrPath(tempPaths);
		String pname = "Or Path " + name;
		paths.put(pname, p);
		updatePathsList();
		popUpNotification("Or Path", "Or Path Created Successfully", "Path: " + pname, 1);
	}

	public void createComposePath() {
		LinkedList<sneps.network.paths.Path> tempPaths = new LinkedList<sneps.network.paths.Path>();
		ObservableList<String> selectedIndices = composeList.getItems();
		String name = "";
		for (String o : selectedIndices) {
			tempPaths.add(paths.get(o));
			name += o + " ";
		}
		ComposePath p = new ComposePath(tempPaths);
		String pname = "Compose Path " + name;
		paths.put(pname, p);
		updatePathsList();
		clearComposeList();
		popUpNotification("Compose Path", "Compose Path Created Successfully", "Path: " + pname, 1);
	}

	public void addPQPath() {
		if (ppath.getText().equalsIgnoreCase("...")) {
			String pname = pathsList.getSelectionModel().getSelectedItem();
			ppath.setText(pname);
		} else if (qpath.getText().equalsIgnoreCase("...")) {
			String pname = pathsList.getSelectionModel().getSelectedItem();
			qpath.setText(pname);
		}
	}

	public void clearPQ1Path() {
		ppath1.setText("...");
		qpath1.setText("...");
		pathNodes2.setText("Select a node");
	}

	public void addPQ1Path() {
		if (ppath1.getText().equalsIgnoreCase("...")) {
			String pname = pathsList.getSelectionModel().getSelectedItem();
			ppath1.setText(pname);
		} else if (qpath1.getText().equalsIgnoreCase("...")) {
			String pname = pathsList.getSelectionModel().getSelectedItem();
			qpath1.setText(pname);
		}
	}

	public void clearPQPath() {
		ppath.setText("...");
		qpath.setText("...");
		pathNodes1.setText("Select a node");
	}

	public void createDomainRestrictPath() {
		String identifier = pathNodes1.getText();
		Node n = null;
		try {
			n = Network.getNode(identifier);
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String pPathName = ppath.getText();
		String qPathName = qpath.getText();
		sneps.network.paths.Path pPath = paths.get(pPathName);
		sneps.network.paths.Path qPath = paths.get(qPathName);

		String name = "{ " + qPathName + " } { " + n.getIdentifier() + " } { " + pPathName + " }";
		DomainRestrictPath p = new DomainRestrictPath(qPath, n, pPath);
		String pname = "Domain Restrict Path " + name;
		paths.put(pname, p);
		updatePathsList();
		popUpNotification("Domain Restrict Path", "Domain Restrict Path Created Successfully", "Path: " + pname, 1);
		clearPQPath();
	}

	public void createRangeRestrictPath() {
		String identifier = pathNodes2.getText();
		Node n = null;
		try {
			n = Network.getNode(identifier);
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String pPathName = ppath1.getText();
		String qPathName = qpath1.getText();
		sneps.network.paths.Path pPath = paths.get(pPathName);
		sneps.network.paths.Path qPath = paths.get(qPathName);

		String name = "{ " + qPathName + " } { " + n.getIdentifier() + " } { " + pPathName + " }";

		RangeRestrictPath p = new RangeRestrictPath(pPath, qPath, n);
		String pname = "Range Restrict Path " + name;
		paths.put(pname, p);
		updatePathsList();
		popUpNotification("Range Restrict Path", "Range Restrict Path Created Successfully", "Path: " + pname, 1);
		clearPQ1Path();
	}

	public void createBangPath() {
		BangPath bp = new BangPath();
		String pname = "Bang Path " + bpcounter++;
		paths.put(pname, bp);
		updatePathsList();
		popUpNotification("Bang Path", "Bang Path Created Successfully", "Path: " + pname, 1);
	}

	public void definePath() {
		Relation r = null;
		try {
			r = Network.getRelation(definePathRelations.getText());
		} catch (RelationDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String pname = pathsList.getSelectionModel().getSelectedItem();
		sneps.network.paths.Path p = (sneps.network.paths.Path) paths.get(pname);
		Network.definePath(r, p);
		popUpNotification("Define Path", "Path Defined Successfully", "Path: " + pname + " Relation: " + r.getName(),
				1);
	}

	public void updateListOfContexts() {
		Set<String> contexts = Controller.getAllNamesOfContexts();
		ArrayList<String> sorted = new ArrayList<String>();
		for (String s : contexts) {
			sorted.add(s);
		}
		Collections.sort(sorted);
		contextList.getItems().clear();
		chooseContext.getItems().clear();
		chooseContext1.getItems().clear();
		for (String s : sorted) {
			MenuItem mi = new MenuItem(s);
			MenuItem mi1 = new MenuItem(s);
			mi.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					chooseContext.setText(mi.getText());
					propoNodesList1.getItems().clear();
					ArrayList<String> sorted = new ArrayList<String>();
					Hashtable<String, Node> propsOfCxt = new Hashtable<String, Node>();
					Context cxt = Controller.getContextByName(mi1.getText());
					PropositionSet props = cxt.getHypothesisSet();
					int[] a = null;
					try {
						a = PropositionSet.getPropsSafely(props);
					} catch (NotAPropositionNodeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NodeNotFoundInNetworkException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for (int i : a) {
						Node n = null;
						try {
							n = Network.getNodeById(i);
						} catch (NodeNotFoundInNetworkException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						propsOfCxt.put(n.getIdentifier(), n);
					}
					Hashtable<String, Node> nodes = Network.getNodes();
					for (Entry<String, Node> entry : nodes.entrySet()) {
						Node x = propsOfCxt.get(entry.getKey());
						if (x == null) {
							if (!(entry.getValue().getTerm() instanceof Variable)) {
								if (entry.getValue().getSemantic().getSemanticType().equalsIgnoreCase("proposition")) {
									sorted.add(entry.getKey());
								}
							}
						}
					}

					Collections.sort(sorted);
					for (String x : sorted) {
						propoNodesList1.getItems().add(x);
					}
				}

			});
			mi1.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					propNodesListOfSelCont.getItems().clear();
					ArrayList<String> sorted = new ArrayList<String>();
					chooseContext1.setText(mi1.getText());
					Context cxt = Controller.getContextByName(mi1.getText());
					PropositionSet props = cxt.getHypothesisSet();
					int[] a = null;
					try {
						a = PropositionSet.getPropsSafely(props);
					} catch (NotAPropositionNodeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NodeNotFoundInNetworkException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for (int i : a) {
						Node n = null;
						try {
							n = Network.getNodeById(i);
						} catch (NodeNotFoundInNetworkException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						sorted.add(n.getIdentifier());
					}
					Collections.sort(sorted);
					for (String x : sorted) {
						propNodesListOfSelCont.getItems().add(x);
					}
				}

			});
			contextList.getItems().add(s);
			chooseContext.getItems().add(mi);
			chooseContext1.getItems().add(mi1);
		}
	}

	public void deleteContext() {
		String cname = contextList.getSelectionModel().getSelectedItem();
		Controller.removeContext(cname);
		popUpNotification("Context", "Context Deleted", "Context: " + cname + " Deleted Successfully", 1);
		updateListOfContexts();
	}

	public void setCurrentContext() {
		String cname = contextList.getSelectionModel().getSelectedItem();
		try {
			Controller.setCurrentContext(cname);
			popUpNotification("Context", "Current context is set", "Context: " + cname + " is the current context", 2);
		} catch (ContradictionFoundException e) {
			Main.userAction(e.getContradictoryHyps());
			e.printStackTrace();
		} catch (ContextNameDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addNodeToPropSet1() {
		String identifier = propoNodesList1.getSelectionModel().getSelectedItem();
		propSet1.getItems().add(identifier);
		propoNodesList1.getItems().remove(identifier);
	}

	public void removeNodeFromPropSet1() {
		String identifier = propSet1.getSelectionModel().getSelectedItem();
		propoNodesList1.getItems().add(identifier);
		propSet1.getItems().remove(identifier);
	}

	public void addPropsToContext() {
		String name = chooseContext.getText();
		int size = propSet1.getItems().size();
		int[] prop = new int[size];
		for (int i = 0; i < size; i++) {
			String identifier = propSet1.getItems().get(i);
			Node n = null;
			try {
				n = Network.getNode(identifier);
			} catch (NodeNotFoundInNetworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			prop[i] = n.getId();
		}
		PropositionSet hyps = null;
		try {
			hyps = new PropositionSet(prop);
		} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Controller.addPropsToContext(name, hyps);
			popUpNotification("Add Propositions To Context", "Propositions were successfully added to context",
					"Propositions were successfully added to context: " + name, 2);
		} catch (NotAPropositionNodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ContextNameDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ContradictionFoundException e) {
			Main.userAction(e.getContradictoryHyps());
			e.printStackTrace();
		} catch (DuplicatePropositionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeNotFoundInPropSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateListOfContexts();
		propSet1.getItems().clear();
	}

	public void assertNodeToCurrentContext() {
		String identifier = nodesList.getSelectionModel().getSelectedItem();
		Node n = null;
		try {
			n = Network.getNode(identifier);
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Controller.addPropToCurrentContext(n.getId());
			popUpNotification("Assert Node", "Node Asserted", "Proposition added to current context", 2);
		} catch (DuplicatePropositionException e) {
			popUpNotification("Assert Node", "Error:", "Proposition already exists in current context", 2);
			e.printStackTrace();
		} catch (NotAPropositionNodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ContextNameDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ContradictionFoundException e) {
			Main.userAction(e.getContradictoryHyps());
			e.printStackTrace();
		}
	}

	public void searchNodes() {
		searchNodes.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() != KeyCode.CAPS) {
					Hashtable<String, Node> nodes = Network.getNodes();
					ArrayList<String> sorted = new ArrayList<String>();
					ArrayList<String> keys = new ArrayList<String>();
					nodesList.getItems().clear();
					for (Entry<String, Node> entry : nodes.entrySet()) {
						String key = entry.getKey();
						keys.add(key);
					}
					String nodename = searchNodes.getText();
					for (int i = 0; i < keys.size(); i++) {
						String nodeKey = keys.get(i);
						if (nodeKey.length() >= nodename.length()) {
							if (nodeKey.substring(0, nodename.length()).equalsIgnoreCase(nodename)) {
								sorted.add(nodeKey);
							}
						}
					}

					Collections.sort(sorted);
					for (String x : sorted) {
						nodesList.getItems().add(x);
					}
				}
			}

		});
	}

	public void addToComposePath() {
		String pname = pathsList.getSelectionModel().getSelectedItem();
		composeList.getItems().add(pname);
	}

	public void clearComposeList() {
		composeList.getItems().clear();
	}

	public void removePropFromContext() {
		String propname = propNodesListOfSelCont.getSelectionModel().getSelectedItem();
		Node n = null;
		try {
			n = Network.getNode(propname);
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int[] i = { n.getId() };
		PropositionSet props = null;
		try {
			props = new PropositionSet(i);
		} catch (NotAPropositionNodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Controller.removeHypsFromContext(props, chooseContext1.getText());
			popUpNotification("Remove Proposition From Context", "Proposition Removed Successfully",
					"Proposition: " + propname + " has been removed from context: " + chooseContext1.getText(), 2);
		} catch (NotAPropositionNodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ContextNameDoesntExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		propNodesListOfSelCont.getItems().remove(propname);
	}

	public void deduce() {
		String identifier = nodesList.getSelectionModel().getSelectedItem();
		PropositionNode n = null;
		String res = "";
		try {
			n = (PropositionNode) Network.getNode(identifier);
		} catch (NodeNotFoundInNetworkException e) {
			System.out.println(e.getMessage());
//			n = Network.buildTemporaryNode(identifier, );
		}
		n.deduce();
		KnownInstances rs = n.getKnownInstances();
		for (Report r : rs) {
			res = res + r.toString() + "\n";
		}
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("Deduce");
		a.setHeaderText("Deduce Result");
		a.setResizable(true);
		a.setContentText(res);
		a.showAndWait();
	}

//..........END Of Menu Methods........................................	

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
		String semList = name + "semList";
		String contexts = name + "contexts";

		ButtonType yes = new ButtonType("Yes");
		ButtonType cancel = new ButtonType("Cancel");
		Alert a = new Alert(AlertType.NONE, "Promote pawn to:", yes, cancel);
		a.setTitle("Save Network");
		a.setHeaderText("Are you sure you want to save to this network file: " + name + " ?");
		a.setResizable(false);
		a.setContentText("If you didn't load the network file that you want to save to, data WILL BE LOST!");
		a.showAndWait().ifPresent(response -> {
			if (response == yes) {
				try {
					Network.save(relations, caseFrames, nodes, molnodes, mc, pc, vc, pn, ni, udms, udps, udvs);
					SemanticHierarchy.save(semList);
					Controller.save(contexts);
					System.out.println("saved");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (response == cancel) {

			}
		});
	}

	public void save(String name) {
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
		String semList = name + "semList";
		String contexts = name + "contexts";

		try {
			Network.save(relations, caseFrames, nodes, molnodes, mc, pc, vc, pn, ni, udms, udps, udvs);
			SemanticHierarchy.save(semList);
			Controller.save(contexts);
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
		String semList = name + "semList";
		String contexts = name + "contexts";

		try {
			Network.clearNetwork();
			Network.load(relations, caseFrames, nodes, molnodes, mc, pc, vc, pn, ni, udms, udps, udvs);
			SemanticHierarchy.load(semList);
			Controller.load(contexts);
			updateNodesList();
			updateCaseFramesList();
			updateRelationSetList();
			updateSemanticLists();
			updateListOfContexts();
			parentSemantics();
			Alert a = new Alert(AlertType.INFORMATION);
			a.setTitle("Load Network");
			a.setHeaderText("Network loaded successfully");
			a.setResizable(false);
			a.setContentText("Network: " + name + " has been loaded successfully.");
			a.showAndWait();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void submitNewNet() throws IOException {
		String name = newNetName.getText();
		boolean r = Network.addToSavedNetworks(name);
		if (r == true) {
			System.out.println("Network Created Successfully!");
			Network.saveNetworks();
			createDefaults();
			Semantic.createDefaultSemantics();
			save(name);
			Network.getCaseFrames().clear();
			Network.getRelations().clear();
			SemanticHierarchy.getSemantics().clear();
			updateRelationSetList();
			updateCaseFramesList();
			updateSemanticLists();
			Alert a = new Alert(AlertType.INFORMATION);
			a.setTitle("New Network");
			a.setHeaderText("Network Created Successfully!");
			a.setResizable(false);
			a.setContentText("Network: " + name + " has been created successfully!");
			a.showAndWait();
		} else {
			System.out.println("Network Already Exists, Please Type Another Name.");
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("New Network");
			a.setHeaderText("Network Already Exists, Please Type Another Name.");
			a.setResizable(true);
			a.setContentText("Network: " + name + " already exists.");
			a.showAndWait();
		}
		updateNetLists();
	}

	public void deleteNetwork() {
		String name = netChoice3.getText();
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
		String semList = name + "semList";
		String contexts = name + "contexts";

		ButtonType yes = new ButtonType("Yes");
		ButtonType cancel = new ButtonType("Cancel");
		Alert a = new Alert(AlertType.NONE, "", yes, cancel);
		a.setTitle("Delete Network");
		a.setHeaderText("Are you sure you want to delete this network?");
		a.setResizable(false);
		a.setContentText("You CANNOT undo this action, Network data will be LOST!");
		a.showAndWait().ifPresent(response -> {
			if (response == yes) {
				Path filePath1 = Paths.get(relations);
				Path filePath2 = Paths.get(caseFrames);
				Path filePath3 = Paths.get(nodes);
				Path filePath4 = Paths.get(molnodes);
				Path filePath5 = Paths.get(mc);
				Path filePath6 = Paths.get(pc);
				Path filePath7 = Paths.get(ni);
				Path filePath8 = Paths.get(udms);
				Path filePath9 = Paths.get(udps);
				Path filePath10 = Paths.get(udvs);
				Path filePath11 = Paths.get(vc);
				Path filePath12 = Paths.get(pn);
				Path filePath13 = Paths.get(semList);
				Path filePath14 = Paths.get(contexts);
				try {
					Network.deleteFromSavedNetworks(name);
					updateNetLists();
					Files.delete(filePath1);
					Files.delete(filePath2);
					Files.delete(filePath3);
					Files.delete(filePath4);
					Files.delete(filePath5);
					Files.delete(filePath6);
					Files.delete(filePath7);
					Files.delete(filePath8);
					Files.delete(filePath9);
					Files.delete(filePath10);
					Files.delete(filePath11);
					Files.delete(filePath12);
					Files.delete(filePath13);
					Files.delete(filePath14);
					popUpNotification("Delete network", "Network Deleted!",
							"The Network: " + name + " is deleted successfully", 2);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (response == cancel) {

			}
		});

	}

	public void updateNetList1() {
		netChoice1.getItems().clear();
		ArrayList<String> temp = Network.getSavedNetworks();
		for (int i = 0; i < temp.size(); i++) {
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
		for (int i = 0; i < temp.size(); i++) {
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

	public void updateNetList3() {
		netChoice3.getItems().clear();
		ArrayList<String> temp = Network.getSavedNetworks();
		for (int i = 0; i < temp.size(); i++) {
			MenuItem mi = new MenuItem(temp.get(i));
			mi.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					netChoice3.setText(mi.getText());
				}

			});
			netChoice3.getItems().add(mi);
		}
	}

	public void updateNetLists() {
		updateNetList1();
		updateNetList2();
		updateNetList3();
	}

//..........End of saving and loading methods...........................

//..........Displaying the network method...............................

	public void generateNetwork() throws IOException {
		String data = null;
		if (Main.isDark()) {
			data = "document.body.innerHTML += Viz('digraph { nodesep=0.5; ranksep=2.5; bgcolor=gray20;";
		} else {
			data = "document.body.innerHTML += Viz('digraph { nodesep=0.5; ranksep=2.5; bgcolor=none;";
		}
		File f = new File("bin/sneps/gui/displayData.html");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write("<!DOCTYPE html>");
		bw.write("<html>");
		bw.write("<head>");
		bw.write("<title>Test</title>");
		if (Main.isDark()) {
			bw.write("<style>body {background-color: #333333;}</style>");
		} else {
			bw.write("<style>body {background-color: #ffffff;}</style>");
		}
		bw.write("<script src='viz.js'></script>");
		bw.write("</head>");
		bw.write("<body>");
		bw.write("<script>");
		bw.write(data);

		Hashtable<String, NodeSet> nodes = Network.getMolecularNodes();
		for (Entry<String, NodeSet> entry : nodes.entrySet()) {
			NodeSet ns = entry.getValue();
			for (int i = 0; i < ns.size(); i++) {
				Molecular molNode = (Molecular) ns.getNode(i).getTerm();
				System.out.println(molNode.getIdentifier());
				DownCableSet dcs = molNode.getDownCableSet();
				Hashtable<String, DownCable> downCables = dcs.getDownCables();
				for (Entry<String, DownCable> entry1 : downCables.entrySet()) {
					String rname = entry1.getKey();
					System.out.println(rname);
					DownCable dc = entry1.getValue();
					NodeSet cableNodes = dc.getNodeSet();
					for (int j = 0; j < cableNodes.size(); j++) {
						Node n = cableNodes.getNode(j);
						String nodeShape = " " + n.getIdentifier() + " [style=filled,color=white];";
						if (n.getTerm() instanceof Molecular) {
							nodeShape = " " + n.getIdentifier() + " [style=filled,color=dodgerblue];";
						} else if (n.getTerm() instanceof Base) {
							nodeShape = " " + n.getIdentifier() + " [style=filled,color=yellow];";
						} else if (n.getTerm() instanceof Variable) {
							nodeShape = " " + n.getIdentifier() + " [style=filled,color=green];";
						}

						System.out.println(n.getIdentifier());
						String nodeName = n.getIdentifier();
						String relation = "[style=filled, color=red, label=\"" + rname + "\", fontcolor=red]";
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
		// System.out.println("Generating Network..");
	}

	public void displayNetwork() {
		try {
			generateNetwork();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = this.getClass().getResource("displayData.html").toExternalForm();
		webView.getEngine().load(url);
	}

	public void createDefaults() {
		Network.defineDefaults();
		updateRelationSetList();
		updateCaseFramesList();
	}

	public void testVisualize() {
		ArrayList<Node> nodes = new ArrayList<Node>();
		Node n1 = null;
		Node n2 = null;
		Node n3 = null;
		Node n4 = null;
		Node n5 = null;
		try {
			n1 = Network.getNode("Bingo");
			n2 = Network.getNode("Fido");
			n3 = Network.getNode("Dog");
			n4 = Network.getNode("V1");
			n5 = Network.getNode("M1");
		} catch (NodeNotFoundInNetworkException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);
		nodes.add(n4);
		nodes.add(n5);

		try {
			Main.visualizeNodes(nodes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
