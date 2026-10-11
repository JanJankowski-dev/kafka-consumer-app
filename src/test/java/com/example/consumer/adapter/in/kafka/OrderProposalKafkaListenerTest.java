package com.example.consumer.adapter.in.kafka;

import com.example.consumer.application.port.in.ProcessOrderProposalCommand;
import com.example.consumer.application.port.in.ProcessOrderProposalUseCase;
import com.example.consumer.application.port.in.OrderProposalReceived;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderProposalKafkaListenerTest {
    @Test
    void shouldMapKafkaContractToApplicationCommand() {
        var useCase = mock(ProcessOrderProposalUseCase.class);
        var event = event();
        var record = new ConsumerRecord<>(
                "investment.order-proposals.v1",
                2,
                41L,
                event.portfolioId().toString(),
                event
        );
        var commandCaptor = ArgumentCaptor.forClass(ProcessOrderProposalCommand.class);
        when(useCase.process(commandCaptor.capture()))
                .thenReturn(new OrderProposalReceived(event.eventId(), event.proposalId()));

        new OrderProposalKafkaListener(useCase).onMessage(record);

        verify(useCase).process(commandCaptor.getValue());
        assertThat(commandCaptor.getValue().messageKey()).isEqualTo(event.portfolioId().toString());
        assertThat(commandCaptor.getValue().eventId()).isEqualTo(event.eventId());
        assertThat(commandCaptor.getValue().source()).isEqualTo("AI_LAB");
    }

    private OrderProposedV1 event() {
        return new OrderProposedV1(
                UUID.fromString("269ad5c6-69ea-4f88-8f21-aa4e283db26e"),
                UUID.fromString("59bd5b26-0c71-4bac-aad4-80e73c9bd9a8"),
                Instant.parse("2026-09-12T10:00:00Z"),
                1,
                UUID.fromString("256f29f4-24d3-4737-88e1-6414a93a3fdc"),
                UUID.fromString("16377765-cc93-4147-a59c-8e8ac634a073"),
                "BUY",
                new BigDecimal("123.45"),
                "PLN",
                "AI_LAB",
                "Positive earnings revision",
                List.of("https://example.com/report"),
                new BigDecimal("0.72"),
                Instant.parse("2026-09-13T10:00:00Z")
        );
    }
}
