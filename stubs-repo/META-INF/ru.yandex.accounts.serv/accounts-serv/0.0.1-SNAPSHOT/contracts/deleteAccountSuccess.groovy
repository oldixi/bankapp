import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешное удаление аккаунта"
    name "delete_account_success"
    label "delete_account_success"

    request {
        method DELETE()
        urlPath("/api/accounts/userzerobalance")
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
                login: "userzerobalance",
                balance: 0.0,
                errors: []
        ])
        bodyMatchers {
            jsonPath('$.name', byNull())
            jsonPath('$.password', byNull())
            jsonPath('$.email', byNull())
            jsonPath('$.birthdate', byNull())
        }
    }
}