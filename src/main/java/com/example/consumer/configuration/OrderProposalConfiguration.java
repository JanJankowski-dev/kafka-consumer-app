package com.example.consumer.configuration;

import com.example.consumer.application.port.in.ProcessOrderProposalUseCase;
import com.example.consumer.application.service.OrderProposalService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderProposalConfiguration {
    @Bean
    ProcessOrderProposalUseCase processOrderProposalUseCase() {
        return new OrderProposalService();
    }
}
