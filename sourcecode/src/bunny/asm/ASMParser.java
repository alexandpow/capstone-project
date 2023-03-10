//This class is used to hold the list of assembly commands to be translated, and contain functions to assist in that process
//Written by Alexander Powers
//CS498 Spring 2022
package bunny.asm;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import bunny.Lib;
import bunny.asm.ASMCompiler.Instruction;

public class ASMParser {
	//private String filename;
	private int index;
	private String[] lines;
	public String current_line;
	public int knownmemsymbols = 0;

	public ASMParser(String filename) {
		try_load_file(filename);
		reset();
	}

	public void reset() {
		index = 0;
		current_line = lines[index];
		//knownsymbols = 0;
	}

	public boolean has_more_lines() {
		//System.out.println((lines.length-1) - index);
		if (current_line == null) {
			return false;
		}
		return (index < (lines.length-1));
	}	

	public int total_lines() {
		return lines.length;
	}
	
	public void advance() {
		index++;
		current_line = lines[index];
	}
	
	public Instruction instructionType() {
		//System.out.println(current);
		//Test if special
		if (!special().equals("")) {
			return Instruction.S_INSTRUCTION;
		}
		//Test if comp
		if (current_line.indexOf('=') > -1) {
			return Instruction.C_INSTRUCTION;
		}
		//Test if @
		if (current_line.indexOf('@') > -1) {
			if (Character.isDigit(current_line.charAt(1))){
				return Instruction.A_INSTRUCTION;
			}
			return Instruction.AL_INSTRUCTION;
		}
		//Test if label
		if (current_line.indexOf('(') > -1) {
			return Instruction.L_INSTRUCTION;
		}
		//Test if jump
		if (current_line.indexOf('J') == 0) {
			return Instruction.J_INSTRUCTION;
		}
		return Instruction.UNKNOWN;
	}
	
	public String comp() {
		int pos = current_line.indexOf('=');
		return current_line.substring(pos+1);
	}
	
	public String dest() {
		int pos = current_line.indexOf('=');
		return current_line.substring(0, pos);
	}
	
	public String at() {
		return current_line.substring(1);
	}
	
	public String label() {
		return current_line.substring(1, current_line.length()-1);
	}
	
	public String jump() {
		return current_line;
	}
	
	public String special() {
		//Function to ensure that the instruction is a valid special instruction
		if (current_line.equals("NOP") || current_line.equals("STOP") || current_line.equals("RESET") || current_line.contains("BIGLOAD") || current_line.equals("HLILOAD") || 
				current_line.equals("HLMLOAD") || current_line.equals("HLINC") || current_line.equals("HLDEC") || current_line.equals("HL=AB") || current_line.equals("AB=HL") ||
				current_line.equals("OVERA")) {
			return current_line;
		}
		return "";
	}
	
	public int byte_amount(int pos,int bytecount, boolean isROM) {
		//return amount of bytes this command will take up
		Instruction type = instructionType();
		if ((type == Instruction.C_INSTRUCTION) && (Lib.isNumeric(comp()))){
			//Assumed immediate load
			return 2;
		}
		if ((type == Instruction.A_INSTRUCTION) ||  (type == Instruction.A_INSTRUCTION)) { 
			if ((Integer.valueOf(at()) <= 255) || (knownmemsymbols <= 255 && !isROM)) { 
				//small load (A or A label)
				return 2;
			}
			else {
				//Otherwise, its a big load A or A label
				return 3;
			}
		}
		if (type == Instruction.S_INSTRUCTION) {
			if (special().equals("HLILOAD")) {
				//HLILOAD
				return 3;
			} else if (special().contains("BIGLOAD")) {
				return Integer.valueOf(special().charAt(7));
			}
		}
		if (type == Instruction.L_INSTRUCTION) {
			//labels have no bytes
			return 0;
		}
		//If nothing strikes us, its just a one byte
		return 1;
	}
	
	private void try_load_file(String filename) {
		Path p = FileSystems.getDefault().getPath(filename);
		List<String> input = new ArrayList<String>();
		List<String> output = new ArrayList<String>();
		try {
			input = Files.readAllLines(p);
		} catch (IOException e) {
			System.out.println("Unable to open " + filename);
		}
		//int index = 0;
		for (int i = 0; i < input.size(); i++) {
			output.add(trim_line(input.get(i)));
		}
		lines = output.toArray( new String[0]);
		//System.out.println(lines.length);
	}
	
	private String trim_line(String line) {
		int pos;
		String temp = line;
		//Remove all whitespace
		temp = temp.trim();
		//Remove anything after a //
		pos = temp.indexOf("//");
		if (pos!=-1) {
			temp = temp.substring(0, pos);
		}
		//trim any whitespace we missed
		temp = temp.replace(" ", "");
		//any blank newlines
		
		//And we are done
		if (pos == 0 || temp.isEmpty()) {
			return "ERROR";			
		}
		return temp;
	}
}
