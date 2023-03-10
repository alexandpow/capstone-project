// Adds 1 + ... + 10
@i
M=1 // i=1
@sum
M=0 // sum=0
(LOOP)
@i
B=M // B=i
A=10
A=B-A // A=i-10
@END
JGR // if (i-10)>0 goto END
@i
A=M // B=i
@sum
M=M+A // sum=sum+i
@i
M=M+1 // i=i+1
@LOOP
JMP // goto LOOP
(END)
@sum //load in our sum to see it
@END
JMP
