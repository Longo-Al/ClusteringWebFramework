@echo off

REM Directory contenente i file sorgente
set SRC_DIR=C:\Users\Achil\Map_project_Lab2\src
REM Directory contenente i file di test
set TEST_DIR=C:\Users\Achil\Map_project_Lab2\test
REM Directory per i file compilati
set BIN_DIR=C:\Users\Achil\Map_project_Lab2\bin
REM Directory contenente le librerie
set LIB_DIR=C:\Users\Achil\Map_project_Lab2\lib

REM Crea la directory bin se non esiste
if not exist %BIN_DIR% mkdir %BIN_DIR%

REM Compila tutte le classi Java
javac -d %BIN_DIR% -cp %LIB_DIR%\junit-4.13.2.jar;%LIB_DIR%\hamcrest-core-1.3.jar %SRC_DIR%\defaultpackage\*.java %SRC_DIR%\clustering\*.java %SRC_DIR%\data\*.java %SRC_DIR%\distance\*.java %TEST_DIR%\clustering\*.java %TEST_DIR%\data\*.java %TEST_DIR%\distance\*.java

REM Esegui i test
java -cp %BIN_DIR%;%LIB_DIR%\junit-4.13.2.jar;%LIB_DIR%\hamcrest-core-1.3.jar org.junit.runner.JUnitCore test.clustering.ClusterTest

REM Esegui la classe principale
java -cp %BIN_DIR% defaultpackage.MainTest
