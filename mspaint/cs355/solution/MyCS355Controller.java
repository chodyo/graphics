package cs355.solution;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import cs355.GUIFunctions;
import cs355.SelectionHelpers.*;
import cs355.model.*;

public class MyCS355Controller implements cs355.CS355Controller {
	
	private MyCreations shapes = new MyCreations();
	private Color currentColor = new Color(255,255,255);
	private int currentButton = BUTTONS.LINE;
	private int numTriangleVertices = 0;
	private Point anchor;
	private int tolerance = 4;
	private MyShape selectedShape = null;
	private Color selectedColor = null;
	ArrayList<DrawnSelectionItem> handles = new ArrayList<DrawnSelectionItem>();
	private DrawnSelectionItem selectedAnchor = null;
	
	public void DrawpadPressed(Point start)	 {
		anchor = start;
		MyShape s = null;
		switch (currentButton) {
			case BUTTONS.LINE:
				s = new MyLine(currentColor, start);
				break;
			case BUTTONS.SQUARE:
				s = new MySquare(currentColor, start);
				break;
			case BUTTONS.RECTANGLE:
				s = new MyRectangle(currentColor, start);
				break;
			case BUTTONS.CIRCLE:
				s = new MyCircle(currentColor, start);
				break;
			case BUTTONS.ELLIPSE:
				s = new MyEllipse(currentColor, start);
				break;
			case BUTTONS.TRIANGLE:
				if (numTriangleVertices == 0) {
					s = new MyTriangle(currentColor, start);
					numTriangleVertices = 1;
				}
				else {
					s = shapes.Pop();
					if (numTriangleVertices == 1) {
						((MyTriangle)s).SetV2(start);
						numTriangleVertices = 2;
					}
					else {
						((MyTriangle)s).SetV3(start);
						numTriangleVertices = 0;
					}
				}
				break;
			case BUTTONS.SELECT:
				selectedAnchor = GetHandleHit(start);
				if (selectedAnchor != null) {
					// TODO?
					System.out.println(selectedAnchor.getClass());
				}
				else {
					selectedShape = shapes.GetShapeHit(start, tolerance);
					if (selectedShape != null) {
						selectedColor = selectedShape.GetColor();
						SetSelectionItems();
						GUIFunctions.changeSelectedColor(selectedColor);
						shapes.SomethingChanged();
					}
					else {
						Unselect();
					}
				}
				return;
			default:
				break;
		}
		shapes.Push(s);
	}
	
	public void DrawpadDraggedReleased(Point updated) {
		if (currentButton == BUTTONS.SELECT) {
			if (selectedShape == null) return;
			
			if (selectedAnchor != null) {
				if (selectedAnchor instanceof RotationAnchor) {
					double angle = Utility.Angle(anchor, selectedShape.GetCenter(), updated);
					double oldAngle = selectedShape.GetRotation();
					selectedShape.SetRotation(angle+oldAngle);
					anchor = updated;
					SetSelectionItems();
					shapes.SomethingChanged();
				}
				return;
			}
			else {			
				int dx = updated.x - anchor.x;
				int dy = updated.y - anchor.y;
				Point oldCenter = selectedShape.GetCenter();
				Point newCenter = new Point(oldCenter.x + dx, oldCenter.y + dy);
				selectedShape.SetCenter(newCenter);
				SetSelectionItems();
				shapes.SomethingChanged();
				anchor = updated;
				return;
			}
		}
		
		
		MyShape s = shapes.Pop();
		if (s instanceof MyLine) {
			((MyLine) s).SetEnd(updated);
		}
		else if (s instanceof MySquare) {			
			int lenX = Math.abs(updated.x-anchor.x);
			int lenY = Math.abs(updated.y-anchor.y);
			double length = Math.min(lenX, lenY);
			
			double x = (updated.x < anchor.x) ? anchor.x-length/2 : anchor.x+length/2;
			double y = (updated.y < anchor.y) ? anchor.y-length/2 : anchor.y+length/2;
			Point center = new Point((int)x, (int)y);
			
			s.SetCenter(center);
			((MySquare) s).SetLength((int)length);
		}
		else if (s instanceof MyRectangle) {	
			double w = Math.abs(updated.x-anchor.x);
			double h = Math.abs(updated.y-anchor.y);
			
			double x = (updated.x < anchor.x) ? anchor.x-w/2 : anchor.x+w/2;
			double y = (updated.y < anchor.y) ? anchor.y-h/2 : anchor.y+h/2;
			Point center = new Point((int)x, (int)y);
			
			s.SetCenter(center);
			((MyRectangle) s).SetWidth((int)w);
			((MyRectangle) s).SetHeight((int)h);
		}
		else if (s instanceof MyCircle) {
			int lenX = Math.abs(updated.x-anchor.x);
			int lenY = Math.abs(updated.y-anchor.y);
			double r = Math.min(lenX, lenY) / 2;
			
			double x = (updated.x < anchor.x) ? anchor.x-r : anchor.x+r;
			double y = (updated.y < anchor.y) ? anchor.y-r : anchor.y+r;
			Point center = new Point((int)x, (int)y);
			
			s.SetCenter(center);
			((MyCircle) s).SetRadius((int)r);
		}
		else if (s instanceof MyEllipse) {
			double w = Math.abs(updated.x-anchor.x);
			double h = Math.abs(updated.y-anchor.y);
			
			double x = (updated.x < anchor.x) ? anchor.x-w/2 : anchor.x+w/2;
			double y = (updated.y < anchor.y) ? anchor.y-h/2 : anchor.y+h/2;
			Point center = new Point((int)x, (int)y);
			
			s.SetCenter(center);
			((MyEllipse) s).SetWidth((int)w);
			((MyEllipse) s).SetHeight((int)h);
		}
		else if (s instanceof MyTriangle) {
//			pass
//			all logic in "DrawpadReleased()"
		}
		shapes.Push(s);
	}
	
	private void Unselect() {
		selectedShape = null;
		handles.clear();
		GUIFunctions.changeSelectedColor(currentColor);
		shapes.SomethingChanged();
	}
	
	public MyShape GetSelectedShape() {
		return selectedShape;
	}
	
	public ArrayList<MyShape> GetShapes() {
		return shapes.GetShapes();
	}
	
//	stores all selection items relative to the center of the selected object
	public void SetSelectionItems() {
		handles = new ArrayList<DrawnSelectionItem>();
		
		if (selectedShape instanceof MyLine) {
			Point2D s = ((MyLine) selectedShape).GetRelativeStart();
			SelectionAnchor a = new SelectionAnchor(s);
			handles.add(a);
			Point2D e = ((MyLine) selectedShape).GetRelativeEnd();
			a = new SelectionAnchor(e);
			handles.add(a);
		}
		else if (selectedShape instanceof MySquare) {
			int halfLength = ((MySquare) selectedShape).GetLength() / 2;

			Point p = new Point(-halfLength, -halfLength);
			handles.add(new SelectionAnchor(p));
			p = new Point(-halfLength, halfLength);
			handles.add(new SelectionAnchor(p));
			p = new Point(halfLength, -halfLength);
			handles.add(new SelectionAnchor(p));
			p = new Point(halfLength, halfLength);
			handles.add(new SelectionAnchor(p));
			
			boolean isOval = false;
			SelectionOutline o = new SelectionOutline(halfLength*2, halfLength*2, isOval);
			handles.add(o);
			
			p = new Point(0, -2*halfLength);
			handles.add(new RotationAnchor(p));
		}
		else if (selectedShape instanceof MyRectangle) {
			int a = ((MyRectangle) selectedShape).GetWidth()/2;
			int b = ((MyRectangle) selectedShape).GetHeight()/2;

			Point p = new Point(-a, -b);
			handles.add(new SelectionAnchor(p));
			p = new Point(-a, b);
			handles.add(new SelectionAnchor(p));
			p = new Point(a, -b);
			handles.add(new SelectionAnchor(p));
			p = new Point(a, b);
			handles.add(new SelectionAnchor(p));
			
			boolean isOval = false;
			SelectionOutline o = new SelectionOutline(a*2, b*2, isOval);
			handles.add(o);
			
			p = new Point(0, -2*b);
			handles.add(new RotationAnchor(p));
		}
		else if (selectedShape instanceof MyCircle) {
			int r = ((MyCircle) selectedShape).GetRadius();

			Point p = new Point(-r, -r);
			handles.add(new SelectionAnchor(p));
			p = new Point(-r, r);
			handles.add(new SelectionAnchor(p));
			p = new Point(r, -r);
			handles.add(new SelectionAnchor(p));
			p = new Point(r, r);
			handles.add(new SelectionAnchor(p));
			
			boolean isOval = true;
			SelectionOutline o = new SelectionOutline(r*2-1, r*2-1, isOval);
			handles.add(o);
			
//			rotating a circle is useless
//			p = new Point(0, -2*r);
//			handles.add(new RotationAnchor(p));
		}
		else if (selectedShape instanceof MyEllipse) {
			int a = ((MyEllipse) selectedShape).GetWidth()/2;
			int b = ((MyEllipse) selectedShape).GetHeight()/2;

			Point p = new Point(-a, -b);
			handles.add(new SelectionAnchor(p));
			p = new Point(-a, b);
			handles.add(new SelectionAnchor(p));
			p = new Point(a, -b);
			handles.add(new SelectionAnchor(p));
			p = new Point(a, b);
			handles.add(new SelectionAnchor(p));
			
			boolean isOval = true;
			SelectionOutline o = new SelectionOutline(a*2-1, b*2-1, isOval);
			handles.add(o);
			
			p = new Point(0, -2*b);
			handles.add(new RotationAnchor(p));
		}
		else if (selectedShape instanceof MyTriangle) {
			int[] x = ((MyTriangle) selectedShape).GetRelativeXPoints();
			int[] y = ((MyTriangle) selectedShape).GetRelativeYPoints();
			
			Point2D p1 = new Point2D.Double(x[0], y[0]);
			handles.add(new SelectionAnchor(p1));
			Point2D p2 = new Point2D.Double(x[1], y[1]);
			handles.add(new SelectionAnchor(p2));
			Point2D p3 = new Point2D.Double(x[2], y[2]);
			handles.add(new SelectionAnchor(p3));
			
			SelectionOutlineTriangle o = new SelectionOutlineTriangle(p1, p2, p3);
			handles.add(o);
			
			double xr = 3*(p1.getX())/2;
			double yr = 3*(p1.getY())/2;
			Point2D rotation = new Point2D.Double(xr, yr);
			handles.add(new RotationAnchor(rotation));
		}
	}
	
	public DrawnSelectionItem GetHandleHit(Point p) {
		if (selectedShape == null) return null;
		Point2D p2 = new Point2D.Double(0,0);
		Utility.WorldToObject(p, p2, selectedShape.GetCenter(), selectedShape.GetRotation());
		for (DrawnSelectionItem i : handles) {
			if (i.Contains(p2)) {
				return i;
			}
		}
		return null;
	}
	
	public ArrayList<DrawnSelectionItem> GetSelectionHandles() {
		return handles;
	}
	
	public void AddObserver(MyViewRefresher vr) {
		shapes.addObserver(vr);
	}

	@Override
	public void colorButtonHit(Color c) {
		GUIFunctions.changeSelectedColor(c);
		if (selectedShape != null) {
			selectedShape.SetColor(c);
			shapes.SomethingChanged();
		}
		else {
			currentColor = c;
		}
	}

	@Override
	public void triangleButtonHit() {
		currentButton = BUTTONS.TRIANGLE;
		Unselect();
	}

	@Override
	public void squareButtonHit() {
		currentButton = BUTTONS.SQUARE;
		if (numTriangleVertices != 0) {
			shapes.Pop();
			numTriangleVertices = 0;
			GUIFunctions.refresh();
		}
		Unselect();
	}

	@Override
	public void rectangleButtonHit() {
		currentButton = BUTTONS.RECTANGLE;
		if (numTriangleVertices != 0) {
			shapes.Pop();
			numTriangleVertices = 0;
			GUIFunctions.refresh();
		}
		Unselect();
	}

	@Override
	public void circleButtonHit() {
		currentButton = BUTTONS.CIRCLE;
		if (numTriangleVertices != 0) {
			shapes.Pop();
			numTriangleVertices = 0;
			GUIFunctions.refresh();
		}
		Unselect();
	}

	@Override
	public void ellipseButtonHit() {
		currentButton = BUTTONS.ELLIPSE;
		if (numTriangleVertices != 0) {
			shapes.Pop();
			numTriangleVertices = 0;
			GUIFunctions.refresh();
		}
		Unselect();
	}

	@Override
	public void lineButtonHit() {
		currentButton = BUTTONS.LINE;
		if (numTriangleVertices != 0) {
			shapes.Pop();
			numTriangleVertices = 0;
			GUIFunctions.refresh();
		}
		Unselect();
	}

	@Override
	public void selectButtonHit() {
		currentButton = BUTTONS.SELECT;
		if (numTriangleVertices != 0) {
			shapes.Pop();
			numTriangleVertices = 0;
			GUIFunctions.refresh();
		}
	}

	@Override
	public void zoomInButtonHit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zoomOutButtonHit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hScrollbarChanged(int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void vScrollbarChanged(int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toggle3DModelDisplay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(Iterator<Integer> iterator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doEdgeDetection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSharpen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doMedianBlur() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doUniformBlur() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doChangeContrast(int contrastAmountNum) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doChangeBrightness(int brightnessAmountNum) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doLoadImage(BufferedImage openImage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toggleBackgroundDisplay() {
		// TODO Auto-generated method stub
		
	}
	
	private class BUTTONS {
		private static final int LINE = 0;
		private static final int SQUARE = 1;
		private static final int RECTANGLE = 2;
		private static final int CIRCLE = 3;
		private static final int ELLIPSE = 4;
		private static final int TRIANGLE = 5;
		private static final int SELECT = 6;
	}

}
