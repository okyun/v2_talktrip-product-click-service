package org.example.talktripproductclickservice.domain.click.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "product_click_event",
        indexes = {
                @Index(name = "idx_pce_event_time", columnList = "eventTime"),
                @Index(name = "idx_pce_product_time", columnList = "productId,eventTime"),
                @Index(name = "idx_pce_member_time", columnList = "memberId,eventTime")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant eventTime;

    @Column(nullable = true)
    private Long productId;

    @Column(nullable = true)
    private Long memberId;

    @Column(nullable = true, length = 200)
    private String kafkaTopic;

    @Column(nullable = true)
    private Integer kafkaPartition;

    @Column(nullable = true)
    private Long kafkaOffset;

    @Column(nullable = true, length = 200)
    private String kafkaKey;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payloadJson;

    public static ProductClickEvent of(
            Instant eventTime,
            Long productId,
            Long memberId,
            String kafkaTopic,
            Integer kafkaPartition,
            Long kafkaOffset,
            String kafkaKey,
            String payloadJson
    ) {
        ProductClickEvent e = new ProductClickEvent();
        e.setEventTime(eventTime);
        e.setProductId(productId);
        e.setMemberId(memberId);
        e.setKafkaTopic(kafkaTopic);
        e.setKafkaPartition(kafkaPartition);
        e.setKafkaOffset(kafkaOffset);
        e.setKafkaKey(kafkaKey);
        e.setPayloadJson(payloadJson);
        return e;
    }
}

