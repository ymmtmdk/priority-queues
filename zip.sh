srcs="Board.java Solver.java"
cd src/main/java
cp $srcs ../../../
cd ../../../
zip submit.zip $srcs
rm $srcs
