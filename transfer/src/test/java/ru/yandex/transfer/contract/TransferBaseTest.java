package ru.yandex.transfer.contract;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.accounts.dto.AccountDto;
import ru.yandex.api.AccountsClient;
import ru.yandex.transfer.TransferController;
import ru.yandex.transfer.TransferService;
import ru.yandex.transfer.config.TestFeignConfig;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment./*RANDOM_PORT*/MOCK)
@AutoConfigureMessageVerifier
@AutoConfigureStubRunner(ids = {"ru.yandex:accounts:0.0.1-SNAPSHOT:stubs:8082",
        "ru.yandex:notifications:0.0.1-SNAPSHOT:stubs:8085"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@ActiveProfiles("test")
@Import(TestFeignConfig.class)
@AutoConfigureWireMock(port = 8084)
public abstract class TransferBaseTest {
    @Autowired
    protected WebApplicationContext webApplicationContext;
/*
    @Autowired
    protected TestRestTemplate testRestTemplate;

    @LocalServerPort
    protected int port;*/

    @Autowired
    protected StubFinder stubFinder;

/*    @Mock
    protected NotificationsClient notificationsClient;*/

/*    @BeforeEach
    public void setup() {
        int accountsPort = stubFinder.findStubUrl("accounts").getPort();
        int notificationsPort = stubFinder.findStubUrl("notifications").getPort();
        System.out.println("Accounts stub port: " + accountsPort);
        System.out.println("Notifications stub port: " + notificationsPort);
    }*/

    @Autowired
    protected TransferController controller;

    @Mock
    protected AccountsClient accountClient;

    @Mock
    protected TransferService transferService;

    @BeforeEach
    public void configureRestAssured() {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);

        StandaloneMockMvcBuilder standaloneMockMvcBuilder = MockMvcBuilders.standaloneSetup(controller);
        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);
    }

    @BeforeEach
    public void createDonor() {
        AccountDto donor = AccountDto.builder()
                .login("donor")
                .name("Вася Пупкин")
                .password("password123")
                .email("donor@example.com")
                .birthdate(LocalDate.of(1990, 1, 1))
                .balance(1000.0)
                .build();

        when(accountClient.getAccount("donor")).thenReturn(donor);
        when(accountClient.getAccount("recipientuser")).thenReturn(
                AccountDto.builder()
                        .login("recipientuser")
                        .balance(500.0)
                        .build());

        when(transferService.transfer(eq("donor"), anyDouble(), eq("recipientuser")))
                .thenReturn(donor);
    }
}
