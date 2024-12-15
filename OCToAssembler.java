
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

class OCToAssembler {
    private static int tempCounter = -1;

    public static void convertir(String ar, String decar, String source_file) {
        try {
            Path path = Paths.get(source_file+".asm");
            if (Files.exists(path)) {
                Files.delete(path);
            }


            Files.createFile(path);

            // Obtener el codigo ensamblador
            String ObCode = readAr(ar);
            String DecCode = readAr(decar);

            String ACode = ensamblador(ObCode, DecCode);


            try (BufferedWriter writer = new BufferedWriter(new FileWriter(source_file+".asm"))) {
                writer.write(ACode);
            }

            System.out.println("Ensamblador generado.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String readAr(String fileName) throws IOException {
        String lines="";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines=lines+line;
                lines=lines+"\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.print(lines);
        return lines;
    }

    public static String ensamblador(String objeto, String declaraciones){
        int i=0;
        String vr = "";
        String STRUC="";

        //JOptionPane.showMessageDialog(null, vr);
        //creamos la estructura base de ensablador
        STRUC+=".model small\n.stack\n.data\n"+declaraciones+"\n"+vr+".code\nINICIO: MOV AX, @DATA\n        MOV DS, AX\n        MOV ES, AX\n\n";

        while(i<4){objeto = objeto.replaceAll("MUL R"+i+",R"+i+",R"+(i+1), "MUL R"+(i+1)).replaceAll("DIV R"+i+",R"+i+",R"+(i+1), "DIV R"+(i+1));i++;}
        //System.out.println(objeto);
        objeto = objeto.replaceAll("LD", "MOV");
        objeto = objeto.replaceAll("R0,R0", "AX");
        objeto = objeto.replaceAll("R1,R1", "BX");
        objeto = objeto.replaceAll("R2,R2", "CX");
        objeto = objeto.replaceAll("R3,R3", "DX");
        objeto = objeto.replaceAll("R0", "AX");
        objeto = objeto.replaceAll("R1", "BX");
        objeto = objeto.replaceAll("R2", "CX");
        objeto = objeto.replaceAll("R3", "DX");
        STRUC+=objeto;

        STRUC+="\nFIN: MOV AX,4C00H\n     INT 21H\n     END\n";
        return STRUC;
    }
}