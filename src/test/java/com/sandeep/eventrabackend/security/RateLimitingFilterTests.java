package com.sandeep.eventrabackend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "app.rate-limit.login.capacity=1",
        "app.rate-limit.login.window=1m"
})
class RateLimitingFilterTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsTooManyRequestsWhenLoginLimitIsExceeded() throws Exception {
        String clientIp = "203.0.113.10";

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", clientIp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", clientIp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(header().string("X-RateLimit-Limit", "1"))
                .andExpect(header().string("X-RateLimit-Remaining", "0"))
                .andExpect(jsonPath("$.status", is(429)))
                .andExpect(jsonPath("$.error", is("Too Many Requests")))
                .andExpect(jsonPath("$.path", is("/api/auth/login")));
    }

    @Test
    void ignoresSpoofedXForwardedForFromUntrustedRemote() throws Exception {
        // A client that is NOT a configured trusted proxy sets a different
        // spoofed X-Forwarded-For on each request. Because the filter no longer
        // trusts forwarded headers from untrusted remotes, both requests share
        // the same rate-limit key (the direct remote address) and the second
        // one is rejected — the spoofing bypass is closed.
        org.springframework.test.web.servlet.request.RequestPostProcessor fromUntrusted =
                request -> {
                    request.setRemoteAddr("203.0.113.99");
                    return request;
                };

        mockMvc.perform(post("/api/auth/login")
                        .with(fromUntrusted)
                        .header("X-Forwarded-For", "1.1.1.1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/auth/login")
                        .with(fromUntrusted)
                        .header("X-Forwarded-For", "2.2.2.2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isTooManyRequests());
    }
}
