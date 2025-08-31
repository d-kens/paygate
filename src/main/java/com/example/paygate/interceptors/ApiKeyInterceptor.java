package com.example.paygate.interceptors;


import com.example.paygate.exceptions.dtos.ErrorDto;
import com.example.paygate.merchants.Merchant;
import com.example.paygate.merchants.MerchantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class ApiKeyInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper;
    private final MerchantService merchantService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String apiKey = request.getHeader("PULSE-API-KEY");

        if (apiKey == null || apiKey.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(new ErrorDto("Missing API key")));
            return false;
        }

        Merchant  merchant = merchantService.getMerchantByApiKey(apiKey);

        if (merchant == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(new ErrorDto("Invalid API Key")));
            return false;
        }

        request.setAttribute("merchant", merchant);
        return true;
    }
}
