package com.example.consumer.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProcessOrderProposalCommand(
        String messageKey,
        UUID eventId,
        UUID proposalId,
        Instant occurredAt,
        int schemaVersion,
        UUID portfolioId,
        UUID instrumentId,
        String side,
        BigDecimal maxNotional,
        String currency,
        String source,
        String rationale,
        List<String> sources,
        BigDecimal confidence,
        Instant validUntil
) {
    public ProcessOrderProposalCommand {
        sources = List.copyOf(sources);
    }
}
