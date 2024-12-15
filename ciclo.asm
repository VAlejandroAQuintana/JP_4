.model small
.stack
.data
mensaje1 db 10,13,"Suma   3   numeros   en   ciclo" , "$"
nbase dw 0
nsuma dw 0
conta dw 1
cf dw 3
mensaje2 db 10,13,"Ingrese   el   numero:   " , "$"
mensaje3 db 10,13,"El   resultado   es:   " , "$"

.code
INICIO: MOV AX, @DATA
        MOV DS, AX
        MOV ES, AX


MOV DX, offset mensaje1
MOV AH, 09H
INT 21H

L1:
MOV BX,conta
CMP cf,BX
JL L2

MOV DX, offset mensaje2
MOV AH, 09H
INT 21H

MOV AH,01
INT 21H
SUB AX,30H
MOV nbase,AX

MOV AX,nsuma
MOV BX,nbase
ADD AX,BX

MOV nsuma,AX

MOV AX,conta
MOV BX,1
ADD AX,BX

MOV conta,AX

JMP L1
L2:

MOV DX, offset mensaje3
MOV AH, 09H
INT 21H


MOV DX,nsuma
ADD DX,30H
MOV AH,02
INT 21H

MOV AX,nbase
MOV BX,nsuma
MUL BX
MOV BX,cf
MUL BX
ADD AX,BX

MOV nbase,AX


FIN: MOV AX,4C00H
     INT 21H
     END
