package dev.lucasaragao.flashsale.domain.service;

import dev.lucasaragao.flashsale.domain.model.Product;
import dev.lucasaragao.flashsale.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService {

    private final ProductRepository productRepository;
    private final RedissonClient redissonClient;

    /**
     * Processa a compra utilizando Distributed Lock (Redisson)
     * para garantir atomicidade em ambiente distribuído.
     */
    public boolean processPurchase(Long productId, Integer quantity) {
        // Define uma chave única para o Lock baseada no ID do produto
        String lockKey = "product-lock-" + productId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // Tenta adquirir o lock:
            // waitTime: espera até 5s para conseguir o lock antes de desistir
            // leaseTime: se o servidor morrer, o lock solta sozinho em 10s (evita deadlock)
            boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);

            if (isLocked) {
                try {
                    return executePurchaseLogic(productId, quantity);
                } finally {
                    lock.unlock(); // SEMPRE liberar o lock no finally
                }
            } else {
                log.warn("⏳ Não foi possível adquirir o lock para o produto {}", productId);
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Erro de interrupção ao tentar pegar lock", e);
            return false;
        }
    }

    // A lógica transacional fica isolada para garantir que o commit ocorra antes do unlock
    @Transactional
    protected boolean executePurchaseLogic(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        if (product.getQuantity() < quantity) {
            log.warn("❌ Estoque insuficiente. Disp: {}, Req: {}", product.getQuantity(), quantity);
            return false;
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        log.info("✅ Venda realizada! Novo estoque: {}", product.getQuantity());
        return true;
    }
}