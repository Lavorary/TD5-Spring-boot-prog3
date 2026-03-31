package com.hei.school.Spring_boot_TD2_TD3.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dish {
    private int id;
    private String name;
    private DishTypeEnum dishType;
    private Double sellingPrice;
    private List<Ingredient> ingredients;

    public Double getDishCost() {
        if (ingredients == null || ingredients.isEmpty()) {
            return 0.0;
        }
        return ingredients.stream()
                .mapToDouble(Ingredient::getPrice)
                .sum();
    }

    public Double getGrossMargin() {
        if (sellingPrice == null) {
            throw new RuntimeException("Dish.sellingPrice cannot be null");
        }
        return sellingPrice - getDishCost();
    }
}