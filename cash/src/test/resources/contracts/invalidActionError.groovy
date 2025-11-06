package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ошибка неподдерживаемого типа операции"
    name "invalid_action"
    label "invalid_action"

    request {
        method POST()
        urlPath("/api/cash/testuser/cash") {
            queryParameters {
                parameter("amount": "100.0")
                parameter("action": "DUMMY")
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
                errors: ["Тип операции не поддерживается"]
        ])
        bodyMatchers {
            jsonPath('$.name', byNull())
            jsonPath('$.password', byNull())
            jsonPath('$.email', byNull())
            jsonPath('$.birthdate', byNull())
        }
    }
}
