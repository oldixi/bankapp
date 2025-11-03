import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ошибка при попытке перевода без указания ЛС получателя"
    name "transfer_no_recipient"
    label "transfer_no_recipient"

    request {
        method POST()
        urlPath("/api/transfer/testuser/transfer-other") {
            queryParameters {
                parameter("amount": "100.0")
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
                errors: ["Получатель не выбран"]
        ])
        bodyMatchers {
            jsonPath('$.name', byNull())
            jsonPath('$.password', byNull())
            jsonPath('$.email', byNull())
            jsonPath('$.birthdate', byNull())
            jsonPath('$.balance', byNull())
        }
    }
}