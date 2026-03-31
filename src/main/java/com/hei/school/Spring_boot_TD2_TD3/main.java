package com.hei.school.Spring_boot_TD2_TD3;

import com.hei.school.Spring_boot_TD2_TD3.datasource.DataSource;
import com.hei.school.Spring_boot_TD2_TD3.service.IngredientService;

public class main {
    public static void main(String[] args) {
        DataSource dataSource = new DataSource();
        System.out.println(dataSource.getConnection());
    }
}
