package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ошибка попытки снять средства при недостаточном балансе"
    name "get_withdraw_insufficient_funds"
    label "get_withdraw_insufficient_funds"

    request {
        method POST()
        urlPath("/api/cash/testuser/cash") {
            queryParameters {
                parameter("amount": "4000.0")
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
                login: "testuser",
                balance: 100.0,
                errors: ["Недостаточно средств на счете"]
        ])
        bodyMatchers {
            jsonPath('$.name', byNull())
            jsonPath('$.password', byNull())
            jsonPath('$.email', byNull())
            jsonPath('$.birthdate', byNull())
        }
    }
}
