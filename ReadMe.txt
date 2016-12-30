Description:
This project is the implementation the MOESI protocol between three caches.

Detailed Working:
There are three caches numbered: 0,1,2
Four lines with address 0,1,2,3

Input format
The program will read input from stdin.  Each line of input will have the following three columns:
Cache number (0, 1, 2)
Command (r for read, w for write)
Line number (0, 1, 2, 3)

Example Command: 1r3

This command tells cache 1 to read from line 3. Based on the availability of the line in the cache, more than one message might be sent from the requesting cache to other caches.

Output format

Initially all lines are considered to be invalid in all caches.
Below is an example of an 0r0 command

The output for this command will be:

0r0
Cache 0, Miss 0
Cache 1, Probe Read 0
Miss
I -> I
End Probe Read

Cache 2, Probe Read 0
Miss
I -> I
End Probe Read
Cache 0
I -> E

Deliverables:
1. makefile
2. MOESI.java

Instructions to execute:
1. make 						      (This will compile the program)
2. make run							  (This will run the MOESI.class)
3. make clean 						  (Optional : This will clean compiled .class files)

Execution:
1. The program will ask user to input the command and print the cache status accordingly. 
2. Press any alphabet to exit the program.

Notes: 
Tested the program on Remote Server (remote.cs.binghamton.edu)