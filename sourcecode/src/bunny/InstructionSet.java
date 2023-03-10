//This class holds a list of every defined instruction, with further work, the whole program could reference this object instead for ensured consistency with changes
//Written by Alexander Powers
//CS498 Spring 2022
package bunny;

public class InstructionSet {
	
  //Class that when constructed, has a list of all of the instructions defined
  private Instruction[] list = new Instruction[256];
  
  //Just going to make this messy 
  InstructionSet() {
	  //Automated generation of comp codes
	  //A, B codes
	  String[] destinations = {"A","B", "M", "L", "H","LZ", "AB"};
	  String[] operationsAB = {"A", "!A", "-A", "A+1", "A-1", "A+B", "A-B", "A&B"};
	  for(int i =0; i<7; i++) {
		  for(int j=0; j<8; j++) {
			  list[Lib.triOctToUnsignedInt(0,j,i)] = new Instruction(destinations[i]+"="+operationsAB[j],Lib.triOctToByte(0, j, i));
		  }
	  }
	  //B, A codes
	  String[] operationsBA = {"B", "!B", "-B","B+1", "B-1", "TEMP_UNDF", "B-A", "A|B"};
	  for(int i =0; i<7; i++) {
		  for(int j=0; j<8; j++) {
			  if (operationsBA[j] != "TEMP_UNDF") {
				  list[Lib.triOctToUnsignedInt(1,j,i)] = new Instruction(destinations[i]+"="+operationsBA[j],Lib.triOctToByte(1, j, i));
			  }
		  }
	  }
	  //A, M codes
	  String[] operationsAM = {"TEMP_UNDF", "TEMP_UNDF", "TEMP_UNDF", "TEMP_UNDF", "TEMP_UNDF", "TEMP_UNDF", "A-M", "A&M"};
	  for(int i =0; i<7; i++) {
		  for(int j=0; j<8; j++) {
			  if (j!=0) {
				  if (operationsAM[j] != "TEMP_UNDF") {
					  list[Lib.triOctToUnsignedInt(2,j,i)] = new Instruction(destinations[i]+"="+operationsAM[j],Lib.triOctToByte(2, j, i));
			  		}
			  } else {
				  list[Lib.triOctToUnsignedInt(2,0,i)] = new Instruction(destinations[i]+"=Next",Lib.triOctToByte(2, 0, i),1);
			  }
			  
		  }
	  }
	  //M, A codes
	  String[] operationsMA = {"M", "!M", "-M", "M+1", "M-1", "A+M", "M-A", "A|M"};
	  for(int i =0; i<7; i++) {
		  for(int j=0; j<8; j++) {
			  list[Lib.triOctToUnsignedInt(3,j,i)] = new Instruction(destinations[i]+"="+operationsMA[j],Lib.triOctToByte(3, j, i));
		  }
	  }
	  //JUMP codes
	  String[] jumps = {"JMP", "JGR", "JEQ", "JGE", "JLE", "JNE", "JLT"};
	  for (int i=0; i<7; i++) {
		  list[Lib.triOctToUnsignedInt(1,5,i)] = new Instruction(jumps[i],Lib.triOctToByte(1, 5, i));
	  }
	  
	  //SPECIAL codes
	  for (int i=0; i<8; i++) {
		  list[Lib.triOctToUnsignedInt(2,1,i)] = new Instruction("BIGLOAD"+(i+1),Lib.triOctToByte(2, 1, i),i+1);
	  }
	  String[] specials = {"HLMLOAD", "TEMP_UNDF", "HLINC", "HLDEC", "HL=AB", "AB=HL", "RESET", "STOP"};
	  for (int i=0; i<8;i++) {
		  list[Lib.triOctToUnsignedInt(0,i,7)] = new Instruction(specials[i],Lib.triOctToByte(0, i, 7));
	  }
	  list[Lib.triOctToUnsignedInt(0, 1, 7)] = new Instruction("HLILOAD", Lib.triOctToByte(0, 1, 7), 2);
	  list[Lib.triOctToUnsignedInt(1,5,7)] = new Instruction("OVERA", Lib.triOctToByte(1,5,7));
  }
  
  public Instruction[] get_list() {
	  return list;
  }
  
  public Instruction get_instruction(int index) {
	  if (list[index] != null) {
		  return list[index];
	  } else {
		  return new Instruction("!!UNDEFINED!!", (byte) 0);
	  }
  }
  
  
  public static void main(String[] args) {
	  InstructionSet set = new InstructionSet();
	  int count = 0;
	  for (int i=0; i<256; i++) {
		  String name = set.get_instruction(i).getName();
		  if (name != "!!UNDEFINED!!") {
			  count += 1;
		  }
		  System.out.println(i + ": " + name);
	  }
	  System.out.println(count +"/256 commands defined");
  }
}
