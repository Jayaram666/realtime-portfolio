package com.realtimeportfolio.common.util;

import java.util.*;

public class StockSymbolTokenMap {

    private static final Map<String, String> STOCK_SYMBOLS;

    static {
        Map<String, String> map = new HashMap<>();

        map.put("ICICIBANK", "NSE_FO|101228");
        map.put("MRF", "NSE_EQ|INE883A01011");
        map.put("HDFC", "NSE_FO|99341");
        map.put("TECHM", "NSE_FO|144742");
        map.put("RELIANCE", "NSE_EQ|INE002A01018");
        map.put("INFY", "NSE_EQ|INE009A01021");
        map.put("TCS", "NSE_FO|144145");

        STOCK_SYMBOLS = Collections.unmodifiableMap(map);
    }

    private StockSymbolTokenMap() {
        // Prevent object creation
    }

    public static String getValueByKey(String key) {
        return STOCK_SYMBOLS.get(key);
    }

    public static Map<String, String> getAllSymbols() {
        return STOCK_SYMBOLS;
    }
    public static Collection<String> getAllInstrumentalKeys(){
        return STOCK_SYMBOLS.values();
    }
}