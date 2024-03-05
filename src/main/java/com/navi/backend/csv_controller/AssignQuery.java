package com.navi.backend.csv_controller;

import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor @Builder
public class AssignQuery {
    private ColumnsQ column;
    private String value;
}
