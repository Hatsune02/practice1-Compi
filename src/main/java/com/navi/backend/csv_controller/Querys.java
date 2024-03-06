package com.navi.backend.csv_controller;
import com.navi.backend.flexycup.TError;

import java.util.ArrayList;
import java.util.Arrays;

public class Querys {
    public static String pathProject = "null";
    public static ArrayList<TError> errors = new ArrayList<>();
    public static ArrayList<String> console = new ArrayList<>();

    public static String select(PathQ path){
        String route = getAbsolutePath(path);
        if(!pathProject.equals("null") && !route.equals("null")){
            path.setPath(route);
            return CSVController.readAllCSV(path);
        }
        else {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            errors.add(err);
            return "";
        }
    }
    public static String selectC(PathQ path,ArrayList<ColumnsQ> columns){
        String route = getAbsolutePath(path);
        if(!pathProject.equals("null") && !route.equals("null")){
            path.setPath(route);
            return CSVController.readCSV(path, columns);
        }
        else {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            errors.add(err);
            return "";
        }
    }
    public static String select(PathQ path, ArrayList<FiltersQuery> filters){
        String route = getAbsolutePath(path);
        if(!pathProject.equals("null") && !route.equals("null")){
            path.setPath(route);
            return CSVController.readAllCSV(path, filters);
        }
        else {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            errors.add(err);
            return "";
        }
    }
    public static String select(PathQ path, ArrayList<ColumnsQ> columns, ArrayList<FiltersQuery> filters){
        String route = getAbsolutePath(path);
        if(!pathProject.equals("null") && !route.equals("null")){
            path.setPath(route);
            return CSVController.readCSV(path, columns, filters);
        }
        else {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            errors.add(err);
            return "";
        }
    }

    public static void insert(PathQ path, ArrayList<String> values){
        String route = getAbsolutePath(path);
        if(!pathProject.equals("null") && !route.equals("null")){
            path.setPath(route);
            CSVController.writeCSV(path, values);
        }
        else {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            errors.add(err);
        }
    }
    public static void insert(PathQ path, ArrayList<ColumnsQ> columns, ArrayList<String> values){
        String route = getAbsolutePath(path);
        if(!pathProject.equals("null") && !route.equals("null")){
            path.setPath(route);
            CSVController.writeCSV(path, columns, values);
        }
        else {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            errors.add(err);
        }
    }

    public static void update(PathQ path, ArrayList<AssignQuery> assigns){
        String route = getAbsolutePath(path);
        if(!pathProject.equals("null") && !route.equals("null")){
            path.setPath(route);
            CSVController.editCSV(path, assigns);
        }
        else {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            errors.add(err);
        }
    }
    public static void update(PathQ path, ArrayList<AssignQuery> assigns, ArrayList<FiltersQuery> filters){
        String route = getAbsolutePath(path);
        if(!pathProject.equals("null") && !route.equals("null")){
            path.setPath(route);
            CSVController.editCSV(path, assigns, filters);
        }
        else {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            errors.add(err);
        }
    }

    public static void delete(PathQ path){
        String route = getAbsolutePath(path);
        if(!pathProject.equals("null") && !route.equals("null")){
            path.setPath(route);
            CSVController.cleanCSV(path);
        }
        else {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            errors.add(err);
        }
    }
    public static void delete(PathQ path, ArrayList<FiltersQuery> filters){
        String route = getAbsolutePath(path);
        if(!pathProject.equals("null") && !route.equals("null")){
            path.setPath(route);
            CSVController.cleanCSV(path, filters);
        }
        else {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            errors.add(err);
        }
    }
    private static String getAbsolutePath(PathQ path){
        String[] projectArr = pathProject.split("/");
        String[] arr = path.getPath().split("/");

        if (projectArr[projectArr.length-1].equals(arr[0])) return pathProject + "/" + String.join("/", Arrays.copyOfRange(arr, 1, arr.length)) + ".csv";

        TError err = new TError(path.getPath(),"Semántico(?","Path del proyecto erroneo", path.getLine(), path.getCol());
        errors.add(err);
        return "null";
    }
}
