import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

 class CToIntermediateCodeConverter {
    private static int tempCounter = 1;
    public static int labelCounter = 0;
    public static String llaveG="close";
    public static String llaveG2="close";
    public static String llaveG3="close";
    public static String llaveG4="close";
    public static String insideIfBlock="false";
    public static String insideElseBlock="false";

    public static void convertir(String ar) {
        try {
            // Verificar si el archivo "intermedio.txt" ya existe y eliminarlo si es así
            Path path = Paths.get("intermedio.txt");
            if (Files.exists(path)) {
                Files.delete(path);
            }

            // Crear un nuevo archivo "intermedio.txt"
            Files.createFile(path);

            // Obtener los cuádruplos
            List<String> tacCode = convertToTAC(ar);

            // Escribir los cuádruplos en el archivo "intermedio.txt"
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("intermedio.txt"))) {
                for (String quadruple : tacCode) {
                    writer.write(quadruple);
                    writer.newLine();
                }
            }

            // Imprimir los cuádruplos en la consola
            
                System.out.println("Intermedio generado.");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public static List<String> convertToTAC(String fileName) throws IOException {
    List<String> quadruples = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        String line;
        while ((line = reader.readLine()) != null) {
            List<String> lineQuadruples = convertLineToQuadruples(line);
            quadruples.addAll(lineQuadruples);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Optimización de cuádruplos
    quadruples = optimizeQuadruples(quadruples);
    return quadruples;
    }

    public static List<String> optimizeQuadruples(List<String> quadruples) {
    List<String> optimizedQuadruples = new ArrayList<>();

    //String bkQuadruple="";
    String bkOperand1="";
    int flag=0;
    for (String quadruple : quadruples) {
        // Extraer componentes del cuádruplo
        String[] parts = quadruple.replace("(", "").replace(")", "").split(", ");
        if (parts.length == 4) {
            String operator = parts[0];
            String operand1 = parts[1];
            String operand2 = parts[2];
            String result = parts[3];

            //System.out.println(operator + operand1 + operand2 + result);

            // Verificar si es redundante: R = R + 0 o R = R * 1
            if ((operator.equals("+") && operand2.equals("0")) ||
                (operator.equals("*") && operand2.equals("1")) ||
                (operator.equals("-") && operand2.equals("0")) ||
                (operator.equals("/") && operand2.equals("1"))) {
                flag=1;
                //bkQuadruple=quadruple;
                bkOperand1=operand1;
                continue;
                
            }
        }
        if(flag!=1){
            optimizedQuadruples.add(quadruple);
        }
        else{
            String operator = parts[0];
            String operand1 = parts[1];
            String operand2 = parts[2];
            String result = parts[3];

            if(result.equals(bkOperand1)){

            }
            else{
                quadruple="(=, "+bkOperand1+",  , "+result+")";
                optimizedQuadruples.add(quadruple);
            }
        }
        flag=0;
    }
    return optimizedQuadruples;
    }

   
    public static List<String> convertLineToQuadruples(String line) {
        String token = new String();
        StringTokenizer tokenizer = new StringTokenizer(line, "+-*/=(); ", true);
        List<String> quadruples = new ArrayList<>();
        Stack<String> operands = new Stack<>();
        Stack<String> operators = new Stack<>();
        Stack<String> labels = new Stack<>();
        Stack<String> jumpLabels = new Stack<>();
        boolean insidePrintfScan = false;
        boolean insideElseIfBlock = false;
    
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken().trim();

            //String TokenG=token;
            //System.out.println(TokenG);
            if (token.isEmpty()) continue;

            if(insideIfBlock.equals("true")&&insideElseBlock.equals("true")){
                insideIfBlock="false";
                insideElseBlock="false";
            }

            if (token.equals("float") || token.equals("int") || token.equals("string")){
                String dec = token;
                int c = 0;
                while(c==0){
                    token = tokenizer.nextToken().trim();
                    if(token.equals(";")){
                        c=1;
                    }
                    else{
                        dec = dec + " " + token;
                    }
                }
                quadruples.add(dec);

            }else if(token.equals("while")){
                //insideWhileBlock = "true";

                if(insideIfBlock.equals("true") && insideElseBlock.equals("false") && llaveG4.equals("open")){
                    String label = "L" + labelCounter;
                    quadruples.add(label + ":");
                    labelCounter++;
                    insideIfBlock="false";
                    llaveG4="close";
                }

                String condition = "";
                while (tokenizer.hasMoreTokens()) {
                    String nextToken = tokenizer.nextToken().trim();
                    if (nextToken.equals("(")) break;
                }
                while (tokenizer.hasMoreTokens()) {
                    String nextToken = tokenizer.nextToken().trim();
                    if (nextToken.equals(")")) break;
                    condition += nextToken;
                }

                //Fragmento para invertir el orden de la condicional
                String cond1="", cond2="", opcond="";
                int c=0;
                for (int i = 0; i <= condition.length()-1; i++) {
                    if(condition.charAt(i) == '>' ||condition.charAt(i) == '<'){
                        i++;
                        if(condition.charAt(i) == '='){
                            i--;
                            opcond=opcond + condition.charAt(i);
                            i++;
                            opcond=opcond + condition.charAt(i);
                        }
                        else{
                            i--;
                            opcond=opcond + condition.charAt(i);
                        }
                        c=i+1;
                        break;
                    }

                    if(condition.charAt(i) == '='){
                        opcond=opcond + "!";
                        i++;
                        opcond=opcond + "=";
                        c=i+1;
                        break;
                    }

                    if(condition.charAt(i) == '!'){
                        opcond=opcond + "=";
                        i++;
                        opcond=opcond + "=";
                        c=i+1;
                        break;
                    }

                    cond1 = cond1 + condition.charAt(i);
                }

                for (int i = c; i <= condition.length()-1; i++) {
                    cond2 = cond2 + condition.charAt(i);
                }

                String conditionInv = cond2+opcond+cond1;

                labelCounter++;
                String label = "L" + labelCounter;
                quadruples.add(label + ":");

                int SndlabelCounter = labelCounter+1;
                String trueLabel = "L" + SndlabelCounter;
                quadruples.add("if " + conditionInv + " goto " + trueLabel);

                llaveG3="open";

            }else if (token.equals("if")) {

                if(insideIfBlock.equals("true") && insideElseBlock.equals("false") && llaveG4.equals("open")){
                    String label = "L" + labelCounter;
                    quadruples.add(label + ":");
                    labelCounter++;
                    insideIfBlock="false";
                    llaveG4="close";
                }

                insideIfBlock = "true";
                String condition = "";
                while (tokenizer.hasMoreTokens()) {
                    String nextToken = tokenizer.nextToken().trim();
                    if (nextToken.equals("(")) break;
                }
                while (tokenizer.hasMoreTokens()) {
                    String nextToken = tokenizer.nextToken().trim();
                    if (nextToken.equals(")")) break;
                    condition += nextToken;
                }

                //Fragmento para invertir el orden de la condicional
                String cond1="", cond2="", opcond="";
                int c=0;
                for (int i = 0; i <= condition.length()-1; i++) {
                    if(condition.charAt(i) == '>'){
                        i++;
                        if(condition.charAt(i) == '='){
                            i--;
                            opcond=opcond + ">";
                            i++;
                            opcond=opcond + condition.charAt(i);
                        }
                        else{
                            i--;
                            opcond=opcond + ">";
                        }
                        c=i+1;
                        break;
                    }

                    if(condition.charAt(i) == '<'){
                        i++;
                        if(condition.charAt(i) == '='){
                            i--;
                            opcond=opcond + "<";
                            i++;
                            opcond=opcond + condition.charAt(i);
                        }
                        else{
                            i--;
                            opcond=opcond + "<";
                        }
                        c=i+1;
                        break;
                    }

                    if(condition.charAt(i) == '='){
                        opcond=opcond + "!";
                        i++;
                        opcond=opcond + "=";
                        c=i+1;
                        break;
                    }

                    if(condition.charAt(i) == '!'){
                        opcond=opcond + "=";
                        i++;
                        opcond=opcond + "=";
                        c=i+1;
                        break;
                    }

                    cond1 = cond1 + condition.charAt(i);
                }

                for (int i = c; i <= condition.length()-1; i++) {
                    cond2 = cond2 + condition.charAt(i);
                }

                String conditionInv = cond2+opcond+cond1;

                labelCounter++;
                String trueLabel = "L" + labelCounter;
                quadruples.add("if " + conditionInv + " goto " + trueLabel);
                llaveG="open";
                llaveG2="open";
                 // Push falseLabel to jump to else block

            } else if (token.equals("else{")) {
                insideElseBlock = "true";
                llaveG="open";
                llaveG4="close";

            } else if (token.equals("else if")) {


            } else if (token.equals("printf") || token.equals("scan") || token.equals("scans")) {

                if(insideIfBlock.equals("true") && insideElseBlock.equals("false") && llaveG4.equals("open")){
                    String label = "L" + labelCounter;
                    quadruples.add(label + ":");
                    labelCounter++;
                    insideIfBlock="false";
                    llaveG4="close";
                }

                insidePrintfScan = true;
                String arg = "";
                while (tokenizer.hasMoreTokens()) {
                    String nextToken = tokenizer.nextToken().trim();
                    if (nextToken.equals("(")) break;
                }
                while (tokenizer.hasMoreTokens()) {
                    String nextToken = tokenizer.nextToken().trim();
                    if (nextToken.equals(")")) break;
                    arg = arg+" "+nextToken;
                }
                quadruples.add(token + "(" + arg + ")");
                insidePrintfScan = false;
            } else if (insidePrintfScan) {
                continue; // Skip tokens inside printf or scan arguments
            } else if (token.matches("[a-zA-Z0-9]+") || token.matches("[0-9]+")) { // If token is a variable or constant

                if(insideIfBlock.equals("true") && insideElseBlock.equals("false") && llaveG4.equals("open")){
                    String label = "L" + labelCounter;
                    quadruples.add(label + ":");
                    labelCounter++;
                    insideIfBlock="false";
                    llaveG4="close";
                }

                operands.push(token);
            } else if (token.matches("[+\\-*/=]")) { // If token is an operator or assignment
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    String op2 = operands.pop();
                    String op1 = operands.pop();
                    String result = "t" + tempCounter++;
                    quadruples.add(constructQuadruple(operators.pop(), op1, op2, result));
                    operands.push(result);
                }
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    String op2 = operands.pop();
                    String op1 = operands.pop();
                    String result = "t" + tempCounter++;
                    quadruples.add(constructQuadruple(operators.pop(), op1, op2, result));
                    operands.push(result);
                }
                if (!operators.isEmpty() && operators.peek().equals("(")) {
                    operators.pop(); // Discard the left parenthesis
                } else {
                    // Handle unbalanced parentheses error
                    System.err.println("Error: Unbalanced parentheses in the expression.");
                    return null;
                }
            }
        }

        while (!operators.isEmpty()) {
            String operador = operators.pop();
            if(operador.equals("=")){
                String op2 = " ";
                String op1 = operands.pop();
                String result = operands.pop();
                quadruples.add(constructQuadruple(operador, op1, op2, result));
                operands.push(result);
            }
            else{
                String op2 = operands.pop();
                String op1 = operands.pop();
                String result = "t" + tempCounter++;
                quadruples.add(constructQuadruple(operador, op1, op2, result));
                operands.push(result);
            }
        }

        if(token.equals("}")){
            llaveG4="open";
            if(llaveG3.equals("open")) {
                quadruples.add("goto " + "L" + labelCounter);
                int SndlabelCounter=labelCounter+1;
                String label = "L" + SndlabelCounter;
                quadruples.add(label + ":");
                labelCounter=labelCounter+1;
                llaveG3="close";
                llaveG4="close";
            }
            if(llaveG2.equals("open")) {
                int SndlabelCounter=labelCounter+1;
                quadruples.add("goto " + "L" + SndlabelCounter);
                llaveG2="close";
            }
            /*if(llaveG4.equals("open")) {
                String label;
                if(insideWhileBlock.equals("true")){
                    int SndlabelCounter=labelCounter-2;
                    label = "L" + SndlabelCounter;
                    insideWhileBlock="false";
                }else{
                    label = "L" + labelCounter;
                }
                quadruples.add(label + ":");
                labelCounter++;
                llaveG4="close";
            }*/
            if(llaveG.equals("open")) {
                String label = "L" + labelCounter;
                quadruples.add(label + ":");
                labelCounter++;
                llaveG="close";
            }
        }

        /*// Add labels for the end of if-else and else if blocks
        while (!labels.isEmpty()) {
            System.out.println(token);
            String label = labels.pop();
            quadruples.add(label + ":");
        }*/


        /*if(insideIfBlock.equals("true") && insideElseBlock.equals("false") && llaveG4.equals("open")){
            String label = "L" + labelCounter;
            quadruples.add(label + ":");
            labelCounter++;
            insideIfBlock="false";
            llaveG4="close";
        }*/
    
        return quadruples;
    }
    
    
    
  
    private static int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return 0;
        }
    }

    private static String constructQuadruple(String operator, String operand1, String operand2, String result) {
        return "(" + operator + ", " + operand1 + ", " + operand2 + ", " + result + ")";
    }
}