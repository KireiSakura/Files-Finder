# Files-Finder
Command line tool to find files in the current directory and/or its subfolders. Also supports regex and extensions.

Compile: javac FilesFinder.java

Usage: java -reg [Regex] [or] [FileName] -ext [extension, e.g. "txt"]

OpenJDK version: "11.0.8" 2020-07-14
Gradle version: "6.6.1"

-ext option clarification:
When a ext option is specified and the -reg option is not specified, then the arguments of the -ext option are appended
to the file name and search is done for the files with -ext option arguments appended to the filename as extension.
For example, for a command like: java "hello.txt" -ext "txt,pdf", then FindFiles will look for the files "hello.txt.txt" and "hello.txt.pdf"
in the current directory.
