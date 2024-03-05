package com.navi.backend.csv_controller;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor @Builder
public class PathQ {
    private String path;
    private int line, col;
}
