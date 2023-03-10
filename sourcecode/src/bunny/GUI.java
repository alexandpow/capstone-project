//This class was my attempt at getting a working GUI for my program, its hardcoded a lot but im happy with it's usability
//Written by Alexander Powers
//CS498 Spring 2022
package bunny;

import javax.swing.BoxLayout;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.BorderLayout;
//import javax.swing.table.AbstractTableModel;

import bunny.asm.ASMCompiler;
import bunny.vm.Chip;
import bunny.vm.Sim;

//import java.util.*;
import java.awt.event.*;  
//Properly working scrollable pane from https://kodejava.org/how-do-i-create-a-scrollable-jtable-component/

public class GUI {
  //65536
	public static void main(String[] args) {
		//String[] stacks = StackMachine.translate("D:\\eclipse\\workspace\\bunny-virtual\\stack\\test\\test.sta", "D:\\eclipse\\workspace\\bunny-virtual\\stack\\test\\test.asm", "D:\\eclipse\\workspace\\bunny-virtual\\asm\\asmlibrary");

		Byte[] testROM = ASMCompiler.assemble("D:\\eclipse\\workspace\\bunny-virtual\\stack\\test\\test.asm", "D:\\eclipse\\workspace\\bunny-virtual\\stack\\test\\test.mch");
		//Byte[] testROM = ASMCompiler.assemble("D:\\eclipse\\workspace\\bunny-virtual\\asm\\test\\desynctest.asm", "D:\\eclipse\\workspace\\bunny-virtual\\asm\\test\\desynctest.mch");

		//Byte[] testROM = stacks;
		Chip[] chipset = {};
		Sim vm = new Sim(chipset);
		vm.load_ROM(testROM);
		//JButton b=new JButton("click");//creating instance of JButton  
		ASMTable mine = new ASMTable();
		mine.loadROM(testROM);
		JTable asmtable = new JTable(mine);
		asmtable.addRowSelectionInterval(0, 0);
		
		DecoderTable decode = new DecoderTable();
		decode.loadData(Decoder.array(testROM));
		JTable dectable = new JTable(decode);
		dectable.addRowSelectionInterval(0, 0);
		
		ASMTable ram = new ASMTable();
		ram.loadRAM(vm.get_RAM());
		JTable ramtable = new JTable(ram);
		
        
		//asmtable.setPreferredScrollableViewportSize(new Dimension(300,400));
        //asmtable.setFillsViewportHeight(true);
		
		
		//panels for registers
		JPanel rp = new JPanel();
		rp.setLayout(new BoxLayout(rp, BoxLayout.Y_AXIS));
		
		//Top panel will have A, H, and PC
		JTextField a_reg = new JTextField(6);
		a_reg.setPreferredSize(new Dimension(25,25));
		a_reg.setActionCommand("AReg");
		JLabel aLabel = new JLabel("A Register:");
		aLabel.setLabelFor(a_reg);
		
		JTextField h_reg = new JTextField(6);
		h_reg.setPreferredSize(new Dimension(25,25));
		h_reg.setActionCommand("HReg");
		JLabel hLabel = new JLabel("H Register:");
		hLabel.setLabelFor(h_reg);
		
		JTextField pc_reg = new JTextField(6);
		pc_reg.setPreferredSize(new Dimension(25,25));
		pc_reg.setActionCommand("PCReg");
		JLabel pcLabel = new JLabel("PC Register:");
		pcLabel.setLabelFor(pc_reg);
		
		//Top panel
		JPanel tp = new JPanel();
		tp.add(aLabel);
		tp.add(a_reg);
		tp.add(hLabel);
		tp.add(h_reg);
		tp.add(pcLabel);
		tp.add(pc_reg);
		rp.add(tp);
		
		//Bottom Panel, A, L, and M
		JTextField b_reg = new JTextField(6);
		b_reg.setPreferredSize(new Dimension(20,25));
		b_reg.setActionCommand("BReg");
		JLabel bLabel = new JLabel("B Register:");
		bLabel.setLabelFor(b_reg);
		
		JTextField l_reg = new JTextField(6);
		l_reg.setPreferredSize(new Dimension(20,25));
		l_reg.setActionCommand("LReg");
		JLabel lLabel = new JLabel("L Register:");
		bLabel.setLabelFor(l_reg);
		
		JTextField m_reg = new JTextField(6);
		m_reg.setPreferredSize(new Dimension(20,25));
		m_reg.setActionCommand("MReg");
		JLabel mLabel = new JLabel("M Register:");
		bLabel.setLabelFor(m_reg);
		
		//Bottom Panel
		JPanel bp = new JPanel();
		bp.add(bLabel);
		bp.add(b_reg);
		bp.add(lLabel);
		bp.add(l_reg);
		bp.add(mLabel);
		bp.add(m_reg);
		rp.add(bp);
		
		rp.setPreferredSize(new Dimension(0,75));
		
		//Execute button
		JButton b = new JButton("Execute");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.step();
				//Update displayed values
				ram.loadRAM(vm.get_RAM());
				ram.fireTableDataChanged();
				asmtable.clearSelection();
				asmtable.addRowSelectionInterval(vm.get_PC(), vm.get_PC());
				dectable.clearSelection();
				dectable.addRowSelectionInterval(vm.get_PC(), vm.get_PC());
				a_reg.setText(Lib.byteformat(vm.get_a_reg()));
				h_reg.setText(Lib.byteformat(vm.get_h_reg()));
				pc_reg.setText(Integer.toString(vm.get_PC()));
				
				b_reg.setText(Lib.byteformat(vm.get_b_reg()));
				l_reg.setText(Lib.byteformat(vm.get_l_reg()));
				m_reg.setText(Lib.byteformat(vm.return_RAM_value(vm.get_index(0))));
			}
		});
		
		//Speed button
		JButton bf = new JButton("do 100");
		bf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i<100; i++) {
					vm.step();
				}
				//Update displayed values
				ram.loadRAM(vm.get_RAM());
				ram.fireTableDataChanged();
				asmtable.clearSelection();
				asmtable.addRowSelectionInterval(vm.get_PC(), vm.get_PC());
				dectable.clearSelection();
				dectable.addRowSelectionInterval(vm.get_PC(), vm.get_PC());
				a_reg.setText(Lib.byteformat(vm.get_a_reg()));
				h_reg.setText(Lib.byteformat(vm.get_h_reg()));
				pc_reg.setText(Integer.toString(vm.get_PC()));
				
				b_reg.setText(Lib.byteformat(vm.get_b_reg()));
				l_reg.setText(Lib.byteformat(vm.get_l_reg()));
				m_reg.setText(Lib.byteformat(vm.return_RAM_value(vm.get_index(0))));
			}
		
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(b);
		buttonPanel.add(bf);
		
		//Initialize all fields
		ram.loadRAM(vm.get_RAM());
		ram.fireTableDataChanged();
		asmtable.clearSelection();
		asmtable.addRowSelectionInterval(vm.get_PC(), vm.get_PC());
		a_reg.setText(Lib.byteformat(vm.get_a_reg()));
		h_reg.setText(Lib.byteformat(vm.get_h_reg()));
		pc_reg.setText(Integer.toString(vm.get_PC()));
		
		b_reg.setText(Lib.byteformat(vm.get_b_reg()));
		l_reg.setText(Lib.byteformat(vm.get_l_reg()));
		m_reg.setText(Lib.byteformat(vm.return_RAM_value(vm.get_index(0))));
		
		//Set up scrollable pane
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setPreferredSize(new Dimension(460,600));
		JScrollPane pane = new JScrollPane(asmtable);
		pane.setPreferredSize(new Dimension(150,600));
		JScrollPane panedec = new JScrollPane(dectable);
		panedec.setPreferredSize(new Dimension(150,600));
		JScrollPane paneram = new JScrollPane(ramtable);
		paneram.setPreferredSize(new Dimension(150,600));
		p.add(pane, BorderLayout.WEST);
		p.add(panedec, BorderLayout.CENTER);
		p.add(paneram, BorderLayout.EAST);
		p.add(buttonPanel, BorderLayout.SOUTH);
		p.add(rp, BorderLayout.NORTH);
		
		
		//Put pane in frame and dispaly
		JFrame f=new JFrame();//creating instance of JFrame  
		p.setOpaque(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setTitle("BunnyView");
		f.setContentPane(p);
		f.pack();
		f.setVisible(true);
	}
	
}
