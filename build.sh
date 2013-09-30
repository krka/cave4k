rm -rf build 
mkdir -p build
rm -rf obf
mkdir -p obf

echo Compiling...
javac -d build -g:none src/Main.java
echo Done!
ls -la build/Main.class

echo Running proguard...
/home/krka/android/android-studio/sdk/tools/proguard/bin/proguard.sh @proguard.pro
ls -la obf/Main.class


echo Zipping...
(cd obf; zip -9 cave4k.jar Main.class)
ls -la obf/cave4k.jar


