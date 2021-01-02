DATA SEGMENT
	a DD
	b DD
	aux DD
	a DD
	b DD
DATA ENDS
CODE SEGMENT
	in eax
	mov a, eax
	in eax
	mov b, eax
START_WHILE_1:
	mov eax, 0
	push eax
	mov eax, b
	pop ebx
	sub eax, ebx
	jle FALSE_GT_1
	mov eax, 1
	jmp END_GT_1
FALSE_GT_1:
	mov eax, 0
END_GT_1:
	jz END_WHILE_1
	jmp START_WHILE_1
END_WHILE_1:
	mov eax, a
	out eax
CODE ENDS
