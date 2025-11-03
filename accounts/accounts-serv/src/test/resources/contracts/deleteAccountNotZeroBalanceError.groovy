import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ошибка при попытке уделания акканту с ненулевым балансом"
    name "delete_account_with_balance"
    label "delete_account_with_balance"

    request {
        method DELETE()
        urlPath("/api/accounts/usernotzero")
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
            login: "usernotzero",
            balance: 100.0,
            errors: ["Удаление счета не возможно: баланс на счете не равен 0"]
        ])
        bodyMatchers {
            jsonPath('$.name', byNull())
            jsonPath('$.password', byNull())
            jsonPath('$.email', byNull())
            jsonPath('$.birthdate', byNull())
        }
    }
}