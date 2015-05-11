package cs355.model;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

public class MyLine extends MyShape {
//	two endpoints & accessors
	private Point s;
	private Point e;
	private double[] normal;
	
	public MyLine(Color color, Point start) {
		// start, end, and center are all equivalent when the line is created.
		super(color, start);
		s = start;
		e = start;
	}
	
	@Override
	public boolean Contains(Point2D p, int t) {
		// TODO: move this paragraph to the class itself
		normal = new double[2];
		normal[1] =  e.x - s.x;
		normal[0] = -e.y + s.y;
 		double normalLength = Math.pow(Math.pow(normal[0], 2) + Math.pow(normal[1], 2), 0.5);
 		normal[0] = normal[0]/normalLength;
 		normal[1] = normal[1]/normalLength;
		double d = Math.abs(s.x*normal[0] + s.y*normal[1]);
		
		double dist = Math.abs(p.getX()*normal[0] + p.getY()*normal[1]) - d;
		dist = Math.abs(dist);
		
		if (dist <= t) {
			// closest point to p ON the line
			double x = p.getX() - dist*normal[0];
			double y = p.getY() - dist*normal[1];
			if (((s.x < x && x < e.x) || (e.x < x && x < s.x)) && ((s.y < y && y < e.y) || (e.y < y && y < s.y))) {
				return true;
			}
		}
		return false;
	}
	
//	needs to change start and end too
	@Override
	public void SetCenter(Point center) {
		Point oldCenter = super.p;
		int dx = center.x - oldCenter.x;
		int dy = center.y - oldCenter.y;
		Point newStart = new Point(s.x+dx, s.y+dy);
		s = newStart;
		Point newEnd = new Point(e.x+dx, e.y+dy);
		e = newEnd;
	}

	public void SetStart(Point start) {
		s = start;
		RecalculateCenter();
	}

	public void SetEnd(Point end) {
		e = end;
		RecalculateCenter();
	}
	
	public Point GetStart() {
		return s;
	}
	
	public Point GetEnd() {
		return e;
	}
	
	public Point2D GetRelativeStart() {
		Point2D ret = new Point2D.Double(s.getX()-p.getX(), s.getY()-p.getY());
		return ret;
	}
	
	public Point2D GetRelativeEnd() {
		Point2D ret = new Point2D.Double(e.getX()-p.getX(), e.getY()-p.getY());
		return ret;
	}
	
	private void RecalculateCenter() {
		int x = (e.x+s.x)/2;
		int y = (e.y+s.y)/2;
		super.p = new Point(x, y);
	}
}
