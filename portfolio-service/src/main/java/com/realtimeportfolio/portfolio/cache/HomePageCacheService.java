package com.realtimeportfolio.portfolio.cache;

import com.realtimeportfolio.common.dto.MenuResponse;
import com.realtimeportfolio.common.dto.StockTickerDto;
import com.realtimeportfolio.common.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface HomePageCacheService {

    void cacheUser(String email, UserDto user);

    Optional<UserDto> getCachedUser(String email);

    void cacheUserMenus(Long userId, List<MenuResponse> menus);

    Optional<List<MenuResponse>> getCachedUserMenus(Long userId);

    void cacheStockTickers(List<StockTickerDto> stockTickers);

    Optional<List<StockTickerDto>> getCachedStockTickers();
}