/* TestContainment.java
   CSC 225 - Summer 2019

   To test if the point (x,y) lies inside the convex hull of a set of points, 
   run with
     java TestContainment <points file> <x> <y>
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

public class TestContainment {

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

        if (args.length != 3){
            System.err.println("Usage: java TestContainment <point file> <x> <y>");
            return;
        }

        String input_filename = args[0];
        double x, y;
        try{
            x = Double.parseDouble(args[1]);
        }catch(NumberFormatException e){
            System.err.printf("\"%s\" is not a number.\n", args[1] );
            return;
        }
        try{
            y = Double.parseDouble(args[2]);
        }catch(NumberFormatException e){
            System.err.printf("\"%s\" is not a number.\n", args[2] );
            return;
        }

        LinkedList<Point2d> points = readPointsFromFile(input_filename);
        if (points == null)
            return;
        HullBuilder hb = new HullBuilder();
        for (Point2d p: points)
            hb.addPoint(p);

        if (hb.isInsideHull(new Point2d(x,y)))
            System.out.printf("Point (%g, %g) is contained in the convex hull.\n",x,y);
        else
            System.out.printf("Point (%g, %g) is not contained in the convex hull.\n",x,y);


    }


}
