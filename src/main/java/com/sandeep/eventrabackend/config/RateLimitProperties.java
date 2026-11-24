package com.sandeep.eventrabackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;
    private EndpointLimit login = new EndpointLimit(5, Duration.ofMinutes(1));
    private EndpointLimit signup = new EndpointLimit(3, Duration.ofMinutes(10));
    private EndpointLimit forgotPassword = new EndpointLimit(3, Duration.ofMinutes(15));
    private EndpointLimit contact = new EndpointLimit(5, Duration.ofMinutes(10));

    /**
     * IP addresses (exact or CIDR) of reverse proxies whose forwarded headers
     * ({@code X-Forwarded-For} / {@code X-Real-IP}) may be trusted when resolving
     * the client IP. Requests whose direct remote address is not in this list
     * ignore those headers entirely, preventing spoofing. Defaults to loopback
     * addresses so local development keeps working.
     */
    private List<String> trustedProxies = new ArrayList<>(Arrays.asList("127.0.0.1", "::1", "0:0:0:0:0:0:0:1"));

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getTrustedProxies() {
        return trustedProxies;
    }

    public void setTrustedProxies(List<String> trustedProxies) {
        this.trustedProxies = trustedProxies;
    }

    public EndpointLimit getLogin() {
        return login;
    }

    public void setLogin(EndpointLimit login) {
        this.login = login;
    }

    public EndpointLimit getSignup() {
        return signup;
    }

    public void setSignup(EndpointLimit signup) {
        this.signup = signup;
    }

    public EndpointLimit getForgotPassword() {
        return forgotPassword;
    }

    public void setForgotPassword(EndpointLimit forgotPassword) {
        this.forgotPassword = forgotPassword;
    }

    public EndpointLimit getContact() {
        return contact;
    }

    public void setContact(EndpointLimit contact) {
        this.contact = contact;
    }

    public static class EndpointLimit {
        private int capacity;
        private Duration window;

        public EndpointLimit() {
        }

        public EndpointLimit(int capacity, Duration window) {
            this.capacity = capacity;
            this.window = window;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public Duration getWindow() {
            return window;
        }

        public void setWindow(Duration window) {
            this.window = window;
        }
    }
}
