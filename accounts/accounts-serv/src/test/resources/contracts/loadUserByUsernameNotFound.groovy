import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ошибка загрузки несвуществующего пользователя для аутентификации"
    name "load_user_by_username_not_found"
    label "load_user_by_username_not_found"

    request {
        method GET()
        urlPath("/api/accounts/nonexistentuser/login")
        headers {
            contentType(applicationJson())
        }
    }

    response {
        status NOT_FOUND()
        headers {
            contentType(applicationJson())
        }
        body([
            timestamp: $(regex("${iso8601WithOffset()}")),
            status: 404,
            error: "Not Found",
            message: "Пользователь не найден",
            path: "/api/accounts/nonexistentuser/login"
        ])
    }
}