package com.pm.bankcards.it;

import com.pm.bankcards.controller.AdminCardController;
import com.pm.bankcards.config.SecurityConfig;
import com.pm.bankcards.entity.User;

import com.pm.bankcards.repository.UserRepository;
import com.pm.bankcards.security.CustomUserDetailsService;
import com.pm.bankcards.security.JwtAuthFilter;
import com.pm.bankcards.security.JwtTokenService;
import com.pm.bankcards.service.api.CardAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminCardController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
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
                  { "number":"4111111111114242", "expiryMonth":10, "expiryYear":2040, "ownerId":1 }
                """;

        when(cardAdminService.create(any(), any())).thenReturn(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        mvc.perform(post("/api/v1/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousUserCannotAccessAdminEndpoints() throws Exception {
        mvc.perform(post("/api/v1/admin/cards"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotBlockActivateOrDeleteCardsAsAdmin() throws Exception {
        mvc.perform(post("/api/v1/admin/cards/1/block")).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/admin/cards/1/activate")).andExpect(status().isForbidden());
        mvc.perform(delete("/api/v1/admin/cards/1")).andExpect(status().isForbidden());
    }
}
