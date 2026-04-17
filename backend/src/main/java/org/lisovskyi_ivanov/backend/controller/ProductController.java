package org.lisovskyi_ivanov.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.dto.request.ProductRequest;
import org.lisovskyi_ivanov.backend.dto.response.ProductDto;
import org.lisovskyi_ivanov.backend.entity.Product;
import org.lisovskyi_ivanov.backend.mapping.dto.ProductMapper;
import org.lisovskyi_ivanov.backend.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.findAll().stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ProductDto> getProductByName(@PathVariable String name) {
        Product product = productService.findByName(name);
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @GetMapping("/category/{categoryNumber}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable Long categoryNumber) {
        List<ProductDto> products = productService.findByCategoryNumber(categoryNumber).stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/ordered")
    public ResponseEntity<List<ProductDto>> getProductsOrderedByName() {
        List<ProductDto> products = productService.findAllOrderByProductName().stream()
                .map(productMapper::toDto)
                .toList();
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductRequest request) {
        Product productToSave = productMapper.toEntity(request);
        Product savedProduct = productService.save(productToSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toDto(savedProduct));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        Product productToUpdate = productMapper.toEntity(request);
        productToUpdate.setIdProduct(id);

        Product updatedProduct = productService.update(productToUpdate);
        return ResponseEntity.ok(productMapper.toDto(updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}