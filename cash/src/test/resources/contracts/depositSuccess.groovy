package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешное зачисление средств"
    name "put_deposit_success"
    label "put_deposit_success"

    request {
        method POST()
        urlPath("/api/cash/testuser") {
            queryParameters {
                parameter("amount": "1000.0")
                parameter("action": "PUT")
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
                birthdate: "1984-03-31",
                balance: 25000.0,
                errors: []
        ])
    }
}