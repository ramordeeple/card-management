package com.pm.bankcards.it;

import com.pm.bankcards.controller.AdminCardController;

import com.pm.bankcards.repository.UserRepository;
import com.pm.bankcards.security.CustomUserDetailsService;
import com.pm.bankcards.security.JwtAuthFilter;
import com.pm.bankcards.security.JwtTokenService;
import com.pm.bankcards.service.api.CardAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminCardController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class
        )
)
class SecurityIT {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CardAdminService cardAdminService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void adminEndpointsShouldBeForbiddenForUser() throws Exception {
        mvc.perform(post("/api/v1/admin/cards"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminEndpointsShouldBeAccessibleForAdmin() throws Exception {
        String json = """
      { "number":"4111111111114242", "expiryMonth":10, "expiryYear":2026, "ownerId":1 }
    """;

        when(cardAdminService.create(any(), any())).thenReturn(null);

        mvc.perform(post("/api/v1/admin/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}
