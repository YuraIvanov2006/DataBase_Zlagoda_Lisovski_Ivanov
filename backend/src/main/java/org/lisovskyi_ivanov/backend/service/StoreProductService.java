package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.repository.store_product_repos.StoreProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreProductService {
    private final StoreProductRepository repository;


}
