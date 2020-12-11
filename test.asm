DATA SEGMENT
	a DD
	b DD
	aux DD
	a DD
	b DD
DATA ENDS
CODE SEGMENT
	mov eax, input
	mov a, eax
	push eax
	pop eax
	mov eax, input
	mov b, eax
	push eax
	pop eax
	START_WHILE_1:
	push eax
	pop ebx
	sub eax, ebx
	jle FALSE_GT_2
	mov eax, 1
	jmp END_GT_2
	FALSE_GT_2:
	mov eax, 0
	END_GT_2:
	jz END_WHILE_1
	jmp START_WHILE_1
	END_WHILE_1:
	pop eax
CODE ENDS
