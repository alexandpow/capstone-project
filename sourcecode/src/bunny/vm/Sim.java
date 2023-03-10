//This class is for simulating the "Bunny" virtual machine
//Written by Alexander Powers
//CS498 Spring 2022
package bunny.vm;

import java.util.concurrent.TimeUnit;

//TODO: I am cheating here a bit, I am not setting an internal flag to store the instruction and then use the next instruction as a value, im just doing it all in one go, probably not how a cpu would do it

public class Sim {
	private final int MAX_SIZE = 65536;
	//private final byte MAX_VAL = (byte)0b11111111;
	//CPU Registers
	private int PC = 0;
	private byte a_reg = 0;
	private byte b_reg = 0;
	private byte h_reg = 0;
	private byte l_reg = 0;
	private boolean overflow = false; //Updates on any addition/subtraction
	//RAM
	private Byte RAM[] = new Byte[MAX_SIZE];
	//ROM
	private Byte ROM[] = new Byte[MAX_SIZE];
	//CPU
	private CPU cpu = new CPU();
	private int delay = 0;
	//Extensions
	private Chip[] chips;

	private boolean stopped = false;
	
	public Sim(Chip[] newchips) {
		chips = newchips.clone();
	}
	
	public void load_ROM(Byte new_ROM[]) {
		ROM = new_ROM;
	}

	public void reset() {
		PC = 0;
		a_reg = 0;
		b_reg = 0;
		h_reg = 0;
		l_reg = 0;
		RAM = new Byte[MAX_SIZE];
		RAM = zero_out(RAM).clone();
		ROM = new Byte[MAX_SIZE];
		ROM = zero_out(ROM).clone();
	}

	private Byte[] zero_out (Byte[] arr) {
		for(int i =0; i< arr.length; i++) {
			arr[i] = 0;
		}
		return arr;
	}
	
	public void step() {
		int oldpc = PC;
		cpu.execute_next();
		//Simulated clockspeed
		if (delay >0) {
			try {
				TimeUnit.MILLISECONDS.sleep(delay *(PC-oldpc));
			} catch (InterruptedException e){
				System.out.println("sleep fail");
			}
		}
		//Run extensions
		for (int i =0; i<chips.length;i++) {
			chips[i].run(RAM);
		}
	}
	
	public void set_delay(int newd) {
		delay = newd;
	}

	public int get_PC() { return PC; }
	public byte get_a_reg() { return a_reg; }
	public byte get_b_reg() { return b_reg; }
	public byte get_h_reg() { return h_reg; }
	public byte get_l_reg() { return l_reg; }
	public Byte[] get_RAM() { return RAM;}
	public byte return_RAM_value(int addr) { return cpu.get_M(); }
	

	public boolean is_stopped() { return stopped; }
	
	public int get_index(int offset) {
		return (Byte.toUnsignedInt(h_reg)*256) + Byte.toUnsignedInt(l_reg)+(byte)offset;
	}
	
	private class CPU {
		//Because of the fun way java assumes these are unsigned, we force it with casting the type
		public  void execute_next() {
			
			final byte first_mask =  (byte) 0b00111111;
			final byte second_mask = (byte) 0b11000111;
			final byte third_mask =  (byte) 0b11111000;
			final byte keep_two =    (byte) 0b11111100;
			final byte keep_three =  (byte) 0b11111000;
			System.out.println("Instruction PC" + PC);
			byte curr = ROM[PC];
			boolean no_inc = false;
			//Instructions are split into their octal values
			// VV VVV VVV
			//  X  X   X

			//First_octal handling
			//mask handling
			// curr = 11010101
			// xor  = 11000000
				//System.out.println("First Oct");
			byte first_oct = (byte) (((curr^first_mask)&curr) >> 6);
			first_oct = (byte) ((first_oct^keep_two)&first_oct);
				//System.out.println(Integer.toBinaryString(first_oct));
				//System.out.println(first_oct);

			//Second octal handling
				//System.out.println("Second Oct");
			byte second_oct = (byte) (((curr^second_mask)&curr) >> 3);
			second_oct = (byte) ((second_oct^keep_three)&second_oct);
				//System.out.println(Integer.toBinaryString(second_oct));
				//System.out.println(second_oct);

			//Third octal handling
				//System.out.println("Third Oct");
			byte third_oct = (byte) (((curr^third_mask)&curr) >> 0);
			third_oct = (byte) ((third_oct^keep_three)&third_oct);
				//System.out.println(Integer.toBinaryString(third_oct));
				//System.out.println(third_oct);

			//Special instructions
			if (first_oct == 0b00 && third_oct == 0b111) {
				handle_special(second_oct);
			//Jump instructions + OVERA
			} else if (first_oct == 0b01 && second_oct == 0b101) {
				no_inc = handle_jumps_overa(third_oct);
			//immediate load instructions
			} else if (first_oct == 0b10 && second_oct == 0b000) {
				handle_immediate_loads(third_oct);
			//big load instructions
			} else if (first_oct == 0b10 && second_oct == 0b001) {
				for (int i = 0; i < third_oct+1; i++) {
					PC++;
					set_M(ROM[PC]);
					adjust_l((byte)1);
				}
				adjust_l((byte)(0-(third_oct+1)));
			//dest=comp instructions
			} else {
				handle_destcomp(first_oct, second_oct, third_oct);
			}
			if (!no_inc) {PC++;}
			//System.out.println("A = " + a_reg +"\nB = " + b_reg + "\nM = " + get_M() + "\nH = " + h_reg + "\nL =" +l_reg);
			//detect overflow
		}

		private void handle_special(final byte second_oct) {
			switch(second_oct) {
				case 0b000: //HLMLOAD
					byte temp_h=get_RAM_safe(get_index(0));
					byte temp_l= get_RAM_safe(get_index(1));
					System.out.println("H=" + get_RAM_safe(get_index(0)) +" L=" + get_RAM_safe(get_index(1)));
					h_reg = temp_h;
					l_reg = temp_l;
					
					break;
				case 0b001: //HLILOAD
					PC++;
					h_reg = ROM[PC];
					PC++;
					l_reg = ROM[PC];
					break;
				case 0b010: //HLINC
					adjust_l((byte) 1);
					break;
				case 0b011: // HLDEC
					adjust_l((byte)-1);
					break;
				case 0b100: //HL=AB
					h_reg = a_reg;
					l_reg = b_reg;
					break;
				case 0b101: //AB = HL
					a_reg = h_reg;
					b_reg = l_reg;
					break;
				case 0b110: //RESET
					reset();
					break;
				case 0b111: //STOP
					stopped = true;
					break;
				}
		}

		//Return the new value of no_inc
		private boolean handle_jumps_overa( final byte third_oct) {
			boolean do_jump = false;
			switch(third_oct) {
			case 0b000:
				do_jump = true;
				break;
			case 0b001:
				if (a_reg > 0) {do_jump = true;}
				break;
			case 0b010:
				if (a_reg == 0) {do_jump = true;}
				break;
			case 0b011:
				if (a_reg >= 0) {do_jump = true;}
				break;
			case 0b100:
				if (a_reg < 0) {do_jump = true;}
				break;
			case 0b101:
				if (a_reg != 0) {do_jump = true;}
				break;
			case 0b110:
				if (a_reg <= 0) {do_jump = true;}
				break;
			case 0b111: //OVERA
				if (overflow) {
					a_reg = 1;
				} else {
					a_reg = 0;
				}
				break;
			}
			if (do_jump) {
				PC = get_index(0);
				return true;
			}
			return false;
		}

		private void handle_immediate_loads(final byte third_oct) {
			switch(third_oct) {
				case 0b000:
					PC++;
					a_reg=ROM[PC];
					break;
				case 0b001:
					PC++;
					b_reg=ROM[PC];
					break;
				case 0b010:
					PC++;
					set_M(ROM[PC]);
					break;
				case 0b011:
					PC++;
					l_reg = ROM[PC];
					break;
				case 0b100:
					PC++;
					h_reg = ROM[PC];
					break;
				case 0b101:
					PC++;
					l_reg = ROM[PC];
					h_reg = 0;
					break;
				case 0b110:
					PC++;
					a_reg=ROM[PC];
					b_reg=ROM[PC];
					break;
			}
		}

		private void handle_destcomp(final byte first_oct, final byte second_oct, final byte third_oct) {
			byte X=0;
			byte Y=0;
			byte comp_val=0;
			switch(first_oct) {
			case 0b00:
				X = a_reg;
				Y = b_reg;
				break;
			case 0b01:
				X = b_reg;
				Y = a_reg;
				break;
			case 0b10:
				X = a_reg;
				Y = get_M();
				break;
			case 0b11:
				X = get_M();
				Y = a_reg;
				break;
			}
			switch(second_oct) {
			case 0b000:
				comp_val = (byte) X;
				break;
			case 0b001:
				comp_val = (byte) ~X;
				break;
			case 0b010:
				comp_val = (byte) (0-X);
				break;
			case 0b011:
				comp_val = (byte) (X+1);
				overflow = check_overflow(X,(byte)1);
				break;
			case 0b100:
				comp_val = (byte) (X-1);
				overflow = check_overflow(X,(byte)-1);
				break;
			case 0b101:
				comp_val = (byte) (X+Y);
				overflow = check_overflow(X,Y);
				break;
			case 0b110:
				comp_val = (byte) (X-Y);
				overflow = check_overflow(X,(byte)(0-Y));
				break;
			case 0b111:
				if (first_oct == 0b00 || first_oct == 0b10) {
					comp_val = (byte) (X&Y);
				} else {
					comp_val = (byte) (X|Y);
				}
				break;
			}
			switch (third_oct) {
			case 0b000:
				a_reg = comp_val;
				break;
			case 0b001:
				b_reg = comp_val;
				break;
			case 0b010:
				set_M(comp_val);
				break;
			case 0b011:
				adjust_l(comp_val);
				break;
			case 0b100:
				h_reg = comp_val;
				break;
			case 0b101:
				l_reg = comp_val;
				h_reg = 0;
				break;
			case 0b110:
				a_reg = comp_val;
				b_reg = comp_val;
				break;
			}
		}	

		private Byte get_M() {
			if (RAM[get_index(0)] == null)
				return 0;
			return RAM[get_index(0)];
		}

		private void set_M(byte value) {RAM[get_index(0)] = value;}

		private void adjust_l(byte value) {
			//Value is how much to adjust the l_register by, will adjust h as needed
			//11111001 = 249 or -7
			//00000011 = 3 or 3	
			//11111100 = 252 or -4
			//So... for testing overflow/underflow

			int result = Byte.toUnsignedInt(l_reg) + value;
			if (result > 255 ) {
				//overflow will happen, increase h by one
				h_reg += 1;
			} else if (result < 0) {
				//underflow will happen, decrease h
				h_reg -= 1;
			}
			l_reg = (byte)result;
			
		}

		private Byte get_RAM_safe(int index) {
			if (RAM[index] == null)
				return 0;
			else
				return RAM[index];
		}
		private boolean check_overflow(byte initial, byte add) {
			//return true if there will be overflow here
			int result = initial + add;
			if (Integer.highestOneBit(result) >= 256) {
				return true;
			}
			return false;
		}
		
	}

}
