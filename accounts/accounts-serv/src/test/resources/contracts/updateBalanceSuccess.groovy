import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешное обновление баланса"
    name "update_balance_success"
    label "update_balance_success"

    request {
        method PUT()
        urlPath("/api/accounts/testuser/balance")
        headers {
            contentType(applicationJson())
        }
        body(2500.0)
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
                name: "Иван Иванов",
                login: "testuser",
                password: $(regex(".+")),
                email: "test@yandex.ru",
                birthdate: "1984-03-31",
                balance: 2500.0,
                errors: []
        ])
    }
}