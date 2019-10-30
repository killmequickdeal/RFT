JC	     = javac
CLASSPATH = -cp src
OBJECTS = src/*.java
TARGET  = classes
CLASS   = $(shell find . -type f -name "*.class" | tr '\n' ' ' | sed 's/\$$/\\$$/g')
.SUFFIXES: .java .class

.java.class:
	$(JC) $(JCFLAGS) $*.java 

all: $(TARGET)

$(TARGET): $(OBJECTS:.java=.class)


run-server:
		java $(RUNFLAGS) $(CLASSPATH) Server

run-client:
		java $(RUNFLAGS) $(CLASSPATH) Client

clean:
		$(RM) $(CLASS)
