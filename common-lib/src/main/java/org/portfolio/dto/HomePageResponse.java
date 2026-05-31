package org.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomePageResponse {
    private  UserDto user;
    private List<MenuResponse> menuResponse;
    private List<StockTickerDto> stockTickerDto;
}
