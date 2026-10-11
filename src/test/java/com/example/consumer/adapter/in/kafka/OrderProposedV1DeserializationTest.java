package com.example.consumer.adapter.in.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class OrderProposedV1DeserializationTest {
    @Test
    void shouldReadProducerContractWithoutJavaTypeHeader() {
        String json = """
                {
                  "eventId":"269ad5c6-69ea-4f88-8f21-aa4e283db26e",
                  "proposalId":"59bd5b26-0c71-4bac-aad4-80e73c9bd9a8",
                  "occurredAt":"2026-09-12T10:00:00Z",
                  "schemaVersion":1,
                  "portfolioId":"256f29f4-24d3-4737-88e1-6414a93a3fdc",
                  "instrumentId":"16377765-cc93-4147-a59c-8e8ac634a073",
                  "side":"BUY",
                  "maxNotional":12345678901234567890.123456789,
                  "currency":"PLN",
                  "source":"AI_LAB",
                  "rationale":"Positive earnings revision",
                  "sources":["https://example.com/report"],
                  "confidence":0.72,
                  "validUntil":"2026-09-13T10:00:00Z"
                }
                """;

        try (var deserializer = new JsonDeserializer<>(OrderProposedV1.class, false)) {
            var event = deserializer.deserialize(
                    "investment.order-proposals.v1",
                    json.getBytes(StandardCharsets.UTF_8)
            );

            assertThat(event.source()).isEqualTo("AI_LAB");
            assertThat(event.maxNotional())
                    .isEqualByComparingTo("12345678901234567890.123456789");
        }
    }
}
