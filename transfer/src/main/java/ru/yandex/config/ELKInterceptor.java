package ru.yandex.config;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ELKInterceptor implements HandlerInterceptor {
    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SPAN_ID_HEADER = "X-Span-Id";
    private static final String PARENT_SPAN_ID_HEADER = "X-Parent-Span-Id";
    private static final String SERVICE_NAME_HEADER = "X-Service-Name";
    private static final String USER_LOGIN_HEADER = "X-User-Login";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler){
        if (handler instanceof HandlerMethod) {
            try {
                String parentSpanId = request.getHeader(PARENT_SPAN_ID_HEADER);
                String spanId = generateId();
                String traceId = getOrGenerateTraceId(request);
                String userLogin = CalcLogin(request.getHeader(USER_LOGIN_HEADER), request);

                if (StringUtils.isNotBlank(parentSpanId)) {
                    MDC.put("parentSpanId", parentSpanId);
                }
                MDC.put("spanId", spanId);
                MDC.put("traceId", traceId);
                MDC.put("serviceName", serviceName);
                MDC.put("userLogin", userLogin);

                response.setHeader(SPAN_ID_HEADER, spanId);
                response.setHeader(TRACE_ID_HEADER, traceId);
                response.setHeader(SERVICE_NAME_HEADER, serviceName);
                response.setHeader(USER_LOGIN_HEADER, userLogin);
            } catch (Exception e) {
                log.warn("preHandle: error - ", e);
            }
        }
        log.info("preHandle: start request - method={}, path={}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception e) {
        if (handler instanceof HandlerMethod) {
            String status = String.valueOf(response.getStatus());
            if (e != null) {
                status = status + " (error: " + e.getClass().getSimpleName() + ")";
            }
            log.info("afterCompletion: finish request - method={}, path={}, status={}",
                    request.getMethod(), request.getRequestURI(), status);
            MDC.clear();
        }
    }

    private String CalcLogin(String userLoginFromHeader, HttpServletRequest request) {
        if (StringUtils.isNotBlank(userLoginFromHeader)) {
            return userLoginFromHeader;
        }

        String authenticatedLogin = getCurrentUserLogin();
        if (StringUtils.isNotBlank(authenticatedLogin) && !"anonymous".equals(authenticatedLogin)) {
            return authenticatedLogin;
        }

        String path = request.getRequestURI();
        if (path.matches(".+/.+")) {
            Pattern pattern = Pattern.compile("/([^/]+)(/?)$");
            Matcher matcher = pattern.matcher(path);
            if (matcher.find()) {
                String potentialLogin = matcher.group(1);
                if (!potentialLogin.matches("(health|metrics|info)")) {
                    return potentialLogin;
                }
            }
        }
        return "system";
    }

    private String getOrGenerateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (StringUtils.isBlank(traceId)) {
            traceId = generateId();
        }
        log.debug("getOrGenerateTraceId: traceId: {}", traceId);
        return traceId;
    }

    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private String getCurrentUserLogin() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !(authentication instanceof AnonymousAuthenticationToken)) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("getCurrentUserLogin: failed to get user login from security context", e);
        }
        return "anonymous";
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }

    private String maskQueryParams(String queryString) {
        if (StringUtils.isBlank(queryString)) {
            return queryString;
        }

        Set<String> sensitiveParams = Set.of("password", "confirmPassword", "currentPassword", "newPassword",
                "token", "access_token", "refresh_token", "authorization");

        return Arrays.stream(queryString.split("&"))
                .map(param -> {
                    String[] keyValue = param.split("=", 2);
                    if (keyValue.length == 2 && sensitiveParams.contains(keyValue[0])) {
                        return keyValue[0] + "=***";
                    }
                    return param;
                })
                .collect(Collectors.joining("&"));
    }

    private String maskSensitiveData(String url) {
        Set<String> sensitiveParams = Set.of("password", "confirmPassword", "currentPassword", "newPassword",
                "token", "access_token", "refresh_token", "authorization");
        try {
            URI uri = URI.create(url);
            String query = uri.getQuery();

            if (StringUtils.isBlank(query)) {
                return url;
            }

            String maskedQuery = Arrays.stream(query.split("&"))
                    .map(param -> {
                        String[] keyValue = param.split("=", 2);
                        if (keyValue.length == 2 && sensitiveParams.contains(keyValue[0])) {
                            return keyValue[0] + "=***";
                        }
                        return param;
                    })
                    .collect(Collectors.joining("&"));

            return uri.getPath() + (StringUtils.isNotBlank(maskedQuery) ? "?" + maskedQuery : "");

        } catch (Exception e) {
            return url;
        }
    }
}
