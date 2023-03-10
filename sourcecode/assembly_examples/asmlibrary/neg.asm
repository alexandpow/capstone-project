//VM neg command
//--NEG--
//negates the value on top of the stack
@SP
HLMLOAD //Go to memory from stack pointer

//Do addition
HLDEC
M=-M
