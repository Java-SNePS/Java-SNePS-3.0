package sneps.gui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import sneps.network.classes.Relation;

public class WireRelation {
	double xStart, yStart, xEnd, yEnd;
	Relation r;

	//Construction must have a relation!!! remember to add it.
	public WireRelation(double xStart, double yStart, double xEnd, double yEnd) {
		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
	}
	
	public Line drawLine() {
		Line l = new Line();
		l.setStartX(xStart);
		l.setEndX(xEnd);
		l.setStartY(yStart);
		l.setEndY(yEnd);
		l.setStrokeWidth(5);
		l.setStroke(Color.RED);
		return l;
	}

}
