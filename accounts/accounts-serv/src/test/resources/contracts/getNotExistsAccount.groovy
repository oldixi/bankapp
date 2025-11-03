import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ошибка при попытке получить информацию по несуществующему ЛС"
    name "get_account_not_found"
    label "get_account_not_found"

    request {
        method GET()
        urlPath("/api/accounts/nonexistentuser")
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
            login: "nonexistentuser",
            errors: ["Лицевой счет не существует"]
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