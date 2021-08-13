/* HullBuilder.java
   CSC 225 - Summer 2019

   Starter code for Convex Hull Builder. Do not change the signatures
   of any of the methods below (you may add other methods as needed).

   B. Bird - 03/18/2019
   Name: Xuhui Wang	ID: V00913734
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
		}
		
		else{
			if(pointsSet.size() == 2){				
				upperHull.addLast(pointsSet.get(0));
				upperHull.addLast(pointsSet.get(1));
				lowerHull.addLast(pointsSet.get(0));
				lowerHull.addLast(pointsSet.get(1));
				setArray();
				catHull();
			}
			pointsSet.addLast(P);			
			if(isInsideHull(P) == false)
				getHull(P);
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
	
	private void getHull(Point2d P){
		
		int turn, upperIndex, lowerIndex;
		if(P.x < mostLeftPoint.x || P.x > mostRightPoint.x){ //on two sides
			getUpperHull(P);
			getLowerHull(P);
		}
		else{
			turn = Point2d.chirality(mostLeftPoint, mostRightPoint, P);
			if(turn == -1)	//getupperhull
				getUpperHull(P);
			else
				getLowerHull(P);
		}
		setArray();
		catHull();
	} 
	
	private void getUpperHull(Point2d P){
		
		int index = getNearestIndex(upperArray, P);
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
		ListIterator<Point2d> upperIter = upperHull.listIterator();
		lowerArray = new double[lowerHull.size()];
		ListIterator<Point2d> lowerIter = lowerHull.listIterator();
		int i = 0;
		
		while(upperIter.hasNext()){
			upperArray[i] = upperIter.next().x;
			i++;		
		}	
		i = 0;
		while(lowerIter.hasNext()){
			lowerArray[i] = lowerIter.next().x;
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
 		if(index < 0){ //on the rights
			index *= -1;
			index --;
		}
		
		return index;
	}

    /* isInsideHull(P)
       Given an point P, return true if P lies inside the convex hull
       of the current point set (note that P may not be part of the
       current point set). Return false otherwise.
     */
    public boolean isInsideHull(Point2d P){

		if(P.x < mostLeftPoint.x || P.x > mostRightPoint.x)
			return false;
		
		int upperTurn = 0;
		int lowerTurn = 0;
		Point2d upperTemp = null;
		Point2d lowerTemp = null;
		
		int upperIndex = getNearestIndex(upperArray, P);
		ListIterator<Point2d> upperIter = upperHull.listIterator();
		int lowerIndex = getNearestIndex(lowerArray, P);
		ListIterator<Point2d> lowerIter = lowerHull.listIterator();
		
		if(upperIndex == 0){
			upperTurn = Point2d.chirality(upperIter.next(), upperIter.next(), P);
		}
		else{ // if(upperIter.nextIndex() < upperHull.size())
			for(int i = 0; i < upperIndex; i++){
				upperTemp = upperIter.next();
			}
			upperTurn = Point2d.chirality(upperTemp, upperIter.next(), P);
		}

		if(lowerIndex == 0){
			lowerTurn = Point2d.chirality(lowerIter.next(), lowerIter.next(), P);
		}
		else{ // if(lowerIter.nextIndex() < lowerHull.size())
			for(int i = 0; i < lowerIndex; i++){
				lowerTemp = lowerIter.next();
			}
			lowerTurn = Point2d.chirality(lowerTemp, lowerIter.next(), P);
		}
		
		int chirality = upperTurn * lowerTurn;
 		if(chirality < 0)
			return true;
		else if(chirality > 0)
			return false;
		else
			return false;      
    }
}