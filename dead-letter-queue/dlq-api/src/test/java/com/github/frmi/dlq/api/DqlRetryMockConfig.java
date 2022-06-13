package com.github.frmi.dlq.api;

import com.github.frmi.dlq.api.service.DlqRetry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import spock.mock.DetachedMockFactory;

@TestConfiguration
public class DqlRetryMockConfig {

    DetachedMockFactory mockFactory = new DetachedMockFactory();

    @Bean
    public DlqRetry dlqRetryMock() {
        return mockFactory.Stub(DlqRetry.class);
    }

}
