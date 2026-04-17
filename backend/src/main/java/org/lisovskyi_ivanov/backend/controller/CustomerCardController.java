package org.lisovskyi_ivanov.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.dto.request.CustomerCardRequest;
import org.lisovskyi_ivanov.backend.dto.response.CustomerCardDto;
import org.lisovskyi_ivanov.backend.entity.CustomerCard;
import org.lisovskyi_ivanov.backend.mapping.dto.CustomerCardMapper;
import org.lisovskyi_ivanov.backend.service.CustomerCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer-cards")
@RequiredArgsConstructor
public class CustomerCardController {

    private final CustomerCardService customerCardService;
    private final CustomerCardMapper customerCardMapper;

    @GetMapping
    public ResponseEntity<List<CustomerCardDto>> getAllCustomerCards() {
        List<CustomerCardDto> cards = customerCardService.findAll().stream()
                .map(customerCardMapper::toDto)
                .toList();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/surname/{surname}")
    public ResponseEntity<List<CustomerCardDto>> getCustomerCardsBySurname(@PathVariable String surname) {
        List<CustomerCardDto> cards = customerCardService.findAllByCustSurname(surname).stream()
                .map(customerCardMapper::toDto)
                .toList();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{cardNumber}")
    public ResponseEntity<CustomerCardDto> getCustomerCardById(@PathVariable String cardNumber) {
        CustomerCard card = customerCardService.findByCardNumber(cardNumber);
        return ResponseEntity.ok(customerCardMapper.toDto(card));
    }

    @PostMapping
    public ResponseEntity<CustomerCardDto> createCustomerCard(@Valid @RequestBody CustomerCardRequest request) {
        CustomerCard cardToSave = customerCardMapper.toEntity(request);
        CustomerCard savedCard = customerCardService.save(cardToSave);

        return ResponseEntity.status(HttpStatus.CREATED).body(customerCardMapper.toDto(savedCard));
    }

    @PutMapping("/{cardNumber}")
    public ResponseEntity<CustomerCardDto> updateCustomerCard(
            @PathVariable String cardNumber,
            @Valid @RequestBody CustomerCardRequest request) {

        CustomerCard cardToUpdate = customerCardMapper.toEntity(request);
        cardToUpdate.setCardNumber(cardNumber);

        CustomerCard updatedCard = customerCardService.update(cardToUpdate);
        return ResponseEntity.ok(customerCardMapper.toDto(updatedCard));
    }

    @DeleteMapping("/{cardNumber}")
    public ResponseEntity<Void> deleteCustomerCard(@PathVariable String cardNumber) {
        customerCardService.deleteByCardNumber(cardNumber);
        return ResponseEntity.noContent().build();
    }
}