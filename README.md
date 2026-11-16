# Spreadsheet project

## How to run
- Open `pom.xml` with IntelliJ (File > Open, "Open as Project").
- Run `src/main/java/spreadsheet/Main.java`. The menu appears in the Run window.
- Run the tests: right click `src/test/java` > Run 'All Tests' (or `mvn test`).

The spreadsheets are saved in the folder `spreadsheets/` as `.s2v` files (there are two examples).

## What is done
1. Spreadsheet data structure (2D array) that stores numbers, text and formulas. The formulas are only stored, not calculated.
2. Load and store in S2V format.
3. Expression tree with the Composite pattern: `Expression` (interface), `NumberExpression` (leaf),
   `Addition`, `Subtraction`, `Multiplication`, `Division` (composites). The trees are built by hand.
4. JUnit tests: `ExpressionTest` (simple and nested expressions, precedence, parentheses),
   `SpreadsheetTest`, `S2VFileTest`.

## What is next
- Parse the formulas into expression trees (cell references, SUMA, MIN, MAX, PROMEDIO...).
- Recalculate the cells when a cell changes, and detect circular dependencies.
