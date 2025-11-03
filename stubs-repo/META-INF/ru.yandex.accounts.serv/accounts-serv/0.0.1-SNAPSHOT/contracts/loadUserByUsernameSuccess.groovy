import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешная загрузка пользователя для аутентификации"
    name "load_user_by_username_success"
    label "load_user_by_username_success"

    request {
        method GET()
        urlPath("/api/accounts/user/login")
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
                username: "user",
                password: $(regex(".+")),
                authorities: [
                        [authority: "ROLE_USER"]
                ],
                enabled: true,
                accountNonExpired: true,
                accountNonLocked: true,
                credentialsNonExpired: true
        ])
    }
}