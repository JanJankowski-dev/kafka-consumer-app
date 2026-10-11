package com.example.consumer.application.service;

import com.example.consumer.application.port.in.ProcessOrderProposalCommand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderProposalServiceTest {
    @Test
    void shouldAcceptSupportedEventWithMatchingPartitionKey() {
        var command = command(PORTFOLIO_ID.toString(), 1);

        var received = new OrderProposalService().process(command);

        assertThat(received.eventId()).isEqualTo(command.eventId());
        assertThat(received.proposalId()).isEqualTo(command.proposalId());
    }

    @Test
    void shouldRejectMismatchedPartitionKey() {
        assertThatThrownBy(() -> new OrderProposalService().process(command("wrong-key", 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Kafka key must match portfolioId");
    }

    @Test
    void shouldRejectUnsupportedSchemaVersion() {
        assertThatThrownBy(() -> new OrderProposalService().process(command(PORTFOLIO_ID.toString(), 2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported schema version");
    }

    private static final UUID PORTFOLIO_ID =
            UUID.fromString("256f29f4-24d3-4737-88e1-6414a93a3fdc");

    private ProcessOrderProposalCommand command(String messageKey, int schemaVersion) {
        return new ProcessOrderProposalCommand(
                messageKey,
                UUID.fromString("269ad5c6-69ea-4f88-8f21-aa4e283db26e"),
                UUID.fromString("59bd5b26-0c71-4bac-aad4-80e73c9bd9a8"),
                Instant.parse("2026-09-12T10:00:00Z"),
                schemaVersion,
                PORTFOLIO_ID,
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
