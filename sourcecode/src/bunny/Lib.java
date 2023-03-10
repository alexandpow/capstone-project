//This class contains a set of static instructions for use in other classes, mostly to help with ease of reading, 
//Written by Alexander Powers
//CS498 Spring 2022
package bunny;

public final class Lib {
	public static void test() {

	}
	public static boolean isNumeric(String str) {
		//Will return true if the string is numerical
		if (str != null) {
			try {
				Integer.valueOf(str);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}
		return false;
	}
	
	public static String byteformat(byte input) {
		//Turn a byte into a proper display format
		String binstring = Integer.toBinaryString(input);
		if (binstring.length() > 8) { //byte counted as signed
			return binstring.substring(24);
		} else if (binstring.length() < 8) { //Bits not included
			int amount_add = 8 - binstring.length();
			String before = "";
			for (int i = 0; i < amount_add; i++) {
				before += "0";
			}
			return before + binstring;
		}
			return binstring;
		}
	
	public static Byte triOctToByte(int oct1, int oct2, int oct3) {
		//Take three octal values and output a byte, much easier to read and compare to chart
		return (byte)((oct1*64) + (oct2*8) + oct3);
	}
	
	public static int byteToInt(byte input, boolean signed) {
		if (signed) {
			return (int) input;
		} else {
			return Byte.toUnsignedInt(input);
		}
	}
	
	public static int triOctToInt(int oct1, int oct2, int oct3, boolean signed) {
		return byteToInt(triOctToByte(oct1,oct2,oct3),signed);
	}
	
	public static int triOctToUnsignedInt(int oct1, int oct2, int oct3) {
		return triOctToInt(oct1, oct2, oct3, false);
	}
}
