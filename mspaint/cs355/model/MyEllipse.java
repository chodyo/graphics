package cs355.model;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

public class MyEllipse extends MyShape{
//	center, height, width, & accessors
	private int w;
	private int h;
	
	public MyEllipse(Color color, Point center) {
		super(color, center);
		w = 0;
		h = 0;
	}
	
	@Override
	public boolean Contains(Point2D p, int t) {
//		Point center = super.p;
		double a = Math.pow(p.getX(), 2);
		double b = Math.pow(w/2, 2);
		double c = Math.pow(p.getY(), 2);
		double d = Math.pow(h/2, 2);
		if (a/b + c/d <= 1) return true;
		else return false;
	}
	
	public void SetWidth(int width) {
		w = width;
	}
	
	public void SetHeight(int height) {
		h = height;
	}
	
	public int GetWidth() {
		return w;
	}
	
	public int GetHeight() {
		return h;
	}
}
