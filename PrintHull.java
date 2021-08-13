/* PrintHull.java
   CSC 225 - Summer 2019

   To print the convex hull of a set of points, run with
     java PrintHull <points file>
   where <points file> is a text file containing points (one point per line, with
   coordinates separated by spaces).

   B. Bird - 03/27/2019
   Name: Xuhui Wang	ID: V00913734
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.util.LinkedList;
import java.util.Scanner;

public class PrintHull {



    private static LinkedList<Point2d> readPointsFromFile(String filename){
        try{
            System.err.println("Reading points from "+filename);

            LinkedList<Point2d> points = new LinkedList<Point2d>();

            Scanner s = new Scanner(new File(filename));
            double x, y;
            while(true){
                if (!s.hasNextDouble())
                    break;
                x = s.nextDouble();
                if (!s.hasNextDouble())
                    break;
                y = s.nextDouble();
                points.add(new Point2d(x,y));
            }
            System.err.printf("Read %d points from %s\n", points.size(), filename);
            return points;
        }catch(IOError e){
            System.err.println("Unable to read from "+filename);
        }catch(FileNotFoundException e){
            System.err.println("Unable to open "+filename);
        }
        return null;
    }



    public static void main(String[] args){

        if (args.length != 1){
            System.err.println("Usage: java PrintHull <point file>");
            return;
        }

        String input_filename = args[0];

        LinkedList<Point2d> points = readPointsFromFile(input_filename);
        if (points == null)
            return;
        HullBuilder hb = new HullBuilder();
        for (Point2d p: points)
            hb.addPoint(p);
        LinkedList<Point2d> hull = hb.getHull();
        for (Point2d p: hull)
            System.out.printf("%g %g\n", p.x, p.y);

    }


}
