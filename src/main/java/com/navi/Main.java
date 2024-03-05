package com.navi;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme;
import com.navi.ui.Dashboard;

public class Main {
    public static void main(String[] args) {
        FlatMaterialDarkerIJTheme.setup();
        Dashboard d = new Dashboard();
        d.setVisible(true);
    }
}