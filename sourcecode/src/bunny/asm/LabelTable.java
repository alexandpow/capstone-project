//This class is for handling the labels of an assembly program when it is translated
//Written by Alexander Powers
//CS498 Spring 2022
package bunny.asm;

import java.util.HashMap;

public class LabelTable {
	
	//Map for holding the location of a label in cleaned asm
	private HashMap<String, Integer> asmtable = new HashMap<String, Integer>();
	//map for holding the location of a label in machine code list
	private HashMap<String, Integer> mchtable = new HashMap<String, Integer>();
	public LabelTable() {//Default labels would go here, if we had any
		addEntryMch("SP", 0);
		addEntryMch("SP1", 1);
		addEntryMch("LCL", 2);
		addEntryMch("LCL1", 3);
		addEntryMch("ARG", 4);
		addEntryMch("ARG1", 5);
		addEntryMch("THIS", 6);
		addEntryMch("THIS1", 7);
		addEntryMch("THAT", 8);
		addEntryMch("THAT1", 9);
		addEntryMch("TEMP", 10);
		addEntryMch("TEMP1", 11);
		addEntryMch("TEMP2", 12);
		addEntryMch("TEMP3", 13);
		addEntryMch("TEMP4", 14);
		addEntryMch("TEMP5", 15);
		
	}
	public void addEntryMch(String label, int address) { 
		mchtable.put(label, address);
	}
	
	public boolean containsMch(String label) {
		return mchtable.containsKey(label);
	}
	
	public int GetAddressMch(String label) {
		return mchtable.get(label);
	}
	
	public void addEntryAsm(String label, int address) { 
		asmtable.put(label, address);
	}
	
	public boolean containsAsm(String label) {
		return asmtable.containsKey(label);
	}
	
	public int GetAddressAsm(String label) {
		return asmtable.get(label);
	}
	
	public boolean EntryExistsAsm(int address) {
		return asmtable.containsValue(address);
	}
	
	
	public int getLabelOffset() {
		return mchtable.size();
	}
	
}
