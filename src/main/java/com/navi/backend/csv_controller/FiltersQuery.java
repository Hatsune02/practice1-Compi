package com.navi.backend.csv_controller;

import lombok.*;
import org.apache.commons.csv.CSVRecord;

@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor @Builder
public class FiltersQuery {
    private ColumnsQ column;
    private String sign;
    private String value;
    private int type;

    public boolean filter(CSVRecord record){
        return switch (sign) {
            case "=" -> filterE(record);
            case ">" -> filterG(record);
            case "<" -> filterL(record);
            case ">=" -> filterGE(record);
            case "<=" -> filterLE(record);
            case "<>" -> filterNE(record);
            default -> false;
        };
    }

    public boolean filterE(CSVRecord record){
        if(isNumber(value)){
            try{
                int col = Integer.parseInt(record.get(column.getName()));
                return col == Integer.parseInt(value);
            }catch (Exception e){
                return false;
            }
        }
        String col = record.get(column.getName());
        return value.equals(col);
    }
    public boolean filterG(CSVRecord record){
        if(isNumber(value)){
            try{
                int col = Integer.parseInt(record.get(column.getName()));
                return col > Integer.parseInt(value);
            }catch (Exception e){
                return false;
            }
        }
        String col = record.get(column.getName());
        return value.compareTo(col) > 0;
    }
    public boolean filterL(CSVRecord record){
        if(isNumber(value)){
            try{
                int col = Integer.parseInt(record.get(column.getName()));
                return col < Integer.parseInt(value);
            }catch (Exception e){
                return false;
            }
        }
        String col = record.get(column.getName());
        return value.compareTo(col) < 0;
    }
    public boolean filterGE(CSVRecord record){
        if(isNumber(value)){
            try{
                int col = Integer.parseInt(record.get(column.getName()));
                return col >= Integer.parseInt(value);
            }catch (Exception e){
                return false;
            }
        }
        String col = record.get(column.getName());
        return value.compareTo(col) >= 0;
    }
    public boolean filterLE(CSVRecord record){
        if(isNumber(value)){
            try{
                int col = Integer.parseInt(record.get(column.getName()));
                return col <= Integer.parseInt(value);
            }catch (Exception e){
                return false;
            }
        }
        String col = record.get(column.getName());
        return value.compareTo(col) <= 0;
    }
    public boolean filterNE(CSVRecord record){
        if(isNumber(value)){
            try{
                int col = Integer.parseInt(record.get(column.getName()));
                return col != Integer.parseInt(value);
            }catch (Exception e){
                return false;
            }
        }
        String col = record.get(column.getName());
        return !value.equals(col);
    }

    public boolean isNumber(String value){
        try{
            Integer.parseInt(value);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
