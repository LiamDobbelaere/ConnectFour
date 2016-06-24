import java.io.Serializable;

public class DropEvent implements Serializable
{
    private int x;
    private int y;

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public String getType()
    {
        return type;
    }

    private String type;

    public DropEvent(int x, int y, String type)
    {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
