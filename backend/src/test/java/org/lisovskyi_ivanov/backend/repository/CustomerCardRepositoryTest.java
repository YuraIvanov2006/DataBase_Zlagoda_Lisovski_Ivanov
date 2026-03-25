package org.lisovskyi_ivanov.backend.repository;

import org.junit.jupiter.api.Test;
import org.lisovskyi_ivanov.backend.entity.CustomerCard;
import org.lisovskyi_ivanov.backend.mapping.mapper.CustomerCardRowMapper;
import org.lisovskyi_ivanov.backend.repository.customer_card_repos.CustomerCardRepository;
import org.lisovskyi_ivanov.backend.repository.customer_card_repos.CustomerCardRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@Import({CustomerCardRepositoryImpl.class, CustomerCardRowMapper.class})
class CustomerCardRepositoryTest {

    @Autowired
    CustomerCardRepository customerCardRepository;

    @Test
    void findAll_shouldReturnEmpty_whenNoCards() {
        var cards = customerCardRepository.findAll();
        assertTrue(cards.isEmpty());
    }

    @Test
    void findAll_shouldReturnCards_whenExist() {
        customerCardRepository.save(buildTestCustomerCard("CARD_1"));
        customerCardRepository.save(buildTestCustomerCard("CARD_2"));

        var cards = customerCardRepository.findAll();

        assertFalse(cards.isEmpty());
        assertEquals(2, cards.size());
    }

    @Test
    void findAllByCustSurname_shouldReturnCards_whenMatch() {
        var testCard = buildTestCustomerCard("CARD_3");
        testCard.setCustSurname("Shevchenko");
        customerCardRepository.save(testCard);

        var cards = customerCardRepository.findAllByCustSurname("Shevchenko");

        assertFalse(cards.isEmpty());
        assertEquals("Shevchenko", cards.getFirst().getCustSurname());
    }

    @Test
    void findByCardNumber_shouldReturnCard_whenExists() {
        var testCard = buildTestCustomerCard("CARD_4");
        customerCardRepository.save(testCard);

        var card = customerCardRepository.findByCardNumber(testCard.getCardNumber());

        assertTrue(card.isPresent());
        assertEquals(testCard.getCardNumber(), card.get().getCardNumber());
    }

    @Test
    void findByCardNumber_shouldReturnEmpty_whenNotExists() {
        var card = customerCardRepository.findByCardNumber("NON_EXISTING");
        assertTrue(card.isEmpty());
    }

    @Test
    void save_shouldReturnSavedCard() {
        var testCard = buildTestCustomerCard("CARD_5");
        var savedCard = customerCardRepository.save(testCard);

        assertNotNull(savedCard);
        assertEquals(testCard.getCardNumber(), savedCard.getCardNumber());
    }

    @Test
    void update_shouldUpdateCard_whenExists() {
        var testCard = buildTestCustomerCard("CARD_6");
        customerCardRepository.save(testCard);

        testCard.setPercent(15);
        testCard.setCustCity("Lviv");
        customerCardRepository.update(testCard);

        var updatedCard = customerCardRepository.findByCardNumber(testCard.getCardNumber());

        assertTrue(updatedCard.isPresent());
        assertEquals(15, updatedCard.get().getPercent());
        assertEquals("Lviv", updatedCard.get().getCustCity());
    }

    @Test
    void deleteByCardNumber_shouldRemoveCard_whenExists() {
        var testCard = buildTestCustomerCard("CARD_7");
        customerCardRepository.save(testCard);

        customerCardRepository.deleteById(testCard.getCardNumber());

        var deletedCard = customerCardRepository.findByCardNumber(testCard.getCardNumber());
        assertTrue(deletedCard.isEmpty());
    }

    @Test
    void delete_shouldRemoveCard_whenExists() {
        var testCard = buildTestCustomerCard("CARD_8");
        customerCardRepository.save(testCard);

        customerCardRepository.delete(testCard);

        var deletedCard = customerCardRepository.findByCardNumber(testCard.getCardNumber());
        assertTrue(deletedCard.isEmpty());
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        var testCard = buildTestCustomerCard("CARD_9");
        customerCardRepository.save(testCard);

        var exists = customerCardRepository.existsById(testCard.getCardNumber());
        assertTrue(exists);
    }

    @Test
    void existsById_shouldReturnFalse_whenNotExists() {
        var exists = customerCardRepository.existsById("NON_EXISTING");
        assertFalse(exists);
    }


    CustomerCard buildTestCustomerCard(String cardNumber) {
        return CustomerCard.builder()
                .cardNumber(cardNumber)
                .custSurname("Ivanov")
                .custName("Ivan")
                .custPatronymic("Ivanovych")
                .custPhoneNumber("+380501234567")
                .custCity("Kyiv")
                .custStreet("Khreshchatyk")
                .custZipCode("01001")
                .percent(5)
                .build();
    }
}