package com.wasacz.hfms.utils.randomizer;

import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Service
public class Randomizer {

    HashMap<String, Properties> expenseCategories;
    HashMap<String, Properties> incomeCategories;

    public Randomizer() {
        expenseCategories = new HashMap<>();
        createExpenseCategoriesList();
        incomeCategories = new HashMap<>();
        createIncomeCategoriesList();
    }

    public HashMap<String, Properties> getExpenseCategories() {
        return expenseCategories;
    }

    public HashMap<String, Properties> getIncomeCategories() {
        return incomeCategories;
    }

    private void createExpenseCategoriesList() {
        expenseCategories.put("Grocery Shopping", Properties.builder().products(
                    List.of(
                        Product.builder().name("Grocery Shopping").minAmount(BigDecimal.valueOf(60)).maxAmount(BigDecimal.valueOf(200)).build()
                    )
        ).build());
        expenseCategories.put("Transport", Properties.builder().products(
                    List.of(
                        Product.builder().name("PKP").minAmount(BigDecimal.valueOf(10)).maxAmount(BigDecimal.valueOf(100)).build(),
                        Product.builder().name("PKS").minAmount(BigDecimal.valueOf(5)).maxAmount(BigDecimal.valueOf(50)).build(),
                        Product.builder().name("MPK").minAmount(BigDecimal.valueOf(1)).maxAmount(BigDecimal.valueOf(100)).build(),
                        Product.builder().name("Flight").minAmount(BigDecimal.valueOf(100)).maxAmount(BigDecimal.valueOf(1000)).build(),
                        Product.builder().name("Bike repair").minAmount(BigDecimal.valueOf(50)).maxAmount(BigDecimal.valueOf(350)).build()
                    )
        ).build());
        expenseCategories.put("Car", Properties.builder().products(
                List.of(
                        Product.builder().name("Fuel").minAmount(BigDecimal.valueOf(50)).maxAmount(BigDecimal.valueOf(250)).build(),
                        Product.builder().name("Car repair").minAmount(BigDecimal.valueOf(100)).maxAmount(BigDecimal.valueOf(2500)).build()
                )
        ).build());
        expenseCategories.put("Bills", Properties.builder().products(
                List.of(
                        Product.builder().name("Internet").minAmount(BigDecimal.valueOf(30)).maxAmount(BigDecimal.valueOf(100)).build(),
                        Product.builder().name("Water").minAmount(BigDecimal.valueOf(20)).maxAmount(BigDecimal.valueOf(50)).build(),
                        Product.builder().name("Electricity").minAmount(BigDecimal.valueOf(20)).maxAmount(BigDecimal.valueOf(50)).build(),
                        Product.builder().name("TV").minAmount(BigDecimal.valueOf(30)).maxAmount(BigDecimal.valueOf(80)).build(),
                        Product.builder().name("Phone").minAmount(BigDecimal.valueOf(25)).maxAmount(BigDecimal.valueOf(60)).build()
                        )
        ).build());
        expenseCategories.put("Culture and entertainment", Properties.builder().products(
                List.of(
                        Product.builder().name("Cinema").minAmount(BigDecimal.valueOf(10)).maxAmount(BigDecimal.valueOf(30)).build(),
                        Product.builder().name("Theatre").minAmount(BigDecimal.valueOf(50)).maxAmount(BigDecimal.valueOf(150)).build(),
                        Product.builder().name("Opera").minAmount(BigDecimal.valueOf(100)).maxAmount(BigDecimal.valueOf(250)).build(),
                        Product.builder().name("Philharmonic").minAmount(BigDecimal.valueOf(100)).maxAmount(BigDecimal.valueOf(300)).build()
                )
        ).build());
        expenseCategories.put("Health and beauty", Properties.builder().products(
                List.of(
                        Product.builder().name("Medication").minAmount(BigDecimal.valueOf(20)).maxAmount(BigDecimal.valueOf(80)).build(),
                        Product.builder().name("Treatment").minAmount(BigDecimal.valueOf(50)).maxAmount(BigDecimal.valueOf(500)).build(),
                        Product.builder().name("Gym").minAmount(BigDecimal.valueOf(50)).maxAmount(BigDecimal.valueOf(60)).build()
                        )
        ).build());
        expenseCategories.put("Personal development", Properties.builder().products(
                List.of(
                        Product.builder().name("Books").minAmount(BigDecimal.valueOf(20)).maxAmount(BigDecimal.valueOf(80)).build(),
                        Product.builder().name("Courses").minAmount(BigDecimal.valueOf(39)).maxAmount(BigDecimal.valueOf(200)).build(),
                        Product.builder().name("School").minAmount(BigDecimal.valueOf(250)).maxAmount(BigDecimal.valueOf(500)).build()
                        )
        ).build());
        expenseCategories.put("Home", Properties.builder().products(
                List.of(
                        Product.builder().name("Furniture").minAmount(BigDecimal.valueOf(50)).maxAmount(BigDecimal.valueOf(1000)).build(),
                        Product.builder().name("Cleaning products").minAmount(BigDecimal.valueOf(10)).maxAmount(BigDecimal.valueOf(100)).build()
                )
        ).build());
        expenseCategories.put("Clothes and accessories", Properties.builder().products(
                List.of(
                    Product.builder().name("Clothes").minAmount(BigDecimal.valueOf(50)).maxAmount(BigDecimal.valueOf(350)).build(),
                    Product.builder().name("Accessories").minAmount(BigDecimal.valueOf(10)).maxAmount(BigDecimal.valueOf(100)).build()
                )
        ).build());
        expenseCategories.put("Other", Properties.builder().products(
                List.of(
                        Product.builder().name("Other expense").minAmount(BigDecimal.valueOf(1)).maxAmount(BigDecimal.valueOf(25)).build()
                )
        ).build());
    }

    private void createIncomeCategoriesList() {
        incomeCategories.put("Salary", Properties.builder().products(
                List.of(
                        Product.builder().name("Salary").minAmount(BigDecimal.valueOf(5000)).maxAmount(BigDecimal.valueOf(7000)).build()
                )
        ).build());
        incomeCategories.put("Selling", Properties.builder().products(
                List.of(
                        Product.builder().name("Selling something").minAmount(BigDecimal.valueOf(10)).maxAmount(BigDecimal.valueOf(100)).build()
                )
        ).build());
        incomeCategories.put("Gifts", Properties.builder().products(
                List.of(
                        Product.builder().name("Gifts").minAmount(BigDecimal.valueOf(50)).maxAmount(BigDecimal.valueOf(100)).build()
                )
        ).build());
    }

    @Getter
    public static class Properties {
        List<Product> products;
        BigDecimal plannedAmount;

        @Builder
        public Properties(List<Product> products, BigDecimal plannedAmount) {
            this.products = products;
            this.plannedAmount = plannedAmount;
        }
    }

    @Getter
    public static class Product {
        String name;
        BigDecimal minAmount;
        BigDecimal maxAmount;

        @Builder
        public Product(String name, BigDecimal minAmount, BigDecimal maxAmount) {
            this.name = name;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
        }
    }
}

