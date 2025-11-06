import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешное получение информации о пользователе"
    name "get_account_success"
    label "get_account_success"

    request {
        method GET()
        urlPath("/api/accounts/user1")
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
            name: "Вася Пупкин",
            login: "user1",
            password: $(regex(".+")),
            email: "test@yandex.ru",
            birthdate: "1984-03-31",
            balance: 1500.0,
            errors: []
        ])
    }
}