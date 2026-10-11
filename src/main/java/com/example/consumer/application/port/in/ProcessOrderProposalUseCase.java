package com.example.consumer.application.port.in;

public interface ProcessOrderProposalUseCase {
    OrderProposalReceived process(ProcessOrderProposalCommand command);
}
