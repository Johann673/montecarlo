JFLAGS = -encoding ISO-8859-1 -g
JC = javac
RUN = java
.SUFFIXES: .java .class
.java.class:l
		$(JC) $(JFLAGS) $*.java
		
CLASSES = \
		Type.java \
		Main.java \
		Mesures.java
		

default: classes

compile: classes

classes: $(CLASSES:.java=.class)

clean: 
		$(RM) *.class

tests: classes
	${RUN} Main 1.2.3.4.5.6 10000000 0 100
	
	
mesures: classes
	${RUN} Mesures
		