//--PROGRAM START--
@SP
M=1 //stack starts at @256
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
@Global0.PASTOVERFLOW
JEQ  //A will be = 0 if no overflow happened

//If we are here, overflow happened, we know it can only be an overflow, so we add 1 onto our temp H register
@TEMP
M=M+1
(Global0.PASTOVERFLOW)


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
