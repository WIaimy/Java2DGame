package com.joebges._2dgame.gfx;

public class Colours {

	public static int get(int colour1, int colour2, int colour3, int colour4) {
		return (get(colour4) << 24) + (get(colour3)<<16) + (get(colour2) << 8) + get(colour1);
	}

	private static int get(int colour) {
		if(colour < 0) return 255;
		int r = colour/100%10;				//make sure it's between 0 and 9
		int g = colour/10%10;
		int b = colour%10;
		return r*36 + g*6 + b;
	}
}
