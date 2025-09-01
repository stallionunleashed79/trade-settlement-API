package com.tradesettlement.trade_service.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class TestCsvGeneratorUtil {

    public static MultipartFile createCsvMultipartFile() throws IOException {
        byte[] csvContent = generateCsvBytes();

        return new MockMultipartFile(
                "csvFile",             // Parameter name in the request
                "data.csv",            // Original filename
                "text/csv",            // Content type
                csvContent             // The CSV data as bytes
        );
    }

    private static byte[] generateCsvBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos, true, StandardCharsets.UTF_8);
        // Write CSV header
        writer.println("tradeId,symbol,quantity,price,side,traderId,timestamp");
        // Write CSV data
        writer.println("T001,AAPL,100,150.25,BUY,trader1,2025-01-15T10:30:00Z");
        writer.println("T002,GOOGL,50,2800.50,SELL,trader2,2025-01-15T10:31:00Z");
        writer.flush();
        return baos.toByteArray();
    }
}
