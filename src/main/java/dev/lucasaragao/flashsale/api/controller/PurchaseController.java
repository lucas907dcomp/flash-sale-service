package dev.lucasaragao.flashsale.api.controller;

import dev.lucasaragao.flashsale.domain.dto.PurchaseRequest;
import dev.lucasaragao.flashsale.domain.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<String> buy(@RequestBody PurchaseRequest request) {
        boolean success = purchaseService.processPurchase(request.productId(), request.quantity());

        if (success) {
            return ResponseEntity.ok("Compra realizada com sucesso!");
        } else {
            return ResponseEntity.status(409).body("Falha na compra: Estoque insuficiente ou sistema ocupado.");
        }
    }
}