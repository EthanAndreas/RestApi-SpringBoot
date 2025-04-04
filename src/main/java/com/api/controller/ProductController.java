package com.api.controller;

import com.api.exception.ItemNotFoundException;
import com.api.model.Product;
import com.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    // GET all items
    @GetMapping
    public ResponseEntity<List<Product>> getAllItems() {
        List<Product> items = productService.getAllProducts();
        return ResponseEntity.ok(items);
    }

    // GET item by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getItemById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id)
                .orElseThrow(() -> new ItemNotFoundException(id)));
    }

    // CREATE new item
    @PostMapping
    public ResponseEntity<Product> createItem(@RequestBody Product product) {
        Product newProduct = productService.addProduct(product);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    // UPDATE item
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateItem(@PathVariable String id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product)
                .orElseThrow(() -> new ItemNotFoundException(id)));
    }

    // DELETE item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        if (!productService.deleteProduct(id)) {
            throw new ItemNotFoundException(id);
        }
        return ResponseEntity.noContent().build();
    }
}