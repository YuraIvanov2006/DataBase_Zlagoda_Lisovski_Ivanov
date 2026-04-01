package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.CustomerCard;
import org.lisovskyi_ivanov.backend.exception.NotFoundException;
import org.lisovskyi_ivanov.backend.repository.customer_card_repos.CustomerCardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.lisovskyi_ivanov.backend.utility.StringGenerator.generateUniqueString;

@Service
@RequiredArgsConstructor
public class CustomerCardService {
    private final CustomerCardRepository repository;

    @Transactional(readOnly = true)
    public List<CustomerCard> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CustomerCard> findAllByCustSurname(String custSurname) {
        if (custSurname == null || custSurname.isBlank()) {
            throw new IllegalArgumentException("Customer surname must not be null or blank");
        }
        return repository.findAllByCustSurname(custSurname);
    }

    @Transactional(readOnly = true)
    public CustomerCard findByCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalArgumentException("Card number must not be null or blank");
        }
        return repository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new NotFoundException(CustomerCard.class, "cardNumber", cardNumber));
    }

    @Transactional
    public CustomerCard save(CustomerCard customerCard) {
        if (customerCard == null) {
            throw new IllegalArgumentException("Customer card must not be null");
        }
        if (customerCard.getCardNumber() == null || customerCard.getCardNumber().isBlank()) {
            String cardNumber = generateUniqueString(13);
            customerCard.setCardNumber(cardNumber);
        }
        return repository.save(customerCard);
    }

    @Transactional
    public CustomerCard update(CustomerCard customerCard) {
        if (customerCard == null) {
            throw new IllegalArgumentException("Customer card must not be null");
        }
        int rows = repository.update(customerCard);
        if (rows == 0) {
            throw new NotFoundException(CustomerCard.class, "cardNumber", customerCard.getCardNumber());
        }
        return customerCard;
    }

    @Transactional(readOnly = true)
    public boolean existsById(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalArgumentException("Card number must not be null or blank");
        }
        return repository.existsById(cardNumber);
    }

    @Transactional
    public void delete(CustomerCard customerCard) {
        if (customerCard == null) {
            throw new IllegalArgumentException("Customer card must not be null");
        }
        if (customerCard.getCardNumber() == null || customerCard.getCardNumber().isBlank()) {
            throw new IllegalArgumentException("Card number must not be null or blank");
        }
        int rows = repository.delete(customerCard);
        if (rows == 0) {
            throw new NotFoundException(CustomerCard.class, "cardNumber", customerCard.getCardNumber());
        }
    }

    @Transactional
    public void deleteByCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalArgumentException("Card number must not be null or blank");
        }
        int rows = repository.deleteById(cardNumber);
        if (rows == 0) {
            throw new NotFoundException(CustomerCard.class, "cardNumber", cardNumber);
        }
    }

}
