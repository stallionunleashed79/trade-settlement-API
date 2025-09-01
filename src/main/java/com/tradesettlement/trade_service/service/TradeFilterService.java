package com.tradesettlement.trade_service.service;

import com.tradesettlement.trade_service.entities.TradeEntity;
import com.tradesettlement.trade_service.mapper.TradeMapper;
import com.tradesettlement.trade_service.models.Trade;
import com.tradesettlement.trade_service.models.TradeFilter;
import com.tradesettlement.trade_service.models.TradeSearchRequest;
import com.tradesettlement.trade_service.repository.TradeRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TradeFilterService {

    private final TradeRepository tradeRepository;
    private final TradeMapper tradeMapper;

    public List<Trade> searchTrades(final TradeSearchRequest tradeSearchRequest,
                                    final Pageable pageable) {
        final TradeFilter filterModel = tradeSearchRequest.getFilterModel();
        final String symbol = filterModel.getSymbol();
        final String side = filterModel.getSide();
        final String status = filterModel.getStatus();
        final LocalDate tradeDateBegin = filterModel.getTradeDateBegin();
        final LocalDate tradeDateEnd = filterModel.getTradeDateEnd();
        Specification<TradeEntity> spec = (root, query, cb) -> cb.conjunction();
        if (StringUtils.isNotBlank(symbol)) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("symbol")), symbol.toLowerCase()));
        }
        if (StringUtils.isNotBlank(side)) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("side")), side.toLowerCase()));
        }
        if (StringUtils.isNotBlank(status)) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
        }

        if (tradeDateBegin != null && tradeDateEnd != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("tradeDate"), tradeDateBegin, tradeDateEnd));
        }

        final Page<TradeEntity> tradeEntities = tradeRepository.findAll(spec, pageable);
        return tradeEntities.hasContent()
                ? tradeEntities.stream().map(tradeMapper::toDto).collect(Collectors.toList())
                : Collections.emptyList();
    }
}
