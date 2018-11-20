CPEN 221 / Lab 11

Grammars and Parsing using ANTLR
====

The goal of this lab activity is to go over the concepts of grammars and parsing, as well as to give you experience using the parser generator ANTLR.

### Exercise 1: Parse and evaluate a simple Boolean Expression

Lets start with expressions which only contain: 

+ `AND` operator (denoted by the `&` symbol) 
+ `True` and `False` literals (denoted by `T` and `F`, respectively.)

For example:

    T 
    T & T 
    T & F & T

How do we parse these expressions? Remember that we divide the procedure into three steps: 
+ characters &rarr; **Lexer** &rarr; tokens
+ tokens &rarr; **Parser** &rarr; concrete syntax tree
+ concrete syntax tree &rarr; **AST creation** &rarr; abstract syntax tree


What will be the inputs and outputs of each of these blocks while parsing input string `"T & T"`? 

#### Step 1. Design the Grammar for our Boolean Expression

Definitions in the grammar take the following form:

    <name> : <definition>;

Let's look at the `Formula.g4` file containing a grammar we will provide to ANTLR in order to generate a parser.

**Key points to notice**: 

 - How to tell parser generator to skip white spaces  
   `WHITESPACE : [ \t\r\n]+ -> skip ;`
 - Difference between lexical rules and parser rules
   - Lexical rules define the tokens
   - Parser rules define the semantics
 - CAPITALIZED vs. lowercase names 
   - CAPITALIZED names are used for lexical rules
   - lowercase names are used for parser rules, which refer to lexical terminals and other parser nonterminals
 - Root rule ends with EOF because we process the entire input sequence at once
 - The `.g4` file contains a bunch of boilerplate definitions (`@header` and `@members`) which you should not need to modify!

#### Step 2. Generate Lexer and Parser using ANTLR 

Compile the `Formula.g4` file to automatically generate a lexer and parser by following these instructions. 

 - Open up a terminal or a command prompt, and from the root folder for the project (the one that contains `build.gradle`), execute: `grade generateGrammarSource`
 - Notice that our `build.gradle` file has listed `antlr` as a plugin and a dependency on Antlr v4.
 - This operation should generate Java source code in the directory `build/generated-src/main/antlr`.
 - Copy the files generated too `src/main/java/formula`.

Key points to remember: 

 - Automatically generated files will have names starting with the name of the `.g4` file. 
 - **You should not edit the automatically-generated files.**
   ANTLR will overwrite them every time you run it.
   **Any changes you have made to these files will be lost.**

What can we expect as output from ANTLR? For our Formula grammar:

- `FormulaLexer.java`
- `FormulaParser.java`
- `FormulaListener.java` and `FormulaBaseListener.java`
- `Formula.tokens` and `FormulaLexer.tokens`

#### Step 3. Use the ANTLR-generated Lexer and Parser to Construct a Concrete Syntax Tree

Let's look at `FormulaFactory.java`, which already contains an implementation for `parse`:

 - Create a character stream from the input
 - Push the stream through the lexer
 - Create a token stream
 - Push that token stream through the parser
 - Ask the parser for the root grammar production (the one containing EOF)  
   Note: you could also ask the parser to parse smaller fragments by calling the appropriate method, e.g. in this case `conjunction()` or `literal()`

At this point we have a concrete parse tree, and we need to create an abstract syntax tree (AST).

#### Debugging the Parse Tree

How can we debug our parse tree?

1. Print it to the console 
2. Inspect it with the GUI
3. Walk down the tree with a listener 

ANTLR provides a way to do depth-first traversal of the parse tree in order to, for example, print out debugging info, or generate an AST.

 - `FormulaListener` is the interface we must implement in order to do the traversal
 - `FormulaBaseListener` is a convenience class which provides empty implementations of all the *callback methods*
 - We can create a subclass of `FormulaBaseListener` and override only the methods we care about with our own implementation

Look at `FormulaListener_PrintEverything`:

 - Running the code, we see what we would expect from a depth-first traversal
 - Using the context object passed to the callback, we can learn about the contents of the tree

*Callback methods* are called callbacks because we write some code, but do not call our method directly &mdash; instead, we pass our listener object to a `ParseTreeWalker`, and it "calls us back" when something we care about happens, e.g. it reaches a particular kind of node in the parse tree.

#### Step 4. Design an AST 
 
What is the recursive datatype definition for conjunctive formulas?

     Formula = BooleanLiteral(value:boolean) + And(left,right:Formula)

What sort of operations would we like to support on our AST?

 - toString : Formula &rarr; String  
   Why do we not add this to the interface? (All `Objects` implement `toString`)
 - evaluate : Formula &rarr; boolean

The recursive implementation for `evaluate()` is already given. 

+ Implement `toString()` for all AST classes. 
+ Does anything need to change in the `Formula` interface? 
+ Is your implementation recursive?

#### Step 5. Generate Abstract Syntax Tree from Concrete Syntax Tree (Stack-based AST generation)

We'll use a listener similar to the one designed for printing a parse tree to construct our AST. Look at `FormulaListener_FormulaCreator` in the file `FormulaFactory.java`.

How does this formula creator work? Let's walk through an example to understand how it uses the stack to construct an instance of `Formula` from the parser output.

For example: `T & F & T`

Draw an instance diagram to show how the stack changes over time:

`[]`  &rarr;  `[T]`  &rarr;  `[T, F]`  &rarr;  `[ And(T, F) ]`  &rarr;  `[ And(T, F), T ]`  &rarr;  `[ And(And(T, F), T) ]`

### Exercise 2: Extend the Implementation to Support Different Syntax for Literals

In addition to "T", we now also want to support "true" (and "false" for "F")

+ What needs to be changed? 

Lexer rules for:

 - `TRUE` e.g. `'T' | 'true'`
 - `FALSE` e.g. `'F' | 'false'`

+ What would we need to do now?  

 - Modify `Formula.g4`
 - Run ANTLR to re-generate the lexer and parser
 - Test with the new literals (in `main`, and in `FormulaFactoryTest`) 

### Exercise 3: Extend the Implementation to Support Negation of Literals

In addition to the binary AND operator, we now want to support the unary NOT operator applied to literals. 

Why not negation of conjunctions? For NOT to work for conjunctions, we need parentheses in our expression syntax.

What needs to be changed? 

 - Lexical rule for `NOT`, e.g. `NOT : '!';`
 - Should we add to the conjunction parse rule?  
   No, negation is not a conjunction
 - Should we add to the literal parse rule?  
   No, it's no longer a literal (which means just a single true or false)
 - Introduce a new production to capture the idea of elements in conjunctions that might be bare literals or negated literals  
   Call it e.g. `term`:  `term : literal | NOT literal ;`

What would we do next?

 - Make the changes to the grammar and re-run ANTLR
 - Modify our `FormulaListener_FormulaCreator`: listen for terms, and check for negation
 - Extend the data type to include a `Not` variant

When adding `Not`, what is the datatype definition for `Formula`?

If the child of a `Not` is a `Formula`, what does that allow us to represent that the parser cannot parse?

 - Negations of conjunctions! What might we choose to do when we encounter such cases?