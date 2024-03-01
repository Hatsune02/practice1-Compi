#! /bin/bash
echo "STARTING JFLEX COMPILING"
java -jar /home/dog/flexycup/jflex-full-1.9.1.jar -d ../../../../java/com/navi/backend/flexycup/ SqlLexer.flex

echo "STARTING CUP COMPILING"
java -jar /home/dog/flexycup/java-cup-11b.jar -parser SqlParser SqlParser.cup
mv SqlParser.java ../../../../java/com/navi/backend/flexycup/SqlParser.java
mv sym.java ../../../../java/com/navi/backend/flexycup/sym.java