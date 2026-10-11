package com.example.consumer.application.port.in;

import java.util.UUID;

public record OrderProposalReceived(UUID eventId, UUID proposalId) {
}
