package com.tradesettlement.trade_service.service;

import com.tradesettlement.trade_service.entities.IdempotencyKeyEntity;
import com.tradesettlement.trade_service.entities.TradeEntity;
import com.tradesettlement.trade_service.mapper.TradeMapper;
import com.tradesettlement.trade_service.models.Trade;
import com.tradesettlement.trade_service.repository.IdempotencyKeyRepository;
import com.tradesettlement.trade_service.repository.OutboxEventRepository;
import com.tradesettlement.trade_service.repository.TradeRepository;
import com.tradesettlement.trade_service.util.TestCsvGeneratorUtil;
import com.tradesettlement.trade_service.util.TradeServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TradeServiceTest {

    private TradeService tradeService;
    private final TradeServiceUtil tradeServiceUtil = new TradeServiceUtil();
    private IdempotencyKeyRepository idempotencyKeyRepositoryMock;
    private TradeMapper tradeMapperMock;
    private TradeRepository tradeRepositoryMock;
    private final ArgumentCaptor<IdempotencyKeyEntity> idempotencyKeyEntityArgumentCaptor = ArgumentCaptor.forClass(
            IdempotencyKeyEntity.class);
    private final ArgumentCaptor<List<TradeEntity>> tradeRepositoryMockCaptor = ArgumentCaptor.forClass(
               List.class);

    @BeforeEach
    void setUp() {
        tradeRepositoryMock = mock(TradeRepository.class);
        tradeMapperMock = mock(TradeMapper.class);
        idempotencyKeyRepositoryMock = mock(IdempotencyKeyRepository.class);
        OutboxEventRepository outboxEventRepositoryMock = mock(OutboxEventRepository.class);
        tradeService = new TradeService(tradeServiceUtil, idempotencyKeyRepositoryMock,
                tradeMapperMock, tradeRepositoryMock, outboxEventRepositoryMock);
    }

    @Test
    void testCsvUpload() throws IOException {// Instantiate the class under test
        when(idempotencyKeyRepositoryMock.findByIdempotencyKey(anyString())).thenReturn(null);
        when(tradeMapperMock.toEntity(any(Trade.class))).thenReturn(TradeEntity.builder().build());
        final MultipartFile mockMultiPartFile = TestCsvGeneratorUtil.createCsvMultipartFile();
        tradeService.uploadCsvFile(mockMultiPartFile);
        verify(idempotencyKeyRepositoryMock, times(1)).save(
                idempotencyKeyEntityArgumentCaptor.capture());
        verify(tradeRepositoryMock, times(1)).saveAll(
                tradeRepositoryMockCaptor.capture());
    }
}
