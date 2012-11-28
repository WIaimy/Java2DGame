package com.joebges._2dgame.gfx;

public class Screen {

	public static final int MAP_WIDTH = 64;
	public static final int MAP_WIDTH_MASK = MAP_WIDTH-1;
	
	public int[] pixels;
	
	public int xOffset = 0;										//need to render the screen
	public int yOffset = 0;
	
	public int width;
	public int height;
	
	public SpriteSheet sheet;
	
	public Screen(int width, int height, SpriteSheet sheet){
		this.width = width;
		this.height = height;
		this.sheet = sheet;
		
		pixels = new int[width*height];
	}

	public void render(int xPos, int yPos, int tile, int colour){
		xPos -= xOffset;
		yPos -= yOffset;
		
		int xTile = tile % 32;		//Position on x-achse
		int yTile = tile / 32;		//Position on y-achse
		int tileOffset = (xTile << 3) + (yTile << 3) * sheet.width;				//2^3 = 8, each Tile is 8 pixels
		
		for(int y = 0; y < 8; y++){
			int ySheet = y;
			if(y+yPos < 0 || y + yPos >= height) continue;
			for(int x = 0; x < 8; x++){
				int xSheet = x;
				if(x + xPos < 0 || x + xPos >= width) continue;
				int col = (colour >> (sheet.pixels[xSheet + ySheet *sheet.width + tileOffset]*8)) & 255;	//colour data on the tile we are on ; verify that it is between 0 and 255 (256)
				if(col < 255){
					pixels[(x+xPos) + (y+yPos) *width] = col;
				}
			}
		}
	}
	
}