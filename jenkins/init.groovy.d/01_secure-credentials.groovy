import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import hudson.util.Secret
import jenkins.model.*
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl

def env = System.getenv()

def githubUsername = env['GITHUB_USERNAME']
def githubToken = env['GITHUB_TOKEN']
def ghcrToken = env['GHCR_TOKEN']
def dockerRegistry = env['DOCKER_REGISTRY']
def secret = env['SECRET']
def mail_secret = env['MAIL_SECRET']
def accounts_secret = env['ACCOUNTS_CLIENT_SECRET']
def front_secret = env['FRONT_CLIENT_SECRET']
def cash_secret = env['CASH_CLIENT_SECRET']
def transfer_secret = env['TRANSFER_CLIENT_SECRET']
def notifications_secret = env['NOTIFICATIONS_CLIENT_SECRET']

def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

if (githubUsername && githubToken) {
    println "--> Creating credential: github-creds (username + token)"
    def githubCreds = new UsernamePasswordCredentialsImpl(
            CredentialsScope.GLOBAL,
            "github-creds",
            "GitHub credentials from ENV",
            githubUsername,
            githubToken
    )
    store.addCredentials(Domain.global(), githubCreds)
}

if (githubUsername) {
    println "--> Creating credential: GITHUB_USERNAME (plain string)"
    def usernameCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "GITHUB_USERNAME",
            "GitHub username only (for GHCR login)",
            Secret.fromString(githubUsername)
    )
    store.addCredentials(Domain.global(), usernameCred)
}

if (ghcrToken) {
    println "--> Creating credential: GHCR_TOKEN"
    def ghcrCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "GHCR_TOKEN",
            "GHCR token from ENV",
            Secret.fromString(ghcrToken)
    )
    store.addCredentials(Domain.global(), ghcrCred)
}

if (dockerRegistry) {
    println "--> Creating credential: DOCKER_REGISTRY"
    def registryCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "DOCKER_REGISTRY",
            "Docker registry address from ENV",
            Secret.fromString(dockerRegistry)
    )
    store.addCredentials(Domain.global(), registryCred)
}

if (secret) {
    println "--> Creating credential: SECRET"
    def dbCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "SECRET",
            "Secret from ENV",
            Secret.fromString(secret)
    )
    store.addCredentials(Domain.global(), dbCred)
}

if (mail_secret) {
    println "--> Creating credential: MAIL_SECRET"
    def dbCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "MAIL_SECRET",
            "Mail secret from ENV",
            Secret.fromString(secret)
    )
    store.addCredentials(Domain.global(), dbCred)
}

if (accounts_secret) {
    println "--> Creating credential: ACCOUNTS_CLIENT_SECRET"
    def dbCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "ACCOUNTS_CLIENT_SECRET",
            "Accounts client secret from ENV",
            Secret.fromString(secret)
    )
    store.addCredentials(Domain.global(), dbCred)
}

if (front_secret) {
    println "--> Creating credential: FRONT_CLIENT_SECRET"
    def dbCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "FRONT_CLIENT_SECRET",
            "Front client secret from ENV",
            Secret.fromString(secret)
    )
    store.addCredentials(Domain.global(), dbCred)
}

if (cash_secret) {
    println "--> Creating credential: CASH_CLIENT_SECRET"
    def dbCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "CASH_CLIENT_SECRET",
            "Cash client secret from ENV",
            Secret.fromString(secret)
    )
    store.addCredentials(Domain.global(), dbCred)
}

if (transfer_secret) {
    println "--> Creating credential: TRANSFER_CLIENT_SECRET"
    def dbCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "TRANSFER_CLIENT_SECRET",
            "Transfer client secret from ENV",
            Secret.fromString(secret)
    )
    store.addCredentials(Domain.global(), dbCred)
}

if (notifications_secret) {
    println "--> Creating credential: NOTIFICATIONS_CLIENT_SECRET"
    def dbCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "NOTIFICATIONS_CLIENT_SECRET",
            "Notifications client secret from ENV",
            Secret.fromString(secret)
    )
    store.addCredentials(Domain.global(), dbCred)
}

println "--> Credential setup complete."