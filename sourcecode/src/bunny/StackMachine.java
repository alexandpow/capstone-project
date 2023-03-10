//This class is for translating the stack language to assembly
//Written by Alexander Powers
//CS498 Spring 2022
package bunny;

import java.io.IOException;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.FileSystems;

public class StackMachine {
	
	private static String[] firsts = {"pop", "push", "add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not", "label", "goto", "if-goto", "function", "call", "return"};
	
	private static String[] pages = {"local", "arg", "this", "that", "thispoint", "thatpoint", "constant", "static", "temp"};
	
	private static enum Template {TEMPLATE_POP, TEMPLATE_PUSH, TEMPLATE_LABEL,TEMPLATE_GOTO,TEMPLATE_IFGOTO, TEMPLATE_FUNC, TEMPLATE_CALL};
	
	public static String[] translate(String input, String output, String asmlibrary) { 
		// This function will return an array of translated ASM strings from stack language, and also save it to output file is that is not null
		// Setup structure
		List<String> asm = new ArrayList<String>();
		
		String current_function = "Global";
		List<String> name_stack = new ArrayList<String>();
		// Load file as an array of strings
		Path p = FileSystems.getDefault().getPath(input);
		try {
			List<String> f = Files.readAllLines(p);
			//first, lets add our bootstrap code
			asm.addAll(create_from_library("boot", asmlibrary, "ERORR"));
			// Loop through every line
			for (int i = 0; i<f.size(); i++) {
				String currline = f.get(i);
				// Read first word 
				int firstspace = currline.indexOf(" ");
				System.out.println(currline);
				String firstword;
				//Single word instruction, dont need to substring
				if (firstspace >0) {
					firstword = currline.substring(0, firstspace);
				} else {
					firstword = currline;
				}
				
				// Do something based on this word
				int matchedfirst = find_match(firstword,firsts);
				switch(matchedfirst) {
				case 0: //pop
					asm.addAll(create_from_template(currline.substring(firstspace+1), Template.TEMPLATE_POP, asmlibrary, current_function + Integer.toString(i)));
					break;
				case 1: //push
					asm.addAll(create_from_template(currline.substring(firstspace+1), Template.TEMPLATE_PUSH, asmlibrary, current_function + Integer.toString(i)));
					break;
				case 16: //return
					//pop old name off the stack
					current_function = name_stack.remove(name_stack.size()-1);
				case 2: //add
				case 3: //sub
				case 4: //neg 
				case 5: //eq 
				case 6: //gt
				case 7: //lt
				case 8: //and 
				case 9: //or
				case 10: //not
					asm.addAll(create_from_library(currline, asmlibrary, current_function + Integer.toString(i)));
					break;
				case 11: //label
					asm.addAll(create_from_template(currline.substring(firstspace+1), Template.TEMPLATE_LABEL, asmlibrary, current_function + Integer.toString(i)));
					break;
				case 12: //goto
					asm.addAll(create_from_template(currline.substring(firstspace+1), Template.TEMPLATE_GOTO, asmlibrary, current_function + Integer.toString(i)));
					break;
				case 13: //if-goto
					asm.addAll(create_from_template(currline.substring(firstspace+1), Template.TEMPLATE_IFGOTO, asmlibrary, current_function + Integer.toString(i)));
					break;
				case 14: //function
					name_stack.add(current_function);
					//get new current function
					current_function = currline.substring(firstspace+1,currline.indexOf(' ',firstspace+1));
					asm.addAll(create_from_template(currline.substring(firstspace+1), Template.TEMPLATE_FUNC, asmlibrary, current_function + Integer.toString(i)));
					break;
				case 15: //call
					asm.addAll(create_from_template(currline.substring(firstspace+1), Template.TEMPLATE_CALL, asmlibrary, current_function + Integer.toString(i)));
					break;
				}
			}
			Path op = FileSystems.getDefault().getPath(output);
			Files.write(op, asm, StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println("Error opening file");
		}
		// Return result
		String[] result = asm.toArray(new String[0]);
		
		
		return result;
	}
	
	private static int find_match(String input, String[] arr) {
		for (int i = 0; i< arr.length; i++) {
			if (input.compareToIgnoreCase(arr[i])==0) {
				return i;
			}
		}
		return -1;
	}
	private static List<String> create_from_template(String subline, Template t, String asmlibrary,String unique) { //TODO finish vm template implementation
		List<String> output = new ArrayList<String>();
		switch (t) {
		case TEMPLATE_PUSH:
			output.addAll(complete_push(subline,asmlibrary));
			uniqueitizer(output, unique);
			break;
		case TEMPLATE_POP:
			output.addAll(complete_pop(subline, asmlibrary));
			uniqueitizer(output, unique);
			break;
		case TEMPLATE_LABEL:
			output.addAll(complete_flow("label", subline, asmlibrary));
			break;
		case TEMPLATE_GOTO:
			output.addAll(complete_flow("goto", subline, asmlibrary));
			break;
		case TEMPLATE_IFGOTO:
			output.addAll(complete_flow("if-goto", subline, asmlibrary));
			break;
		case TEMPLATE_FUNC:
			output.addAll(complete_func(subline, asmlibrary, unique));
			break;
		case TEMPLATE_CALL:
			output.addAll(complete_call(subline, asmlibrary, unique));
			break;
		}
		//Unique-itize labels
		
		
		return output;
	}
	private static List<String> create_from_library(String line, String asmlibrary, String unique) {
		// Uses a saved asm file to be a placeholder for vm->asm, where putting this code in is universal
		//List<String> output = new ArrayList<String>();
		Path p = FileSystems.getDefault().getPath(asmlibrary, line+".asm");
		List<String> output = new ArrayList<String>();
		try {
			output = Files.readAllLines(p);
			uniqueitizer(output,unique);
			return output;
		} catch (IOException e){
			System.out.println("Error creating from library");
		}
		return null;
	}
	
	private static List<String> complete_pop(String subline, String asmlibrary) {
		List<String> output = new ArrayList<String>();
		Path p = FileSystems.getDefault().getPath(asmlibrary, "pop.asmT");
		try {
			output =Files.readAllLines(p);
			
			String page_replace = "!!ERROR!!";
			//String offset_replace = "!!ERROR!!";
			boolean pointer = false;
			//find our symbols to replace, for pop it is PAGE and OFFSET
			int spacepos = subline.indexOf(' ');
			String page = subline.substring(0,spacepos);
			String offset = subline.substring(spacepos+1);
			//Lets start with that PAGE
			System.out.println(subline);
			System.out.println(page);
			System.out.println(offset);
			//Now, lets get the page label we are going to use
			page_replace = get_symbol_for_page(page);
			if (find_match(page,pages)<4) {
				pointer = true;
			}
			handleConditional(pointer, "{POINTER}", output);
		
			replacePhraseWithString(output, "{--PAGE--}", page_replace);
			
			replacePhraseWithString(output, "{--OFFSET--}", offset);
		} catch (IOException e) {
			System.out.println("Error opening file for template use");
		}
		
		return output;
	}

	private static List<String> complete_push(String subline, String asmlibrary) {
		List<String> output = new ArrayList<String>();
		Path p = FileSystems.getDefault().getPath(asmlibrary, "push.asmT");
		try {
			output =Files.readAllLines(p);
			String page_replace = "!!ERROR!!";
			//String offset_replace = "!!ERROR!!";
			boolean notconstant = true;
			//find our symbols to replace, for pop it is PAGE and OFFSET
			int spacepos = subline.indexOf(' ');
			String page = subline.substring(0,spacepos);
			String offset = subline.substring(spacepos+1);
			//Lets start with that PAGE
			System.out.println(subline);
			System.out.println(page);
			System.out.println(offset);
			page_replace = get_symbol_for_page(page);
			//Special handling for if constant
			if (page_replace.equals("!CONSTANT")) {
				notconstant = false;
			}
			handleConditional(notconstant,"{NOTCONSTANT}", output);
	
			replacePhraseWithString(output, "{--PAGE--}", page_replace);
			
			replacePhraseWithString(output, "{--OFFSET--}", offset);
			
			replacePhraseWithString(output, "{--CONSTANT--}", offset);
		} catch (IOException e) {
			System.out.println("Error opening file for template use");
		}
	
		return output;
	}

	private static List<String> complete_flow(String filename, String label, String asmlibrary) {
		List<String> output = new ArrayList<String>();
		Path p = FileSystems.getDefault().getPath(asmlibrary, filename + ".asmT");
		try {
			output = Files.readAllLines(p);
			
			replacePhraseWithString(output, "{--LABEL--}", label);
		} catch (IOException e) {
			System.out.println("Error opening file for template use");
		}
		return output;
	}
	
	private static List<String> complete_func(String subline, String asmlibrary, String unique) {
		List<String> output = new ArrayList<String>();
		Path p = FileSystems.getDefault().getPath(asmlibrary, "function.asmT");
		try {
			output =Files.readAllLines(p);
			
			//find our symbols to replace, for pop it is PAGE and OFFSET
			int spacepos = subline.indexOf(' ');
			String name = subline.substring(0,spacepos);
			String locals = subline.substring(spacepos+1);
			//Lets start with that PAGE
			System.out.println(subline);
			System.out.println(name);
			System.out.println(locals);
		
			
			
			replacePhraseWithString(output, "{--LOCALS--}", locals);
			uniqueitizer(output, unique);
			replacePhraseWithString(output, "{--FUNCTION LABEL--}",  "("+name+")");
		} catch (IOException e) {
			System.out.println("Error opening file for template use");
		}
		return output;
	}
	private static List<String> complete_call(String subline, String asmlibrary,String unique) {
		List<String> output = new ArrayList<String>();
		Path p = FileSystems.getDefault().getPath(asmlibrary, "call.asmT");
		try {
			output =Files.readAllLines(p);
			
			//find our symbols to replace, for pop it is PAGE and OFFSET
			int spacepos = subline.indexOf(' ');
			String name = subline.substring(0,spacepos);
			String args = subline.substring(spacepos+1);
			//Lets start with that PAGE
			System.out.println(subline);
			System.out.println(name);
			System.out.println(args);
		
			
			
			replacePhraseWithString(output, "{--ARGS--}", args);
			
			uniqueitizer(output, unique);
			
			replacePhraseWithString(output, "{--FUNCTION LABEL--}", name);
		} catch (IOException e) {
			System.out.println("Error opening file for template use");
		}
		return output;
	}
	
	private static String get_symbol_for_page(String page) {
		String page_replace = "!!ERROR!!";
		int found = find_match(page, pages);
		switch(found) {
		case 0: // local
			page_replace = "LCL";
			break;
		case 1: // arg
			page_replace = "ARG";
			break;
		case 2: // this
			page_replace = "THIS";
			break;
		case 3: // that
			page_replace = "THAT";
			break;
		case 4: // thispoint
			page_replace = "THISPOINT";
			break;
			//These are not a valid choices for popping from stact, put in error, need to do something else for these cases
		case 5: // thatpoint
			page_replace = "THATPOINT";
			break;
		case 6: // constant
			page_replace = "!CONSTANT";
			break;
		case 7: // static
			break;
		case 8:
			page_replace = "TEMP";
		}
		return page_replace;
	}
	
	private static void handleConditional(boolean value,  String condition, List<String> arr) {
		//we have an IF/ELSE to handle in this one,
		int loc_if = 0;
		int loc_else = 0;
		int loc_end = 0;
		//Find startements
		for (int i = 0; i< arr.size(); i++) {
			if (arr.get(i).contains("#IF")) {
				loc_if = i;
			} else if (arr.get(i).contains("#ELSE")) {
				loc_else = i;
			} else if (arr.get(i).contains("#ENDIF")) {
				loc_end = i;
			}
		}
		//for now, hardcoding the if statement,
		if (value && arr.get(loc_if).contains(condition)) {
			arr.remove(loc_if);
			//remove the block from else->end
			for(int i = 0; i<=loc_end-loc_else; i++) {
				arr.remove(loc_else-1);
			}
		} else {
			arr.remove(loc_end);
			//remove the block from else->end
			for(int i = 0; i<=loc_else-loc_if; i++) {
				arr.remove(loc_if);
			}
		}
	}
	
	private static void uniqueitizer(List<String> arr, String unique) {
		//This function will uniqueitize any label in a list of asm commands by appening a unique identifier on the end
		for (int i=0; i< arr.size(); i++) {
			//First, find a label to uniqueitize
			if (arr.get(i).length() > 0 && arr.get(i).charAt(0) == '(') {
				//using "(" to find labels
				String labelline = arr.get(i);
				int labelstart = 1;
				int labelend = labelline.indexOf(')') -1;
				String label = labelline.substring(labelstart, labelend);
				String uniquelabel = unique + "." + label;
				for (int j=0; j <arr.size(); j++) {
					if (arr.get(j).contains(label)) {
						arr.set(j,  arr.get(j).replace(label, uniquelabel));
					}
				}
			}
		}
	}

	private static void replacePhraseWithString(List<String> arr, String phrase, String newPhrase) {
		for(int i =0; i< arr.size(); i++) {
			if (arr.get(i).contains(phrase)) {
				System.out.println("FOUND "+newPhrase+" AT " + i);
				arr.set(i, arr.get(i).replace(phrase, newPhrase));
			}
		}
	}
}


