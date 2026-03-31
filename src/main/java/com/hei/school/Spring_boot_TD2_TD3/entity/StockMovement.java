package com.hei.school.Spring_boot_TD2_TD3.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockMovement {
    private int id;
    private StockValue value;
    private MovementTypeEnum type;
    private Instant creationDatetime;
}