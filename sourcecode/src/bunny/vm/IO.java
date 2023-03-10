//This class is an example of usage for a "chip" in the virtual machine, this was not expanded upon as I worked more on the project
//Written by Alexander Powers
//CS498 Spring 2022
package bunny.vm;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Canvas;

//1 byte for input
//8192 bytes needed for each bit to = one pixel

//DFFE, 57,342 for input
//DFFF, 57,343
//FFFF, 65535

//TODO probably optimized like not very good

public class IO extends Chip {
	private JFrame f;
	private Display d;
	public IO(int allostart, int alloend) {
		super(allostart, alloend);
		
		f= new JFrame();
		d = new Display();
		
		f.setSize(256, 256);
		f.add(d);
		f.setVisible(true);

	}
	
	public void run (Byte[] RAM) {
		//Take memory values
		//Do graphics stuff with them
		System.out.println("chip run");
		d.update_data(RAM);
		f.add(d);
	}
	
	private class Display extends Canvas {
		private Byte[][] data = new Byte[256][32];
		
		/*public void Display() {
			data = new Byte[256][32];
			d.update_data(new Byte[8192]);
		}*/
		public void paint(Graphics g) {
			for(int i=0; i<256;i++) {
				for (int j=0; j<32;j++) {
					int length = 0;
					if (data[i][j] != null) {
						length = Integer.bitCount(Byte.toUnsignedInt(data[i][j]));
						//System.out.println(i+" " +j+" "+length);
						g.drawLine(j*8, i, (j*8)+length, i);
				    }
				}
			}
		}
		
		public void update_data(Byte[] RAM) {
			int index = allostart+1;
			for (int i=0; i<256; i++) {
				for (int j=0; j<32; j++) {
					data[i][j] = RAM[index+j+(i*32)];
					if (data[i][j] != null) {
						System.out.println("FOUND ONE");
					}
				}
			}
		}
	}
}
