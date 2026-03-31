package com.hei.school.Spring_boot_TD2_TD3.service;

import com.hei.school.Spring_boot_TD2_TD3.entity.Dish;
import com.hei.school.Spring_boot_TD2_TD3.entity.Ingredient;
import com.hei.school.Spring_boot_TD2_TD3.exception.NotFoundException;
import com.hei.school.Spring_boot_TD2_TD3.repository.DishRepository;
import com.hei.school.Spring_boot_TD2_TD3.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public List<Ingredient> findIngredientsByDishId(int dishId, String ingredientName, Double ingredientPriceAround) {
    findById(dishId);

    return dishRepository.findIngredientsByDishId(dishId, ingredientName, ingredientPriceAround);
}

    public Dish findById(int id) {
        Dish dish = dishRepository.findById(id);
        if (dish == null) {
            throw new NotFoundException("Dish.id=" + id + " is not found");
        }
        return dish;
    }


    public Dish updateIngredients(int dishId, List<Ingredient> ingredients) {
        findById(dishId);

        List<Integer> validIds = ingredientRepository.findAll()
                .stream()
                .map(Ingredient::getId)
                .collect(Collectors.toList());

        List<Ingredient> validIngredients = ingredients.stream()
                .filter(i -> validIds.contains(i.getId()))
                .collect(Collectors.toList());

        return dishRepository.updateIngredients(dishId, validIngredients);
    }
}