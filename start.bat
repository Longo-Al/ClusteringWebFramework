@echo off
setlocal

REM Percorso principale del progetto
set PROJECT_DIR=C:\Users\Achil\Map_project

REM Percorsi delle directory sorgente e binario
set SRC_DIR=%PROJECT_DIR%\src
set BIN_DIR=%PROJECT_DIR%\bin

REM Pulizia della directory bin
if exist %BIN_DIR% (rmdir /s /q %BIN_DIR%)
mkdir %BIN_DIR%

REM Compilazione delle classi
javac -d %BIN_DIR% %SRC_DIR%\defaultpackage\MainTest.java %SRC_DIR%\defaultpackage\Keyboard.java
javac -d %BIN_DIR% %SRC_DIR%\exceptions\*.java
javac -d %BIN_DIR% %SRC_DIR%\clustering\*.java
javac -d %BIN_DIR% %SRC_DIR%\data\*.java
javac -d %BIN_DIR% %SRC_DIR%\distance\*.java

REM Esecuzione del programma principale
java -cp %BIN_DIR% defaultpackage.MainTest

REM Esecuzione dei test
echo Running tests...
REM Aggiungi il percorso di JUnit al classpath (aggiorna il percorso di junit.jar come necessario)
set JUNIT_PATH=path\to\junit-4.13.2.jar;path\to\hamcrest-core-1.3.jar
javac -d %BIN_DIR% -cp %JUNIT_PATH%;%BIN_DIR% %SRC_DIR%\Test\clustering\*.java %SRC_DIR%\Test\data\*.java %SRC_DIR%\Test\distance\*.java
java -cp %JUNIT_PATH%;%BIN_DIR% org.junit.runner.JUnitCore Test.clustering.ClusterSetTest
java -cp %JUNIT_PATH%;%BIN_DIR% org.junit.runner.JUnitCore Test.clustering.ClusterTest
java -cp %JUNIT_PATH%;%BIN_DIR% org.junit.runner.JUnitCore Test.clustering.DendrogramTest
java -cp %JUNIT_PATH%;%BIN_DIR% org.junit.runner.JUnitCore Test.clustering.HierarchicalClusterMinerTest
java -cp %JUNIT_PATH%;%BIN_DIR% org.junit.runner.JUnitCore Test.data.DataTest
java -cp %JUNIT_PATH%;%BIN_DIR% org.junit.runner.JUnitCore Test.data.ExampleTest
java -cp %JUNIT_PATH%;%BIN_DIR% org.junit.runner.JUnitCore Test.distance.AverageLinkdistanceTest
java -cp %JUNIT_PATH%;%BIN_DIR% org.junit.runner.JUnitCore Test.distance.SingleLinkDistanceTest

endlocal
pause
