# Compiler

This program can compile code written in a language called "C*16"* which is defined specifically 
for a univerity course. Some sample inputs have been included with the project.

 Usage:
java [main class] [options] [input file(s)]

Option Listing:     
-h | -help     -- Displays this help menu     
-l | -lex      -- Process up to the Lexer phase   
-p | -parse    -- Process up to the Parser phase    
-s | -sem      -- Process up to the Semantic Analysis phase
-t | -tup      -- Process up to the Tuple phase     
-c | -compile  -- Process all phases and compile (default behavior)     
-q | -quiet    -- Only display error messages (default behavior)     
-v | -verbose  -- Display all trace messages     
-e | -err      -- Error file (See -o | -out for details)     
-o | -out      -- Output file →  takes the next argument as the destination output file for     the compilation of the input file.
