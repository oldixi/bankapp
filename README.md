# bankapp

## Функциональность
Микросервисное приложение «Банк» — это приложение с веб-интерфейсом, которое позволяет пользователю (клиенту банка):
* регистрироваться в системе по логину и паролю (заводить аккаунт);
* класть виртуальные деньги на счёт пользователя и снимать их;
* переводить деньги на другой счёт.<br>

Приложение состоит из следующих микросервисов:
* фронта (front);
* сервиса аккаунтов (accounts);
* сервиса обналичивания денег (cash);
* сервиса перевода денег между счетами одного или двух аккаунтов (transfer);
* сервиса уведомлений (notifications).

## Фронт (front)
Фронт (front) — это веб-приложение с клиентским HTML-интерфейсом. Предоставляет следующие HTML-страницы:
* Cтраницу ввода логина/пароля:
  * поле ввода логина;
  * поле ввода пароля;
  * кнопку «Войти», при нажатии на которую происходит аутентификация/авторизация пользователя и редирект на главную страницу.
* Страницу выхода из приложения (разлогинивания) содержит ссылку, при нажатии на которую:
  * пользователь разлогинивается;
  * происходит редирект на страницу ввода логина/пароля.
* Страницу регистрации пользователя в приложении (нового аккаунта):
  * поле ввода фамилии, имени, почты, даты рождения;
  * поле ввода логина, пароля и подтверждения пароля;
  * кнопку «Зарегистрироваться», при нажатии на которую происходит проверка введённых данных и создаётся новый аккаунт с автоматической аутентификацией/авторизацией пользователя и редиректом на главную страницу.
* Главную страницу, которая доступна только после успешной аутентификации/авторизации пользователя:
  * блок настроек аккаунта состоит из: 
    * логина пользователя без возможности редактирования, но с возможностью удаления (если есть хотя бы один ненулевой счёт, то должна появляться ошибка);
    * поля ввода нового пароля и кнопки «Изменить пароль», при нажатии на которую меняется пароль пользователя (пароль должен быть непустой);
    * фамилии, имени, почты (если предполагается отправлять уведомления на почту) и даты рождения с возможностью их редактирования (должна быть предусмотрена валидация: все поля заполнены, возраст старше 18 лет);
    * списка счетов пользователя с возможностью их добавления, редактирования и удаления (у пользователя может быть не более одного счёта в определённой валюте);
    * ссылки на страницу разлогинивания;
  * блок внесения и снятия виртуальных денег состоит из:
      * текущей суммы на счёте; 
      * поля ввода суммы снятия (обязательно);
      * кнопок «Положить» и «Снять» (если сумма, которую нужно снять, больше суммы на счёте, то должна появляться ошибка);
  * блок перевода денег на счёт другого аккаунта состоит из:
    * поля выбора счёта получателя (обязательно, с поиском по аккаунту);
    * поля ввода суммы перевода (если сумма больше суммы на счёте отправления, то должна появляться ошибка);
    * кнопки, при нажатии на которую осуществляется перевод денег.

## Сервис аккаунтов (accounts)
Сервис аккаунтов хранит информацию о зарегистрированных аккаунтах и счетах в каждом из них (именно в нём хранятся логин/пароль, которые проверяются при аутентификации пользователя).
Фронт выполняет REST-запросы (в формате JSON) из блока настроек аккаунта в сервис Accounts при получении данных аккаунта и данных о счетах, их редактировании, добавлении и удалении.
В свою очередь, accounts выполняет REST-запросы (в формате JSON) в notifications.
Также в accounts приходят запросы при регистрации нового аккаунта (из формы регистрации).

## Сервис обналичивания денег (cash)
Сервис обналичивания денег осуществляет пополнение счёта или снятие денег со счёта.
Фронт выполняет REST-запросы (в формате JSON) из блока внесения и снятия виртуальных денег в сервис cash.
В свою очередь, cash выполняет REST-запросы (в формате JSON) в accounts (нотификация notifications из accounts).

## Сервис перевода денег между счетами (transfer)
Сервис перевода денег между счетами осуществляет перевод денег между счетами одного пользователя и между счетами разных пользователей.
Фронт выполняет REST-запросы (в формате JSON) из блока перевода денег между своими счетами и блока перевода денег на счёт другого аккаунта в сервис transfer.
В свою очередь, transfer выполняет REST-запросы (в формате JSON) в accounts (нотификация notifications из accounts).

## Сервис уведомлений (notifications)
Сервис уведомлений отправляет уведомления на почту пользователю о выполненном действии: 
* регистрации аккаунта, 
* удалении аккаунта, 
* переводе денег, 
* пополнении счёта, 
* снятии денег со счёта 
* и т. д.

## _Структура проекта_
> Spring Framework<br>
> Java: 21<br>
> СУБД PostgreSQL: 17.5<br>


## _Запуск приложения_
Предусмотренно развертывание приложения в Kubernetes с помощью инструментов helm. <br>
* Возможно запустить каждый сервис по-отдельности (в таком случае следует не забыть развернуть сервис keycloak и postgres -см. зависимости)<br>
* Все приложение можно развернуть с помощью зонтичного чарта.

Для этого необходимо:
* получить образы сервисов
```bash
./gradlew bootJar
docker build -f notifications/Dockerfile -t notifications .
docker build -f accounts/Dockerfile -t accounts .
docker build -f cash/Dockerfile -t cash .
docker build -f transfer/Dockerfile -t transfer .
docker build -f front/Dockerfile -t front .
```
* скачать зависимости (для postgresql, keycloak и kafka)
```bash
cd bankapp-chart
helm dependency build
```
* создать секреты в Kubernetis (в примере пространство имен bankapp)
```bash
kubectl create secret generic accounts-secrets \
  --namespace=bankapp \
  --from-literal=datasource-username=bankapp \
  --from-literal=datasource-password=bankapp \
  --from-literal=liquibase-user=bankapp \
  --from-literal=liquibase-password=bankapp \
  --from-literal=oauth2-client-secret=vRBqHqy5rIgQjqHLk4ntrFMflfZZ1V5Y \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic cash-secrets \
  --namespace=bankapp \
  --from-literal=oauth2-client-secret=VKgaEyKXFsc5QJJrtDolB2Luv7KyeXth \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic transfer-secrets \
  --namespace=bankapp \
  --from-literal=oauth2-client-secret=tFVIAzOu86RAkgbIzmZgEkeCoOYk74w1 \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic notifications-secrets \
  --namespace=bankapp \
  --from-literal=oauth2-client-secret=zwNU03EpVjSvqo7UpsJdghw6v0EVe0hC \
  --from-literal=mail-password=irwlzrzewdogocpw \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic front-secrets \
  --namespace=bankapp \
  --from-literal=oauth2-client-secret=moY8OTX4GbDI5AwmholMgAXT0aJDCSpf \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic postgresql-secrets \
  --namespace=bankapp \
--from-literal=postgres-password=bankapp \
  --from-literal=password=bankapp \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic keycloak-secrets \
  --namespace=bankapp \
  --from-literal=postgres-password=bankapp \
  --from-literal=password=bankapp \
  --dry-run=client -o yaml | kubectl apply -f -
  
kubectl create secret generic kafka-secrets \
  --namespace=bankapp \
  --from-literal=cluster-id=bankapp \
  --from-literal=controller-0-id=0 \
  --from-literal=client-passwords=bankapp \
  --from-literal=inter-broker-password=bankapp \
  --from-literal=controller-password=bankapp \
  --dry-run=client -o yaml | kubectl apply -f -  
```

* установить чарт (в примере пространство имен bankapp)
```bash
helm upgrade --install bankapp . -n bankapp
```

Вход приложения осуществляется по адресу:<br>
http://bankapp/login

Регистрация пользователя доступна на странице:<br>
http://bankapp/signup

Основная страница приложения (доступна после логина):<br>
http://bankapp/user/main

Предварительно необходимо: 
* настроить /etc/hosts:
```bash
echo "127.0.0.1 bankapp" | sudo tee -a /etc/hosts
```
* прокинуть порты:
```bash
sudo kubectl port-forward -n ingress-nginx svc/ingress-nginx-controller 80:80
```

Для автоматического развертывания приложения в Kubernetes после push'а в любую ветку
```bash
cd jenkins
docker compose up -d --build
```

