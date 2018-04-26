package sneps.gui;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class BaseNodeShape {
	public String identifier;
	public String semtantic;
	public double x, y;

	public BaseNodeShape(String identifier, String semantic, double x, double y) {
		this.identifier = identifier;
		this.semtantic = semantic;
		this.x = x;
		this.y = y;
	}

	public Group makeShape() {
		Group x = new Group();
		StackPane sp = new StackPane();
		Ellipse nodeBack = new Ellipse();
		nodeBack.setFill(Color.web("#f8ff1f"));
		nodeBack.setRadiusX(50.0);
		nodeBack.setRadiusY(40.0);
		Label txt = new Label(identifier);
		txt.setStyle("-fx-text-fill: black");
		sp.getChildren().add(nodeBack);
		sp.getChildren().add(txt);
		x.getChildren().add(sp);
		return x;
	}
	
	public void setXY(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public double xStart() {
		return x-50;
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
