package dynamikColorTiles;


import javafx.scene.paint.*;

import java.util.*;


public class ColorSpace
{

    List<Color> colorList = new ArrayList<>();
    List<Integer> powersOfTwo = Arrays.asList(1,2,4,8,16,32,64,128,256);

    public ColorSpace(int colorCount, ColorType ct)
    {

        if(ct.equals(ColorType.arbitrary))randomColorInitiation(colorCount);
        if(ct.equals(ColorType.grey))greyColorInitiation(colorCount);

    }

    private void randomColorInitiation(int colorCount)
    {

        for(int n=0;n<colorCount;n++)
        {
            int r = (int)(Math.random()*256);
            int g = (int)(Math.random()*256);
            int b = (int)(Math.random()*256);

            colorList.add(Color.rgb(r,g,b));
        }
    }

    private void greyColorInitiation(int colorCount)
    {

        for(int n=0;n<colorCount;n++)
        {
            int r = 256-(256/(n+1));
            int g = 256-(256/(n+1));
            int b = 256-(256/(n+1));

            colorList.add(Color.rgb(r,g,b));
        }
    }

    public Color getColorByNr(int x)
    {
        return colorList.get(x);
    }

    public int getNrOfColor(Color c)
    {

        for(int n=0;n<colorList.size();n++)
        {
            if(c.equals(colorList.get(n)))return n;
        }

        throw new IllegalArgumentException("Color ain't in Color Space");
    }

    public int getColorCount()
    {
        return colorList.size();
    }

    public List<Color> getColorList(){return colorList;}

    public enum ColorType
    {

        arbitrary,
        grey,
        rainbow;
    }
}
