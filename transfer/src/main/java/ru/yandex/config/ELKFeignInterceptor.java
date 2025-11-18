package ru.yandex.config;

import io.micrometer.common.util.StringUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ELKFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        try {
            String traceId = MDC.get("traceId");
            String currentSpanId = MDC.get("spanId");
            String userLogin = MDC.get("userLogin");

            if (StringUtils.isNotBlank(traceId)) {
                template.header("X-Trace-Id", traceId);
            }
            if (StringUtils.isNotBlank(currentSpanId)) {
                template.header("X-Parent-Span-Id", currentSpanId);
            }
            if (StringUtils.isNotBlank(userLogin)) {
                template.header("X-User-Login", userLogin);
            }
            log.info("apply: start feign request - target={}, method={}, path={}",
                    template.feignTarget().name(),
                    template.method(),
                    template.url());
        } catch (Exception e) {
            log.warn("apply: error ", e);
        }
    }
}
