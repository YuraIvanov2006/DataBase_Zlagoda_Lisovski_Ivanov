package org.lisovskyi_ivanov.backend.service;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.CustomerCard;
import org.lisovskyi_ivanov.backend.repository.customer_card_repos.CustomerCardRepository;
import org.springframework.stereotype.Service;

import static org.lisovskyi_ivanov.backend.utility.StringGenerator.generateUniqueString;

@Service
@RequiredArgsConstructor
public class CustomerCardService {
    private final CustomerCardRepository repository;



    public CustomerCard save(CustomerCard customerCard) {
        if (customerCard.getCardNumber() == null || customerCard.getCardNumber().isBlank()) {
            String cardNumber = generateUniqueString(13);
            customerCard.setCardNumber(cardNumber);
        }
        return repository.save(customerCard);
    }

}
