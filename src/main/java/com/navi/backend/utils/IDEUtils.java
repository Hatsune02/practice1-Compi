package com.navi.backend.utils;

import java.io.*;
import lombok.*;
import javax.swing.*;

@NoArgsConstructor @AllArgsConstructor
@Setter @Getter @Builder
public class IDEUtils {
    private String name;
    private String absolutePath;


    public void generarArchivoIde() {
        File ideFile = new File(absolutePath + "/" + name + ".ide");
        if (!ideFile.exists()) {
            JOptionPane.showMessageDialog(null,"Este directorio no es un proyecto","Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            FileWriter fileWriter = new FileWriter(ideFile);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println("<PROYECTO nombre=\"" + name + "\">");
            agregarArchivosYCarpetas(absolutePath, printWriter);
            printWriter.println("</PROYECTO>");

            printWriter.close();
            System.out.println("Archivo .ide actualizado correctamente.");
        } catch (IOException e) {
            System.out.println("Error al actualizar el archivo .ide.");
            e.printStackTrace();
        }
    }

    private void agregarArchivosYCarpetas(String path, PrintWriter printWriter) {
        File directorio = new File(path);
        File[] archivos = directorio.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isFile()) {
                    printWriter.println("\t<ARCHIVO nombre=\"" + archivo.getName() + "\" ubicacion=\"" + archivo.getAbsolutePath() + "\"/>");
                } else if (archivo.isDirectory()) {
                    printWriter.println("\t<CARPETA nombre=\"" + archivo.getName() + "\" ubicacion=\"" + archivo.getAbsolutePath() + "\">");
                    agregarArchivosYCarpetas(archivo.getAbsolutePath(), printWriter);
                    printWriter.println("\t</CARPETA>");
                }
            }
        }
    }
}
