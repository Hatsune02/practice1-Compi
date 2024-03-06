package com.navi.backend.utils;

import com.formdev.flatlaf.icons.FlatFileViewDirectoryIcon;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;

public class IDEFilter extends FileSystemView {
    private FileSystemView defaultFileSystemView = FileSystemView.getFileSystemView();
    private Icon carpetaIconoIDEResaltado = new ImageIcon(new ImageIcon("/home/dog/IdeaProjects/Compi/practice1-Compi/src/main/resources/icon/icono_ide.png")
            .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));

    @Override
    public Boolean isTraversable(File file) {
        return defaultFileSystemView.isTraversable(file);
    }

    @Override
    public String getSystemDisplayName(File file) {
        return defaultFileSystemView.getSystemDisplayName(file);
    }

    @Override
    public Icon getSystemIcon(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".ide")) {
                        return carpetaIconoIDEResaltado;
                    }
                }
            }
        }
        return defaultFileSystemView.getSystemIcon(file);
    }

    @Override
    public File createNewFolder(File file) throws IOException {
        return defaultFileSystemView.createNewFolder(file);
    }

    @Override
    public String getSystemTypeDescription(File file) {
        return defaultFileSystemView.getSystemTypeDescription(file);
    }

}
