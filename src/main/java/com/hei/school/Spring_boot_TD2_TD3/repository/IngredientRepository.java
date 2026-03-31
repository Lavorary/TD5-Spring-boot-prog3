package com.hei.school.Spring_boot_TD2_TD3.repository;

import com.hei.school.Spring_boot_TD2_TD3.datasource.DataSource;
import com.hei.school.Spring_boot_TD2_TD3.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class IngredientRepository {

    private final DataSource dataSource;

    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = """
    select id, reference, creation_datetime, status, total_ttc
                    from "order" o
                    group by id
                    order by id desc
""";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ingredients.add(mapIngredient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ingredients;
    }

    public Ingredient findById(int id) {

        String sql = """
                select id, name, price, category, required_quantity, initial_stock
                from ingredient where id = ?
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Ingredient ingredient = mapIngredient(rs);
                ingredient.setStockMovementList(findStockMovements(conn, id));
                return ingredient;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<StockMovement> findStockMovements(Connection conn, int ingredientId) throws SQLException {
        List<StockMovement> movements = new ArrayList<>();
        String sql = """
                select id, id_ingredient, quantity, type, unit, creation_datetime
                from stock_movement where id_ingredient = ?
                order by creation_datetime desc
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ingredientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movements.add(mapStockMovement(rs));
            }
        }
        return movements;
    }

    public Ingredient save(Ingredient ingredient) {
        String upsertSql = """
                INSERT INTO ingredient (name, price, category)
                VALUES (?, ?, ?::ingredient_type)
                ON CONFLICT (name) DO NOTHING
                RETURNING id
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(upsertSql)) {
            stmt.setString(1, ingredient.getName());
            stmt.setDouble(2, ingredient.getPrice());
            stmt.setString(3, ingredient.getCategory().name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ingredient.setId(rs.getInt("id"));
            }
            if (ingredient.getStockMovementList() != null) {
                saveStockMovements(conn, ingredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ingredient;
    }

    private void saveStockMovements(Connection conn, Ingredient ingredient) throws SQLException {
        String sql = """
                INSERT INTO stock_movement (id_ingredient, quantity, type, unit, creation_datetime)
                VALUES (?, ?, ?::movement_type, ?::unit, ?)
                ON CONFLICT (id) DO NOTHING
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (StockMovement movement : ingredient.getStockMovementList()) {
                stmt.setInt(1, ingredient.getId());
                stmt.setDouble(2, movement.getValue().getQuantity());
                stmt.setString(3, movement.getType().name());
                stmt.setString(4, movement.getValue().getUnit().name());
                stmt.setTimestamp(5, Timestamp.from(movement.getCreationDatetime()));
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private Ingredient mapIngredient(ResultSet rs) throws SQLException {
        return Ingredient.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .price(rs.getDouble("price"))
                .category(CategoryEnum.valueOf(rs.getString("category")))
                .stockMovementList(new ArrayList<>())
                .build();
    }

    private StockMovement mapStockMovement(ResultSet rs) throws SQLException {
        return StockMovement.builder()
                .id(rs.getInt("id"))
                .value(new StockValue(
                        rs.getDouble("quantity"),
                        UnitEnum.valueOf(rs.getString("unit"))
                ))
                .type(MovementTypeEnum.valueOf(rs.getString("type")))
                .creationDatetime(rs.getTimestamp("creation_datetime").toInstant())
                .build();
    }
}