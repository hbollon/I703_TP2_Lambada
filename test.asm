DATA SEGMENT
	a DD
	b DD
	aux DD
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
	mov eax, b
	push eax
	mov eax, a
	pop ebx
	mov ecx, eax
	div ecx, ebx
	mul ecx, ebx
	sub eax, ecx
	mov aux, eax
	mov eax, b
	mov a, eax
	mov eax, aux
	mov b, eax
	jmp START_WHILE_1
END_WHILE_1:
	mov eax, a
	out eax
CODE ENDS
