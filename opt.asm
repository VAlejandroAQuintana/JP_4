.model small
.stack
.data
mensaje1 db 10,13,"multiplicar" , "$"
V dw 0
nuno dw 5
ndos dw 0

.code
INICIO: MOV AX, @DATA
        MOV DS, AX
        MOV ES, AX


MOV DX, offset mensaje1
MOV AH, 09H
INT 21H


MOV ndos,AX

MOV AX,nuno
MOV BX,5
ADD AX,BX

MOV ndos,AX

MOV AX,ndos
MOV BX,2
ADD AX,BX

MOV ndos,AX


MOV ndos,AX


FIN: MOV AX,4C00H
     INT 21H
     END
