package com.navi.backend.flexycup;
import java_cup.runtime.*;
import static com.navi.backend.flexycup.sym.*;

%% //separador de area

%class SqlLexer
%cup
%line
%column

LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]
//Identifier = [:jletter:][:jletterdigit]*
entero = [0-9]

%{
    StringBuilder string = new StringBuilder();
    private Symbol symbol(int type){
        return new Symbol(type, yyline+1,yycolumn+1);
    }
    private Symbol symbol(int type, Object value){
        return new Symbol(type, yyline+1, yycolumn+1, value);
    }
    private void error(){
        System.out.println("Error en linea: "+(yyline+1)+", columna: "+(yycolumn+1));
    }
%}

%%
//Reglas lexicas

    ";"             { return symbol(P_COMA, yytext()); }
    {entero}          { return symbol(ENTERO, Integer.valueOf(yytext())); }
//    Identifier      { return symbol(ID, yytext());  }
//    "="             { return symbol(IGUAL); }
    "+"             { return symbol(SUMA, yytext()); }
    "-"             { return symbol(RESTA, yytext()); }
    {WhiteSpace}      { /*return symbolx(COMA);*/ }

[^]                 {throw new RuntimeException("Illegal symbol");}


<<EOF>>             {return symbol(EOF); }