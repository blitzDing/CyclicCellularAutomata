package diffGeo;


import javafx.scene.canvas.Canvas;

import javafx.util.Pair;


public class AxisCanvasSupplier
{

    private final Canvas canvas;

    private final double width;
    private final double height;
    private final Pair<Double, Double> center;

    public AxisCanvasSupplier(double width, double height)
    {

        canvas = new Canvas(width, height);
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();

        center = new Pair<>(this.width/2, this.height/2);
        System.out.println("W: "+this.width+", " + width);
        System.out.println("H: "+this.height+", " + height);
    }

    public double getWidth()
    { return width; }

    public double getHeight()
    { return height; }

    public Canvas getCanvas()
    { return canvas; }

    public Pair<Double, Double> getCenterInCanvasCoordinates()
    { return center; }

    public void dropPoint(double size, double x, double y)
    {
        canvas.getGraphicsContext2D().fillOval(x-(size/2),y-(size/2), size, size);
    }

    public Pair<Double, Double> getCenteredCoordinates(double x, double y)
    {

        double cx = center.getKey();
        double cy = center.getValue();

        return new Pair<Double, Double>(x-cx, cy-y);
    }

    public Pair<Double, Double> getCanvasCoordinates(double x, double y)
    {

        double cx = center.getKey();
        double cy = center.getValue();

        return new Pair<Double, Double>(x+cx,cy-y);
    }
}