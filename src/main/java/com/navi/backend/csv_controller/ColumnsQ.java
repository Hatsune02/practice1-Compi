package com.navi.backend.csv_controller;
import lombok.*;

@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor @Builder
public class ColumnsQ {
    private String name;
    private int line, col;
}
