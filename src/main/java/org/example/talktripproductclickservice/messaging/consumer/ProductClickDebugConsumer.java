package org.example.talktripproductclickservice.messaging.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.talktripproductclickservice.domain.click.service.ProductClickPersistService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * product-click 토픽 디버깅/감사용 Consumer.
 *
 * - back_end의 디버그 Consumer에서 product-click 구독만 분리 이관.
 * - 이 서비스에서는 DB 조회 없이 payload의 id만 로그로 남깁니다.
 */
@Component
public class ProductClickDebugConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProductClickDebugConsumer.class);

    private final ProductClickPersistService persistService;

    public ProductClickDebugConsumer(ProductClickPersistService persistService) {
        this.persistService = persistService;
    }

    @KafkaListener(
            topics = "${kafka.topics.product-click:product-click}",
            groupId = "debug-product-click-consumer",
            concurrency = "1"
    )
    public void listenProductClick(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        Long productId = numberFromPayload(payload, "productId");
        Long memberId = numberFromPayload(payload, "memberId");
        logger.info("product-click 수신: productId={}, memberId={}, topic={}, partition={}, offset={}, key={}",
                productId, memberId, topic, partition, offset, key);
    }

    @KafkaListener(
            topics = "${kafka.topics.product-click:product-click}",
            groupId = "audit-product-click-consumer",
            concurrency = "1"
    )
    public void listenProductClickAudit(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        Long productId = numberFromPayload(payload, "productId");
        Long memberId = numberFromPayload(payload, "memberId");
        logger.debug("product-click 수신(audit): productId={}, memberId={}, topic={}, partition={}, offset={}, key={}",
                productId, memberId, topic, partition, offset, key);

        // audit group에서만 clickDB에 적재 (debug group까지 저장하면 2중 적재됨)
        persistService.save(payload, productId, memberId, topic, partition, offset, key);
    }

    private Long numberFromPayload(Map<String, Object> payload, String key) {
        if (payload == null) return null;
        Object v = payload.get(key);
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try {
            return Long.parseLong(v.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

