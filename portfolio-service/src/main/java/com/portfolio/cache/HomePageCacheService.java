package com.portfolio.cache;

import org.portfolio.dto.MenuResponse;
import org.portfolio.dto.StockTickerDto;
import org.portfolio.dto.UserDto;

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