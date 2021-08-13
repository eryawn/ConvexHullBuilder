/* HullBuilder.java
   CSC 225 - Summer 2019

   Starter code for Convex Hull Builder. Do not change the signatures
   of any of the methods below (you may add other methods as needed).

   B. Bird - 03/18/2019
   Name: Xuhui Wang/Eric Wang ID: V00913734 Date: 07/06/2019
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
			if(pointsSet.size() == 2){	//these points are sorted	
				upperHull.addLast(pointsSet.get(0));
				upperHull.addLast(pointsSet.get(1));
				lowerHull.addLast(pointsSet.get(0));
				lowerHull.addLast(pointsSet.get(1));
				setArray();				//copy the points into corresponding arrays
				catHull();
			}
			pointsSet.addLast(P);			
			if(isInsideHull(P) == false)	//if the point is contained in the convex hull, then do not add it
				getHull(P);		//once the number of points >=2, we produce the polygon every time a point is added
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
	
    /* getHull(Point2d P)
	   A helper method to work on convex hull, modify upper and lower hull,
        Finally produce the convex hull in which points are arranged in
		clockwise order.
    */
	private void getHull(Point2d P){
		
		int turn, upperIndex, lowerIndex;
		if(P.x < mostLeftPoint.x || P.x > mostRightPoint.x ){ //the point on either one side of the polygon,  
			getUpperHull(P);								//then we have to consider both the upper hull
			getLowerHull(P);								// and lower hull to conntect to the given point
		}
		else{	//the point is above or below the polygon, then we only have to consider one possibility.
			turn = Point2d.chirality(mostLeftPoint, mostRightPoint, P);
			if(turn == -1){		//left turns represents the point above the middle line
				getUpperHull(P);
			}
			else{				//right turns represents the point below the middle line
				getLowerHull(P);
			}
		}
		setArray();		//push the points into corresponding arrays
		catHull();		//combine upper hull and lower hull into one cempelte hull
	} 
	
    /* getUpperHull(Point2d P)
       A helper method to produce the upper hull. The method connects the given
	   point and removes any points that is no longer a part of convex hull.
    */
	private void getUpperHull(Point2d P){
		
		int index = getNearestIndex(upperArray, P);		//find the index of the nearest point to the given point
		ListIterator<Point2d> iter = upperHull.listIterator(); 	//traverse a part of upper hull with an iterator
		for(int i = 0; i < index; i++){
			iter.next();
		}
		iter.add(P); 	//add the given point to the upper hull
				
   		while(iter.nextIndex() < upperHull.size() - 1){ //traverse the upper hull until reach the end on the right				
 			if(Point2d.chirality(P, iter.next(), iter.next()) == 1){ //right turn represents the point no needed to remove
				iter.previous();
				iter.previous();
				break;
			}
			else{	//left turn represents the point needed to remove
				iter.previous();
				iter.previous();
				iter.remove();
			}			
		}
		iter.previous();
		
   		while(iter.previousIndex() > 0){ //traverse the upper hull until reach the end on the left				
 			if(Point2d.chirality(P, iter.previous(), iter.previous()) == -1){//left turn represents the point no needed to remove
				iter.next();
				iter.next();
				break;
			}
			else{	// right turn represents the point needed to remove
				iter.next();
				iter.next();
				iter.remove();
			}		
		} 		
	}

    /* getLowerHull(Point2d P)
       A helper method to produce the lower hull. The method connects the given
	   point and removes any points that is no longer a part of convex hull.
    */
	private void getLowerHull(Point2d P){

		int index = getNearestIndex(lowerArray ,P);	 //find the index of the nearest point to the given point
		ListIterator<Point2d> iter = lowerHull.listIterator();	//traverse a part of upper hull with an iterator

		for(int i = 0; i < index; i++){
			iter.next();
		}
		iter.add(P);	//add the given point to the upper hull

   		while(iter.nextIndex() < lowerHull.size() - 1){ //traverse the upper hull until reach the end on the right				
 			if(Point2d.chirality(P, iter.next(), iter.next()) == -1){//left turn represents the point no needed to remove
				iter.previous();
				iter.previous();
				break;
			}
			else{// right turn represents the point needed to remove
				iter.previous();
				iter.previous();
				iter.remove();
			}			
		}
		iter.previous();

   		while(iter.previousIndex() > 0){ ////traverse the upper hull until reach the end on the left

 			if(Point2d.chirality(P, iter.previous(), iter.previous()) == 1){//right turn represents the 
				iter.next();												//point no needed to remove
				iter.next();
				break;
			}
			else{// left turn represents the point needed to remove
				iter.next();
				iter.next();
				iter.remove();
			}		
		} 
	}

    /* setArray()
       Copy x-coordinate of every point in upper hull and lower hull to the
	   corresponding arrays, in order to determine if a particular
	   point is contained in the polygon, using binarySearch.
    */
	private void setArray(){
				
		mostLeftPoint = upperHull.getFirst();
		mostRightPoint = upperHull.getLast();
		upperArray = new double[upperHull.size()];
		ListIterator<Point2d> upperIter = upperHull.listIterator();
		lowerArray = new double[lowerHull.size()];
		ListIterator<Point2d> lowerIter = lowerHull.listIterator();
		int i = 0;
		
		while(upperIter.hasNext()){ //copy x-coordinate of the point into corresponding position in the array
			upperArray[i] = upperIter.next().x;
			i++;		
		}	
		i = 0;
		while(lowerIter.hasNext()){
			lowerArray[i] = lowerIter.next().x;
			i++;
		}		
	}

    /* catHull()
       Concatenate the upper hull and lower hull to one complete convex hull.
    */
	private void catHull(){

		hull.clear();
		hull.addAll(upperHull); //Appends all of the elements in upper hull to hull
		ListIterator<Point2d> iter = lowerHull.listIterator();
		
		if(upperHull.getFirst() != lowerHull.getFirst()){
			hull.addFirst(lowerHull.getFirst());
		}
		for(int i = 0; i < lowerHull.size() - 1; i++){
			iter.next();
		}
		for(int i = 0; i < lowerHull.size() - 2; i++){//Appends all of the elements in lower hull to hull
			hull.addLast(iter.previous());
		}
	}

    /* getNearestIndex()
       Return the index of the point nearest to the given point 
	   in the array of upper hull or lower hull, using binarySearch.
    */
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

		if(P.x < mostLeftPoint.x || P.x > mostRightPoint.x)//the point on either one side of the polygon,
			return false;									//is an exterior point
		
		int upperTurn = 0;
		int lowerTurn = 0;
		Point2d upperTemp = null;
		Point2d lowerTemp = null;
		
		int upperIndex = getNearestIndex(upperArray, P);
		ListIterator<Point2d> upperIter = upperHull.listIterator();
		int lowerIndex = getNearestIndex(lowerArray, P);
		ListIterator<Point2d> lowerIter = lowerHull.listIterator();
		
		if(upperIndex == 0){//find the upper bound of the given point in upper hull
			upperTurn = Point2d.chirality(upperIter.next(), upperIter.next(), P);
		}
		else{ 
			for(int i = 0; i < upperIndex; i++){
				upperTemp = upperIter.next();
			}
			upperTurn = Point2d.chirality(upperTemp, upperIter.next(), P);
		}

		if(lowerIndex == 0){//find the upper bound of the given point in upper hull
			lowerTurn = Point2d.chirality(lowerIter.next(), lowerIter.next(), P);
		}
		else{
			for(int i = 0; i < lowerIndex; i++){
				lowerTemp = lowerIter.next();
			}
			lowerTurn = Point2d.chirality(lowerTemp, lowerIter.next(), P);
		}
		
		int chirality = upperTurn * lowerTurn;
 		if(chirality < 0)// if the upper bound and lower bound of the point both make the left turn or right turn 
			return true;					//to the given point, then the point must be outside of the polygon
		else if(chirality > 0)				//if the upper bound and lower bound of the point make different turn
			return false;					//to the given point, then the point must be inside of the polygon
		else
			return true;      
    }
}