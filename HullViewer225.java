/* HullViewer225.java
   CSC 225 - Summer 2019
   Visualization tool for points and convex hulls.

   Run with 
     java HullViewer225

   B. Bird - 03/16/2019
   Name: Xuhui Wang	ID: V00913734
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.LineMetrics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Collections;

public class HullViewer225 {


    /* Warning: Although the Vector type (an array-backed list) is used in this viewer program,
                it may have undesirable asymptotic properties for the assignment implementation.
                (Therefore, you should carefully consider the worst-case running time of Vector
                 operations before using that data structure in your submission)
     */
    Vector<Point2d> initial_points;
    Vector<Point2d> points;
    LinkedList<Point2d> hull;

    private JFrame viewerWindow;
    private JPanel lowerPanel, lowerLeftPanel, lowerRightPanel;
    private HullCanvas hullCanvas;
    private HullBuilder hullBuilder;

    private HullViewer225(){
        initial_points = new Vector<Point2d>();
        points = new Vector<Point2d>();
        hull = null;
        hullBuilder = new HullBuilder();

        initialize();

        resetPoints();

    }
    private HullViewer225(String point_filename){
        initial_points = new Vector<Point2d>();
        points = new Vector<Point2d>();
        hull = null;
        hullBuilder = new HullBuilder();

        initialize();

        loadPointsFromFile(point_filename);
    }


    private void initialize() {
        viewerWindow = new JFrame();
        lowerPanel = new JPanel();
        lowerLeftPanel = new JPanel();
        lowerRightPanel = new JPanel();

        viewerWindow.setTitle("CSC 225 - Summer 2019 - Hull Viewer");
        viewerWindow.setBounds(100, 100, 1000, 600);
        viewerWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        hullCanvas = new HullCanvas();
        viewerWindow.getContentPane().add(hullCanvas, BorderLayout.CENTER);

        viewerWindow.getContentPane().add(lowerPanel, BorderLayout.SOUTH);
        lowerPanel.setLayout(new BorderLayout(0, 0));
        lowerPanel.add(lowerLeftPanel, BorderLayout.WEST);
        lowerPanel.add(lowerRightPanel, BorderLayout.EAST);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetPoints();
                hullCanvas.resetSize();
            }
        });
        lowerLeftPanel.add(resetButton);
        resetButton.setHorizontalAlignment(SwingConstants.LEFT);


        JButton resetViewButton = new JButton("Default View");
        resetViewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hullCanvas.resetSize();
            }
        });
        lowerLeftPanel.add(resetViewButton);
        resetViewButton.setHorizontalAlignment(SwingConstants.LEFT);

        JButton sizeToContentsButton = new JButton("Zoom to All");
        sizeToContentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hullCanvas.scaleToContents();
            }
        });
        lowerLeftPanel.add(sizeToContentsButton);
        sizeToContentsButton.setHorizontalAlignment(SwingConstants.LEFT);




        JButton computeHullButton = new JButton("Compute Convex Hull");
        computeHullButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                computeHull();
            }
        });
        lowerRightPanel.add(computeHullButton);
        computeHullButton.setHorizontalAlignment(SwingConstants.LEFT);


        JButton saveButton = new JButton("Save Points");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                savePointFile();
            }
        });
        lowerRightPanel.add(saveButton);
        saveButton.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton loadButton = new JButton("Load Points");
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPointFile();
            }
        });
        lowerRightPanel.add(loadButton);
        loadButton.setHorizontalAlignment(SwingConstants.RIGHT);

        
    }



    private class HullCanvas extends JComponent{
    
        private static final long serialVersionUID = 1L;


        private boolean dragActive = false;
        private Point2d canvasOrigin = new Point2d(0,0); //Coordinates of the lower left corner of the canvas in space
        double scaleFactor = 1.0; //Number of pixels per unit length in the coordinate system (larger means higher zoom)
        private Point2d dragOrigin = new Point2d(0,0);
        private Point2d dragStart = new Point2d(0,0);
        private Dimension currentSize = null;

        boolean testContainment = false;

        private Point2d currentMouseCoordinates = null; //If the mouse is currently on the canvas, this will store its location in the coordinate space.

        private static final int locusRadius = 5;
        private final Color bgColour = new Color(255, 255,255);
        private final Color locusColour = new Color(0,0,0);
        private final Color hullColour = new Color(192, 192,224);
        private final Color frameColour = new Color(224,224,224);
        private final Color tagBGColour = new Color(192,255,192);
        private final Color tagFrameColour = new Color(0,128,0);
        private final Color tagTextColour = new Color(0,32,0);
        private final Color mouseXYColour = new Color(0,0,0);
        private final Color insideHullColour = new Color(0,255,0);
        private final Color outsideHullColour = new Color(255,0,0);


        public HullCanvas(){

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    resize();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    int y = getHeight() - e.getY();
                    if (e.getButton() != 1)
                        return;
                    Point2d p = canvasToCoordinates(x,y);
                    addPoint(p);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    int x = e.getX();
                    int y = getHeight() - e.getY();
                    mouseOver( canvasToCoordinates(x,y) );
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    testContainment = false;
                    currentMouseCoordinates = null;
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() != 3)
                        return;
                    testContainment = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.getButton() != 3)
                        return;
                    testContainment = false;
                    repaint();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int x = e.getX();
                    int y = getHeight() - e.getY();
                    if (testContainment) {
                        mouseMoved(e);
                        return;
                    }
                    drag(x,y);
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    int x = e.getX();
                    int y = getHeight() - e.getY();
                    mouseOver( canvasToCoordinates(x,y) );
                }

            });
            addMouseWheelListener(new MouseWheelListener() {
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int z = e.getWheelRotation();
                    zoom(z);
                }
            });
        }

        @Override
        public void paintComponent(Graphics g){
            g.setColor(bgColour);
            g.fillRect(0, 0, getWidth(), getHeight());

            {
                g.setColor(frameColour);
                Point2d coordinateOrigin = coordinatesToCanvas(0,0);
                int zx = (int)coordinateOrigin.x;
                int zy = getHeight() - (int)coordinateOrigin.y;
                g.drawLine(zx,0,zx,getHeight());
                g.drawLine(0,zy,getWidth(),zy);
            }

            if (hull != null) {

                int[] xPts = new int[hull.size()];
                int[] yPts = new int[hull.size()];
                int i = 0;
                for (Point2d p: hull){
                    Point2d canvasPoint = coordinatesToCanvas(p);
                    xPts[i] = (int) canvasPoint.x;
                    yPts[i] = (int) (getHeight() - canvasPoint.y);
                    i++;
                }
                g.setColor(hullColour);
                g.fillPolygon(xPts, yPts, hull.size());

            }

            g.setColor(locusColour);
            for (Point2d p: points){
                Point2d canvasPoint = coordinatesToCanvas(p.x, p.y);
                int x = (int)canvasPoint.x - locusRadius;
                int y = (int)(getHeight() - canvasPoint.y) - locusRadius;
                int w = 2*locusRadius;
                int h = w;
                g.fillOval( x, y, w, h);

            }
            if (currentMouseCoordinates != null){
                FontMetrics fm = g.getFontMetrics();
                for (Point2d p: points){
                    if (p.dist(currentMouseCoordinates) <= locusRadius/scaleFactor){
                        Point2d canvasPoint = coordinatesToCanvas(p.x, p.y);
                        String pointStr = String.format("(%g,%g)",p.x,p.y);
                        LineMetrics lm = fm.getLineMetrics(pointStr, g);
                        int h = (int)lm.getHeight();
                        int hoff = (int)(.1*lm.getHeight());
                        int w = (int)fm.stringWidth(pointStr);
                        int cx = (int)canvasPoint.x;
                        int cy = (int)(getHeight() - canvasPoint.y);
                        g.setColor(tagBGColour);
                        g.fillRect(cx,cy-h-hoff, w, h+2*hoff);
                        g.setColor(tagFrameColour);
                        g.drawRect(cx,cy-h-hoff, w, h+2*hoff);
                        g.setColor(tagTextColour);
                        g.drawString(pointStr,cx,cy-hoff);
                    }
                }

                String coordsStr = String.format("%g,%g", currentMouseCoordinates.x, currentMouseCoordinates.y);
                g.setColor(mouseXYColour);
                g.drawString(coordsStr, getWidth()-fm.stringWidth(coordsStr),getHeight());

                if (testContainment){
                    if (pointInsideHull(currentMouseCoordinates)){
                        g.setColor(insideHullColour);
                        System.out.printf("Point (%g,%g) is inside the convex hull.\n", currentMouseCoordinates.x, currentMouseCoordinates.y);
                    } else {
                        g.setColor(outsideHullColour);
                        System.out.printf("Point (%g,%g) is outside the convex hull.\n", currentMouseCoordinates.x, currentMouseCoordinates.y);
                    }
                    Point2d canvasPoint = coordinatesToCanvas(currentMouseCoordinates.x, currentMouseCoordinates.y);
                    int x = (int)canvasPoint.x - locusRadius;
                    int y = (int)(getHeight() - canvasPoint.y) - locusRadius;
                    int w = 2*locusRadius;
                    int h = w;
                    g.fillOval( x, y, w, h);
                }
            }

        }




        public void resetSize(){
            canvasOrigin = new Point2d( -getWidth()/2.0, -getHeight()/2.0 );
            scaleFactor = 1;
            repaint();
        }

        private void resize(){
            if (currentSize == null){
                canvasOrigin = new Point2d( -getWidth()/2.0, -getHeight()/2.0 );
            }else{
                Dimension newSize = getSize();
                int dx = newSize.width - currentSize.width;
                int dy = newSize.height - currentSize.height;
                canvasOrigin = canvasToCoordinates(-dx/2,-dy/2);
            }
            currentSize = getSize();
        }

        private void scaleToContents(){
            double minX, minY, maxX, maxY;
            if (points.size() == 0){
                minX = minY = -100;
                maxX = maxY = 100;
            }else{
                minX = minY = points.firstElement().x;
                maxX = maxY = points.firstElement().y;
                for (Point2d p: points){
                    minX = (p.x < minX)? p.x: minX;
                    maxX = (p.x > maxX)? p.x: maxX;
                    minY = (p.y < minY)? p.y: minY;
                    maxY = (p.y > maxY)? p.y: maxY;
                }
            }
            //Scale the view to contain the bounding box defined by
            //(minX, minY) and (maxX, maxY) padded by a margin of 16% on each side
            double marginX = (maxX-minX)*.16;
            double marginY = (maxY-minY)*.16;
            if (marginX == 0)
                marginX = 1;
            if (marginY == 0)
                marginY = 1;

            minX -= marginX;
            maxX += marginX;
            minY -= marginY;
            maxY += marginY;

            double xScale = getWidth()/(maxX-minX);
            double yScale = getHeight()/(maxY-minY);
            if (xScale < yScale){
                scaleFactor = xScale;
                minY -= 0.5*getHeight()*(1/xScale-1/yScale);
            }else{
                scaleFactor = yScale;
                minX -= 0.5*getWidth()*(1/yScale-1/xScale);
            }
            canvasOrigin.x = minX;
            canvasOrigin.y = minY;

            repaint();
        }

        private void drag(double canvasX, double canvasY){
            if (!dragActive) {
                dragStart = canvasToCoordinates(canvasX,canvasY);
                dragOrigin = canvasOrigin;
                dragActive = true;
            }else{
                //If dragging is already active, translate the drag origin (the canvas origin at the time the drag started) by the amount
                //of movement from the start point of the drag to the current position.
                Point2d dragLocalCoords = canvasToCoordinates(canvasX,canvasY,dragOrigin);
                double dx = (dragLocalCoords.x - dragStart.x);
                double dy = (dragLocalCoords.y - dragStart.y);
                canvasOrigin = dragOrigin.offset(-dx,-dy);
                repaint();
            }
        }

        private void mouseOver(Point2d currentCoords){
            currentMouseCoordinates = currentCoords;
            Point2d p = coordinatesToCanvas(currentMouseCoordinates);
            dragActive = false;
            repaint();
        }

        private void zoom(int z){
            double zoomDelta = Math.pow(0.9,z);
            if (currentMouseCoordinates != null){
                canvasOrigin.x = (currentMouseCoordinates.x*(zoomDelta-1) + canvasOrigin.x)/zoomDelta;
                canvasOrigin.y = (currentMouseCoordinates.y*(zoomDelta-1) + canvasOrigin.y)/zoomDelta;
                scaleFactor = scaleFactor*zoomDelta;
            } else {
                scaleFactor = scaleFactor*zoomDelta;
                canvasOrigin.x /= zoomDelta;
                canvasOrigin.y /= zoomDelta;
            }
            repaint();
        }


        private Point2d canvasToCoordinates(double x, double y, Point2d canvasOrigin){
            return new Point2d(x/scaleFactor+canvasOrigin.x,y/scaleFactor+canvasOrigin.y);
        }
        private Point2d canvasToCoordinates(double x, double y){
            return canvasToCoordinates(x,y,canvasOrigin);
        }
        private Point2d canvasToCoordinates(Point2d p){
            return canvasToCoordinates(p.x,p.y);
        }
        private Point2d coordinatesToCanvas(double x, double y){
            return new Point2d((x-canvasOrigin.x)*scaleFactor,(y-canvasOrigin.y)*scaleFactor);
        }
        private Point2d coordinatesToCanvas(Point2d p){
            return coordinatesToCanvas(p.x,p.y);
        }



    }

    private void resetPoints(){
        points.clear();
        for(Point2d p: initial_points){
            points.add(p);
        }
        hull = null;
        hullBuilder = new HullBuilder();
        for(Point2d p: points)
            hullBuilder.addPoint(p);
        hullCanvas.repaint();
    }


    private void openPointFile(){
        JFileChooser chooser = new JFileChooser(Paths.get(".").toAbsolutePath().toString());
        int result = chooser.showOpenDialog(viewerWindow);
        if (result != JFileChooser.APPROVE_OPTION){
            System.out.println("Cancelled");
            return;
        }
        File f = chooser.getSelectedFile();
        loadPointsFromFile(f.getName());
    }

    private void loadPointsFromFile(String filename){
        try{
            System.out.println("Opening "+filename);

            Vector<Point2d> newPoints = new Vector<Point2d>();

            Scanner s = new Scanner(new File(filename));
            double x, y;
            while(true){
                if (!s.hasNextDouble())
                    break;
                x = s.nextDouble();
                if (!s.hasNextDouble())
                    break;
                y = s.nextDouble();
                newPoints.add(new Point2d(x,y));
            }

            initial_points.clear();
            initial_points.addAll(newPoints);
            hull = null;

            resetPoints();

            System.out.printf("Loaded %d points from %s\n", points.size(), filename);
        }catch(IOError e){
            System.out.println("Unable to load from "+filename);
        }catch(FileNotFoundException e){
            System.out.println("Unable to load from "+filename);
        }
    }

    private void savePointFile(){
        JFileChooser chooser = new JFileChooser(Paths.get(".").toAbsolutePath().toString());
        int result = chooser.showSaveDialog(viewerWindow);
        if (result != JFileChooser.APPROVE_OPTION){
            System.out.println("Cancelled");
            return;
        }
        File f = chooser.getSelectedFile();
        System.out.println("Saving "+f.getName());
        try {
            PrintStream ps = new PrintStream(f);
            for(Point2d p: points){
                ps.printf("%g %g\n", p.x, p.y);
            }
            ps.close();
        }catch (IOError e){
            System.out.println("Unable to write to "+f.getName());
        }catch (FileNotFoundException e){
            System.out.println("Unable to write to "+f.getName());
        }
    }



    public void addPoint(Point2d p){
        points.add(p);
        if (hullBuilder == null)
            hullBuilder = new HullBuilder();
        hullBuilder.addPoint(p);
        hullCanvas.repaint();
    }

    public void computeHull(){
        System.out.println("Computing Convex Hull");
        hull = hullBuilder.getHull();
        if (hull == null){
            System.out.println("getHull() returned null");
        }else{
            System.out.printf("getHull() returned %d points\n", hull.size());
            for(Point2d p: hull) 
                System.out.printf("\t(%g, %g)\n",p.x,p.y);
        }
        hullCanvas.repaint();
    }

    public boolean pointInsideHull(Point2d p){
        return hullBuilder.isInsideHull(p);
    }


    public static void main(String[] args){

        final String input_filename = (args.length > 0)? args[0]: null;

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    HullViewer225 window;
                    if (input_filename != null)
                        window = new HullViewer225(input_filename);
                    else
                        window = new HullViewer225();
                    window.viewerWindow.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


}