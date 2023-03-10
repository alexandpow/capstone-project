//--PROGRAM START--
@SP
M=1 //stack starts at @256
//--CALL--
//template assembly for the call <name> <args> command

//Make space for the new args on the stack, saving where they are to use later
//Save the current position on the stack
@SP
HLMLOAD
AB=HL

@TEMP
M=A
HLINC
M=B

//make space on the stack
@SP1
A=1
M=M+A

//check overflow
OVERA
@Global0.NO_OVERFLOW
JEQ
@SP
M=M+1
(Global0.NO_OVERFLOW)

//push our return flag label onto the stack
@Global0.RETURN_FLAG
AB=HL

@SP
HLMLOAD
M=A
HLINC
M=B
HLINC

//Save current position
AB=HL
@SP
M=A
HLINC
M=B

//Push our current local, arg, this, and that onto the directory
//Since these values are close together, it makes a lot more sense just to loop them
@LCL
AB=HL

@TEMP2
M=B //spot for LCL in TEMP2

(Global0.PUSH_LOOP)
//put whatever is currently meant to be put on the stack onto the stack
@TEMP2
LZ=M
HLMLOAD //get from what we want
AB=HL

@SP // put on stack
HLMLOAD
M=A
HLINC
M=B
HLINC

//Update stack pointer
AB=HL
@SP
M=A
HLINC
M=B

//Check loop condition
@TEMP2
A=2
M=M+A

//if TEMP2 = 10, stop loop
A=10
A=A-M
@Global0.PUSH_LOOP
JNE

//set the new args to where it is in the stack
@TEMP
HLMLOAD
AB=HL

@ARG
M=A
HLINC
M=B

//Go to function label
@main
JMP

//Return label
(Global0.RETURN_FLAG)
//--FUNCTION--
//template assembly for function <name> <locals> command
(return5)
//set local to current stack pointer and increment the stack pointer by the amount of locals wanted
@SP
HLMLOAD
AB=HL

@LCL
M=A
HLINC
M=B

@SP1
A=0
M=A+M

OVERA
@return52.NO_OVERFLOW
JEQ
@SP
M=M+1
(return52.NO_OVERFLOW)

//--PUSH--
//Template assembly program for pushing a value from memory page->stack
//For the purposes of explanation to a reader, lets assume the vm command "push local 5"


//block only used if constant being pushed onto stack
B=5


//Now with our value, lets put it on the stack
@SP
HLMLOAD
M=B

//Finally, go back to update the stack pointer
@SP
HLMLOAD
HLINC
AB=HL
@SP
M=A
@SP1
M=B
//--POP--
//Template assembly for stack->memory page
//Lets say it is pop local 4

//This block should only appear if the page has a pointer (arg, local, this,that)
@ARG //@LCL THIS @ TEMPLATED FOR DIFFERENT MEMORY SEGMENTS
HLMLOAD //Load local pointer into temp space for modification
AB=HL
@TEMP
M=A
@TEMP1
M=B


//setup pointer to the point in memory requested (local 4)
@TEMP1
A=0  //4 THIS VALUE TEMPLATED FOR DIFFERENT ADDR
M=A+M

//See if overflow
OVERA
@return54.PASTOVERFLOW
JEQ  //A will be = 0 if no overflow happened

//If we are here, overflow happened, we know it can only be an overflow, so we add 1 onto our temp H register
@TEMP
M=M+1
(return54.PASTOVERFLOW)


//Take the value off the top of the stack
@SP
HLMLOAD
HLDEC
A=M

//Now we have our pointer, lets get there with our value!
@TEMP
HLMLOAD 

M=A

//Finally, go back to update the stack pointer
@SP
HLMLOAD
HLDEC
AB=HL
@SP
M=A
@SP1
M=B
//--RETURN--
//assembly for returning to a point where a function was called

//Pop our current local, arg, this, and that out of the stack
//Since these values are close together, it makes a lot more sense just to loop them
@THAT
AB=HL

@TEMP2
M=B //spot for LCL in TEMP2

(Global5.POP_LOOP)
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
@Global5.POP_LOOP
JNE

//pull and jump to new code pointer
@SP
HLMLOAD
HLDEC
HLDEC
HLMLOAD
JMP
//--FUNCTION--
//template assembly for function <name> <locals> command
(main)
//set local to current stack pointer and increment the stack pointer by the amount of locals wanted
@SP
HLMLOAD
AB=HL

@LCL
M=A
HLINC
M=B

@SP1
A=0
M=A+M

OVERA
@main7.NO_OVERFLOW
JEQ
@SP
M=M+1
(main7.NO_OVERFLOW)

//--CALL--
//template assembly for the call <name> <args> command

//Make space for the new args on the stack, saving where they are to use later
//Save the current position on the stack
@SP
HLMLOAD
AB=HL

@TEMP
M=A
HLINC
M=B

//make space on the stack
@SP1
A=1
M=M+A

//check overflow
OVERA
@main8.NO_OVERFLOW
JEQ
@SP
M=M+1
(main8.NO_OVERFLOW)

//push our return flag label onto the stack
@main8.RETURN_FLAG
AB=HL

@SP
HLMLOAD
M=A
HLINC
M=B
HLINC

//Save current position
AB=HL
@SP
M=A
HLINC
M=B

//Push our current local, arg, this, and that onto the directory
//Since these values are close together, it makes a lot more sense just to loop them
@LCL
AB=HL

@TEMP2
M=B //spot for LCL in TEMP2

(main8.PUSH_LOOP)
//put whatever is currently meant to be put on the stack onto the stack
@TEMP2
LZ=M
HLMLOAD //get from what we want
AB=HL

@SP // put on stack
HLMLOAD
M=A
HLINC
M=B
HLINC

//Update stack pointer
AB=HL
@SP
M=A
HLINC
M=B

//Check loop condition
@TEMP2
A=2
M=M+A

//if TEMP2 = 10, stop loop
A=10
A=A-M
@main8.PUSH_LOOP
JNE

//set the new args to where it is in the stack
@TEMP
HLMLOAD
AB=HL

@ARG
M=A
HLINC
M=B

//Go to function label
@return5
JMP

//Return label
(main8.RETURN_FLAG)
//--POP--
//Template assembly for stack->memory page
//Lets say it is pop local 4

//This block should only appear if the page doesnt have a pointer (temp, static, thispoint, thatpoint)
@TEMP
AB=HL
@TEMP
M=A
@TEMP1
M=B

//setup pointer to the point in memory requested (local 4)
@TEMP1
A=5  //4 THIS VALUE TEMPLATED FOR DIFFERENT ADDR
M=A+M

//See if overflow
OVERA
@main9.PASTOVERFLOW
JEQ  //A will be = 0 if no overflow happened

//If we are here, overflow happened, we know it can only be an overflow, so we add 1 onto our temp H register
@TEMP
M=M+1
(main9.PASTOVERFLOW)


//Take the value off the top of the stack
@SP
HLMLOAD
HLDEC
A=M

//Now we have our pointer, lets get there with our value!
@TEMP
HLMLOAD 

M=A

//Finally, go back to update the stack pointer
@SP
HLMLOAD
HLDEC
AB=HL
@SP
M=A
@SP1
M=B
