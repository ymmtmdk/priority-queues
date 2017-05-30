# CP=lib/algs4.jar:build/classes/main:src/main/java
CP=lib/algs4.jar:src/main/java
# java -Xrunhprof:cpu=times,file=cpu_times.txt -cp "$CP" Solver ./8puzzle/puzzle04.txt
java -cp "$CP" -Xrunhprof:cpu=times,file=cpu_times.txt Solver ./8puzzle/puzzle4x4-23.txt
