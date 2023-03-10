//This class contains static functions that will decode binary instructions back to slightly more readable assembly
//Written by Alexander Powers
//CS498 Spring 2022
package bunny;

public class Decoder {
	//This class will have a static function that will "decode a binary string
	
	private static InstructionSet set = new InstructionSet();
	
	public static String single(byte input, boolean isValue) {
		return single(input, isValue, true);
	}
	
	public static String single(byte input, boolean isValue, boolean signed) {
		//Just a single instruction, 
		if (isValue) {
			return Integer.toString(Lib.byteToInt(input, signed));
		} else {
			String name = set.get_instruction(Byte.toUnsignedInt(input)).getName();
		    return name;
		}
	}
	
	public static String[] array(Byte[] input) {
		String result[] = new String[input.length];
		int loadqueue = 0; //Holding how many bytes have to be interpreted as value
		for (int i =0; i<result.length; i++) {
			if (loadqueue > 0) {
				result[i] = single(input[i], true, false);
				loadqueue -= 1;
			} else {
				result[i] = single(input[i], false);
				loadqueue += set.get_instruction(Byte.toUnsignedInt(input[i])).getLoad();
			}
			
		}
		return result;
	}
}
