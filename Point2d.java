/* Point2d.java
   CSC 225 - Summer 2019
   
   A simplified class to store (x,y) points in double precision.

   B. Bird - 03/17/2019
   Name: Xuhui Wang	ID: V00913734
*/


public class Point2d {
    public double x,y;
    public Point2d(){
        x = y = 0;
    }
    public Point2d(double x, double y){
        this.x = x; 
        this.y = y;
    }

    /* chirality(P,Q,R)
       Given three points P, Q and R, determine whether the path
       PQR is a straight line, makes a left turn or makes a right turn.
       The return value will be:
         -1 if PQR makes a left turn
         0  if PQR is a straight line (that is, if P, Q and R are collinear)
         1  if PQR makes a right turn
       Although this method might be convenient, you are not required to use it.
    */
    public static int chirality( Point2d P, Point2d Q, Point2d R ){
        double c = (Q.y-P.y)*(R.x-P.x) + (P.x-Q.x)*(R.y-P.y);
        if (c < 0)
            return -1;
        else if (c > 0)
            return 1;
        else
            return 0;
    }

    /* The convenience methods below may be helpful for coordinate
       transformations (but there is no need to use them). */

    /* offset(dx, dy, scale)
       Return the Point2d resulting from translating this point
       by scale*(dx,dy).
    */
    public Point2d offset(double dx, double dy, double scale){
        return new Point2d(x+dx*scale,y+dy*scale);
    }
    /* offset(dx, dy)
       Return the Point2d resulting from translating this point
       by (dx,dy).
    */
    public Point2d offset(double dx, double dy){
        return offset(dx,dy,1);
    }
    /* offset(p, scale)
       Return the Point2d resulting from translating this point
       by scale*p.
    */
    public Point2d offset(Point2d P, double scale){
        return offset(P.x,P.y,scale);
    }
    /* offset(p)
       Return the Point2d resulting from translating this point
       by p.
    */
    public Point2d offset(Point2d P){
        return offset(P.x,P.y,1);
    }

    /* negate()
       Return the Point2d resulting from negating both coordinates
       of this point.
    */
    public Point2d negate(){
        return new Point2d( -x, -y );
    }

    /* dist(P)
       Return the Euclidean distance between this point and P.
    */
    public double dist(Point2d P){
        double dx = x - P.x;
        double dy = y - P.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    /* length()
       Return the length of this point (when treated as a 2d vector).
       This will equal the distance between (0,0) and this point.
    */
    public double length(){
        return Math.sqrt(x*x + y*y);
    }
}