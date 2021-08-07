package sneps.gui;

import java.io.Serializable;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class VarNodeShape implements Serializable {
	
	public String identifier;
	public double x, y;

	public VarNodeShape(String identifier, double x, double y) {
		this.identifier = identifier;
		this.x = x;
		this.y = y;
	}

	public Group makeShape() {
		Group x = new Group();
		StackPane sp = new StackPane();
		Ellipse nodeBack = new Ellipse();
		nodeBack.setFill(Color.web("#00ff22"));
		nodeBack.setRadiusX(50.0);
		nodeBack.setRadiusY(40.0);
		Label txt = new Label(identifier);
		txt.setStyle("-fx-text-fill: black");
		sp.getChildren().add(nodeBack);
		sp.getChildren().add(txt);
		x.getChildren().add(sp);
		return x;
	}
	
	public double xStart() {
		return x-50;
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

	public double xEnd() {
		return x+50;
	}
	
	public double yStart() {
		return y-40;
	}
	
	public double yEnd() {
		return y+40;
	}

}
