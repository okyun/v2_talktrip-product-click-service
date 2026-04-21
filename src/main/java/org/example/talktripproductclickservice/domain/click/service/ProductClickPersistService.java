package org.example.talktripproductclickservice.domain.click.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.talktripproductclickservice.domain.click.entity.ProductClickEvent;
import org.example.talktripproductclickservice.domain.click.repository.ClickRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductClickPersistService {

    private final ClickRepository clickRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void save(
            Map<String, Object> payload,
            Long productId,
            Long memberId,
            String topic,
            int partition,
            long offset,
            String key
    ) {
        String payloadJson = toJson(payload);
        ProductClickEvent event = ProductClickEvent.of(
                Instant.now(),
                productId,
                memberId,
                topic,
                partition,
                offset,
                key,
                payloadJson
        );
        clickRepository.save(Objects.requireNonNull(event));
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return String.valueOf(payload);
        }
    }
}

