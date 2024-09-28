package org.mypetproject.translationservice.controller;

import lombok.extern.slf4j.Slf4j;

import org.mypetproject.translationservice.domain.exception.ExceptionBody;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(AmqpException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ExceptionBody handleAmqpException(AmqpException e) {
        log.error("RabbitMQ error occurred", e);
        return new ExceptionBody("RabbitMQ error: " + e.getMessage());
    }

    @ExceptionHandler(ListenerExecutionFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleListenerExecutionFailedException(ListenerExecutionFailedException e) {
        log.error("RabbitMQ listener error occurred", e);
        return new ExceptionBody("RabbitMQ listener error: " + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error("Validation failed", e);
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed.");
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        exceptionBody.setErrors(errors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existingMessage, newMessage) -> existingMessage + " " + newMessage)
                ));
        return exceptionBody;
    }



    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleException(final Exception e) {
        log.error("Unexpected error occurred", e);
        return new ExceptionBody("Internal error.");
    }
}