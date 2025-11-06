import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешное обновление баланса"
    name "update_balance_success"
    label "update_balance_success"

    request {
        method POST()
        urlPath("/api/accounts/testuser/balance") {
            queryParameters {
                parameter("balance": 2500.0)
            }
        }
        headers {
            contentType(applicationJson())
        }
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
                name: "Вася Пупкин",
                login: "testuser",
                password: $(regex(".+")),
                email: "test@yandex.ru",
                birthdate: "1984-03-31",
                balance: 2500.0,
                errors: []
        ])
    }
}