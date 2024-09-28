package org.mypetproject.translationservice.configuration;

import lombok.extern.slf4j.Slf4j;
import org.mypetproject.translationservice.dtos.Recipe;
import org.mypetproject.translationservice.dtos.RecipeTitlesDTO;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;
@Configuration
@Slf4j
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "recipe_translation_queue";
    public static final String RESPONSE_QUEUE_NAME = "translated_recipes_queue";
    public static final String TITLES_QUEUE_NAME = "recipe_titles_translation_queue";
    public static final String TITLES_RESPONSE_QUEUE_NAME = "translated_titles_queue";

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setJavaTypeMapper(createTypeMapper());
        return converter;
    }

    private DefaultJackson2JavaTypeMapper createTypeMapper() {
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("RecipeDTO", Recipe.class);
        idClassMapping.put("RecipeTitlesDTO", RecipeTitlesDTO.class);
        typeMapper.setIdClassMapping(idClassMapping);
        return typeMapper;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        rabbitTemplate.setReceiveTimeout(5000);
        rabbitTemplate.setConfirmCallback(this::confirmCallback);
        rabbitTemplate.setRetryTemplate(retryTemplate());
        return rabbitTemplate;
    }

    private void confirmCallback(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("Message sent successfully with correlation data: {}", correlationData);
        } else {
            log.error("Failed to send message with correlation data: {}. Cause: {}", correlationData, cause);
        }
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(createBackOffPolicy());
        retryTemplate.setRetryPolicy(createRetryPolicy());
        return retryTemplate;
    }

    private FixedBackOffPolicy createBackOffPolicy() {
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1000); // 1 секунда между попытками
        return backOffPolicy;
    }

    private SimpleRetryPolicy createRetryPolicy() {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3); // Максимум 3 попытки
        return retryPolicy;
    }

    @Bean
    public Queue translationQueue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    public Queue responseQueue() {
        return new Queue(RESPONSE_QUEUE_NAME, false);
    }

    @Bean
    public Queue titlesTranslationQueue() {
        return new Queue(TITLES_QUEUE_NAME, false);
    }

    @Bean
    public Queue titlesResponseQueue() {
        return new Queue(TITLES_RESPONSE_QUEUE_NAME, false);
    }
}
