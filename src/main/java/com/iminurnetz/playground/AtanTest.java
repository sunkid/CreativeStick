package com.iminurnetz.playground;

public class AtanTest {
	public static void value(float x, float y) {
		int direction = (int) Math.floor(Math.atan2(y, x)*2/Math.PI + .5);
		try {
			System.out.print("x=" + x + " y=" + y + " atan2(y,x)=" + direction);
		} catch (Exception e) {
			System.out.println("x=" + x + " y=" + y + " atan(y/x)=error"
					+ "    atan2(y,x)=" + Math.PI/Math.atan2(y, x));
		}
		String dir = "";
		switch(direction) {
		case 0:
			dir = "East";
			break;
		case 1:
			dir = "North";
			break;
		case 2:
			dir = "West";
			break;
		case -1:
			dir = "South";
			break;
		}
		
		System.out.println(" pointing " + dir);
	} // Main method

	public static void main(String[] args) {
		value(1, 0);
		value(1, 0.9f);
		value(1, 1);
		value(0, 1);
		value(-1, 1);
		value(-1, 0);
		value(-1, -1);
		value(0, -1);
		value(1, -1);
	}
}
