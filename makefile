all: moesi

moesi: MOESI.java
	javac MOESI.java

run: moesi
	java MOESI

clean:
	rm -rf *.class
