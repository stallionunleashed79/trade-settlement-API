package com.tradesettlement.trade_service.service;

import com.tradesettlement.trade_service.entities.TradeEntity;
import com.tradesettlement.trade_service.mapper.TradeMapper;
import com.tradesettlement.trade_service.models.Trade;
import com.tradesettlement.trade_service.models.TradeRequest;
import com.tradesettlement.trade_service.repository.IdempotencyKeyRepository;
import com.tradesettlement.trade_service.repository.TradeRepository;
import com.tradesettlement.trade_service.util.TestCsvGeneratorUtil;
import com.tradesettlement.trade_service.util.TradeServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TradeServiceTest {

    private TradeService tradeService;
    private final TradeServiceUtil tradeServiceUtil = new TradeServiceUtil();
    private KafkaTemplate kafkaProducerMock;
    private IdempotencyKeyRepository idempotencyKeyRepositoryMock;
    private TradeMapper tradeMapperMock;
    private TradeRepository tradeRepositoryMock;

    @BeforeEach
    void setUp() {
        kafkaProducerMock = mock(KafkaTemplate.class);
        tradeRepositoryMock = mock(TradeRepository.class);
        tradeMapperMock = mock(TradeMapper.class);
        idempotencyKeyRepositoryMock = mock(IdempotencyKeyRepository.class);
        tradeService = new TradeService(tradeServiceUtil, kafkaProducerMock,
                idempotencyKeyRepositoryMock, tradeMapperMock, tradeRepositoryMock);
        ReflectionTestUtils.setField(tradeService, "tradeEventsTopic", "trade-events");
    }

    @Test
    void testCsvUpload() throws IOException {// Instantiate the class under test
        when(kafkaProducerMock.send(anyString(), any(TradeRequest.class))).thenReturn(mock(CompletableFuture.class));
        when(idempotencyKeyRepositoryMock.findByIdempotencyKey(anyString())).thenReturn(null);
        when(tradeMapperMock.toEntity(any(Trade.class))).thenReturn(TradeEntity.builder().build());
        final MultipartFile mockMultiPartFile = TestCsvGeneratorUtil.createCsvMultipartFile();
        tradeService.uploadCsvFile(mockMultiPartFile);
        verify(kafkaProducerMock, times(2)).send(eq("trade-events"),
                any(TradeRequest.class));
    }
}
