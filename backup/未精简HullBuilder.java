/* HullBuilder.java
   CSC 225 - Summer 2019

   Starter code for Convex Hull Builder. Do not change the signatures
   of any of the methods below (you may add other methods as needed).

   B. Bird - 03/18/2019
   (Add your name/studentID/date here)
*/

import java.util.LinkedList;
import java.util.Arrays;
import java.util.ListIterator;

public class HullBuilder{
	
	LinkedList<Point2d> pointsSet = new LinkedList<Point2d>();
	LinkedList<Point2d> hull = new LinkedList<Point2d>();
	LinkedList<Point2d> upperHull = new LinkedList<Point2d>();
	LinkedList<Point2d> lowerHull = new LinkedList<Point2d>();
	double[] upperArray;
	double[] lowerArray;
	Point2d mostLeftPoint;
	Point2d mostRightPoint;

    /* addPoint(P)
       Add the point P to the internal point set for this object.
       Note that there is no facility to delete points (other than
       destroying the HullBuilder and creating a new one). 
    */
    public void addPoint(Point2d P){
		
        if(pointsSet.size() < 2){		
			if(pointsSet.size() == 0){
				pointsSet.addFirst(P);
				return;
			}
			else{
				if(P.x >= pointsSet.get(0).x){
					pointsSet.addLast(P);
					return;
				}
				else{
					pointsSet.addFirst(P);
					return;
				}
			}
/* 			for(int i = 0; i < pointsSet.size() - 1; i++){
				if(P.x >= pointsSet.get(i).x && P.x <= pointsSet.get(i + 1).x){
					pointsSet.add(i + 1, P);
					return;
				}
			}
			if(P.x <= pointsSet.get(0).x){
				pointsSet.addFirst(P);
			else
				pointsSet.addLast(P);*/
		}	
		else{

			if(pointsSet.size() == 2)
				getHulla();
			
			pointsSet.addLast(P);
			
			if(isInsideHull(P) == false)
				getHullb(P);
		}
    }
	
    /* getHull()
       Return a java.util.LinkedList object containing the points
       in the convex hull, in order (such that iterating over the list
       will produce the same ordering of vertices as walking around the 
       polygon).
    */	
 	public LinkedList<Point2d> getHull(){
		
		return hull;
	}
	
    public LinkedList<Point2d> getHulla(){
/*  			upperHull.clear();
			lowerHull.clear();
			hull.clear(); */
			upperHull.addLast(pointsSet.get(0));
			upperHull.addLast(pointsSet.get(1));
			for(int i = 1; i < pointsSet.size(); i++){
				Point2d p = pointsSet.get(i);
				while(upperHull.size() >= 2){
					Point2d a = upperHull.get(upperHull.size() - 2); 
					Point2d b = upperHull.get(upperHull.size() - 1);
					if(Point2d.chirality(a, b, p) > 0){
						break;
					}
					else{
						upperHull.removeLast();
					}
				}
				upperHull.addLast(p);
			}
		
			lowerHull.addLast(pointsSet.get(0));
			lowerHull.addLast(pointsSet.get(1));
			for(int i = 1; i < pointsSet.size(); i++){
				Point2d p = pointsSet.get(i);
				while(lowerHull.size() >= 2){
					Point2d a = lowerHull.get(lowerHull.size() - 2); 
					Point2d b = lowerHull.get(lowerHull.size() - 1);
					if(Point2d.chirality(a, b, p) < 0){
						break;
					}
					else{
						lowerHull.removeLast();
					}
				}
				lowerHull.addLast(p);
			}
		setArray();
		catHull();
        return hull;
    }
	
	public void getHullb(Point2d P){
		
		int turn, upperIndex, lowerIndex;
		if(P.x < mostLeftPoint.x || P.x > mostRightPoint.x){ //on two sides
			getUpperHull(P);
			getLowerHull(P);
		}
		
		
		else{ //same region
/*   			if(isInsideHull(P)){
				System.out.println("oweriueoriueoriuoe");
				return;  
			} */
			turn = Point2d.chirality(mostLeftPoint, mostRightPoint, P);
			if(turn == -1){	//getupperhull
			System.out.println("at here");
				getUpperHull(P);
				
			}
			
			else{//getlowerhull
				getLowerHull(P);
				
				
			}
			
			
			
		}
		setArray();
		catHull();
	} 
	
	private void getUpperHull(Point2d P){
		
		int index = getNearestIndex(upperArray, P);
		//System.out.println("upper hull nearest index is " + index);

		ListIterator<Point2d> iter = upperHull.listIterator();
		for(int i = 0; i < index; i++){
			iter.next();
		}
		iter.add(P);

				
   		while(iter.nextIndex() < upperHull.size() - 1){ //reach the end on the right
					
 			if(Point2d.chirality(P, iter.next(), iter.next()) == 1){
				iter.previous();
				iter.previous();
				break;
			}
			else{
				iter.previous();
				iter.previous();
				iter.remove();
			}
				
		}
		iter.previous();
   		while(iter.previousIndex() > 0){ //reach the end on the left
					
 			if(Point2d.chirality(P, iter.previous(), iter.previous()) == -1){
				iter.next();
				iter.next();
				break;
			}
			else{
				iter.next();
				iter.next();
				iter.remove();
			}
		
		} 
		
	}
	
	private void getLowerHull(Point2d P){

		int index = getNearestIndex(lowerArray ,P);
				
		ListIterator<Point2d> iter = lowerHull.listIterator();
		for(int i = 0; i < index; i++){
			iter.next();
		}
		iter.add(P);

				
   		while(iter.nextIndex() < lowerHull.size() - 1){ //reach the end on the right
					
 			if(Point2d.chirality(P, iter.next(), iter.next()) == -1){
				iter.previous();
				iter.previous();
				break;
			}
			else{
				iter.previous();
				iter.previous();
				iter.remove();
			}
				
		}
		iter.previous();
   		while(iter.previousIndex() > 0){ //reach the end on the left
					
 			if(Point2d.chirality(P, iter.previous(), iter.previous()) == 1){
				iter.next();
				iter.next();
				break;
			}
			else{
				iter.next();
				iter.next();
				iter.remove();
			}
		
		} 

		
	}
	
	private void setArray(){
				
		mostLeftPoint = upperHull.getFirst();
		mostRightPoint = upperHull.getLast();

		upperArray = new double[upperHull.size()];
		ListIterator<Point2d> iter = upperHull.listIterator();
		int i = 0;
		//System.out.println(upperHull);
		
		while(iter.hasNext()){
			upperArray[i] = iter.next().x;
			System.out.println(upperArray[i]);
			i++;
			
		}
		
		
		lowerArray = new double[lowerHull.size()];
		ListIterator<Point2d> iter1 = lowerHull.listIterator();
		i = 0;
		while(iter1.hasNext()){
			lowerArray[i] = iter1.next().x;
			i++;
		}		
	}
	
	private void catHull(){
		

		
		hull.clear();
		hull.addAll(upperHull);

		ListIterator<Point2d> iter = lowerHull.listIterator();
		for(int i = 0; i < lowerHull.size() - 1; i++){
			iter.next();
		}
		for(int i = 0; i < lowerHull.size() - 2; i++){
			hull.addLast(iter.previous());
		}
	}
	
	private int getNearestIndex(double[] array,Point2d P){
	
		int index = Arrays.binarySearch(array, P.x);

		System.out.println(index);
 		if(index < 0){ //on the rights
			index *= -1;
			index -= 1;
		}
		
		return index;
	}

	public String toString(){
		String s = "";
		for(int i = 0; i < hull.size(); i++){
			s += hull.get(i).toString();
		}
		return s;
	}

    /* isInsideHull(P)
       Given an point P, return true if P lies inside the convex hull
       of the current point set (note that P may not be part of the
       current point set). Return false otherwise.
     */
    public boolean isInsideHull(Point2d P){
		System.out.println(P.x);
		System.out.println("left x is"+mostLeftPoint.x);
		System.out.println("right x is"+mostRightPoint.x);
		if(P.x < mostLeftPoint.x || P.x > mostRightPoint.x){

			return false;
		}
		
		int upperTurn = 0;
		int lowerTurn = 0;

		int upperIndex = getNearestIndex(upperArray, P);
		System.out.println("upperindex is " + upperIndex);
		Point2d temp = null;
		ListIterator<Point2d> upperIter = upperHull.listIterator();

		if(upperIndex == 0){
			upperTurn = Point2d.chirality(upperIter.next(), upperIter.next(), P);
		}
		else if(upperIter.nextIndex() < upperHull.size()){
			for(int i = 0; i < upperIndex; i++){
				temp = upperIter.next();
			}
			upperTurn = Point2d.chirality(temp, upperIter.next(), P);
		}
			System.out.println("upperturn is "+ upperTurn);
		
		
		int lowerIndex = getNearestIndex(lowerArray, P);
		System.out.println("lowerindex is " + lowerIndex);
		System.out.println("lowerindex is " + lowerIndex);
		Point2d temp1 = null;
		ListIterator<Point2d> lowerIter = lowerHull.listIterator();

		if(lowerIndex == 0){
			lowerTurn = Point2d.chirality(lowerIter.next(), lowerIter.next(), P);
		}
		else if(lowerIter.nextIndex() < lowerHull.size()){
			for(int i = 0; i < lowerIndex; i++){
				temp1 = lowerIter.next();
			}
			lowerTurn = Point2d.chirality(temp1, lowerIter.next(), P);
		}		
		System.out.println("lowerturn is "+ lowerTurn);
		
		
		int hand = upperTurn * lowerTurn;
 		if(hand < 0)
			return true;
		else if(hand > 0)
			return false;
		else
			return false;
        
    }
}