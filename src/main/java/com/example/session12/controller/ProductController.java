package com.example.session12.controller;

import com.example.session12.entity.Product;
import com.example.session12.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 👀 Ai cũng xem được
    @GetMapping
    public List<Product> getAll() {
        return productService.getAll();
    }

    // 🔐 ADMIN / STAFF
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.create(product);
    }

    // 🔐 ADMIN / STAFF
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    // 🔐 ADMIN / STAFF
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "Deleted";
    }
}