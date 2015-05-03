package cs355.solution;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Stack;

import cs355.GUIFunctions;
import cs355.model.*;

public class MyCS355Controller implements cs355.CS355Controller {
	
	private MyCreations shapes = new MyCreations();
	private Color currentColor = new Color(255,255,255);
	private int currentButton = BUTTONS.LINE;
	
	public void DrawShape(Point start) {
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
//				TODO
				break;
			case BUTTONS.SELECT:
//				TODO
				break;
			default:
				break;
		}
		shapes.Push(s);
	}
	
	public void UpdateShape(Point updated) {
		MyShape s = shapes.Pop();
		if (s instanceof MyLine) {
			((MyLine)s).Update(updated);
		}
		else if (s instanceof MySquare) {
			Point a = ((MySquare)s).GetAnchor();
			
			int lenX = Math.abs(updated.x-a.x);
			int lenY = Math.abs(updated.y-a.y);
			int length = Math.min(lenX, lenY);
			
			int x = (updated.x < a.x) ? a.x-length : a.x;
			int y = (updated.y < a.y) ? a.y-length : a.y;
			Point newTl = new Point(x, y);
			
			((MySquare)s).Update(newTl, length);
		}
		else if (s instanceof MyRectangle) {
			Point a = ((MyRectangle)s).GetAnchor();
			
			int width = Math.abs(updated.x-a.x);
			int height = Math.abs(updated.y-a.y);
			
			int x = (updated.x < a.x) ? a.x-width : a.x;
			int y = (updated.y < a.y) ? a.y-height : a.y;
			Point newTl = new Point(x, y);
			
			((MyRectangle)s).Update(newTl, width, height);
		}
		else if (s instanceof MyCircle) {
			Point a = ((MyCircle)s).GetAnchor();
			
			int lenX = Math.abs(updated.x-a.x);
			int lenY = Math.abs(updated.y-a.y);
			int rad = Math.min(lenX, lenY) / 2;
			
			int x = (updated.x < a.x) ? a.x-rad : a.x+rad;
			int y = (updated.y < a.y) ? a.y-rad : a.y+rad;
			Point newCenter = new Point(x, y);
			
			((MyCircle)s).Update(newCenter, rad);
		}
		else if (s instanceof MyEllipse) {
			Point a = ((MyEllipse)s).GetAnchor();
			
			int w = Math.abs(updated.x-a.x);
			int h = Math.abs(updated.y-a.y);
			
			int x = (updated.x < a.x) ? a.x-w/2 : a.x+w/2;
			int y = (updated.y < a.y) ? a.y-h/2 : a.y+h/2;
			Point newCenter = new Point(x, y);
			
			((MyEllipse)s).Update(newCenter, w, h);
		}
		shapes.Push(s);
	}
	
	public Stack<MyShape> GetShapes() {
		return shapes.GetShapes();
	}
	
	public void AddObserver(MyViewRefresher vr) {
		shapes.addObserver(vr);
	}

	@Override
	public void colorButtonHit(Color c) {
		GUIFunctions.changeSelectedColor(c);
		currentColor = c;
	}

	@Override
	public void triangleButtonHit() {
		currentButton = BUTTONS.TRIANGLE;
	}

	@Override
	public void squareButtonHit() {
		currentButton = BUTTONS.SQUARE;
	}

	@Override
	public void rectangleButtonHit() {
		currentButton = BUTTONS.RECTANGLE;
	}

	@Override
	public void circleButtonHit() {
		currentButton = BUTTONS.CIRCLE;
	}

	@Override
	public void ellipseButtonHit() {
		currentButton = BUTTONS.ELLIPSE;
	}

	@Override
	public void lineButtonHit() {
		currentButton = BUTTONS.LINE;
	}

	@Override
	public void selectButtonHit() {
		currentButton = BUTTONS.SELECT;
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
