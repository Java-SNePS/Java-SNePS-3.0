package sneps.gui;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import sneps.network.classes.CaseFrame;

public class MolNodeShape {

	private double x,y;
	//private String identifier;
	private CaseFrame cf;
	
	public MolNodeShape(double x, double y, CaseFrame cf) {
		//this.identifier = identifier;
		this.x = x;
		this.y = y;
		this.cf = cf;
		
	}
	
	public Group drawShape() {
		Group x = new Group();
		StackPane sp = new StackPane();
		Ellipse nodeBack = new Ellipse();
		nodeBack.setFill(Color.web("#00a1ff"));
		nodeBack.setRadiusX(50.0);
		nodeBack.setRadiusY(40.0);
		Label txt = new Label("Temp Mol");
		txt.setStyle("-fx-text-fill: black");
		sp.getChildren().add(nodeBack);
		sp.getChildren().add(txt);
		x.getChildren().add(sp);
		return x;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public CaseFrame getCf() {
		return cf;
	}

	public void setCf(CaseFrame cf) {
		this.cf = cf;
	}
	
	

}
