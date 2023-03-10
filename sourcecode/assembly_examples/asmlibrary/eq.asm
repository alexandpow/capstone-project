//VM eq command
//--EQ--
//if the two top values on the stack are equal, put a 1 on the stack
@SP
HLMLOAD //Go to memory from stack pointer

HLDEC
A=M
HLDEC
B=M

A=A-B
M=0
@ISFALSE
JNE
M=1
(ISFALSE)
HLINC

//Update stack pointer
AB=HL
@SP
M=A
HLINC
M=B
