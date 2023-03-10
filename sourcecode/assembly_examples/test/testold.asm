//Test immediate loading
//Every STOP is a point where the upper level runner will compare and assume all things are working
A=1
B=2
M=3
H=4
L=5
STOP

LZ=6 //Bigger assignments
AB=7
STOP

HLILOAD, 8, 9 //Special assignment
STOP

@15 //@ assignment
STOP
@5000
STOP

//All assignments assumed working by this point
//Test comp statements
A=1
B=4
A=A //NOP
@0
M=A //@0 = 1

A=!A //flip all bits (11111110,-2 or 254 )
@1
M=A //@1 = -2 or 254

A=-A
@2
M=A //@2 = 2

A=A+1
@3
M=A //@3 = 3

A=A-1
@4
M=A //@4 = 2

A=A+B
@5
M=A //@5 = 6

A=A-B
@6
M=A //@6 = 2

A=A&B 
@7
M=A //@7 = 0

A=A|B
@8
M=A //@8 = 6

STOP //Compare
//All comp statements assumed working here