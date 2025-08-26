package com.tradesettlement.trade_service.service;

import com.tradesettlement.trade_service.models.TradeStatusUpdate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatusBroadcastService {
    private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();

    public void addEmitter(final SseEmitter sseEmitter) {
        emitters.add(sseEmitter);
    }

    public void removeEmitter(final SseEmitter sseEmitter) {
        emitters.remove(sseEmitter);
    }

    public void broadcastStatusUpdate(TradeStatusUpdate update) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("trade-status")
                        .data(update));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });

        emitters.removeAll(deadEmitters);
    }
}
