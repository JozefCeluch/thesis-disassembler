//type inference test
public class Sable {
	public static void f(short i) {
		Circle c;
		Rectangle r;
		Drawable d; // 3
		boolean is_fat;
		if (i > 10) {
			r = new Rectangle(i, i);
			is_fat = r.isFat();
			d = r;
		} else {
			c = new Circle(i);
			is_fat = c.isFat();
			d = c;
		}
		if (!is_fat) d.draw();
	}

	public static void main(String args[]) // 19
	{
		f((short) 11);
	}
}