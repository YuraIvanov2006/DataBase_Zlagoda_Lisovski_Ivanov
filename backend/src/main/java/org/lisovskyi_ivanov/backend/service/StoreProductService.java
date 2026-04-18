package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.StoreProduct;
import org.lisovskyi_ivanov.backend.exception.NotFoundException;
import org.lisovskyi_ivanov.backend.repository.store_product_repos.StoreProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreProductService {
    private final StoreProductRepository repository;

    @Lazy
    @Autowired
    private StoreProductService self;

    @Transactional(readOnly = true)
    public List<StoreProduct> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<StoreProduct> findAllPromotional() {
        return repository.findAllPromotional();
    }

    @Transactional(readOnly = true)
    public List<StoreProduct> findAllNotPromotional() {
        return repository.findAllNotPromotional();
    }

    @Transactional(readOnly = true)
    public List<StoreProduct> findAllByProductId(Long productId) {
        if (productId == null)
            throw new IllegalArgumentException("Product ID must not be null");
        return repository.findAllByProductId(productId);
    }

    @Transactional(readOnly = true)
    public StoreProduct findByUPC(String upc) {
        if (upc == null || upc.isBlank())
            throw new IllegalArgumentException("UPC must not be null or blank");
        return repository.findByUPC(upc)
                .orElseThrow(() -> new NotFoundException(StoreProduct.class, "upc", upc));
    }

    @Transactional
    public StoreProduct save(StoreProduct storeProduct) {
        if (storeProduct == null)
            throw new IllegalArgumentException("StoreProduct must not be null");

        // Акційний товар обов'язково повинен мати базовий товар
        if (storeProduct.isPromotionalProduct()) {
            if (storeProduct.getBaseProduct() == null ||
                    storeProduct.getBaseProduct().getUpc() == null) {
                throw new IllegalArgumentException(
                        "Promotional product must have a base product reference");
            }
            StoreProduct base = self.findByUPC(storeProduct.getBaseProduct().getUpc());
            storeProduct.setSellingPrice(calculatePromoPrice(base.getSellingPrice()));
        }

        validateNonNegative(storeProduct.getSellingPrice(), "Selling price");
        validateNonNegative(BigDecimal.valueOf(storeProduct.getProductsNumber()), "Products number");

        return repository.save(storeProduct);
    }

    @Transactional
    public StoreProduct update(StoreProduct storeProduct) {
        if (storeProduct == null)
            throw new IllegalArgumentException("StoreProduct must not be null");

        validateNonNegative(storeProduct.getSellingPrice(), "Selling price");
        validateNonNegative(BigDecimal.valueOf(storeProduct.getProductsNumber()), "Products number");

        if (!storeProduct.isPromotionalProduct()) {
            List<StoreProduct> promoProducts =
                    repository.findAllByBaseProductUpc(storeProduct.getUpc());
            for (StoreProduct promo : promoProducts) {
                promo.setSellingPrice(calculatePromoPrice(storeProduct.getSellingPrice()));
                repository.update(promo);
            }
        }

        // Акційний товар обов'язково повинен мати базовий товар
        if (storeProduct.isPromotionalProduct()) {
            if (storeProduct.getBaseProduct() == null ||
                    storeProduct.getBaseProduct().getUpc() == null) {
                throw new IllegalArgumentException(
                        "Promotional product must have a base product reference");
            }
            StoreProduct base = self.findByUPC(storeProduct.getBaseProduct().getUpc());
            storeProduct.setSellingPrice(calculatePromoPrice(base.getSellingPrice()));
        }

        int rows = repository.update(storeProduct);
        if (rows == 0)
            throw new NotFoundException(StoreProduct.class, "upc", storeProduct.getUpc());
        return storeProduct;
    }

    @Transactional
    public void deleteByUPC(String upc) {
        if (upc == null || upc.isBlank())
            throw new IllegalArgumentException("UPC must not be null or blank");
        int rows = repository.deleteByUPC(upc);
        if (rows == 0)
            throw new NotFoundException(StoreProduct.class, "upc", upc);
    }

    @Transactional
    public void delete(StoreProduct storeProduct) {
        if (storeProduct == null)
            throw new IllegalArgumentException("Store product must not be null");
        int deleted = repository.delete(storeProduct);
        if (deleted == 0)
            throw new NotFoundException(StoreProduct.class, "upc", storeProduct.getUpc());
    }

    // private methods
    private BigDecimal calculatePromoPrice(BigDecimal basePrice) {
        return basePrice.multiply(BigDecimal.valueOf(0.8))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void validateNonNegative(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException(fieldName + " must not be null or negative");
    }
}