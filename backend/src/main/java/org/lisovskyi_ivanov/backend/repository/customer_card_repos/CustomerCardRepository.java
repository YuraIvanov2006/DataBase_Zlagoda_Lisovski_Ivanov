package org.lisovskyi_ivanov.backend.repository.customer_card_repos;

import org.lisovskyi_ivanov.backend.entity.CustomerCard;

import java.util.List;
import java.util.Optional;

public interface CustomerCardRepository {
    List<CustomerCard> findAll();
    List<CustomerCard> findAllByCustSurname(String custSurname); // Added a common custom search
    Optional<CustomerCard> findByCardNumber(String cardNumber);
    CustomerCard save(CustomerCard customerCard);
    int update(CustomerCard customerCard);
    int delete(CustomerCard customerCard);
    int deleteById(String cardNumber);
    boolean existsById(String cardNumber);
}