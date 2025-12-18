package be.kdg.gobackend.config.rabbitMQ;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQTopology {

    private final RabbitMQProperties properties;

    @Bean
    TopicExchange xivExchange() {
        return new TopicExchange(properties.getExchangeName());
    }
}
