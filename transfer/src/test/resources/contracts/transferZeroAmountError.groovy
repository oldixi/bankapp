import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ошибка при попытке перевода нулевой суммы"
    name "transfer_zero_amount"
    label "transfer_zero_amount"

    request {
        method POST()
        urlPath("/api/transfer/testuser/transfer-other") {
            queryParameters {
                parameter("amount": "0.0")
                parameter("toLogin": "recipientuser")
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
                balance: 0.0,
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