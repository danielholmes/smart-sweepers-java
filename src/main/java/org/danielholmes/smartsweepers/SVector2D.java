package org.danielholmes.smartsweepers;

public class SVector2D {
    public double x;
    public double y;

    public SVector2D() {
        this(0.0, 0.0);
    }

    public SVector2D(double a, double b) {
        x = a;
        y = b;
    }

    //overload the * operator
    public SVector2D times(double rhs)
    {
        return new SVector2D(x * rhs, y * rhs);
    }

    public SVector2D plus(SVector2D rhs)
    {
        return new SVector2D(x + rhs.x, y + rhs.y);
    }

    public SVector2D minus(double rhs)
    {
        return new SVector2D(x - rhs, y - rhs);
    }

    public SVector2D minus(SVector2D rhs)
    {
        return new SVector2D(x - rhs.x, y - rhs.y);
    }

    //	returns the length of a 2D vector
    public double Vec2DLength()
    {
        return Math.sqrt(x * x + y * y);
    }

    //	normalizes a 2D Vector
    public void Vec2DNormalize()
    {
        double vector_length = Vec2DLength();

        x = x / vector_length;
        y = y / vector_length;
    }

    //	calculates the dot product
    public double Vec2DDot(SVector2D other)
    {
        return x * other.x + y * other.y;
    }

    //  returns positive if v2 is clockwise of v1, minus if anticlockwise
    public int Vec2DSign(SVector2D other)
    {
        if (y*other.x > x*other.y)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}
