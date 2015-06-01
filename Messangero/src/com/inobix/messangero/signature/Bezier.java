package com.inobix.messangero.signature;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Bezier {
private Point controlPointOne;
private Point controlPointTwo;
private int drawSteps;
private Point endPoint;
private int mColor;
private Point startPoint;

public Bezier() {
}

public Bezier(Point paramPoint1, Point paramPoint2, Point paramPoint3,
        Point paramPoint4) {
    this.startPoint = paramPoint1;
    this.controlPointOne = paramPoint2;
    this.controlPointTwo = paramPoint3;
    this.endPoint = paramPoint4;
    this.drawSteps = ((int) (paramPoint1.distanceTo(paramPoint2)
            + paramPoint2.distanceTo(paramPoint3) + paramPoint3
            .distanceTo(paramPoint4)));
}

public void draw(Canvas paramCanvas, Paint paramPaint, float startWidth,
        float endWidth) {
   	
	float originalWidth = paramPaint.getStrokeWidth();
	  float widthDelta = endWidth - startWidth;

	  for (int i = 0; i < drawSteps; i++) {
	    // Calculate the Bezier (x, y) coordinate for this step.
	    float t = ((float) i) / drawSteps;
	    float tt = t * t;
	    float ttt = tt * t;
	    float u = 1 - t;
	    float uu = u * u;
	    float uuu = uu * u;

	    float x = uuu * startPoint.getX();
	    x += 3 * uu * t * controlPointOne.getX();
	    x += 3 * u * tt * controlPointTwo.getX();
	    x += ttt * endPoint.getX();

	    float y = uuu * startPoint.getY();
	    y += 3 * uu * t * controlPointOne.getY();
	    y += 3 * u * tt * controlPointTwo.getY();
	    y += ttt * endPoint.getY();

	    // Set the incremental stroke width and draw.
	    //paramPaint.setStrokeWidth(startWidth + ttt * widthDelta);
	    //paramPaint.setStrokeWidth(1);
	    paramCanvas.drawPoint(x, y, paramPaint);
	  }

	  //paramPaint.setStrokeWidth(originalWidth);
}

public int getColor() {
    return this.mColor;
}

public Point getControlPointOne() {
    return this.controlPointOne;
}

public Point getControlPointTwo() {
    return this.controlPointTwo;
}

public int getDrawSteps() {
    return this.drawSteps;
}

public Point getEndPoint() {
    return this.endPoint;
}

public Point getStartPoint() {
    return this.startPoint;
}

public void setColor(int paramInt) {
    this.mColor = Color.BLACK;
}

public void setControlPointOne(Point paramPoint) {
    this.controlPointOne = paramPoint;
}

public void setControlPointTwo(Point paramPoint) {
    this.controlPointTwo = paramPoint;
}

public void setDrawSteps(int paramInt) {
    this.drawSteps = paramInt;
}

public void setEndPoint(Point paramPoint) {
    this.endPoint = paramPoint;
}

public void setStartPoint(Point paramPoint) {
    this.startPoint = paramPoint;
}
}