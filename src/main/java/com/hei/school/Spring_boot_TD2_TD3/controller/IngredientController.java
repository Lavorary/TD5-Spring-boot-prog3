package com.hei.school.Spring_boot_TD2_TD3.controller;

import com.hei.school.Spring_boot_TD2_TD3.entity.Ingredient;
import com.hei.school.Spring_boot_TD2_TD3.entity.StockValue;
import com.hei.school.Spring_boot_TD2_TD3.entity.UnitEnum;
import com.hei.school.Spring_boot_TD2_TD3.exception.NotFoundException;
import com.hei.school.Spring_boot_TD2_TD3.service.IngredientService;
import com.hei.school.Spring_boot_TD2_TD3.service.StockValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;
    private final StockValueService stockValueService;

    @GetMapping("/ingredients")
    public ResponseEntity<List<Ingredient>> findAll() {
        return ResponseEntity.ok(ingredientService.findAll());
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(ingredientService.findById(id));
        } catch (NotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .header("Content-Type", "text/plain")
                    .body(e.getMessage());
        }
    }

    @GetMapping("/ingredients/{id}/stock")
    public ResponseEntity<?> getStockValueAt(
            @PathVariable int id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit) {

        if (at == null || unit == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "text/plain")
                    .body("Either mandatory query parameter `at` or `unit` is not provided.");
        }

        try {
            Instant instant = Instant.parse(at);
            UnitEnum unitEnum = UnitEnum.valueOf(unit.toUpperCase());
            StockValue stockValue = stockValueService.getStockValueAt(id, instant, unitEnum);
            return ResponseEntity.ok(stockValue);
        } catch (NotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .header("Content-Type", "text/plain")
                    .body(e.getMessage());
        }
    }
}