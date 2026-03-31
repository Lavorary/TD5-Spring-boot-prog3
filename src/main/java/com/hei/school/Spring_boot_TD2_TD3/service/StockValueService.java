package com.hei.school.Spring_boot_TD2_TD3.service;

import com.hei.school.Spring_boot_TD2_TD3.entity.Ingredient;
import com.hei.school.Spring_boot_TD2_TD3.entity.StockValue;
import com.hei.school.Spring_boot_TD2_TD3.entity.UnitEnum;
import com.hei.school.Spring_boot_TD2_TD3.exception.NotFoundException;
import com.hei.school.Spring_boot_TD2_TD3.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class StockValueService {

    private final IngredientRepository ingredientRepository;

    public StockValue getStockValueAt(int ingredientId, Instant at, UnitEnum unit) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId);
        if (ingredient == null) {
            throw new NotFoundException("Ingredient.id=" + ingredientId + " is not found");
        }
        return ingredient.getStockValueAt(at);
    }
}