#Distributed-Computing-Project-2 (File Transfer)
From the directory this README is in the following commands can be used

####make 
Build the .class files for all java files 
####make clean 
Clean up .class files, etc. 
####make run-server
run the server (should be ran on in-csci-rrpc02.cs.iupui.edu 10.234.136.56)
####make run-client 
run the client (can be done on any server)
 
defaulted to look for the server running on:

Server: in-csci-rrpc02.cs.iupui.edu 10.234.136.56)

Port: 7555

Examples:
--
#####[rjdeal@in-csci-rrpc02 RFT]$ make

javac  src/*.java

#####[rjdeal@in-csci-rrpc02 RFT]$ make clean

rm -f ./src/Client.class ./src/Utility.class ./src/Server.class

