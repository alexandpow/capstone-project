//Sample assembly program for doing a pop operation for the purposes of creating a template
//Lets say it is pop local 4

//Take the value off the top of the stack
@SP
HLDEC
A=M

//Store for later
@TEMP0
M=A

@LCL //--! THIS @ TEMPLATED FOR DIFFERENT MEMORY SEGMENTS
HLMLOAD //Load local pointer into temp space for modification
AB=HL
@TEMP1
M=A
@TEMP2
M=B

//setup pointer to the point in memory requested (local 4)
@TEMP2
B=4  //--!THIS VALUE TEMPLATED FOR DIFFERENT ADDR
M=B+M

//See if overflow
OVERA
@PASTOVERFLOW
JNE  //A will be = 0 if no overflow happened

//If we are here, overflow happened, we know it can only be an overflow, so we add 1 onto our temp H register
@TEMP1
M=M+1
{PASTOVERFLOW}

//Now we have our pointer, lets get there with our value!
@TEMP0
A=M
@TEMP1
HLMLOAD 

//Finally, go back for 