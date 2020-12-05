
# currently set to use the Sun compiler
# switch the default to "gcjcompile" to use gcj to compile to object code

default: javacompile

gcjcompile:
	gcj --main=ChatClient -o ChatClient *.java
	gcj --main=ChatServer -o ChatServer *.java

javacompile:
	javac *.java

spotless: clean
	rm -f ChatClient ChatServer

clean: tidy
	rm -f *.class

tidy: 
	rm -f *~

