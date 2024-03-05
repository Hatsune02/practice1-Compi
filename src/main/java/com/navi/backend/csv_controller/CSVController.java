package com.navi.backend.csv_controller;

import com.navi.backend.flexycup.TError;
import org.apache.commons.csv.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.io.*;


public class CSVController {

    public static String readAllCSV(PathQ path) {
        try {
            Reader reader = new FileReader(path.getPath());
            CSVParser csvParser = CSVFormat.DEFAULT.parse(reader);
            StringBuilder row = new StringBuilder();
            for (CSVRecord csvRecord : csvParser) {
                for (int i = 0; i < csvRecord.size(); i++) {
                    row.append(csvRecord.get(i)).append(", ");
                }
                row.append("\n");
            }
            return row.toString();
        } catch (Exception e) {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            Querys.errors.add(err);
            return "";
        }
    }
    public static String readAllCSV(PathQ path, ArrayList<FiltersQuery> filters) {
        try {
            Reader reader = new FileReader(path.getPath());
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(reader);
            Map<String, Integer> headerMap = csvParser.getHeaderMap();
            ArrayList<ColumnsQ> columns = new ArrayList<>();
            for(FiltersQuery f: filters){
                columns.add(f.getColumn());
            }
            if(columnExist(csvParser, columns)){
                StringBuilder row = new StringBuilder();
                for (String columnName : headerMap.keySet()) {
                    row.append(columnName).append(", ");
                }
                row.append("\n");
                for (CSVRecord csvRecord : csvParser) {
                    if(conditions(csvRecord, filters)){
                        for (int i = 0; i < csvRecord.size(); i++) {
                            row.append(csvRecord.get(i)).append(", ");
                        }
                        row.append("\n");
                    }
                }
                return row.toString();
            }
            return "";
        } catch (Exception e) {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            Querys.errors.add(err);
            return "";
        }
    }
    public static String readCSV(PathQ path, ArrayList<ColumnsQ> columns) {
        try {
            Reader reader = new FileReader(path.getPath());
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(reader);
            if(columnExist(csvParser, columns)){
                StringBuilder row = new StringBuilder();
                for(ColumnsQ col: columns){
                    row.append(col.getName()).append(", ");
                }
                row.append("\n");
                for (CSVRecord csvRecord : csvParser) {
                    for(ColumnsQ col: columns){
                        row.append(csvRecord.get(col.getName())).append(", ");
                    }
                    row.append("\n");
                }
                return row.toString();
            }
            return "";
        } catch (Exception e) {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            Querys.errors.add(err);
            return "";
        }
    }
    public static String readCSV(PathQ path, ArrayList<ColumnsQ> columns, ArrayList<FiltersQuery> filters) {
        try {
            Reader reader = new FileReader(path.getPath());
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(reader);

            ArrayList<ColumnsQ> columnsS = mergeColumns(columns, filters);
            if(columnExist(csvParser, columnsS)){
                StringBuilder row = new StringBuilder();
                for(ColumnsQ col: columns){
                    row.append(col.getName()).append(", ");
                }
                row.append("\n");
                for (CSVRecord csvRecord : csvParser) {
                    if(conditions(csvRecord, filters)){
                        for(ColumnsQ col: columns){
                            row.append(csvRecord.get(col.getName())).append(", ");
                        }
                        row.append("\n");
                    }
                }
                return row.toString();
            }
            return "";
        } catch (Exception e) {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            Querys.errors.add(err);
            return "";
        }
    }

    public static void writeCSV(PathQ path, ArrayList<String> values){
        try {
            FileWriter writer = new FileWriter(path.getPath(),true);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            csvPrinter.printRecord(values);
            csvPrinter.flush();
            csvPrinter.close();

            System.out.println("Fila insertada correctamente");
        } catch (Exception e) {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            Querys.errors.add(err);
        }
    }
    public static void writeCSV(PathQ path, ArrayList<ColumnsQ> columns, ArrayList<String> values){
        try {
            FileWriter writer = new FileWriter(path.getPath(),true);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

            FileReader fileReader = new FileReader(path.getPath());
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(fileReader);

            if(columnExist(csvParser, columns)){
                List<Object> row = new ArrayList<>();
                for (ColumnsQ col : columns) {
                    row.add(values.get(columns.indexOf(col)));
                }
                csvPrinter.printRecord(row);
                csvPrinter.flush();
                csvPrinter.close();
                System.out.println("Fila insertada correctamente");
            }
        } catch (Exception e) {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            Querys.errors.add(err);
        }
    }

    public static void editCSV(PathQ path, ArrayList<AssignQuery> assigns){
        try {
            String temp = path.getPath().replace(".csv","1.csv");
            FileWriter writer = new FileWriter(temp);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

            FileReader fileReader = new FileReader(path.getPath());
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(fileReader);

            var col = new ArrayList<String>();
            var columns = new ArrayList<ColumnsQ>();
            var values = new ArrayList<String>();
            for(AssignQuery a: assigns){
                columns.add(a.getColumn());
                col.add(a.getColumn().getName());
                values.add(a.getValue());
            }

            if(columnExist(csvParser, columns)){
                for(CSVRecord csvRecord: csvParser){
                    List<String> row = new ArrayList<>();
                    for(String c: csvParser.getHeaderNames()){
                        if(col.contains(c)){
                            row.add(values.get(col.indexOf(c)));
                        }
                        else row.add(csvRecord.get(c));
                    }
                    csvPrinter.printRecord(row);
                }
                csvPrinter.flush();
            }
            csvPrinter.close();
            fileReader.close();

            Files.move(Paths.get(temp), Paths.get(path.getPath()), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Archivo actualizado correctamente");
        } catch (Exception e) {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            Querys.errors.add(err);
        }
    }
    public static void editCSV(PathQ path, ArrayList<AssignQuery> assigns, ArrayList<FiltersQuery> filters){
        try {
            String temp = path.getPath().replace(".csv","1.csv");
            FileWriter writer = new FileWriter(temp);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

            FileReader fileReader = new FileReader(path.getPath());
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(fileReader);

            var col = new ArrayList<String>();
            var columns = new ArrayList<ColumnsQ>();
            var values = new ArrayList<String>();
            for(AssignQuery a: assigns){
                columns.add(a.getColumn());
                values.add(a.getValue());
                col.add(a.getColumn().getName());
            }

            if(columnExist(csvParser, columns)){
                for(CSVRecord csvRecord: csvParser){
                    if(conditions(csvRecord, filters)){
                        List<String> row = new ArrayList<>();
                        for(String c: csvParser.getHeaderNames()){
                            if(col.contains(c)){
                                row.add(values.get(col.indexOf(c)));
                            }
                            else row.add(csvRecord.get(c));
                        }
                        csvPrinter.printRecord(row);
                    }
                    else csvPrinter.printRecord(csvRecord);
                }
                csvPrinter.flush();
            }
            csvPrinter.close();
            fileReader.close();

            Files.move(Paths.get(temp), Paths.get(path.getPath()), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Archivo actualizado correctamente");
        } catch (Exception e) {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            Querys.errors.add(err);
        }
    }

    public static void cleanCSV(PathQ path){
        try {
            String temp = path.getPath().replace(".csv","1.csv");
            FileWriter writer = new FileWriter(temp);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

            FileReader fileReader = new FileReader(path.getPath());
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(fileReader);

            csvPrinter.printRecord(csvParser.getHeaderNames());

            csvPrinter.flush();
            csvPrinter.close();
            fileReader.close();

            Files.move(Paths.get(temp), Paths.get(path.getPath()), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Archivo actualizado correctamente");
        } catch (Exception e) {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            Querys.errors.add(err);
        }
    }
    public static void cleanCSV(PathQ path, ArrayList<FiltersQuery> filters){
        try {
            String temp = path.getPath().replace(".csv","1.csv");
            FileWriter writer = new FileWriter(temp);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

            FileReader fileReader = new FileReader(path.getPath());
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(fileReader);
            for(CSVRecord csvRecord: csvParser){
                if(!conditions(csvRecord, filters)){
                    csvPrinter.printRecord(csvRecord);
                }
            }
            csvPrinter.flush();
            csvPrinter.close();
            fileReader.close();

            Files.move(Paths.get(temp), Paths.get(path.getPath()), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Archivo actualizado correctamente");
        } catch (Exception e) {
            TError err = new TError(path.getPath(),"Semántico(?","Archivo inexistente", path.getLine(), path.getCol());
            Querys.errors.add(err);
        }
    }
    public static boolean conditions(CSVRecord record,ArrayList<FiltersQuery> filters){
        boolean conditions = false;
        int type = filters.get(filters.size()-1).getType();

        for(FiltersQuery f: filters){
            conditions = f.filter(record);
            if(type == 3 && conditions) break;
        }
        return conditions;
    }
    public static ArrayList<ColumnsQ> mergeColumns(ArrayList<ColumnsQ> columns, ArrayList<FiltersQuery> filters){
        var mergedCol = new ArrayList<>(columns);
        for(FiltersQuery f: filters){
            mergedCol.add(f.getColumn());
        }
        return mergedCol;
    }
    public static boolean columnExist(CSVParser parser, ArrayList<ColumnsQ> columns){
        boolean allColumns = false;
        for(ColumnsQ c: columns){
            if(parser.getHeaderMap().containsKey(c.getName())){
                allColumns = true;
            }
            else{
                System.out.println("No existe la columna: " + c);
                TError err = new TError(c.getName(), "Semántico(?", "Columna inexistente", c.getLine(), c.getCol());
                Querys.errors.add(err);
                allColumns = false;
            }
        }
        return allColumns;
    }
}
