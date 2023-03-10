//This class is used mainly for help in decoding bytecode, I wanted to expand on this so that it could do operations, but that was getting tricky to put together
//Written by Alexander Powers
//CS498 Spring 2022
package bunny;

public class Instruction {
	private String name;
	private byte code;
	private Operation op;
	private int load; //How many bytes this operation loads infront of it (if none, 0)
	Instruction(String newname, byte newcode, Operation newop) {
		name = newname;
		code = newcode;
		op = newop;
		load = 0;
	}

	Instruction(String newname, byte newcode) {
		name = newname;
		code = newcode;
		load = 0;
	}
	
	Instruction(String newname, byte newcode, int newload) {
		name = newname;
		code = newcode;
		load = newload;
	}
	
	public String getName( ) {
		return name;
	}
	
	public byte getCode() {
		return code;
	}
	
	public int getLoad() {
		return load;
	}
	
	public byte doOperation(byte a, byte b) {
		return op.doOperation(a,b);
	}
	
	private interface Operation {
		byte doOperation(byte a, byte b);
	}
}
