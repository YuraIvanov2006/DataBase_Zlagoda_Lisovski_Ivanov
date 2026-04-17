package org.lisovskyi_ivanov.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.dto.request.StoreProductRequest;
import org.lisovskyi_ivanov.backend.dto.response.StoreProductDto;
import org.lisovskyi_ivanov.backend.entity.Product;
import org.lisovskyi_ivanov.backend.entity.StoreProduct;
import org.lisovskyi_ivanov.backend.mapping.dto.StoreProductMapper;
import org.lisovskyi_ivanov.backend.service.StoreProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/store-products")
@RequiredArgsConstructor
public class StoreProductController {
    private final StoreProductService storeProductService;
    private final StoreProductMapper storeProductMapper;

    // -- GET
    @GetMapping
    public ResponseEntity<List<StoreProductDto>> getAllStoreProducts() {
        List<StoreProductDto> products = storeProductService.findAll().stream()
                .map(storeProductMapper::toDto)
                .toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/promotional")
    public ResponseEntity<List<StoreProductDto>> getPromotionalStoreProducts() {
        List<StoreProductDto> products = storeProductService.findAllPromotional().stream()
                .map(storeProductMapper::toDto)
                .toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/not-promotional")
    public ResponseEntity<List<StoreProductDto>> getNotPromotionalStoreProducts() {
        List<StoreProductDto> products = storeProductService.findAllNotPromotional().stream()
                .map(storeProductMapper::toDto)
                .toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StoreProductDto>> getStoreProductsByProductId(@PathVariable Long productId) {
        List<StoreProductDto> products = storeProductService.findAllByProductId(productId).stream()
                .map(storeProductMapper::toDto)
                .toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{upc}")
    public ResponseEntity<StoreProductDto> getStoreProductByUpc(@PathVariable String upc) {
        StoreProduct storeProduct = storeProductService.findByUPC(upc);
        return ResponseEntity.ok(storeProductMapper.toDto(storeProduct));
    }

    // -- POST
    @PostMapping
    public ResponseEntity<StoreProductDto> createStoreProduct(@Valid @RequestBody StoreProductRequest request) {
        // 1. Мапимо Request у сутність
        StoreProduct storeProductToSave = storeProductMapper.toEntity(request);

        // Додаємо "заглушки" зв'язків для бази даних
        if (request.idProduct() != null) {
            storeProductToSave.setProduct(Product.builder().idProduct(request.idProduct()).build());
        }
        if (request.baseProductUpc() != null && !request.baseProductUpc().isBlank()) {
            storeProductToSave.setBaseProduct(StoreProduct.builder().upc(request.baseProductUpc()).build());
        }

        // 2. Зберігаємо
        StoreProduct savedStoreProduct = storeProductService.save(storeProductToSave);

        // 3. Віддаємо назад DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(storeProductMapper.toDto(savedStoreProduct));
    }

    // -- PUT
    @PutMapping("/{upc}")
    public ResponseEntity<StoreProductDto> updateStoreProduct(
            @PathVariable String upc,
            @Valid @RequestBody StoreProductRequest request) {

        // Створюємо сутність з новими даними
        StoreProduct storeProductToUpdate = storeProductMapper.toEntity(request);

        // Обов'язково сетаємо UPC з URL
        storeProductToUpdate.setUpc(upc);

        // Відновлюємо зв'язки
        if (request.idProduct() != null) {
            storeProductToUpdate.setProduct(Product.builder().idProduct(request.idProduct()).build());
        }
        if (request.baseProductUpc() != null && !request.baseProductUpc().isBlank()) {
            storeProductToUpdate.setBaseProduct(StoreProduct.builder().upc(request.baseProductUpc()).build());
        }

        StoreProduct updatedStoreProduct = storeProductService.update(storeProductToUpdate);
        return ResponseEntity.ok(storeProductMapper.toDto(updatedStoreProduct));
    }

    // -- DELETE
    @DeleteMapping("/{upc}")
    public ResponseEntity<Void> deleteStoreProduct(@PathVariable String upc) {
        storeProductService.deleteByUPC(upc);
        return ResponseEntity.noContent().build();
    }
}