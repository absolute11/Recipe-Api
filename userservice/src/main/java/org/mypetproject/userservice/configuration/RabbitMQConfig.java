package org.mypetproject.userservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mypetproject.userservice.web.dto.RecipeDTO;
import org.mypetproject.userservice.web.dto.RecipeSaveMessage;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class RabbitMQConfig {
    public final static String REGISTRATION_EMAIL_QUEUE = "registration_email_queue";
    public final static String RECIPE_SAVE_EMAIL_QUEUE = "recipe_save_email_queue";
    public static final String USER_RECIPE_REQUEST_QUEUE = "user.recipe.request.queue";
    public static final String USER_RECIPE_RESPONSE_QUEUE = "user.recipe.response.queue";
    public static final String REQUEST_QUEUE_NAME = "recipe_translate_queue";
    public static final String RESPONSE_QUEUE_NAME = "translated_recipes_queue";
    public static final String RECIPE_GET_QUEUE = "recipe_get_queue";
    public static final String USER_RECIPE_DTO_QUEUE = "user.recipe.dto.queue";
    public static final String USER_RECIPE_URL_QUEUE = "user.recipe.url.queue";

    @Bean
    public Queue recipeSaveEmailQueue() {
        return new Queue(RECIPE_SAVE_EMAIL_QUEUE, false);
    }
    @Bean
    public Queue registrationEmailQueue() {
        return new Queue(REGISTRATION_EMAIL_QUEUE, false);
    }


    @Bean
    public Queue userRecipeRequestQueue() {
        return new Queue(USER_RECIPE_REQUEST_QUEUE, false);
    }

    @Bean
    public Queue userRecipeResponseQueue() {
        return new Queue(USER_RECIPE_RESPONSE_QUEUE, false);
    }

    @Bean
    public Queue translationQueue() {
        return new Queue(REQUEST_QUEUE_NAME, false);
    }
    @Bean
    public Queue userRecipeDTOQueue() {
        return new Queue(USER_RECIPE_DTO_QUEUE, false);
    }

    @Bean
    public Queue userRecipeURLQueue() {
        return new Queue(USER_RECIPE_URL_QUEUE, false);
    }

    @Bean
    public Queue responseQueue() {
        return new Queue(RESPONSE_QUEUE_NAME, false);
    }

    @Bean
    public Queue recipeGetQueue() {
        return new Queue(RECIPE_GET_QUEUE, false);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("RecipeDTO", RecipeDTO.class);
        idClassMapping.put("RecipeSaveMessage", RecipeSaveMessage.class);
        typeMapper.setIdClassMapping(idClassMapping);

        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        rabbitTemplate.setReceiveTimeout(20000);

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("Message sent successfully with correlation data: " + correlationData);
            } else {
                log.error("Failed to send message with correlation data: " + correlationData + ". Cause: " + cause);
            }
        });
        rabbitTemplate.setRetryTemplate(retryTemplate());
        return rabbitTemplate;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1000); // 1 секунда между попытками
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3); // Максимум 3 попытки
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}