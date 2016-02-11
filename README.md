# Number Buffer

Hello :) 
These instructions are mostly for a UNIX-based system, so maybe you will come across a few commands that won't work on Windows. Please let me know if this a problem :)
Any feedback is welcome.

## Compiling

You can use ``mvn package`` straight from the application folder.
It only depends on the JUnit package for testing purposes.
It also runs the test suit.


## Arguments
Please provide the:
1. Number of the port.
2. Number of elements in the buffer.

## Running
From application folder:

```
  java -cp target/number-buffer-1.0-SNAPSHOT.jar br.com.biancarosa.buffer.Executor 8000 10

```
