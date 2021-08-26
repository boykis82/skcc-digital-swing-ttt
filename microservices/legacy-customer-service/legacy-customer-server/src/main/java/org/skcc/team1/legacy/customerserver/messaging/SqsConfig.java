package org.skcc.team1.legacy.customerserver.messaging;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

@Configuration
public class SqsConfig {

    private final String accessKey;
    private final String secretKey;
    private final String sqsQueueUrl;
    private final String sqsRegion;
    private final long sqsQueueSendTimeoutMilliSeconds;

    public SqsConfig(@Value("${cloud.aws.credentials.accessKey}") String accessKey,
                     @Value("${cloud.aws.credentials.secretKey}") String secretKey,
                     @Value("${cloud.aws.sqs.queue.url}") String sqsQueueUrl,
                     @Value("${cloud.aws.region.static}") String sqsRegion,
                     @Value("${cloud.aws.sqs.queue.timeout}") long sqsQueueSendTimeoutMilliSeconds) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.sqsQueueUrl = sqsQueueUrl;
        this.sqsRegion = sqsRegion;
        this.sqsQueueSendTimeoutMilliSeconds = sqsQueueSendTimeoutMilliSeconds;
    }

    @Bean
    public AwsClientBuilder.EndpointConfiguration endpointConfiguration() {
        return new AwsClientBuilder.EndpointConfiguration(sqsQueueUrl, sqsRegion);
    }

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync(final AwsClientBuilder.EndpointConfiguration endpointConfiguration) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonSQSAsyncClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
/*
    @Bean
    protected MessageConverter messageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setStrictContentTypeMatch(false);
        return converter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
*/
    @Bean
    public MessageSender messageSender(AmazonSQSAsync amazonSQSAsync) {
        return new SqsMessageSender(amazonSQSAsync, sqsQueueSendTimeoutMilliSeconds);
    }

    @Bean
    public CustomerMessagePublisher customerMessagePublisher(MessageSender messageSender) {
        return new CustomerMessagePublisher(messageSender, sqsQueueUrl);
    }
}

