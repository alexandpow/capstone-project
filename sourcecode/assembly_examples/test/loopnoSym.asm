//Example, adding 1 +2+...+10
//STEP 1 SETUP
	@16
	M = 1 //i=1
	@17
	M = 0 //sum = 0
//STEP 2 LOOP CONDITION
(LOOP)
	@16
	D=0
	D=D+M //D = i
	@10
	D=A
	@17
	M=D
	@16
	D=0
	D=D+M
	@17
	D=D-M  //D=i-10
	@28
	D;JGT //if (i-10)>0 goto END
//STEP 3 ADD TO SUM
	@16
	D=0
	D=D+M //D=i
	@17
	M=D+M //sum=sum+i
	@16
	D=1
	M=D+M //i=i+1
	@4
	0;JMP
//STEP 4 END
(END)
	@28
0;JMP