
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

class ICToObject {
    private static int tempCounter = -1;
    public static int msjCounter = 1;
    public static int msjCounter2 = 1;

    public static void convertir(String ar) {
        try {
            // Verificar si el archivo "objeto.txt" ya existe y eliminarlo si es así
            Path path = Paths.get("objeto.txt");
            if (Files.exists(path)) {
                Files.delete(path);
            }

            Path path2 = Paths.get("declaraciones.txt");
            if (Files.exists(path2)) {
                Files.delete(path2);
            }

            // Crear un nuevo archivo "objeto.txt"
            Files.createFile(path);

            // Obtener el codigo objeto
            List<String> ObCode = convertToOb(ar);
            List<String> Declarations = writeDecs(ar);

            // Escribir los cuádruplos en el archivo "intermedio.txt"
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("objeto.txt"))) {
                for (String line : ObCode) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("declaraciones.txt"))) {
                for (String line : Declarations) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            // Imprimir los cuádruplos en la consola

            System.out.println("Objeto generado.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> writeDecs(String fileName) throws IOException {
        List<String> decs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String dec;
            while ((dec = reader.readLine()) != null) {
                List<String> lineObject2 = convertLineToDec(dec);
                decs.addAll(lineObject2);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return decs;
    }


    public static List<String> convertToOb(String fileName) throws IOException {
        List<String> lines = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String var;
            while ((var = reader.readLine()) != null) {
                List<String> stringObject = detectVarString(var);
                strings.addAll(stringObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*for (String var : strings) {
            System.out.println(var);
        }*/

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> lineObject = convertLineToObject(line, strings);
                lines.addAll(lineObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static List<String> detectVarString(String line) {
        String token = new String();
        StringTokenizer tokenizer = new StringTokenizer(line, "+-*/=() ", true);
        List<String> decs = new ArrayList<>();

        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken().trim();
            //String TokenG=token;
            //System.out.println(TokenG);
            if (token.isEmpty()) continue;

            if (token.equals("string")) {
                String nv = "";
                String vv = "";
                int c = 1;
                while (c <= 3) {
                    token = tokenizer.nextToken().trim();
                    c++;
                }
                nv = token;
                decs.add(nv);
            }

        }
        return decs;
    }

    public static List<String> convertLineToDec(String line) {
        String token = new String();
        StringTokenizer tokenizer = new StringTokenizer(line, "+-*/=() ", true);
        List<String> decs = new ArrayList<>();

        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken().trim();
            //String TokenG=token;
            //System.out.println(TokenG);
            if (token.isEmpty()) continue;

            if (token.equals("float") || token.equals("int")) {
                String nv = "";
                String vv = "";
                int c = 1;
                while (c <= 3) {
                    token = tokenizer.nextToken().trim();
                    c++;
                }
                nv = token;
                c = 1;
                while (c <= 6) {
                    token = tokenizer.nextToken().trim();
                    c++;
                }
                vv = token;
                decs.add(nv + " dw " + vv);
            }
            else if(token.equals("string")){
                String nv = "";
                String vv = "";
                int c = 1;
                while (c <= 3) {
                    token = tokenizer.nextToken().trim();
                    c++;
                }
                nv = token;
                decs.add(nv + " db 50,?,10 dup(' ')");
            }
            else if(token.equals("printf")){
                int c=1;
                while (c <= 3) {
                    token = tokenizer.nextToken().trim();
                    c++;
                }
                if(token.contains("\"")){
                    String msj="";
                    
                    int x=1;
                    while (x==1) {
                    	msj=msj+token+" ";
                    	token = tokenizer.nextToken().trim();
                    	if (token.equals(")")) x=0;
                    }
                    
                    decs.add("mensaje"+msjCounter+" db 10,13,"+msj+", \"$\"");
                    msjCounter++;
                }
            }
        }

        return decs;
    }

    public static List<String> convertLineToObject(String line, List<String> strVar){
        String token = new String();
        StringTokenizer tokenizer = new StringTokenizer(line, "+-*/=() ", true);
        List<String> lines = new ArrayList<>();
        Stack<String> operands = new Stack<>();
        Stack<String> operators = new Stack<>();
        Stack<String> labels = new Stack<>();
        Stack<String> jumpLabels = new Stack<>();

        boolean insidePrintfScan = false;
        boolean insideElseIfBlock = false;

        String ADD = "ADD";
        String SUB = "SUB";
        String DIV = "DIV";
        String MUL = "MUL";

        String IGUAL = "JE";
        String MENOR = "JLE";
        String MAYOR = "JGE";
        String DIFERENTE = "JNE";
        String MENORI = "JL";
        String MAYORI = "JG";

        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken().trim();
            //String TokenG=token;
            //System.out.println(TokenG);
            if (token.isEmpty()) continue;

            if (token.equals("(")) {
                token = tokenizer.nextToken().trim();

                if (token.equals("=")) {
                    tempCounter = -1;
                    lines.add("");
                    int c = 1;
                    while (c <= 8) {
                        token = tokenizer.nextToken().trim();
                        c++;
                    }
                    lines.add("MOV "+token+",AX\n");
                }

                if (token.equals("/")) {

                    if (tempCounter >= 1) {
                        token = tokenizer.nextToken().trim();
                        while (token.equals(" ") || token.equals(",")) {
                            token = tokenizer.nextToken().trim();
                        }
                        token = tokenizer.nextToken().trim();

                        if (token.contains("t")) {
                            token = tokenizer.nextToken().trim();
                            while (token.equals(" ") || token.equals(",")) {
                                token = tokenizer.nextToken().trim();
                            }
                            token = tokenizer.nextToken().trim();

                            if (token.contains("t")) {
                                lines.add(DIV + " R0,R0,R1");
                            } else {
                                token = token.replaceAll(",", "");
                                lines.add("LD R" + tempCounter + "," + token);

                                lines.add(DIV + " R0,R0,R1");
                            }
                        } else {
                            token = token.replaceAll(",", "");
                            lines.add("LD R" + tempCounter + "," + token);

                            token = tokenizer.nextToken().trim();
                            while (token.equals(" ") || token.equals(",")) {
                                token = tokenizer.nextToken().trim();
                            }
                            token = tokenizer.nextToken().trim();

                            if (token.contains("t")) {
                                lines.add(DIV + " R0,R1,R0");
                            } else {
                                tempCounter++;
                                token = token.replaceAll(",", "");
                                lines.add("LD R" + tempCounter + "," + token);

                                lines.add(DIV + " R1,R1,R2");
                                tempCounter--;
                            }
                        }
                    } else {
                        token = tokenizer.nextToken().trim();
                        while (token.equals(" ") || token.equals(",")) {
                            token = tokenizer.nextToken().trim();
                        }
                        token = tokenizer.nextToken().trim();

                        token = token.replaceAll(",", "");
                        tempCounter++;
                        lines.add("LD R" + tempCounter + "," + token);

                        token = tokenizer.nextToken().trim();
                        while (token.equals(" ") || token.equals(",")) {
                            token = tokenizer.nextToken().trim();
                        }
                        token = tokenizer.nextToken().trim();

                        token = token.replaceAll(",", "");
                        tempCounter++;
                        lines.add("LD R" + tempCounter + "," + token);

                        tempCounter--;
                        String P1 = " R" + tempCounter + ",R" + tempCounter;
                        tempCounter++;
                        String P2 = ",R" + tempCounter;
                        lines.add(DIV + P1 + P2);
                    }

                } else if (token.equals("+")) {

                    if (tempCounter >= 1) {
                        token = tokenizer.nextToken().trim();
                        while (token.equals(" ") || token.equals(",")) {
                            token = tokenizer.nextToken().trim();
                        }
                        token = tokenizer.nextToken().trim();

                        if (token.contains("t")) {
                            token = tokenizer.nextToken().trim();
                            while (token.equals(" ") || token.equals(",")) {
                                token = tokenizer.nextToken().trim();
                            }
                            token = tokenizer.nextToken().trim();

                            if (token.contains("t")) {
                                lines.add(ADD + " R0,R0,R1");
                            } else {
                                token = token.replaceAll(",", "");
                                lines.add("LD R" + tempCounter + "," + token);

                                lines.add(ADD + " R0,R0,R1");
                            }
                        } else {
                            token = token.replaceAll(",", "");
                            lines.add("LD R" + tempCounter + "," + token);

                            token = tokenizer.nextToken().trim();
                            while (token.equals(" ") || token.equals(",")) {
                                token = tokenizer.nextToken().trim();
                            }
                            token = tokenizer.nextToken().trim();

                            if (token.contains("t")) {
                                lines.add(ADD + " R0,R1,R0");
                            } else {
                                tempCounter++;
                                token = token.replaceAll(",", "");
                                lines.add("LD R" + tempCounter + "," + token);

                                lines.add(ADD + " R1,R1,R2");
                                tempCounter--;
                            }
                        }
                    } else {
                        token = tokenizer.nextToken().trim();
                        while (token.equals(" ") || token.equals(",")) {
                            token = tokenizer.nextToken().trim();
                        }
                        token = tokenizer.nextToken().trim();

                        token = token.replaceAll(",", "");
                        tempCounter++;
                        lines.add("LD R" + tempCounter + "," + token);

                        token = tokenizer.nextToken().trim();
                        while (token.equals(" ") || token.equals(",")) {
                            token = tokenizer.nextToken().trim();
                        }
                        token = tokenizer.nextToken().trim();

                        token = token.replaceAll(",", "");
                        tempCounter++;
                        lines.add("LD R" + tempCounter + "," + token);

                        tempCounter--;
                        String P1 = " R" + tempCounter + ",R" + tempCounter;
                        tempCounter++;
                        String P2 = ",R" + tempCounter;
                        lines.add(ADD + P1 + P2);
                    }

                } else if (token.equals("-")) {

                    if (tempCounter >= 1) {
                        token = tokenizer.nextToken().trim();
                        while (token.equals(" ") || token.equals(",")) {
                            token = tokenizer.nextToken().trim();
                        }
                        token = tokenizer.nextToken().trim();

                        if (token.contains("t")) {
                            token = tokenizer.nextToken().trim();
                            while (token.equals(" ") || token.equals(",")) {
                                token = tokenizer.nextToken().trim();
                            }
                            token = tokenizer.nextToken().trim();

                            if (token.contains("t")) {
                                lines.add(SUB + " R0,R0,R1");
                            } else {
                                token = token.replaceAll(",", "");
                                lines.add("LD R" + tempCounter + "," + token);

                                lines.add(SUB + " R0,R0,R1");
                            }
                        } else {
                            token = token.replaceAll(",", "");
                            lines.add("LD R" + tempCounter + "," + token);

                            token = tokenizer.nextToken().trim();
                            while (token.equals(" ") || token.equals(",")) {
                                token = tokenizer.nextToken().trim();
                            }
                            token = tokenizer.nextToken().trim();

                            if (token.contains("t")) {
                                lines.add(SUB + " R0,R1,R0");
                            } else {
                                tempCounter++;
                                token = token.replaceAll(",", "");
                                lines.add("LD R" + tempCounter + "," + token);

                                lines.add(SUB + " R1,R1,R2");
                                tempCounter--;
                            }
                        }
                    } else {
                        token = tokenizer.nextToken().trim();
                        while (token.equals(" ") || token.equals(",")) {
                            token = tokenizer.nextToken().trim();
                        }
                        token = tokenizer.nextToken().trim();

                        token = token.replaceAll(",", "");
                        tempCounter++;
                        lines.add("LD R" + tempCounter + "," + token);

                        token = tokenizer.nextToken().trim();
                        while (token.equals(" ") || token.equals(",")) {
                            token = tokenizer.nextToken().trim();
                        }
                        token = tokenizer.nextToken().trim();

                        token = token.replaceAll(",", "");
                        tempCounter++;
                        lines.add("LD R" + tempCounter + "," + token);

                        tempCounter--;
                        String P1 = " R" + tempCounter + ",R" + tempCounter;
                        tempCounter++;
                        String P2 = ",R" + tempCounter;
                        lines.add(SUB + P1 + P2);
                    }
                } else if (token.equals("*")) {

                    if (tempCounter >= 1) {
                        token = tokenizer.nextToken().trim();
                        while (token.equals(" ") || token.equals(",")) {
                            token = tokenizer.nextToken().trim();
                        }
                        token = tokenizer.nextToken().trim();

                        if (token.contains("t")) {
                            token = tokenizer.nextToken().trim();
                            while (token.equals(" ") || token.equals(",")) {
                                token = tokenizer.nextToken().trim();
                            }
                            token = tokenizer.nextToken().trim();

                            if (token.contains("t")) {
                                lines.add(MUL + " R0,R0,R1");
                            } else {
                                token = token.replaceAll(",", "");
                                lines.add("LD R" + tempCounter + "," + token);

                                lines.add(MUL + " R0,R0,R1");
                            }
                        } else {
                            token = token.replaceAll(",", "");
                            lines.add("LD R" + tempCounter + "," + token);

                            token = tokenizer.nextToken().trim();
                            while (token.equals(" ") || token.equals(",")) {
                                token = tokenizer.nextToken().trim();
                            }
                            token = tokenizer.nextToken().trim();

                            if (token.contains("t")) {
                                lines.add(MUL + " R0,R1,R0");
                            } else {
                                tempCounter++;
                                token = token.replaceAll(",", "");
                                lines.add("LD R" + tempCounter + "," + token);

                                lines.add(MUL + " R1,R1,R2");
                                tempCounter--;
                            }
                        }
                    } else {
                        token = tokenizer.nextToken().trim();
                        while (token.equals(" ") || token.equals(",")) {
                            token = tokenizer.nextToken().trim();
                        }
                        token = tokenizer.nextToken().trim();

                        token = token.replaceAll(",", "");
                        tempCounter++;
                        lines.add("LD R" + tempCounter + "," + token);

                        token = tokenizer.nextToken().trim();
                        while (token.equals(" ") || token.equals(",")) {
                            token = tokenizer.nextToken().trim();
                        }
                        token = tokenizer.nextToken().trim();

                        token = token.replaceAll(",", "");
                        tempCounter++;
                        lines.add("LD R" + tempCounter + "," + token);

                        tempCounter--;
                        String P1 = " R" + tempCounter + ",R" + tempCounter;
                        tempCounter++;
                        String P2 = ",R" + tempCounter;
                        lines.add(MUL + P1 + P2);
                    }
                }
            }else if(token.equals("printf")){
                int c=1;
                while (c <= 3) {
                    token = tokenizer.nextToken().trim();
                    c++;
                }
                if(token.contains("\"")){
                    lines.add("\nMOV DX, offset mensaje"+msjCounter2);
                    lines.add("MOV AH, 09H");

                    msjCounter2++;
                }
                else{
                    int llaveString=0;
                    for (String var : strVar) {
                        if(token.equals(var)){
                            llaveString=1;
                        }
                    }

                    if(llaveString==1){
                        lines.add("MOV BL, "+token+"[1]");
                        lines.add("MOV "+token+"[BX+2], '$'");
                        lines.add("MOV DX, offset "+token+" + 2");
                        lines.add("MOV AH,09H");
                    }
                    else {
                        lines.add("\nMOV DX," + token);
                        lines.add("ADD DX,30H");
                        lines.add("MOV AH,02");
                    }
                }

                lines.add("INT 21H\n");


            }else if(token.equals("scan")){
                int c=1;
                while (c <= 3) {
                    token = tokenizer.nextToken().trim();
                    c++;
                }

                int llaveString=0;
                for (String var : strVar) {
                    if(token.equals(var)){
                        llaveString=1;
                    }
                }

                //System.out.println(llaveString);

                if(llaveString==1){
                    lines.add("MOV DX, offset "+ token);
                    lines.add("MOV AH, 0AH");
                    lines.add("INT 21H\n");
                }
                else {
                    lines.add("MOV AH,01");
                    lines.add("INT 21H");
                    lines.add("SUB AX,30H");
                    lines.add("MOV " + token + ",AX\n");
                }

            }else if(token.equals("if")){
                int c=1;
                while (c <= 2) {
                    token = tokenizer.nextToken().trim();
                    c++;
                }

                String condition = token;
                //System.out.println(condition);

                token = tokenizer.nextToken().trim();
                if(token.contains("=")){
                    condition=condition+token;
                    token = tokenizer.nextToken().trim();
                    condition=condition+token;
                    //System.out.println(condition);
                }

                String cond1="", cond2="", opcond="";
                int w=0;
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
                        w=i+1;
                        break;
                    }

                    if(condition.charAt(i) == '='){
                        opcond=opcond + "==";
                        i++;
                        
                        w=i+1;
                        break;
                    }

                    if(condition.charAt(i) == '!'){
                        opcond=opcond + "!=";
                        i++;
                        
                        w=i+1;
                        break;
                    }

                    cond1 = cond1 + condition.charAt(i);
                }

                for (int i = w; i <= condition.length()-1; i++) {
                    cond2 = cond2 + condition.charAt(i);
                }
                
                if(opcond.equals("==")){
                	token = tokenizer.nextToken().trim();
                	cond2 = token;
                }
                
                //System.out.println(cond1);
                //System.out.println(opcond);
                //System.out.println(cond2);

                lines.add("MOV BX,"+cond2);
                lines.add("CMP "+cond1+",BX");

                int s=1;
                while (s != 0) {
                    if(token.contains("L")){
                        s=0;
                    }
                    else{
                        token = tokenizer.nextToken().trim();
                    }
                }

                switch(opcond){
                    case ">":
                        lines.add(MAYOR+" "+token);
                        break;
                    case "<":
                        lines.add(MENOR+" "+token);
                        break;
                    case "!=":
                        lines.add(DIFERENTE+" "+token);
                        break;
                    case ">=":
                        lines.add(MAYORI+" "+token);
                        break;
                    case "<=":
                        lines.add(MENORI+" "+token);
                        break;
                    case "==":
                        lines.add(IGUAL+" "+token);
                }


            }else if(token.equals("goto")){
                int c=1;
                while (c <= 2) {
                    token = tokenizer.nextToken().trim();
                    c++;
                }

                lines.add("JMP "+token);
            }else if(token.contains("L")){
                lines.add(token);
            }
        }

        return lines;
    }
}