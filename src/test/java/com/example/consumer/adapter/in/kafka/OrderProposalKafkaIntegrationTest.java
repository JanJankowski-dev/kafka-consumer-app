package com.example.consumer.adapter.in.kafka;

import com.example.consumer.application.port.in.OrderProposalReceived;
import com.example.consumer.application.port.in.ProcessOrderProposalCommand;
import com.example.consumer.application.port.in.ProcessOrderProposalUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "app.kafka.topic=investment.order-proposals.integration-test",
        "spring.kafka.consumer.group-id=investment-order-proposals-integration-test",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer"
})
@EmbeddedKafka(
        partitions = 3,
        topics = "investment.order-proposals.integration-test"
)
@DirtiesContext
class OrderProposalKafkaIntegrationTest {
    private static final UUID EVENT_ID =
            UUID.fromString("269ad5c6-69ea-4f88-8f21-aa4e283db26e");
    private static final UUID PROPOSAL_ID =
            UUID.fromString("59bd5b26-0c71-4bac-aad4-80e73c9bd9a8");
    private static final UUID PORTFOLIO_ID =
            UUID.fromString("256f29f4-24d3-4737-88e1-6414a93a3fdc");

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockitoBean
    private ProcessOrderProposalUseCase useCase;

    @Test
    void shouldConsumeJsonContractWithoutProducerJavaTypeHeader() {
        when(useCase.process(any(ProcessOrderProposalCommand.class)))
                .thenReturn(new OrderProposalReceived(EVENT_ID, PROPOSAL_ID));

        kafkaTemplate.send(
                        "investment.order-proposals.integration-test",
                        PORTFOLIO_ID.toString(),
                        json()
                )
                .orTimeout(Duration.ofSeconds(10).toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
                .join();

        verify(useCase, timeout(10_000)).process(argThat(command -> {
            assertThat(command.messageKey()).isEqualTo(PORTFOLIO_ID.toString());
            assertThat(command.eventId()).isEqualTo(EVENT_ID);
            assertThat(command.proposalId()).isEqualTo(PROPOSAL_ID);
            assertThat(command.maxNotional())
                    .isEqualByComparingTo("12345678901234567890.123456789");
            return true;
        }));
    }

    private String json() {
        return """
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
    }
}
