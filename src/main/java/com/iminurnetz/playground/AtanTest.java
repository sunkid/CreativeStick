/**
 * LICENSING
 * 
 * This software is copyright by sunkid <sunkid@iminurnetz.com> and is
 * distributed under a dual license:
 * 
 * Non-Commercial Use:
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Commercial Use:
 *    Please contact sunkid@iminurnetz.com
 */
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
