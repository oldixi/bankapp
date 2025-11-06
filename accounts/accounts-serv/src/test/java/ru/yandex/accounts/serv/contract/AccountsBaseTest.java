package ru.yandex.accounts.serv.contract;

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
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.accounts.serv.accounts.AccountController;
import ru.yandex.accounts.serv.accounts.AccountRepository;
import ru.yandex.accounts.serv.accounts.AccountsApplication;
import ru.yandex.accounts.serv.api.NotificationsClient;
import ru.yandex.accounts.serv.config.TestFeignConfig;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = AccountsApplication.class)
@AutoConfigureMessageVerifier
@AutoConfigureStubRunner(ids = {"ru.yandex:notifications:0.0.1-SNAPSHOT:stubs:8085"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@ActiveProfiles("test")
@Import(TestFeignConfig.class)
@AutoConfigureWireMock(port = 8082)
public abstract class AccountsBaseTest {
    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Mock
    protected NotificationsClient notificationsClient;

    @Mock
    protected AccountRepository accountRepository;

    @Autowired
    protected AccountController controller;

    @BeforeEach
    public void configureRestAssured() {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        RestAssuredMockMvc.mockMvc(mockMvc);

        StandaloneMockMvcBuilder standaloneMockMvcBuilder = MockMvcBuilders.standaloneSetup(controller);
        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);

        setupFeignMocks();
    }

    private void setupFeignMocks() {
        doNothing().when(notificationsClient).sendNotification(anyString(), anyString());
    }
}
