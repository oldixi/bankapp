import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешный перевод средств"
    name "transfer_success"
    label "transfer_success"

    request {
        method POST()
        urlPath("/api/transfer/testuser/transfer-other") {
            queryParameters {
                parameter("amount": "500.0")
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
                name: "Иван Иванов",
                login: "testuser",
                password: $(regex(".+")),
                email: "testuser@example.com",
                birthdate: "1990-01-01",
                balance: 1000.0,
                errors: []
        ])
    }
}