package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.StoreProduct;
import org.lisovskyi_ivanov.backend.repository.store_product_repos.StoreProductRepository;
import org.springframework.stereotype.Service;

import static org.lisovskyi_ivanov.backend.utility.StringGenerator.generateUniqueString;

@Service
@RequiredArgsConstructor
public class StoreProductService {
    private final StoreProductRepository repository;


    public StoreProduct save(StoreProduct storeProduct) {
        if (storeProduct.getUpc() == null || storeProduct.getUpc().isBlank()) {
            String upc = generateUniqueString(12);
            storeProduct.setUpc(upc);
        }
        return repository.save(storeProduct);
    }


}
