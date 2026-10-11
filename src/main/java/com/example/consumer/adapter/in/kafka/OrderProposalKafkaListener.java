package com.example.consumer.adapter.in.kafka;

import com.example.consumer.application.port.in.ProcessOrderProposalCommand;
import com.example.consumer.application.port.in.ProcessOrderProposalUseCase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderProposalKafkaListener {
    private static final Logger log = LoggerFactory.getLogger(OrderProposalKafkaListener.class);

    private final ProcessOrderProposalUseCase useCase;

    public OrderProposalKafkaListener(ProcessOrderProposalUseCase useCase) {
        this.useCase = useCase;
    }

    @KafkaListener(topics = "${app.kafka.topic}")
    public void onMessage(ConsumerRecord<String, OrderProposedV1> record) {
        var event = record.value();
        var received = useCase.process(new ProcessOrderProposalCommand(
                record.key(),
                event.eventId(),
                event.proposalId(),
                event.occurredAt(),
                event.schemaVersion(),
                event.portfolioId(),
                event.instrumentId(),
                event.side(),
                event.maxNotional(),
                event.currency(),
                event.source(),
                event.rationale(),
                event.sources(),
                event.confidence(),
                event.validUntil()
        ));

        log.info(
                "Order proposal received eventId={} proposalId={} portfolioId={} partition={} offset={}",
                received.eventId(),
                received.proposalId(),
                event.portfolioId(),
                record.partition(),
                record.offset()
        );
    }
}
