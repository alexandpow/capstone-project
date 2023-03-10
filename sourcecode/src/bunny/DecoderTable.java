//This class is for handling the Decoded ASM table display on the GUI application
//Written by Alexander Powers
//CS498 Spring 2022
package bunny;
import javax.swing.table.AbstractTableModel;
import java.util.*;

public class DecoderTable extends AbstractTableModel {
	private String[] columnNames = {"address", "value"};
	private Object[][] data = {{0, ""}};
	
	public DecoderTable() {
		super();
	}
	
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
	
	public void loadData(String arr[]) {
		List<String> input = new ArrayList<String>();
		//Load the byteformat strings in
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != null) {
				input.add(arr[i]);
				//System.out.println(ROM[i]);
			} else {
				input.add("ERROR");
			}
		}
		String[] output  = input.toArray(new String[0]);
		data = new Object[output.length][2];
		for (int i = 0; i < output.length; i++) {
			Object[] temp = {i, output[i]};
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
