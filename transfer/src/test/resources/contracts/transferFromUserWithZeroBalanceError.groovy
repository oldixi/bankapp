import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ошибка при попытке перевода средств со счета, на котором их недостаточно"
    name "transfer_from_zero_balance"
    label "transfer_from_zero_balance"

    request {
        method POST()
        urlPath("/api/transfer/newuser/transfer-other") {
            queryParameters {
                parameter("amount": "100.0")
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
                login: "newuser",
                balance: 0.0,
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