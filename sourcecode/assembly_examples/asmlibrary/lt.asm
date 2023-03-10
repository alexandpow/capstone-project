//VM lt command
//--LT--
//if the two top values on the stack are X<Y (where X is the lower on the stack), put a 1 on the stack
@SP
HLMLOAD //Go to memory from stack pointer

HLDEC
A=M
HLDEC
B=M

A=A-B
M=0
@ISFALSE
//A=3 B=5, 3-5=-2 JLE works
JLE
M=1
(ISFALSE)
HLINC

//Update stack pointer
AB=HL
@SP
M=A
HLINC
M=B
