package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Product;
import org.lisovskyi_ivanov.backend.exception.NotFoundException;
import org.lisovskyi_ivanov.backend.repository.product_repos.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> findByCategoryNumber(Long categoryNumber) {
        if (categoryNumber == null) {
            throw new IllegalArgumentException("Category number must not be null");
        }
        return repository.findByCategoryNumber(categoryNumber);
    }

    @Transactional(readOnly = true)
    public List<Product> findAllOrderByProductName() {
        return repository.findAllOrderByProductName();
    }


    @Transactional(readOnly = true)
    public Product findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(Product.class, id));
    }

    @Transactional(readOnly = true)
    public Product findByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        return repository.findByName(name)
                .orElseThrow(() -> new NotFoundException(Product.class, "name", name));
    }

    @Transactional
    public Product save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        return repository.save(product);
    }

    @Transactional
    public Product update(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        Long id = product.getIdProduct();
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        int rows = repository.update(product);
        if (rows == 0) {
            throw new NotFoundException(Product.class, id);
        }
        return product;
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        return repository.existsById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        int rows = repository.deleteById(id);
        if (rows == 0) {
            throw new NotFoundException(Product.class, id);
        }
    }

    @Transactional
    public void delete(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        Long id = product.getIdProduct();
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        int rows = repository.deleteById(id);
        if (rows == 0) {
            throw new NotFoundException(Product.class, id);
        }
    }


}
