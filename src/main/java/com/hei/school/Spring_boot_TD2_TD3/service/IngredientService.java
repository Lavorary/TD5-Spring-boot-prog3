package com.hei.school.Spring_boot_TD2_TD3.service;

import com.hei.school.Spring_boot_TD2_TD3.entity.Ingredient;
import com.hei.school.Spring_boot_TD2_TD3.entity.StockValue;
import com.hei.school.Spring_boot_TD2_TD3.exception.NotFoundException;
import com.hei.school.Spring_boot_TD2_TD3.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public List<Ingredient> findAll(){
        return ingredientRepository.findAll();
    }

    public Ingredient findById(int id){
        Ingredient ingredient = ingredientRepository.findById(id);

        if(ingredient == null){
            throw new NotFoundException("Ingredient.id=" + id + " is not found");
        }

        return ingredient;
    }


    public Ingredient save(Ingredient ingredient){
        return ingredientRepository.save(ingredient);
    }
}
