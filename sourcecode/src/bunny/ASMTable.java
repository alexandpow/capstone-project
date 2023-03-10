//This class is for handling the Binary Instruction and RAM table display on the GUI application
//Written by Alexander Powers
//CS498 Spring 2022
package bunny;
import javax.swing.table.AbstractTableModel;
import java.util.*;


public class ASMTable extends AbstractTableModel {
	private String[] columnNames = {"address", "value"};
	private Object[][] data = {{0, ""}};
	
	public ASMTable() {
		super();
	}
	
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
	
	public void loadROM(Byte ROM[]) {
		List<String> input = new ArrayList<String>();
		//Load the byteformat strings in
		for (int i = 0; i < ROM.length; i++) {
			if (ROM[i] != null) {
				input.add(Lib.byteformat(ROM[i]));
				//System.out.println(ROM[i]);
			} else {
				input.add("00000000");
			}
		}
		String[] output  = input.toArray(new String[0]);
		data = new Object[output.length][2];
		for (int i = 0; i < output.length; i++) {
			Object[] temp = {i, output[i]};
			data[i] = temp;
		}
		
	}
	
	public void loadRAM(Byte RAM[]) {
		List<String> input = new ArrayList<String>();
		//Load the byteformat strings in
		for (int i = 0; i < RAM.length; i++) {
			if (RAM[i] != null) {
				input.add(Lib.byteformat(RAM[i]));
				//System.out.println(ROM[i]);
			} else {
				input.add("00000000");
			}
		}
		String[] output  = input.toArray(new String[0]);
		data = new Object[output.length][2];
		for (int i = 0; i < output.length; i++) {
			//Object[] temp;
			Object[] temp  = {i, output[i]};
			switch (i) {
			case 0:
				temp[0] = "SP";
				break;
			case 2:
				temp[0] = "LCL";
				break;
			case 4:
				temp[0] = "ARG";
				break;
			case 6:
				temp[0] = "THIS";
				break;
			case 8:
				temp[0] = "THAT";
				break;
			case 10:
				temp[0] = "TEMP0";
				break;
			case 11:
				temp[0] = "TEMP1";
				break;
			case 12:
				temp[0] = "TEMP2";
				break;
			case 13:
				temp[0] = "TEMP3";
				break;
			case 14:
				temp[0] = "TEMP4";
				break;
			case 15:
				temp[0] = "TEMP5";
				break;
			case 1:
			case 3:
			case 5:
			case 7:
			case 9:
				temp[0] = "-";
				
			}
			data[i] = temp;
		}
	}
	
	public int getColumnCount() {
		return columnNames.length;
	}
	public int getRowCount() {
		return data.length;
	}
}
