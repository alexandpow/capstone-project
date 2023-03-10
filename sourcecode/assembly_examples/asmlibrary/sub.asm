//VM sub command
//--SUB--
//subtracts the values on top of the stack together
@SP
HLMLOAD //Go to memory from stack pointer

//Do addition
HLDEC
A=M
HLDEC
B=M

M=A-B
HLINC
//Update stack pointer
AB=HL
@SP
M=A
HLINC
M=B
