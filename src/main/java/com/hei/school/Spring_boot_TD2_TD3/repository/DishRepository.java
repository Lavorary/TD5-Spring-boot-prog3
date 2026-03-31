package com.hei.school.Spring_boot_TD2_TD3.repository;

import com.hei.school.Spring_boot_TD2_TD3.datasource.DataSource;
import com.hei.school.Spring_boot_TD2_TD3.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DishRepository {

    private final DataSource dataSource;

    public List<Dish> findAll() {
        List<Dish> dishes = new ArrayList<>();
        String sql = """
                SELECT d.id, d.name, d.dish_type, d.selling_price,
                       i.id as ing_id, i.name as ing_name,
                       i.price as ing_price, i.category as ing_category
                FROM dish d
                LEFT JOIN dish_ingredient di ON d.id = di.id_dish
                LEFT JOIN ingredient i ON di.id_ingredient = i.id
                ORDER BY d.id
                """;
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            dishes = mapDishes(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dishes;
    }

    public Dish findById(int id) {
        String sql = """
                SELECT d.id, d.name, d.dish_type, d.selling_price,
                       i.id as ing_id, i.name as ing_name,
                       i.price as ing_price, i.category as ing_category
                FROM dish d
                LEFT JOIN dish_ingredient di ON d.id = di.id_dish
                LEFT JOIN ingredient i ON di.id_ingredient = i.id
                WHERE d.id = ?
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            List<Dish> dishes = mapDishes(rs);
            return dishes.isEmpty() ? null : dishes.get(0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dish updateIngredients(int dishId, List<Ingredient> ingredients) {
        String deleteSql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
        String insertSql = """
                INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit)
                VALUES (?, ?, 1, 'KG'::unit)
                ON CONFLICT DO NOTHING
                """;
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, dishId);
                deleteStmt.executeUpdate();
            }
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Ingredient ingredient : ingredients) {
                    insertStmt.setInt(1, dishId);
                    insertStmt.setInt(2, ingredient.getId());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return findById(dishId);
    }

    private List<Dish> mapDishes(ResultSet rs) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        Dish currentDish = null;
        while (rs.next()) {
            int dishId = rs.getInt("id");
            if (currentDish == null || currentDish.getId() != dishId) {
                currentDish = Dish.builder()
                        .id(dishId)
                        .name(rs.getString("name"))
                        .dishType(DishTypeEnum.valueOf(rs.getString("dish_type")))
                        .sellingPrice(rs.getObject("selling_price") != null
                                ? rs.getDouble("selling_price") : null)
                        .ingredients(new ArrayList<>())
                        .build();
                dishes.add(currentDish);
            }
            if (rs.getObject("ing_id") != null) {
                currentDish.getIngredients().add(
                        Ingredient.builder()
                                .id(rs.getInt("ing_id"))
                                .name(rs.getString("ing_name"))
                                .price(rs.getDouble("ing_price"))
                                .category(CategoryEnum.valueOf(rs.getString("ing_category")))
                                .build()
                );
            }
        }
        return dishes;
    }
}