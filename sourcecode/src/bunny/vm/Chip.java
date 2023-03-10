//This class was used as a way to try and extend the virtual machine, did not pan out to be very useful
//Written by Alexander Powers
//CS498 Spring 2022
package bunny.vm;

public abstract class Chip {
	protected int allostart, alloend;
	public Chip(int newallostart, int newalloend) {
		allostart = newallostart;
		alloend = newalloend;
	}
	
	public abstract void run(Byte[] RAM);
}
