package com.joebges._2dgame;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.joebges._2dgame.gfx.Colours;
import com.joebges._2dgame.gfx.Screen;
import com.joebges._2dgame.gfx.SpriteSheet;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 3;
	public static final String NAME = "Game";
	public boolean running = false;
	public int tickCount = 0;

	private JFrame frame; // Container
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
			BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData(); // How many pixels inside the image
	private int[] colours = new int[6 * 6 * 6]; 								// 6 shades for 3 colours (RGB)

	private Screen screen;
	public InputHandler input;

	public Game() {
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE)); 		// Größe für das frame/canvas
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

		frame = new JFrame(NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Bei schließen = exit ausführen
		frame.setLayout(new BorderLayout());

		frame.add(this, BorderLayout.CENTER); // Canvas in der Mitte des Layouts anzeigen
		frame.pack();

		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void init() {
		int index = 0;
		for (int r = 0; r < 6; r++) {

			for (int g = 0; g < 6; g++) {

				for (int b = 0; b < 6; b++) {
					int rr = (r * 255/5);			//255 will be transparent
					int gg = (g * 255/5);			//shades of colours
					int bb = (b * 255/5);
					
					colours[index++] = rr << 16 | gg << 8 | bb;
				}
			}
		}
		
		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet_original.png"));
		input = new InputHandler(this);
	}

	public synchronized void start() {
		running = true;
		new Thread(this).start();
	}

	public synchronized void stop() {
		running = false;
	}

	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 60D; // 1 tick = 1/60 sec.

		int ticks = 0; // how many updates (logic) have been done
		int frames = 0; // how many frames (graphics == logic to screen)
		long lastTimer = System.currentTimeMillis();
		double delta = 0; // how many unprocessed nanoseconds have passed by

		init();

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;

			while (delta >= 1) { // max. delta = 59
				ticks++;
				tick();
				delta -= 1; // while-loop finishes with delta = 0
				shouldRender = true;
			}

			try { // small pause to not overload the system
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (shouldRender) {
				frames++;
				render();
			}

			if (System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
				System.out.println(ticks + " ticks . " + frames + " frames");
				frames = 0; // reset
				ticks = 0;
			}
		}
	}

	public void tick() { // update the game
		tickCount++;
		if (input.up.isPressed()) {
			screen.yOffset--;
		}
		if (input.down.isPressed()) {
			screen.yOffset++;
		}
		if (input.left.isPressed()) {
			screen.xOffset--;
		}
		if (input.right.isPressed()) {
			screen.xOffset++;
		}
	}

	public void render() { // print out what was updated in tick
		BufferStrategy bs = getBufferStrategy(); // organize data on the image
		if (bs == null) {
			createBufferStrategy(3); // triple buffering, higher numbers =
										// better image quality
			return;
		}

		for(int y = 0; y < 32; y++){			//32 tiles
			for(int x = 0; x < 32; x++){
				boolean flipX = x%2 == 1;
				boolean flipY = y%2 == 1;
				screen.render(x<<3, y<<3, 0, Colours.get(555, 505, 055, 550), flipX, flipY);			//shift by 8 for 8 pixels, mirroring
			}
		}
		
		for(int y = 0; y < screen.height; y++){			//32 tiles
			for(int x = 0; x < screen.width; x++){
				int colourCode = screen.pixels[x + y * screen.width];
				if(colourCode < 255) pixels[x+y*WIDTH] = colours[colourCode];
			}
		}
		
		Graphics g = bs.getDrawGraphics();

		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

		g.dispose(); 				// Free up memory&resources that image is using
		bs.show(); 					// show & display content of buffer
	}

	public static void main(String[] args) {
		new Game().start();
	}

}
