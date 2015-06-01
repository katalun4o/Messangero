package com.inobix.messangero.signature;

import android.util.FloatMath;

public class Point {

	private long time;
    private float x;
    private float y;

    public Point(float paramFloat1, float paramFloat2) {
        this.x = paramFloat1;
        this.y = paramFloat2;
    }

    public Point(float paramFloat1, float paramFloat2, long paramLong) {
        this.x = paramFloat1;
        this.y = paramFloat2;
        this.time = paramLong;
    }

    protected float distanceTo(Point paramPoint) {
        float f1 = this.x - paramPoint.getX();
        float f2 = this.y - paramPoint.getY();
        return FloatMath.sqrt(f1 * f1 + f2 * f2);
    }

    public long getTime() {
        return this.time;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void setX(float paramFloat) {
        this.x = paramFloat;
    }

    public void setY(float paramFloat) {
        this.y = paramFloat;
    }

    public float velocityFrom(Point paramPoint) {
            return distanceTo(paramPoint) / (this.time - paramPoint.time);
    }
}
