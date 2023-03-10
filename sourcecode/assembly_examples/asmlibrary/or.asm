//VM or command
//--OR--
//ors the values on top of the stack together
@SP
HLMLOAD //Go to memory from stack pointer

//Do anding
HLDEC
A=M
HLDEC
B=M

M=A|B
HLINC
//Update stack pointer
AB=HL
@SP
M=A
HLINC
M=B
