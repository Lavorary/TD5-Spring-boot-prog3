package com.hei.school.Spring_boot_TD2_TD3.controller;


import com.hei.school.Spring_boot_TD2_TD3.entity.Dish;
import com.hei.school.Spring_boot_TD2_TD3.entity.Ingredient;
import com.hei.school.Spring_boot_TD2_TD3.exception.NotFoundException;
import com.hei.school.Spring_boot_TD2_TD3.repository.DishRepository;
import com.hei.school.Spring_boot_TD2_TD3.repository.IngredientRepository;
import com.hei.school.Spring_boot_TD2_TD3.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DishController {
    private final DishRepository dishRepository;
    private final DishService dishService;
    private final IngredientRepository ingredientRepository;

    @GetMapping("/dishes")
    public ResponseEntity<List<Dish>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "text/plain")
                .body(dishService.findAll());
    }


    @PutMapping("/dishes/{id}/ingredients")
    public ResponseEntity<?> updateIngredients(
            @PathVariable int id,
            @RequestBody(required = false) List<Ingredient> ingredients) {

        if (ingredients == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "text/plain")
                    .body("Request body is required.");
        }

        try {
            Dish updated = dishService.updateIngredients(id, ingredients);
            return ResponseEntity.ok(updated);
        } catch (NotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .header("Content-Type", "text/plain")
                    .body(e.getMessage());
        }
    }

    @GetMapping("/dishes/{id}/ingredients")
public ResponseEntity<?> findIngredientsByDishId(
        @PathVariable int id,
        @RequestParam(required = false) String ingredientName,
        @RequestParam(required = false) Double ingredientPriceAround) {
    try {
        List<Ingredient> ingredients = dishService.findIngredientsByDishId(
                id, ingredientName, ingredientPriceAround);
        return ResponseEntity.ok(ingredients);
    } catch (NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header("Content-Type", "text/plain")
                .body("Dish.id={id)" + id + "is not found.");
    }
}
}
