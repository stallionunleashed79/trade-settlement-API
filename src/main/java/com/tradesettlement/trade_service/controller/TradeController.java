package com.tradesettlement.trade_service.controller;

import com.tradesettlement.trade_service.models.TradeSearchRequest;
import com.tradesettlement.trade_service.models.TradeSearchResponse;
import com.tradesettlement.trade_service.models.Trade;
import com.tradesettlement.trade_service.service.StatusBroadcastService;
import com.tradesettlement.trade_service.service.TradeService;
import com.tradesettlement.trade_service.validator.ValidFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin("http://localhost:4200")
public class TradeController {

    private final TradeService tradeService;
    private final StatusBroadcastService statusBroadcastService;

    @PostMapping(path = "/trades/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<List<Trade>> uploadCsvFile(@RequestParam("file") @Valid @ValidFile(allowedContentTypes = {"text/csv"}, maxSize = 2 * 1024 * 1024) MultipartFile file) {
        return ResponseEntity.ok(tradeService.uploadCsvFile(file));
    }

    @PostMapping(path = "/trades")
    public ResponseEntity<TradeSearchResponse> getTrades(@RequestBody TradeSearchRequest tradeSearchRequest) {
        final List<Trade> trades = tradeService.getAllTrades(tradeSearchRequest);
        final TradeSearchResponse tradeSearchResponse = TradeSearchResponse.builder()
                .data(trades)
                .totalRecords(trades.size())
                .build();
        return ResponseEntity.ok(tradeSearchResponse);
    }

    @GetMapping(value = "/trades/status/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamTradeStatus() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Register emitter with status service
        statusBroadcastService.addEmitter(emitter);

        emitter.onCompletion(() -> statusBroadcastService.removeEmitter(emitter));
        emitter.onTimeout(() -> statusBroadcastService.removeEmitter(emitter));

        return emitter;
    }
}
