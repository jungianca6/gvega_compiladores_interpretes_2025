grammar Simple;

prog:   'hola' ID ;
ID:     [a-zA-Z]+ ;
WS:     [ \t\r\n]+ -> skip ;