import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешное получение списка ЛС для перевода"
    name "get_accounts_for_transfer"
    label "get_accounts_for_transfer"

    request {
        method GET()
        urlPath("/api/accounts/existinguser/transfer")
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
                [
                        login: "user1",
                        name: "User 1"
                ],
                [
                        login: "user2",
                        name: "User 2"
                ],
                [
                        login: "user3",
                        name: "User 3"
                ]
        ])
    }
}