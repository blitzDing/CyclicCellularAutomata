package dynamikColorTiles;

import java.util.*;

public class Tile
{

    private int idNr;

    private final int x, y;

    public Tile(int idNr, int x, int y)
    {

        this.idNr = idNr;
        this.x = x;
        this.y = y;
    }

    public int getIdNr(){return idNr;}

    public void setIdNr(int idNr){this.idNr = idNr;}

    public int getX(){return x;}

    public int getY(){return y;}

    public int hashCode()
    {
        return Objects.hash(idNr,x,y);
    }

    public boolean equals(Object obj)
    {

        if (this == obj)return true;
        if (obj == null)return false;
        if (getClass() != obj.getClass())return false;

        final Tile other = (Tile) obj;

        if(other.getX()!=this.getX())return false;
        if(other.getY()!=this.getY())return false;
        if(other.getIdNr()!=this.getIdNr())return false;

        return true;
    }

    public Tile clone()
    {
        return new Tile(idNr,x,y);
    }
}
