package com.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.api.model.Product;
import com.api.model.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private List<Product> products = new ArrayList<>();
    private final String JSON_FILE = "products.json";
    private final ObjectMapper objectMapper;

    public ProductService() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setDefaultPrettyPrinter(new CustomPrettyPrinter());
    }

    @Autowired
    private ProductValidator validator;

    @PostConstruct
    public void init() {
        loadProductsFromFile();
    }

    private void loadProductsFromFile() {
        try {
            File file = new File(JSON_FILE);
            if (file.exists()) {
                products = objectMapper.readValue(file, new TypeReference<List<Product>>() {});
            } else {
                products = createInitialData();
                saveProductsToFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Product> createInitialData() {
        List<Product> initialData = new ArrayList<>();
        return initialData;
    }

    private void saveProductsToFile() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                       .writeValue(new File(JSON_FILE), products);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public Optional<Product> getProductById(String id) {
        return products.stream()
                      .filter(p -> p.getId().equals(id))
                      .findFirst();
    }

    private String getNextAvailableId() {
        if (products.isEmpty()) {
            return "1";
        }

        // Convertir les IDs en entiers et les trier
        List<Integer> ids = new ArrayList<>();
        for (Product p : products) {
            ids.add(Integer.parseInt(p.getId()));
        }
        Collections.sort(ids);

        // Trouver le premier ID manquant
        int expectedId = 1;
        for (int currentId : ids) {
            if (currentId != expectedId) {
                return String.valueOf(expectedId);
            }
            expectedId++;
        }

        // Si aucun ID manquant n'est trouvé, retourner le suivant
        return String.valueOf(expectedId);
    }

    public Product addProduct(Product product) {
        validator.validateProduct(product);

        String nextId = getNextAvailableId();
        product.setId(nextId);

        products.add(product);
        saveProductsToFile();
        return product;
    }

    public Optional<Product> updateProduct(String id, Product updatedProduct) {
        validator.validateProduct(updatedProduct);

        Optional<Product> existingProduct = getProductById(id);
        if (!existingProduct.isPresent()) {
            return Optional.empty();
        }

        products.removeIf(p -> p.getId().equals(id));
        updatedProduct.setId(id);
        products.add(updatedProduct);
        saveProductsToFile();
        return Optional.of(updatedProduct);
    }

    public boolean deleteProduct(String id) {
        boolean removed = products.removeIf(p -> p.getId().equals(id));
        if (removed) {
            saveProductsToFile();
        }
        return removed;
    }
}