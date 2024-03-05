package com.navi.ui;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

public class FileTextPane extends JScrollPane {
    private String PATH;
    private JTextPane pane;
    private NumLine numLine;
    public FileTextPane(String path, String fileContent){
        super();
        this.PATH = path;
        pane = new JTextPane();
        pane.setEditable(true);
        pane.setText(fileContent);
        pane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
                    saveFile();
                }
            }
        });
        numLine = new NumLine(pane);
        this.setRowHeaderView(numLine);
        this.setViewportView(pane);

    }
    public FileTextPane(String fileContent){
        super();
        pane = new JTextPane();
        pane.setEditable(true);
        pane.setText(fileContent);
        numLine = new NumLine(pane);
        this.setRowHeaderView(numLine);
        this.setViewportView(pane);
    }
    public void saveFile(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH))) {
            writer.write(pane.getText());
            System.out.println("Se han guardado los cambios en el archivo.");
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

}
