import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Arrays;

public class test{
	
	
	public static void main(String [] args){

		Point2d p1 = new Point2d(0,100);
		//Point2d p2 = new Point2d(0,-100);
		Point2d p3 = new Point2d(-50,50);
		//Point2d p4 = new Point2d(50,50);
		Point2d p5 = new Point2d(-50,-50);
		//Point2d p6 = new Point2d(50,-50);
		//Point2d pout = new Point2d(-50,-51);
		HullBuilder a = new HullBuilder();

		a.addPoint(p1);
				
		
		//a.addPoint(p2);
		a.addPoint(p3);
		a.addPoint(p5);
		//a.addPoint(p4);

		//a.addPoint(p6);
		//a.getHull();
		//System.out.println(a.upperArray[0]);
		//System.out.println(a.lowerArray[0]);
		System.out.println(a.upperHull);
		System.out.println(a.lowerHull);
		System.out.println(a.hull);
		//
		//System.out.println(a.isInsideHull(pout));
		//a.addPoint(p6);
/* 		System.out.println(a.hull);
		System.out.println(a.upperHull);
		System.out.println(a.lowerHull);
		System.out.println(a.hull.size());
		System.out.println(a.upperHull.size());
		System.out.println(a.lowerHull.size()); */
		
	
		


















		
	}
	
}