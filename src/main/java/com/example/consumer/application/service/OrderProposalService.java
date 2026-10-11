package com.example.consumer.application.service;

import com.example.consumer.application.port.in.OrderProposalReceived;
import com.example.consumer.application.port.in.ProcessOrderProposalCommand;
import com.example.consumer.application.port.in.ProcessOrderProposalUseCase;

import java.util.Objects;

public class OrderProposalService implements ProcessOrderProposalUseCase {
    @Override
    public OrderProposalReceived process(ProcessOrderProposalCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        if (!command.portfolioId().toString().equals(command.messageKey())) {
            throw new IllegalArgumentException("Kafka key must match portfolioId");
        }
        if (command.schemaVersion() != 1) {
            throw new IllegalArgumentException("Unsupported schema version");
        }
        return new OrderProposalReceived(command.eventId(), command.proposalId());
    }
}
