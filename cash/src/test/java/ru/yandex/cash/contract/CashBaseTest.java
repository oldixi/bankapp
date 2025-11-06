package ru.yandex.cash.contract;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.api.AccountsClient;
import ru.yandex.cash.CashController;
import ru.yandex.cash.config.TestFeignConfig;

import java.time.LocalDate;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
@AutoConfigureStubRunner(ids = {"ru.yandex:accounts:0.0.1-SNAPSHOT:stubs:8082",
        "ru.yandex:notifications:0.0.1-SNAPSHOT:stubs:8085"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@ActiveProfiles("test")
@Import(TestFeignConfig.class)
@AutoConfigureWireMock(port = 8083)
public abstract class CashBaseTest {
    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Mock
    protected AccountsClient accountsClient;

    @Autowired
    protected CashController controller;

    @BeforeEach
    public void configureRestAssured() {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);

        StandaloneMockMvcBuilder standaloneMockMvcBuilder = MockMvcBuilders.standaloneSetup(controller);
        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);

        setupFeignMocks();
    }

    private void setupFeignMocks() {
        AccountDto accountNegative = AccountDto.builder()
                .login("testuser")
                .name("Вася Пупкин")
                .password("********")
                .email("test@yandex.ru")
                .birthdate(LocalDate.of(1984, 3, 31))
                .balance(20000.0)
                .build();

        when(accountsClient.getAccount("testuser")).thenReturn(accountNegative);
        when(accountsClient.updateBalance("testuser", 5000.0)).thenReturn(accountNegative.toBuilder().balance(5500.0).build());
    }
}
