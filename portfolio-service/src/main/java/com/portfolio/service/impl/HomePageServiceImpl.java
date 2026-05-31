package com.portfolio.service.impl;


import com.portfolio.cache.HomePageCacheService;
import com.portfolio.service.HomePageService;
import com.portfolio.service.StackMarketService;
import com.portfolio.service.UserMenuPermissionService;
import com.portfolio.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.portfolio.dto.HomePageResponse;
import org.portfolio.dto.MenuResponse;
import org.portfolio.dto.StockTickerDto;
import org.portfolio.dto.UserDto;
import org.portfolio.exception.InvalidLoginException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomePageServiceImpl implements HomePageService {

    private final UserMenuPermissionService userMenuPermissionService;
    private final StackMarketService stockTickerService;
    private final UserService userService;
    private final HomePageCacheService homePageCacheService;


    @Override
    public HomePageResponse getHomePageData(String email) {


        UserDto user = getUserWithFallback(email);

        List<MenuResponse> menus =
                getMenusWithFallback(user.getUserId());

        List<StockTickerDto> stockTickers =
                getStockTickersWithFallback();

        return new HomePageResponse(
                user,
                userMenuPermissionService.findMenuByUserId(user.getUserId()),
                stockTickerService.getStockTickers());
    }

    private UserDto getUserWithFallback(String email) {

        try {
            UserDto user = userService.getUserDataByEmail(email);

            if (user == null) {
                throw new InvalidLoginException(
                        "User not found with email: " + email
                );
            }

            homePageCacheService.cacheUser(email, user);

            return user;

        } catch (Exception ex) {
            log.error(
                    "Failed to fetch user from live source. Trying user cache. email={}",
                    email,
                    ex
            );

            return homePageCacheService.getCachedUser(email)
                    .orElseThrow(() -> new InvalidLoginException(
                            "User not found with email: " + email
                    ));
        }
    }

    private List<MenuResponse> getMenusWithFallback(Long userId) {

        try {
            List<MenuResponse> menus =
                    userMenuPermissionService.findMenuByUserId(userId);

            homePageCacheService.cacheUserMenus(userId, menus);

            return menus;

        } catch (Exception ex) {
            log.error(
                    "Failed to fetch user menus from live source. Trying menu cache. userId={}",
                    userId,
                    ex
            );

            return homePageCacheService.getCachedUserMenus(userId)
                    .orElseGet(() -> {
                        log.warn(
                                "Menu cache not available. Returning empty menu list. userId={}",
                                userId
                        );
                        return Collections.emptyList();
                    });
        }
    }

    private List<StockTickerDto> getStockTickersWithFallback() {

        try {
            List<StockTickerDto> stockTickers =
                    stockTickerService.getStockTickers();

            homePageCacheService.cacheStockTickers(stockTickers);

            return stockTickers;

        } catch (Exception ex) {
            log.error(
                    "Failed to fetch stock tickers from live source. Trying stock ticker cache.",
                    ex
            );

            return homePageCacheService.getCachedStockTickers()
                    .orElseGet(() -> {
                        log.warn(
                                "Stock ticker cache not available. Returning empty ticker list."
                        );
                        return Collections.emptyList();
                    });
        }
    }
}
