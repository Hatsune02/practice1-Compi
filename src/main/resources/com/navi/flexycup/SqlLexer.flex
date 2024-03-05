package com.navi.backend.flexycup;
import java_cup.runtime.*;
import static com.navi.backend.flexycup.sym.*;
import com.navi.backend.csv_controller.*;
import java.util.ArrayList;
%% //separador de area

%public
%class SqlLexer
%cup
%line
%column


LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]
Digit = [0-9]+
Identifier = [a-zA-Z0-9_@+#*-]+
path = {Identifier}("."{Identifier})+
Cadena = \"([^\"]*)\" | ”([^\"]*)”
RelatedOperations = "!=" | "<>" | "<=" | ">=" | "=" | "<" | ">"

%{
    public static ArrayList<TError> errors = new ArrayList<>();

    private Symbol symbol(int type){
        return new Symbol(type, yyline+1,yycolumn+1);
    }
    private Symbol symbol(int type, Object value){
        return new Symbol(type, yyline+1, yycolumn+1, value);
    }
    private void error(){
        System.out.println("Error en linea: "+(yyline+1)+", columna: "+(yycolumn+1));
        TError err = new TError(yytext(), "Error Léxico", "Símbolo inválido", yyline+1, yycolumn+1);
        errors.add(err);
    }
%}

%%
//Reglas lexicas

    ";"                     { return symbol(P_COMA, yytext());        }
    ","                     { return symbol(COMA, yytext());          }
    "*"                     { return symbol(ASTERISCO, yytext());     }
    "("                     { return symbol(PARENT_1, yytext());      }
    ")"                     { return symbol(PARENT_2, yytext());      }
    "AND"                   { return symbol(AND, yytext());           }
    "OR"                    { return symbol(OR, yytext());            }
    "SELECCIONAR"           { return symbol(SELECCIONAR, yytext());   }
    "FILTRAR"               { return symbol(FILTRAR, yytext());       }
    "INSERTAR"              { return symbol(INSERTAR, yytext());      }
    "ACTUALIZAR"            { return symbol(ACTUALIZAR, yytext());    }
    "ASIGNAR"               { return symbol(ASIGNAR, yytext());       }
    "ELIMINAR"              { return symbol(ELIMINAR, yytext());      }
    "EN"                    { return symbol(EN, yytext());            }
    "VALORES"               { return symbol(VALORES, yytext());       }
    {path}                  {return symbol(PATH, yytext());  }
    {Digit}                 { return symbol(DIGIT, yytext());         }
    {Cadena}                { return symbol(CADENA, yytext());        }
    {Identifier}            { return symbol(ID, yytext());            }
    "="                     { return symbol(EQUAL, yytext());         }
    {RelatedOperations}     { return symbol(REL_OP, yytext());        }
    {WhiteSpace}            { /**/ }

[^]                 {error(); }


<<EOF>>             {return symbol(EOF); }