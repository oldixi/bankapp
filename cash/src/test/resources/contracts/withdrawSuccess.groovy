package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешное снятие средств"
    name "get_withdraw_success"
    label "get_withdraw_success"

    request {
        method POST()
        urlPath("/api/cash/testuser") {
            queryParameters {
                parameter("amount": "5000.0")
                parameter("action": "GET")
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
                password: "*********",
                email: "test@yandex.ru",
                birthdate: "1984-31-03",
                balance: 20000.0,
                errors: []
        ])
    }
}
