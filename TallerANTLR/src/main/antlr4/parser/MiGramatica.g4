grammar MiGramatica;

prog:   'hola' ID ;
ID:     [a-zA-Z]+ ;
WS:     [ \t\r\n]+ -> skip ;