//This class is for translating assembly code to binary code for the virtual machine
//Written by Alexander Powers
//CS498 Spring 2022
package bunny.asm;

import java.util.*;

import bunny.Lib;

//import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;

public class ASMCompiler {
	// This class will assemble assembly code into byte commands for the BunnyVM
	public enum Instruction {
		C_INSTRUCTION, A_INSTRUCTION, AL_INSTRUCTION,
		L_INSTRUCTION, J_INSTRUCTION, 
		S_INSTRUCTION, UNKNOWN
	}
	/*C instruction:
		dest = comp
	A_Instruction:
		@43 (numbers
	AL Instruction
		@LABEL
	L Instruction
		(LABEL)
	J Instruction
		JMP
	S INSTRUCTION
		STOP
	*/
	/*public static void main(String[] args) {
		//Command-line running of the class purpose
		if (args.length != 2) {
			System.out.println("Usage: <source-asm> <destination-mch>");
			return;
		}
		Byte[] output = assemble(args[0], args[1]);
		
		
	}*/

	public static Byte[] assemble(String filein, String fileout) {  //TODO make it write to the .mch file, if null, dont write
		// This function will assemble code from filein, translate it to byte code, and then return and output it ot fileout

		//Read file into list of instructions, removing whitespace
		ASMParser parsey = new ASMParser(filein);
		//System.out.println(parsey.current);

		//Do symbol read of assembly
		LabelTable labtab = read_labels(parsey);
		parsey.reset();

		//Translate into binary and handle labels
		Byte[] output = translate(parsey, labtab);
		//Output
		
		//for (int i= 0; i<output.length; i++) {
		//	System.out.println("PC" + i + ": " + Lib.byteformat(output[i]));
		//}
		
		//Write the data to file

		Path p = FileSystems.getDefault().getPath(fileout);
		byte[] writable = new byte[output.length];
		for (int i = 0; i< output.length; i++) {
	    	writable[i] = output[i];
		}
		
		try {
			Files.write(p, writable);
		} catch (IOException e) {
			System.out.println("ERROR WRITING");
			System.out.println(writable[0]);
		}
		
		return output;
	}
	
	/*public static Byte[] assemble_from_array(String[] arr) {
		
	}*/
	
	private static LabelTable read_labels(ASMParser parsey) {  //TODO: BUG!! incorrectly finds the byte location of a label, 
		//How to fix this
		//1. first make my own array with every asm statement, labels pointed to the point in THAT array
		//2. go through and determine where the address for the label would be
		//3. assign that label that address
		// This function will create a lable table of any labels used in the assembly code, marking it's point in the assembly code
		System.out.println("READING LABELS");
		
		LabelTable labtab = new LabelTable();
		// Needed offset for any default symbols
		parsey.knownmemsymbols = labtab.getLabelOffset();
		
		int pos = 0; // byte position in output rom
		//First pass, find location in clean ASM
		boolean still_more = true;
		while (still_more) {
			
			//System.out.println(parsey.current);

			Instruction type = parsey.instructionType();
			if (type == Instruction.L_INSTRUCTION) {
				// If the instruction is a label, add to our table, and update known synbols 
				String label = parsey.label();
				labtab.addEntryAsm(label, pos);
				pos+=1;
				//parsey.knownsymbols += 1;
			
				//System.out.println("ADD SYMBOL " + label + " AT BYTE " +(pos));
			} else {
				//	Update our predicted position in the output rom
				//pos += parsey.byte_amount(pos,labtab);
				pos += 1;
				//parsey.index += parsey.byte_amount();
			}
			if (parsey.has_more_lines()) {
				parsey.advance();
			} else {
				still_more = false;
			}
			
		}
		parsey.reset();
		
		//Maybe move this to the translate step, so that im not trying to guess where a label should point trying to keep a count
		//Second pass, find location in RAM
		pos = 0;
		int bytecount = 0;
		still_more = true;
		while(still_more ) {
			//if we know there is a label here, add it to our Mch table
			if (labtab.EntryExistsAsm(pos)) {
				labtab.addEntryMch(parsey.label(), bytecount);
				System.out.println(pos + ": "+ parsey.current_line + " ADDING LABEL " + parsey.label() + " AT BYTE " + bytecount);
			}
			//otherwise, increase our bytecount and pos
			boolean isROM = true; //flag set if we are accessing an existing at below 255 byte
			if (labtab.containsMch(parsey.at())) {
				System.out.println("LABTAB HAS " + parsey.at());
				if (labtab.GetAddressMch(parsey.at()) <= 255) {
					isROM = false;
					System.out.println("setting flag to count it as 2 bytes");
				}
			}
			if (bytecount <= 255) {
				isROM = false;
				System.out.println("setting flag to count it as 2 bytes");
			}
			bytecount += parsey.byte_amount(pos, bytecount, isROM);
			pos +=1;
			
			
			if (parsey.has_more_lines()) {
				parsey.advance();
			} else {
				still_more = false;
			}
			
		}
			
			
		return labtab;
	}
	
	private static Byte[] translate (ASMParser parsey, LabelTable labtab) {
		// Do the actual translation of every instruction in the Parser to bytecode
		List<Byte> lines = new ArrayList<Byte>(); // fill up lines with binary
		
		System.out.println("TRANSLATING");
		
		//int count = 0; // Counter for debugging
		
		boolean still_more = true;
		while (still_more) {
		
			//System.out.println("INSTRUCTION" + count);
			
			switch (parsey.instructionType()) { //TODO update with new HL commands
			case C_INSTRUCTION:
				// handle comp instructions
				lines.addAll(ASMCode.translate_c(parsey.comp(), parsey.dest()));
				break;
			case A_INSTRUCTION:
				// Handle At instructions no symbol
				int avalue = Integer.valueOf(parsey.at());
				lines.addAll(ASMCode.translate_a(avalue));
				break;
			case AL_INSTRUCTION:
				int lvalue = handle_al_instruction(parsey, labtab);
				lines.addAll(ASMCode.translate_a(lvalue));
				break;
			case L_INSTRUCTION:
				break; //Do nothing, should be already handled my labletable
			case J_INSTRUCTION:
				// Handle Jump instructions
				lines.add(ASMCode.translate_j(parsey.jump()));
				break;
			case S_INSTRUCTION:
				// Handle special instructions
				lines.addAll(ASMCode.translate_s(parsey.special()));
				break;
			case UNKNOWN:
				System.out.println("ERROR: UNKNOWN COMMAND" + parsey.current_line);
				break;
			}
			if (parsey.has_more_lines()) {
				//count++;
				
				parsey.advance(); // Next instruction
			}
			else {
				still_more = false;
			}
		}
		
		Byte[] output = lines.toArray(new Byte[0]);
		
		return output;
	}
	
	private static int handle_al_instruction(ASMParser parsey, LabelTable labtab) {
		// Handle At instructions symbol
		String at = parsey.at();
		// See if we have already seen this label before, if not, add it to our table
		int lvalue;
		if (labtab.containsMch(at)) {
			lvalue = labtab.GetAddressMch(at);
			System.out.println("getting location for "+ at + " its byte " + lvalue);
		} else {
			lvalue = parsey.knownmemsymbols;
			labtab.addEntryMch(at, parsey.knownmemsymbols);
			parsey.knownmemsymbols+=1;
		}
		return lvalue;
	}

	public static class ASMCode {
		//This class takes a string ASM and outputs a binary literal
		//private static String[] ops = {"Y", "!X", "-X", "X+1", "X-1", "X+Y", "X-Y",
		//						"X&Y", "X|Y"};
		
		//private static String[] vars = {"AB", "BA", "AM", "MA"};
		
		//private static String[] dests = {"A", "B", "M", "L", "H", "LZ", "AB"};
		
		private static String[] jumps = {"JMP", "JGR", "JEQ", "JGE", "JLE", "JNE", "JLT"};
		
		//private static String[] specials = {"NOP", "STOP", "RESET", "BIGLOAD", 
		//						"HLILOAD", "HLMLOAD"};

		public static List<Byte> translate_c(String comp, String dest) {
			// function to translate comp and dest strings into one byte instruction
			List <Byte> output = new ArrayList<Byte>();
			int err = 0;

			int varcode = -1;

			//Test if immediate load
			boolean load = false; //flag for if immediate load statement
			if (Lib.isNumeric(comp)) {
				load = true;
			
			} else {
				//Get Varcode
				varcode = get_varcode(comp,0);
			}
			if (varcode == -1) {
				err = 1;
			}

			//Figure out which operator comp is using (2nd octal)
			int compcode = get_compcode(load, comp);

			if (compcode < 0) {
				err = compcode;
			}

			//Figure out dest (3rd octal)
			int destcode = get_destcode(dest);
			
			// Sanity checks
			// Main purpose is to make sure that the duplicate operations (A+B and B+A) are not encoded as such according to the
			// comp algorithm because those values are reserved for other things
			if (varcode == 1 && compcode == 5) {
				varcode = 0;
			} else if (varcode == 2 && compcode == 5) {
				varcode = 3;
			}
			
			// if loading force the varcode
			if (load) {
				varcode = 2;
			}
			
			//piece the 3 octals into one instruction (2 if immediate load)
			byte result = Lib.triOctToByte(varcode, compcode, destcode);
			output.add(result);
			if (load) {
				int temp = Integer.valueOf(comp);
				output.add((byte)temp);
			}
			
			if (err != 0) {
				System.out.println("whoops an error");
			}

			return output;
		}
		
		private static int get_varcode(String comp, int index) {
			//Figure out which set of vars is in the comp (first 1st octal)
			int varcode = -1;
			//This is not an immediate load, so now we figure out what vars are used
			if(comp.charAt(index) == 'A') {
				if (comp.contains("M")) {
					return 2; //AM (immediate possibility handled above)
				}
				return 0; //AB
			}
			if (comp.charAt(index) == 'B') {
				return 1; //BA
			} 
			if (comp.charAt(index) == 'M') {
				return 3; //MA
			}
			if (varcode ==-1) { // The statement must be using a before operator (-A or !A) go one deeper
				return get_varcode(comp, index+1);
			}			
			// Cant find vars, default to 1 and raise err
			//TODO actually raise error
			System.out.println("ERROR FINDING VARS IN COMP STATEMENT");
			return -1;
		}

		private static int get_compcode(boolean load, String comp) {
			// if this is a load, compcode is auto set to 0, no change needed
			// Same for if there is just one char, it is register to register assignment
			if (load || comp.length() == 1) {
				return 0;
			}

			if (comp.length() == 2) { //Flip and negation
				if (comp.charAt(0) == '!') {
					return 1; // Flip
				}
				if ( comp.charAt(0) == '-') {
					return 2; // Negate
				}
				return -2;
			}
			if (comp.length() == 3) {
				int offset = 0;
				if (!(comp.charAt(2) == '1')) {
					offset = 2; //Increment/Add modifier offset
				}
				if (comp.charAt(1) == '+') {
					return 3 + offset; // Increment/Add both vars
				}
				if (comp.charAt(1) == '-') {
					return 4 + offset; // Decrement/Subtract both vars
				}
				if (comp.charAt(1) == '|' || comp.charAt(1) == '&') {
					return 7; // Bitwise OR and AND
				}
				return -3;
			}
			return -1;
			//TODO: NO CASE NEEDED FOR SINGLE REGISTER ASSIGNMENT RIGHT I THINK? TEST
		}

		private static int get_destcode(String dest) {
			//Figure out dest (3rd octal)
			switch(dest) {
			case "A":
				return 0;
			case "B":
				return 1;
			case "M":
				return 2;
			case "L":
				return 3;
			case "H":
				return 4;
			case "LZ":
				return 5;
			case "AB":
			case "BA":
				return 6;
			}
			return -4;
		}

		public static List<Byte> translate_a(int value) {
			List <Byte> output = new ArrayList<Byte>();
			//Load H and L
			// value > 255
			//	HLILOAD
			//	VALUE
			//	VALUE
			//value <=255
			//  LZ=immediate
			//	value
			if (value < 255) {
				output.add((byte) 0b10000101); //LZ = immediate
				output.add((byte) value);
			} else {
				output.add((byte) 0b00001111); //HLILOAD
				output.add((byte) (value/256));
				output.add((byte) (value));
			}
			return output;
				
		}

		public static Byte translate_j(String jump) {
			byte base = 0b01101000;
			for (int i = 0; i < jumps.length; i++) {
				if (jump.equals(jumps[i])) {
					base+=i;
					break;
				}
			}
			return base;
		}

		public static List<Byte> translate_s(String special) {
			List <Byte> output = new ArrayList<Byte>();
			if (special.equals("NOP")) {
				output.add((byte) 0b00000000); // 0 0 0
			} else if (special.equals("STOP")) {
				output.add((byte) 0b00111111); // 0 7 7
			} else if (special.equals("RESET")) {
				output.add((byte) 0b00110111); // 0 6 7
			} else if (special.equals("HLMLOAD")) {
				output.add((byte) 0b00000111);
			} else if (special.contains("HLILOAD")) {
				int firstcomm = special.indexOf(',');
				int secondcomm = special.indexOf(',', firstcomm+1); 
				int firstval = Integer.valueOf(special.substring(firstcomm+1, secondcomm-1));
				int secondval = Integer.valueOf(special.substring(secondcomm+1));
				output.add((byte) 0b00000111);
				output.add((byte) firstval);
				output.add((byte) secondval);
			} else if (special.equals("HLINC")) {
				output.add((byte) 0b00010111);
			} else if (special.equals("HLDEC")) {
				output.add((byte) 0b00011111);
			} else if (special.equals("HL=AB")) {
				output.add((byte) 0b00100111);
			} else if (special.equals("AB=HL")) {
				output.add((byte) 0b00101111); // 0 
			} else if (special.equals("OVERA")) {
				output.add((byte) 0b01101111); // 1 5 7
			}  else if (special.contains("BIGLOAD")) {
				output.addAll(handle_bigload(special));
			}
			return output;
		}
		private static List<Byte> handle_bigload(String special) {
			int commbefore = special.indexOf(',');
			int commafter = special.indexOf(',', commbefore+1);
			int amount = Integer.valueOf(special.substring(commbefore+1, commafter));
			List <Byte> output = new ArrayList<Byte>();
			output.add((byte) (0b10001000 + (amount-1)));
			for (int i = 0; i < amount; i++) {
				commbefore = commafter;
				commafter = special.indexOf(',', commbefore+1);
				int val = 0;
				if (i != (amount -1)) {
					val = Integer.valueOf(special.substring(commbefore+1, commafter));
				} else {
					val = Integer.valueOf(special.substring(commbefore+1));
				}
				System.out.println(val);
				output.add((byte) val);
				
			}
			return output;
		}
	}
}

