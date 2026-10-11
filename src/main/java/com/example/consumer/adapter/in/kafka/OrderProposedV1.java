package com.example.consumer.adapter.in.kafka;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderProposedV1(
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
    public OrderProposedV1 {
        sources = List.copyOf(sources);
    }
}
