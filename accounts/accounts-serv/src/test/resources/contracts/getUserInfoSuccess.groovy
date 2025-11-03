import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should successfully get account information"
    name "get_account_success"
    label "get_account_success"

    request {
        method GET()
        urlPath("/api/accounts/existinguser")
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
            email: "test@yandex.ru",
            birthdate: "1984-03-31",
            balance: 1500.0,
            errors: []
        ])
    }
}