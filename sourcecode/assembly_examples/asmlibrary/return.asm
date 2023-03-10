//--RETURN--
//assembly for returning to a point where a function was called

//Pop our current local, arg, this, and that out of the stack
//Since these values are close together, it makes a lot more sense just to loop them
@THAT
AB=HL

@TEMP2
M=B //spot for LCL in TEMP2

(POP_LOOP)
//put stack->vars
@SP
HLMLOAD

//get from stack
HLDEC
HLMLOAD
AB=HL

//go to var spot
@TEMP2
LZ=M

//place in var spot
M=A
HLINC
M=B

//update stack pointer
@SP
HLMLOAD
HLDEC
HLDEC
AB=HL
@SP
M=A
@SP1
M=B

//Check loop condition
@TEMP2
A=2
M=M-A

//if TEMP2 = 1, stop loop
A=1
A=A-M
@POP_LOOP
JNE

//pull and jump to new code pointer
@SP
HLMLOAD
HLDEC
HLDEC
HLMLOAD
JMP
