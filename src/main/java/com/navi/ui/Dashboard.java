package com.navi.ui;

import com.formdev.flatlaf.ui.FlatTreeUI;
import com.navi.backend.csv_controller.Querys;
import com.navi.backend.flexycup.*;
import com.navi.backend.utils.IDEFilter;
import com.navi.backend.utils.IDEUtils;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static javax.swing.JOptionPane.*;

public class Dashboard extends javax.swing.JFrame {
    File projectFolder;
    DefaultMutableTreeNode root;
    DefaultTreeModel model;
    DefaultTreeCellEditor editor;
    NumLine numLine;

    public Dashboard() {
        initComponents();
        initStyles();
    }
    private void initStyles(){
        setTitle("SQL IDE");
        label.putClientProperty("FlatLaf.style","font: 14 $semibold.font");
        label.setForeground(Color.WHITE);

        newFileB.setForeground(Color.WHITE);
        newFolderB.setForeground(Color.WHITE);
        sendQueryB.setForeground(Color.WHITE);

        tree.setEditable(true);
        editor = new DefaultTreeCellEditor(tree, (DefaultTreeCellRenderer) tree.getCellRenderer());
        tree.setCellEditor(editor);

        numLine = new NumLine(textQuery);
        scrollQuerys.setRowHeaderView(numLine);

        UIManager.put("Tree.paintLines", true);
        UIManager.put("Tree.repaintWholeRow", true);
        UIManager.put("Tree.icon.expandedColor", true);
        UIManager.put("Tree.hash", new Color(0,153,153));

        tree.setUI(new FlatTreeUI());

        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int tabIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
                    if(tabIndex != -1){
                        JPopupMenu popup = new JPopupMenu();
                        JMenuItem closeItem = new JMenuItem("Cerrar");
                        closeItem.addActionListener(ev -> tabbedPane.remove(tabIndex));
                        popup.add(closeItem);
                        popup.show(tabbedPane, e.getX(), e.getY());
                    }
                }
                if(e.getButton() == MouseEvent.BUTTON2){
                    int tabIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
                    tabbedPane.remove(tabIndex);
                }
            }
        });
        editor.addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node != null) {
                    Object newValue = editor.getCellEditorValue();
                    var actual = new File(getAbsolutePathOfNode(node));
                    var aux = new File(actual.getParent(), String.valueOf(newValue));
                    if(actual.renameTo(aux)) System.out.println("Se renombro con exito");
                }
            }

            @Override
            public void editingCanceled(ChangeEvent changeEvent) {}
        });

        JPopupMenu popupMenu = getjPopupMenu();
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        tree.setSelectionPath(path);
                        popupMenu.show(tree, e.getX(), e.getY());
                    }
                }
                if (e.getClickCount() == 2){
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                        String filePath = getAbsolutePathOfNode(selectedNode);
                        if(!isFileAlreadyOpen(selectedNode.getUserObject().toString())){
                            try {
                                String fileContent = readFile(filePath);
                                FileTextPane pane = new FileTextPane(filePath, fileContent);
                                tabbedPane.addTab(selectedNode.getUserObject().toString(), pane);
                                tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
                            } catch (IOException ignored) {}
                        }
                    }
                }
            }
        });

        textQuery.addKeyListener(new KeyAdapter() {
            @Override
                public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && textQuery.getText().endsWith(";")) {
                    lexer = new SqlLexer(new StringReader(textQuery.getText()));
                    parser = new SqlParser(lexer);
                    try {
                        parser.parse();
                        for(FileTextPane p: parser.panes){
                            tabbedPane.addTab("SELECT", p);
                        }
                        tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
                    } catch (Exception ex) {
                        showMessageDialog(textQuery ,"Se detectaron errores al momento de ejecutar la query","Error",JOptionPane.ERROR_MESSAGE);
                    }
                    showErrConsole();
                }
            }
        });
    }

    private boolean isFileAlreadyOpen(String fileName) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            String tabName = tabbedPane.getTitleAt(i);
            if (tabName.equals(fileName)) {
                tabbedPane.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    private String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    //Obtener la ruta del archivo en Jtree
    private String getAbsolutePathOfNode(DefaultMutableTreeNode node){
        StringBuilder root = new StringBuilder();
        Object[] nodes = node.getPath();
        if(projectFolder != null){
            root.append(projectFolder.getAbsolutePath());
            for (int i = 1; i < nodes.length; i++) {
                root.append("/");
                root.append(((DefaultMutableTreeNode) nodes[i]).getUserObject().toString());
            }

        }
        return root.toString();
    }

    private void deleteFile(DefaultMutableTreeNode selectedNode){
        if(selectedNode != null){
            File file = new File(getAbsolutePathOfNode(selectedNode));
            if(file.isDirectory()){
                File[] files = file.listFiles();
                if(files!=null){
                    for(File f: files){
                        deleteFile(f);
                    }
                }
            }
            if(file.delete()) {
                reloadTree(projectFolder);
            }

        }
    }
    private void deleteFile(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files!=null){
                for(File f: files){
                    deleteFile(f);
                }
            }
        }
        if(file.delete()) {
            reloadTree(projectFolder);
        }

    }

    //Crear folders o files
    private void createFolder(DefaultMutableTreeNode selectedNode){
        if(selectedNode != null){
            String folderName = showInputDialog("Enter Folder Name");
            if(folderName!=null && !folderName.isEmpty() && folderName.matches("[a-zA-Z0-9_@+#*-]+")) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(folderName);
                String path = getAbsolutePathOfNode(selectedNode);
                File newFolder = new File(path, folderName);
                if(newFolder.mkdir()){
                    model.insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
                    reloadTree(projectFolder);
                }
                else showMessageDialog(this,"No puedes crear una carpeta aqui","Error",JOptionPane.ERROR_MESSAGE);

            }
            else showMessageDialog(this,"No puedes usar ese nombre para la creacion del archivo","Error",JOptionPane.ERROR_MESSAGE);
        }

    }
    private void createFile(DefaultMutableTreeNode selectedNode){
        if(selectedNode != null){
            String fileName = showInputDialog("Enter File Name");
            if(fileName!=null && !fileName.isEmpty()) {
                createFiles(selectedNode, fileName);
            }
        }
    }
    private void createCSVFile(DefaultMutableTreeNode selectedNode){
        if(selectedNode != null){
            String fileName = showInputDialog("Enter File Name");
            if(fileName!=null && !fileName.isEmpty()) {
                fileName = fileName + ".csv";
                createFiles(selectedNode, fileName);
            }
        }
    }
    private void createFiles(DefaultMutableTreeNode selectedNode, String fileName) {
        if(fileName.matches("[a-zA-Z0-9_@+#*-]+")){
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(fileName);
            String path = getAbsolutePathOfNode(selectedNode);
            File newFile = new File(path, fileName);
            try {
                if (newFile.createNewFile()) {
                    model.insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
                    reloadTree(projectFolder);
                } else {
                    showMessageDialog(this,"El archivo ya existe.","Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                showMessageDialog(this,"Error al crear archivo: "+e.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);

            }
        }
        else showMessageDialog(this,"No puedes usar ese nombre para la creacion del archivo","Error",JOptionPane.ERROR_MESSAGE);
    }

    //Crear menu Popup
    private JPopupMenu getjPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem newCSVFileItem = new JMenuItem("Nuevo Archivo CSV");
        JMenuItem newFileItem = new JMenuItem("Nuevo Archivo");
        JMenuItem newFolderItem = new JMenuItem("Nueva Carpeta");
        JMenuItem deleteFile = new JMenuItem("Eliminar");


        newCSVFileItem.addActionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            createCSVFile(selectedNode);
        });
        newFileItem.addActionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            createFile(selectedNode);
        });
        newFolderItem.addActionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            createFolder(selectedNode);
        });
        deleteFile.addActionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            int option = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que quieres eliminar '" + selectedNode.getUserObject() + "'?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if(option == YES_OPTION) deleteFile(selectedNode);
        });
        popupMenu.add(newCSVFileItem);
        popupMenu.add(newFileItem);
        popupMenu.add(newFolderItem);
        popupMenu.add(deleteFile);
        return popupMenu;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        background = new javax.swing.JPanel();
        panelTree = new javax.swing.JPanel();
        scrollTree = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        newFolderB = new javax.swing.JButton();
        newFileB = new javax.swing.JButton();
        panelFiles = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        scrollTextA = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();
        panelQuery = new javax.swing.JPanel();
        scrollQuerys = new javax.swing.JScrollPane();
        textQuery = new TextPaneB();
        label = new javax.swing.JLabel();
        sendQueryB = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        newProject = new javax.swing.JMenuItem();
        openProject = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelTree.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 204, 255), 1, true));

        scrollTree.setViewportView(tree);

        newFolderB.setText("Project +");
        newFolderB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFolderBActionPerformed(evt);
            }
        });

        newFileB.setText("Open+");
        newFileB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFileBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTreeLayout = new javax.swing.GroupLayout(panelTree);
        panelTree.setLayout(panelTreeLayout);
        panelTreeLayout.setHorizontalGroup(
                panelTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelTreeLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(panelTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollTree, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(panelTreeLayout.createSequentialGroup()
                                                .addComponent(newFolderB)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(newFileB, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())))
        );
        panelTreeLayout.setVerticalGroup(
                panelTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTreeLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(newFolderB)
                                        .addComponent(newFileB))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollTree, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE))
        );

        scrollTextA.setViewportView(textPane);

        tabbedPane.addTab("archivo", scrollTextA);

        javax.swing.GroupLayout panelFilesLayout = new javax.swing.GroupLayout(panelFiles);
        panelFiles.setLayout(panelFilesLayout);
        panelFilesLayout.setHorizontalGroup(
                panelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 741, Short.MAX_VALUE)
                        .addGroup(panelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panelFilesLayout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addComponent(tabbedPane)
                                        .addGap(5, 5, 5)))
        );
        panelFilesLayout.setVerticalGroup(
                panelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 374, Short.MAX_VALUE)
                        .addGroup(panelFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
        );

        scrollQuerys.setViewportView(textQuery);

        label.setText(" Area de consultas");

        sendQueryB.setText("Mandar");
        sendQueryB.setPreferredSize(new java.awt.Dimension(90, 30));
        sendQueryB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendQueryBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelQueryLayout = new javax.swing.GroupLayout(panelQuery);
        panelQuery.setLayout(panelQueryLayout);
        panelQueryLayout.setHorizontalGroup(
                panelQueryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelQueryLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelQueryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelQueryLayout.createSequentialGroup()
                                                .addComponent(label, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 514, Short.MAX_VALUE)
                                                .addComponent(sendQueryB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())
                                        .addComponent(scrollQuerys, javax.swing.GroupLayout.Alignment.TRAILING)))
        );
        panelQueryLayout.setVerticalGroup(
                panelQueryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelQueryLayout.createSequentialGroup()
                                .addGroup(panelQueryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelQueryLayout.createSequentialGroup()
                                                .addGap(22, 22, 22)
                                                .addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelQueryLayout.createSequentialGroup()
                                                .addGap(13, 13, 13)
                                                .addComponent(sendQueryB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollQuerys, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout backgroundLayout = new javax.swing.GroupLayout(background);
        background.setLayout(backgroundLayout);
        backgroundLayout.setHorizontalGroup(
                backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(backgroundLayout.createSequentialGroup()
                                .addComponent(panelTree, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(panelQuery, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundLayout.createSequentialGroup()
                                        .addGap(267, 267, 267)
                                        .addComponent(panelFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        backgroundLayout.setVerticalGroup(
                backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelTree, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(backgroundLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(panelQuery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addGroup(backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(backgroundLayout.createSequentialGroup()
                                        .addComponent(panelFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(236, 236, 236)))
        );

        jMenu1.setText("  Archivo  ");

        newProject.setText("Nuevo Proyecto");
        newProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectActionPerformed(evt);
            }
        });
        jMenu1.add(newProject);

        openProject.setText("Abrir Proyecto");
        openProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openProjectActionPerformed(evt);
            }
        });
        jMenu1.add(openProject);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>

    private void newProjectActionPerformed(java.awt.event.ActionEvent evt) {
        NewProject dialog = new NewProject(this);
        dialog.setVisible(true);
    }
    private void openProjectActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setFileSystemView(new IDEFilter());
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            projectFolder = fileChooser.getSelectedFile(); //ObtenerFichero
            Querys.pathProject = projectFolder.getAbsolutePath();
            reloadTree(projectFolder);
        }
    }
    //Recargar arbol Jtree en base al directorio escogido
    public void reloadTree(File file){
        root = new DefaultMutableTreeNode(file.getName());
        model = new DefaultTreeModel(root);
        create(root, file);
        sortTree(root);
        IDEUtils project = new IDEUtils(projectFolder.getName(), projectFolder.getAbsolutePath());
        project.generarArchivoIde();
        tree.setModel(model);
    }
    private void create(DefaultMutableTreeNode root, File file){
        File[] files = file.listFiles();
        if(files != null){
            int count = 0;
            for(File f: files){
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName());
                model.insertNodeInto(child, root, count);
                count++;
                if(f.isDirectory()){
                    create(child,f);
                }
            }
        }
    }
    //Ordena el arbol Jtree de forma folder->file y alfabeticamente
    private void sortTree(DefaultMutableTreeNode node) {
        if (node.getChildCount() > 0) {
            List<DefaultMutableTreeNode> childNodes = new ArrayList<>();
            Enumeration<?> children = node.children();
            while (children.hasMoreElements()) {
                childNodes.add((DefaultMutableTreeNode) children.nextElement());
            }

            // Ordenar los nodos hijos
            childNodes.sort((o1, o2) -> {
                boolean esCarpeta1 = o1.getChildCount() > 0;
                boolean esCarpeta2 = o2.getChildCount() > 0;

                if (esCarpeta1 && !esCarpeta2) {
                    return -1;
                } else if (!esCarpeta1 && esCarpeta2) {
                    return 1;
                } else {
                    return o1.toString().compareToIgnoreCase(o2.toString());
                }
            });

            // Eliminar los nodos hijos actuales
            node.removeAllChildren();

            // Agregar los nodos hijos ordenados
            for (DefaultMutableTreeNode child : childNodes) {
                node.add(child);
                sortTree(child);
            }
        }
    }
    private void newFolderBActionPerformed(java.awt.event.ActionEvent evt) {
        NewProject dialog = new NewProject(this);
        dialog.setVisible(true);
    }

    private void newFileBActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setFileSystemView(new IDEFilter());
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            projectFolder = fileChooser.getSelectedFile(); //ObtenerFichero
            Querys.pathProject = projectFolder.getAbsolutePath();
            reloadTree(projectFolder);
        }
    }

    private void sendQueryBActionPerformed(java.awt.event.ActionEvent evt) {
        if(!textQuery.getText().isEmpty()){
            lexer = new SqlLexer(new StringReader(textQuery.getText()));
            parser = new SqlParser(lexer);
            try {
                parser.parse();
                for(FileTextPane p: parser.panes){
                    tabbedPane.addTab("SELECT", p);
                }
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
            } catch (Exception e) {
                showMessageDialog(this,"Se detectaron errores al momento de ejecutar la query","Error",JOptionPane.ERROR_MESSAGE);
            }
            showErrConsole();
        }
    }

    private void showErrConsole() {
        if(!Querys.errors.isEmpty()){
            StringBuilder errors = new StringBuilder();
            for(TError err: Querys.errors){
                errors.append(err).append("\n");
            }
            Querys.errors.clear();
            FileTextPane pane = new FileTextPane(errors.toString());
            tabbedPane.addTab("ERRORES", pane);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
        }
        if(!Querys.console.isEmpty()){
            StringBuilder console = new StringBuilder();
            int count = 1;
            for(String c: Querys.console){
                console.append(count).append(") ").append(c).append("\n");
                count++;
            }
            FileTextPane pane = new FileTextPane(console.toString());
            tabbedPane.addTab("CONSOLE", pane);
        }
    }


    private SqlLexer lexer;
    private SqlParser parser;

    // Variables declaration - do not modify
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextPane textQuery;
    private javax.swing.JPanel background;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JTree tree;
    private javax.swing.JLabel label;
    private javax.swing.JButton newFileB;
    private javax.swing.JButton newFolderB;
    private javax.swing.JMenuItem newProject;
    private javax.swing.JMenuItem openProject;
    private javax.swing.JPanel panelFiles;
    private javax.swing.JPanel panelQuery;
    private javax.swing.JPanel panelTree;
    private javax.swing.JScrollPane scrollQuerys;
    private javax.swing.JScrollPane scrollTextA;
    private javax.swing.JScrollPane scrollTree;
    private javax.swing.JButton sendQueryB;
    private javax.swing.JTextPane textPane;
    // End of variables declaration
}

