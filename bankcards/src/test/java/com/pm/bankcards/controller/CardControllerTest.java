package com.pm.bankcards.controller;

import com.pm.bankcards.security.CustomUserDetailsService;
import com.pm.bankcards.security.JwtAuthFilter;
import com.pm.bankcards.security.JwtTokenService;
import com.pm.bankcards.service.api.CardQueryService;
import com.pm.bankcards.dto.card.CardResponse;
import com.pm.bankcards.entity.CardStatus;
import com.pm.bankcards.dto.card.CardFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.config.http.MatcherType.mvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired MockMvc mvc;
    @MockBean CardQueryService cardQueryService;
    @MockBean JwtAuthFilter jwtAuthFilter;
    @MockBean JwtTokenService jwtTokenService;
    @MockBean CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldReturnPagedCards() throws Exception {
        var card = new CardResponse(
                42L,
                "**** **** **** 4242",
                "Alice",
                12,
                2030,
                CardStatus.ACTIVE,
                BigDecimal.valueOf(15420.50)
        );
        var page = new PageImpl<>(List.of(card));

        when(cardQueryService.findMyCards(any(), any(), any())).thenReturn(page);

        // создаём principal c полем id
        var principal = new UsernamePasswordAuthenticationToken(
                new Object() { public Long id = 1L; },
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        mvc.perform(get("/api/v1/cards")
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(42));
    }
}


