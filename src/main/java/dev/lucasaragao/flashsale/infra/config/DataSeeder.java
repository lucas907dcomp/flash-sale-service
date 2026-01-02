package dev.lucasaragao.flashsale.infra.config;

import dev.lucasaragao.flashsale.domain.model.Product;
import dev.lucasaragao.flashsale.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            log.info("🛒 Populando banco de dados com produtos de teste...");

            Product iphone = Product.builder()
                    .title("iPhone 15 Pro Max")
                    .description("Titânio Natural, 256GB")
                    .price(new BigDecimal("9000.00"))
                    .quantity(100) // Estoque crítico para o teste de concorrência
                    .build();

            productRepository.save(iphone);

            log.info("✅ Produto inserido: ID={} | Estoque={}", iphone.getId(), iphone.getQuantity());
        }
    }
}