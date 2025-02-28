package Assignment3;

import java.io.*;
import java.util.*;

public class InfixToPostfix {

    private static int precedence(String operator) {
        switch (operator) {
            case "==": case "!=": return 1;
            case "<": case ">": case "<=": case ">=": return 2;
            case "+": case "-": return 3;
            case "*": case "/": return 4;
            case "^": return 5;
            case "||": return 6;
            default: return 0;
        }
    }

    public static boolean isValidInfix(String expr) {
        Stack<Character> stack = new Stack<>();
        boolean lastWasOperator = true;
        boolean lastWasOpenParen = false;
        boolean foundOperand = false;
        String allowedOperators = "+-*/^><=!";
        List<String> tokens = tokenize(expr);

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            if (token.matches("[a-zA-Z0-9]+")) {  // Operand (letters or numbers)
                lastWasOperator = false;
                foundOperand = true;
            } else if (token.equals("(")) {
                stack.push('(');
                lastWasOpenParen = true;
                lastWasOperator = true;
            } else if (token.equals(")")) {
                if (stack.isEmpty() || lastWasOperator) return false;  // Unbalanced or empty parentheses
                stack.pop();
                lastWasOperator = false;
            } else {  // Operator
                if (lastWasOperator && !lastWasOpenParen) return false;  // Consecutive operators (invalid)
                lastWasOperator = true;
                lastWasOpenParen = false;
            }
        }

        return stack.isEmpty() && !lastWasOperator && foundOperand; // Balanced, ends with operand
    }

    public static String infixToPostfix(String expression) {
        StringBuilder output = new StringBuilder();
        Stack<String> stack = new Stack<>();
        List<String> tokens = tokenize(expression);

        for (String token : tokens) {
            if (token.matches("[a-zA-Z0-9]+")) {  // Operand
                output.append(token).append(" ");
            } else if (token.equals("(")) {  
                stack.push(token);
            } else if (token.equals(")")) {  
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.append(stack.pop()).append(" ");
                }
                stack.pop();
            } else {  
                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(token)) {
                    output.append(stack.pop()).append(" ");
                }
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            output.append(stack.pop()).append(" ");
        }

        return output.toString().trim();
    }

    private static List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        String[] multiCharOps = {"==", "!=", ">=", "<=", "||"};
        String operators = "+-*/^()><=!";
    
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
    
            if (Character.isWhitespace(ch)) continue;
    
            if (Character.isLetterOrDigit(ch)) {
                token.append(ch);
            } else {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
    
                if (i < expression.length() - 1) {
                    String twoCharOp = "" + ch + expression.charAt(i + 1);
                    if (Arrays.asList(multiCharOps).contains(twoCharOp)) {
                        tokens.add(twoCharOp);
                        i++;
                        continue;
                    }
                }
    
                if (operators.indexOf(ch) != -1) {
                    tokens.add(String.valueOf(ch));
                }
            }
        }
    
        if (token.length() > 0) {
            tokens.add(token.toString());
        }
    
        return tokens;
    }

    // Read from file and process each expression
    public static void convertFile(String inputFile) {
        try {
            File file = new File(inputFile);
            if (!file.exists()) {
                System.err.println("Error: Input file not found!");
                return;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            int expressionNumber = 1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                System.out.println("Expression " + expressionNumber + ":");
                System.out.println("Infix exp: " + line);

                if (isValidInfix(line)) {
                    String postfixExpr = infixToPostfix(line);
                    System.out.println("Valid");
                    System.out.println("Postfix exp: " + postfixExpr);
                } else {
                    System.out.println("Not-Valid");
                }

                System.out.println();
                expressionNumber++;
            }

            reader.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter File: ");
        String filePath = scanner.nextLine();

        System.out.println("You entered: '" + filePath + "'");

        File inputFile = new File(filePath);

        if (!inputFile.exists()) {
            System.out.println("Error: File does not exist.");
            return;
        }

        if (!inputFile.isFile()) {
            System.out.println("Error: The path is not a file.");
            return;
        }

        convertFile(filePath); 
    }
}
//The file that I used = C:\\Users\\CAMT-STD\\Downloads\\input1.csv
