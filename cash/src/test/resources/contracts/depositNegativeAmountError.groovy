package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ошибка попытки зачисления отрицательной суммы"
    name "put_deposit_negative_amount"
    label "put_deposit_negative_amount"

    request {
        method POST()
        urlPath("/api/cash/testuser/cash") {
            queryParameters {
                parameter("amount": "-1000.0")
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
                login: "testuser",
                balance: 15100.0,
                errors: ["Сумма перевода должна быть положительной"]
        ])
        bodyMatchers {
            jsonPath('$.name', byNull())
            jsonPath('$.password', byNull())
            jsonPath('$.email', byNull())
            jsonPath('$.birthdate', byNull())
        }
    }
}
