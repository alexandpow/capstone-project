//This class was used to test translation and execution of the code, right now it just tests the translation from stack machine to assembly code
//Written by Alexander Powers
//CS498 Spring 2022
package bunny;

//import java.util.Timer;

import bunny.asm.ASMCompiler;
import bunny.vm.Sim;
import bunny.vm.Chip;

//1.25 delay = 800 KhZ
//5 delay = 200KhZ

//60 fps = 16,667 milliseconds per frame
//800 khz = 13,333 commands per frame
//200 khz = 3,333 commands per frame

//TODO bug where H and L swap for some bad reason

public class Runner {

	public static void main(String[] args) {
		//String[] stacks = StackMachine.translate("D:\\eclipse\\workspace\\bunny-virtual\\stack\\test\\test.sta", "D:\\eclipse\\workspace\\bunny-virtual\\stack\\test\\test.asm", "D:\\eclipse\\workspace\\bunny-virtual\\asm\\asmlibrary");
		//for (int i = 0; i < stacks.length; i++)
		//	System.out.println(stacks[i]);
		//Chip[] chipset = {new IO(57342,65535)};
		Chip[] chipset = {};
		Sim vm = new Sim(chipset);
		setup_test(vm);
		vm.set_delay(1);
		run_test(vm);
		compare_test(vm);
	}

	private static void setup_test(Sim vm) {
		long starttime = System.nanoTime();
		Byte[] testROM = ASMCompiler.assemble("D:/spring2022/capstone deliverables/sourcecode/assembly_examples/test/test.asm", "D:/spring2022/capstone deliverables/sourcecode/assembly_examples/test/test.mch");
		long endtime = System.nanoTime();
		System.out.println("Compilation time: " + (endtime-starttime)/1000L);
		//byte testROM[] = new byte[128];
		//Test A instruction (0x10 = 42) 
		//testROM[0] = (byte) 0b10000000; //A= immediate (2 0 0
		//testROM[1] = (byte) 0b00101010; //42
		//testROM[2] = (byte) 0b00000010; //M=A (0 0 2) 
		//Test comps are correct
		//Test dests are correct
		//Test jumps are correct
		vm.load_ROM(testROM);
	}
	private static void run_test(Sim vm) {
		for (int i = 0; i<2; i++) {
			if (!vm.is_stopped()) {
				vm.step();
				System.out.println("H = " + vm.get_h_reg());
				System.out.println("L = " + vm.get_l_reg());
			}	
		}
	}
	private static void compare_test(Sim vm) {
		System.out.println(Byte.toUnsignedInt(vm.return_RAM_value(0)));
	}
}