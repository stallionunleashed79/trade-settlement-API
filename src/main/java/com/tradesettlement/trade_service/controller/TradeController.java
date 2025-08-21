package com.tradesettlement.trade_service.controller;

import com.tradesettlement.trade_service.models.Trade;
import com.tradesettlement.trade_service.service.TradeService;
import com.tradesettlement.trade_service.validator.ValidFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin("http://localhost:4200")
public class TradeController {

    private final TradeService tradeService;

    @PostMapping(path = "/trades/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<List<Trade>> uploadCsvFile(@RequestParam("file") @Valid @ValidFile(allowedContentTypes = {"text/csv"}, maxSize = 2 * 1024 * 1024) MultipartFile file) {
        return ResponseEntity.ok(tradeService.uploadCsvFile(file));
    }

    @GetMapping(path = "/trades")
    public ResponseEntity<List<Trade>> uploadCsvFile() {
        return ResponseEntity.ok(tradeService.getAllTrades());
    }
}
