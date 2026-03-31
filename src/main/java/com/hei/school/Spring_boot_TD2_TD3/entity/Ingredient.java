package com.hei.school.Spring_boot_TD2_TD3.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {
    private int id;
    private String name;
    private double price;
    private CategoryEnum category;
    private List<StockMovement> stockMovementList;

    public StockValue getStockValueAt(Instant t) {
        if (stockMovementList == null || stockMovementList.isEmpty()) {
            return new StockValue(0, UnitEnum.KG);
        }
        double total = stockMovementList.stream()
                .filter(m -> !m.getCreationDatetime().isAfter(t))
                .mapToDouble(m -> m.getType() == MovementTypeEnum.IN
                        ? m.getValue().getQuantity()
                        : -m.getValue().getQuantity())
                .sum();
        return new StockValue(total, UnitEnum.KG);
    }
}