package diffGeo;


import javafx.application.Platform;

import javafx.scene.canvas.Canvas;

import javafx.util.Pair;

import java.util.function.Function;


public class AniThread extends Thread
{

    Function<Double, Double> dgXComponent = (t)->t; //infinity sign (t)-> 2*Math.sin(t);
    Function<Double, Double> dgYComponent = (t)->Math.sin(t); //infinity sign(t)-> Math.sin(2*t);
    Function<Double, Pair<Double, Double>> dgFormula = (t)-> new Pair<>(dgXComponent.apply(t), dgYComponent.apply(t));

    private final AxisCanvasSupplier acs;

    public final double xScaling = 10;
    public final double yScaling = 10;
    public static final double start = -350;
    private static final double end = 150;
    private final double stepSize = 0.01;
    private final int pauseInNanoSeconds = 2;
    private final double lineFatness = 2;

    private double t;

    private final Runnable drawPoint = ()->
    {

        Platform.runLater(()->
        {

            final double z = t;
            /*Why is this here?
            if(!((t-1)<(0.0000001)))*/dropThere(z);
        });
    };

    public AniThread(double width, double height)
    {
        acs = new AxisCanvasSupplier(width, height);
    }

    @Override
    public void run()
    {

        t = start;

        while(t<end)
        {
            new Thread(drawPoint).start();
            t += stepSize;
        }
        try{Thread.sleep(0, pauseInNanoSeconds);}catch(Exception e){e.printStackTrace();}
    }

    private void dropThere(double t)
    {

        Pair<Double, Double> place = dgFormula.apply(t);

        double xd = place.getKey();
        double yd = place.getValue();

        Pair<Double, Double> p = acs.getCanvasCoordinates((xd*xScaling), (yd*yScaling));
        acs.dropPoint(lineFatness, p.getKey(), p.getValue());
    }

    public Canvas getCanvas()
    {
        return acs.getCanvas();
    }
}