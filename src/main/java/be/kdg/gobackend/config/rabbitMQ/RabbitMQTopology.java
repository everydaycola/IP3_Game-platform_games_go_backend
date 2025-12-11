package be.kdg.gobackend.config.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {

    private final RabbitMQProperties properties;

    public RabbitMQTopology(RabbitMQProperties properties) {
        this.properties = properties;
    }

    @Bean
    TopicExchange xivExchange() {
        return new TopicExchange(properties.getExchangeName());
    }
}
