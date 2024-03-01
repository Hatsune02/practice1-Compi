package com.navi;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme;
import com.navi.backend.flexycup.*;
import com.navi.ui.Dashboard;

import javax.swing.*;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) {
        /*String expr = "3+2;3-1";
        SqlLexer l = new SqlLexer(new StringReader(expr));
        SqlParser p = new SqlParser(l);

        try {
            p.parse();
        } catch (Exception e) {
            System.out.println(e);
        }*/
        FlatMaterialDarkerIJTheme.setup();
        Dashboard d = new Dashboard();
        d.setVisible(true);
    }
}