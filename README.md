Bollon Hugo
<h1 align="center">TP Compilation : Génération d'arbres abstraits et conversion en code assembleur</h1>

À partir de l'arbre abstrait construit lors du dernier TP, avec les outils JFlex et CUP, l'objectif consiste à générer du code pour la machine à registres décrite dans le cours, afin d'être en mesure d'exécuter les programmes reconnus par l'analyseur sur la machine à registres.

## Utilisation 

Pour utiliser ce programme il faut suffit de le lancer dans un ide (comme Eclipse par exemple) ou de l'éxecuter avec maven.
Vous pourrez ensuite entrer via le terminal du code, il le convertira en arbre et l'affichera dans le terminal sous forme de string lors qu'il rencontrera un point.
Lors que vous fermerez le parser (Ctrl+D sur Eclipse), il génèrera un fichier assembleur (test.asm à la racine du projet).

## Exemples 

Code entré:
```ada
let prixHt = 200;
let prixTtc =  prixHt * 119 / 100 .
```

Sortie dans le terminal:
```
(; (let prixHt  200 ) (let prixTtc  (/ (* prixHt  119 ) 100 )))
```

Code généré:
```asm
DATA SEGMENT
	prixTtc DD
	prixHt DD
DATA ENDS
CODE SEGMENT
	mov eax, 200
	mov prixHt, eax
	mov eax, prixHt
	push eax
	mov eax, 119
	pop ebx
	mul eax, ebx
	push eax
	mov eax, 100
	pop ebx
	div eax, ebx
	mov prixTtc, eax
CODE ENDS
```

---

Code entré:
```ada
let a = input;
let b = input;
while (0 < b)
do (let aux=(a mod b); let a=b; let b=aux );
output a
.
```

Sortie dans le terminal:
```
(; (let a  input ) (; (let b  input ) (; (while (< 0  b ) (do (; (let aux  (mod a  b )) (; (let a  b ) (let b  aux ))))) (output a ))))
```

Code généré:
```asm
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

```