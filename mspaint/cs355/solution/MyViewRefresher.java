package cs355.solution;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Stack;

import cs355.GUIFunctions;
import cs355.SelectionHelpers.*;
import cs355.model.*;

public class MyViewRefresher implements cs355.ViewRefresher, java.util.Observer {

	// anti-aliasing = personal taste. nothing to do with labs.
	private static boolean AA_ON = true;
	private MyCS355Controller contr;
	
	public MyViewRefresher(MyCS355Controller c) {
		contr = c;
		contr.AddObserver(this);
	}

	@Override
	public void refreshView(Graphics2D g2d) {
		if (AA_ON) g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		MyCircle s = new MyCircle(new Color(128, 128, 128), new Point(250, 250));
		s.SetRadius(50);

		AffineTransform objToWorld = new AffineTransform();
		objToWorld.translate(s.GetCenter().getX(), s.GetCenter().getY());
		objToWorld.rotate(s.GetRotation());
		g2d.setTransform(objToWorld);
		
		// draw all shapes
		ArrayList<MyShape> shapes = contr.GetShapes();
		for (MyShape s : shapes) {
			
			Color shapeColor = s.GetColor();
			g2d.setColor(shapeColor);
			
			if (s instanceof MyLine) {
				Point b = ((MyLine)s).GetStart();
				Point e = ((MyLine)s).GetEnd();
				
				g2d.setStroke(new BasicStroke(1));
				
				int x1 = b.x;
				int y1 = b.y;
				int x2 = e.x;
				int y2 = e.y;
				g2d.drawLine(x1, y1, x2, y2);
			}
			else if (s instanceof MySquare) {
				
//				Point c = s.GetCenter();
				int l = ((MySquare)s).GetLength();
//				int x = c.x - l/2;
//				int y = c.y - l/2;
				g2d.fillRect(0, 0, l, l);
			}
			else if (s instanceof MyRectangle) {
				
//				Point c = s.GetCenter();
				int w = ((MyRectangle)s).GetWidth();
				int h = ((MyRectangle)s).GetHeight();
//				int x = c.x - w/2;
//				int y = c.y - h/2;
				g2d.fillRect(0, 0, w, h);
			}
			else if (s instanceof MyCircle) {
				
//				Point c = s.GetCenter();
				int r = ((MyCircle)s).GetRadius();
//				int x = c.x-r;
//				int y = c.y-r;
				g2d.fillOval(0, 0, r*2, r*2);
			}
			else if (s instanceof MyEllipse) {
				
//				Point c = s.GetCenter();
				int w = ((MyEllipse)s).GetWidth();
				int h = ((MyEllipse)s).GetHeight();
//				int x = c.x-w/2;
//				int y = c.y-h/2;
				g2d.fillOval(0, 0, w, h);
			}
			else if (s instanceof MyTriangle) {
				
				int[] xPoints = ((MyTriangle) s).GetXPoints();
				int[] yPoints = ((MyTriangle) s).GetYPoints();
				int nPoints = 3;
				if (xPoints[2] == xPoints[0] && yPoints[2] == yPoints[0]) {
					if (xPoints[1] == xPoints[0] && yPoints[1] == yPoints[0])
						nPoints = 1;
					else
						nPoints = 2;
				}
				if (nPoints < 3) {
					g2d.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
				}
				else g2d.fillPolygon(xPoints, yPoints, nPoints);
			}
		}
		
		g2d.setTransform(new AffineTransform());
		
		// draw the selection stuff
		ArrayList<DrawnSelectionItem> handles = contr.GetSelectionHandles();
		if (handles.size() > 0) {
			for (DrawnSelectionItem i : handles) {
				
				g2d.setColor(i.GetColor());
				g2d.setStroke(new BasicStroke(1));
				
				if (i instanceof SelectionAnchor) {
					Point2D p = ((SelectionAnchor) i).GetPoint();
					int r = ((SelectionAnchor) i).GetRadius();
					Ellipse2D.Double anchor = new Ellipse2D.Double(p.getX()-r, p.getY()-r, r*2, r*2);
					g2d.draw(anchor);
				}
				else if (i instanceof RotationAnchor) {
					Point2D p = ((RotationAnchor) i).GetPoint();
					int r = ((RotationAnchor) i).GetRadius();
					Ellipse2D.Double anchor = new Ellipse2D.Double(p.getX()-r, p.getY()-r, r*2, r*2);
					g2d.draw(anchor);
				}
				else if (i instanceof SelectionOutline) {
					Point tl = ((SelectionOutline) i).GetPoint();
					int w = ((SelectionOutline) i).GetWidth();
					int h = ((SelectionOutline) i).GetHeight();
					if (((SelectionOutline) i).IsOval())
						g2d.drawOval(tl.x, tl.y, w, h);
					else
						g2d.drawRect(tl.x, tl.y, w, h);
				}
				else if (i instanceof SelectionOutlineTriangle) {
					Point v1 = ((SelectionOutlineTriangle) i).GetV1();
					Point v2 = ((SelectionOutlineTriangle) i).GetV2();
					Point v3 = ((SelectionOutlineTriangle) i).GetV3();
					g2d.drawLine(v1.x, v1.y, v2.x, v2.y);
					g2d.drawLine(v2.x, v2.y, v3.x, v3.y);
					g2d.drawLine(v3.x, v3.y, v1.x, v1.y);
				}
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		GUIFunctions.refresh();
	}

}
