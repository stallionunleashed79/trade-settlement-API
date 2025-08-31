package com.tradesettlement.trade_service.util;

import com.tradesettlement.trade_service.models.Trade;
import com.tradesettlement.trade_service.models.TradeStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class TradeServiceUtil {

     public List<Trade> buildTradeEvents(final MultipartFile file) {
         try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
             String line;
             final List<Trade> trades = new ArrayList<>();
             while ((line = reader.readLine()) != null) {
                 String[] tokens = line.split(",");
                 boolean isHeader = Arrays.stream(tokens).anyMatch(
                         token -> token.equalsIgnoreCase("tradeId"));
                 if (isHeader) {
                     continue;
                 }
                 String tradeId = tokens[0];
                 String symbol = tokens[1];
                 int quantity = Integer.parseInt(tokens[2]);
                 String price = tokens[3];
                 String side = tokens[4];
                 String traderId = tokens[5];
                 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                 Date parsedDate = dateFormat.parse(tokens[6]);
                 Instant instant = parsedDate.toInstant();
                 ZoneId zoneId = ZoneId.systemDefault(); // Using the system's default time zone
                 LocalDateTime tradeDate = LocalDateTime.ofInstant(instant, zoneId);
                 Trade trade = Trade.builder()
                         .traderId(traderId)
                         .symbol(symbol)
                         .quantity(quantity)
                         .price(new BigDecimal(price))
                         .side(side)
                         .tradeId(tradeId)
                         .tradeDate(tradeDate)
                         .status(TradeStatus.UPLOADED.name())
                         .build();
                 trades.add(trade);
             }
             // Process the CSV lines (e.g., parse, save to database)
             System.out.println("Uploaded CSV content:");
             return trades;

         } catch (Exception e) {
             return new ArrayList<>();
         }
     }
}
