package se.miun.dt176g.sika2001.reactive;


/**
 * <h1>Shape</h1> Abstract class which derived classes builds on.
 * <p>
 * This class consists of the attributes common to all geometric shapes.
 * Specific shapes are based on this class.
 */
public abstract class Shape implements Drawable {

	// private member : some container storing coordinates
    private final Point topLeft;
    private final Point bottomRight;
    private final int width;
    private final int height;

    public Shape(Point start, Point end) {
        // make sure start is always the top leftmost point, and end is the bottom rightmost point,
        // in case the shape is drawn from bottom to top, or right to left.
        if (end.x() < start.x()) {
            int x = start.x();
            start = new Point(end.x(), start.y());
            end = new Point(x, end.y()) ;
        }
        if (end.y() < start.y()) {
            int y = start.y();
            start = new Point(start.x(), end.y());
            end = new Point(end.x(), y);
        }

        this.topLeft = start;
        this.bottomRight = end;
        this.width = topLeft.x() - bottomRight.x();
        this.height = topLeft.y() - bottomRight.y();
    }

    public Point topLeft() { return this.topLeft; }
    public Point bottomRight() { return this.bottomRight; }
    public int width() { return this.width; }
    public int height() { return this.height; }
}
