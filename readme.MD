# SSO-GC API Documentation

## Overview

The SSO-JC (Single Sign-On for Java Applications) project provides a robust implementation of OAuth 2.0 and OpenID Connect protocols, facilitating secure authentication and authorization functionalities across distributed systems. This system manages user sessions and tokens, enabling applications to
authenticate users and access their profile information securely.


## Up and Running

You can run the project using `docker-compose`.


### Using `docker-compose`

```sh
docker-compose up -d
```

## Configuration

The application's configuration is managed through a `application.yml` file, which must specify keys such as `server_port`, `sso_issuer`, `jwk_set_uri`, `client_id`, and `client_secret`. Errors in configuration loading or parsing are handled with immediate log output and system halt to prevent startup with incorrect settings.

If you are using `docker-compose`, you should change the environment variables in the `docker-compose.yaml` file to match your configuration:

```yaml
version: '3.7'
services:
  sso-java-client:
    network_mode: host
    ports:
      - "8080:8080"
    environment:
      - CLIENT_ID=YOUR_CLIENT_ID
      - CLIENT_SECRET=YOUR_CLIENT_SECRET
      - SSO_ISSUER=https://demo-accounts-api.tapsi.ir/api/v1/sso-user/oidc
      - SSO_JWK_SET_URI=https://demo-accounts-api.tapsi.ir/api/v1/sso-user/oidc/keys
      - AUTH_REDIRECT_URI=http://localhost:3000/
    build:
      context: ./
```


## Main Components

### `auth`

Contains core authentication logic, including the retrieval of OpenID configurations, token generation, user information fetching, and custom claim handling.

### `user`

Each time one of the protected APIs of the app is called, the user is retrieved and authenticated through a cookie 
and set in SecurityContextHolder's context.

## APIs

<p align="center">
  <img src="assets/Authorization%20Code%20Flow.png" alt="Authorization Code Flow">
</p>

*There are also some comments in codes that demonstrate which step of the flow is being implemented.

### OpenID Configuration

**Endpoint:** `GET /.well-known/openid-configuration`

Retrieves the OpenID Connect configuration which includes endpoints and capabilities of the OpenID provider.

### Token Generation

**Endpoint:** `POST /token`

Generates tokens based on request parameters. This can include:

- **Access Token:** Used to access protected resources.
- **ID Token:** Contains user profile information in a JWT format.
- **Refresh Token:** Used to renew access tokens without user interaction.

**Parameters:**

- `code`: Authorization code received during user authentication.
- `redirect_uri`: URI to redirect users after authentication.
- `grant_type`: Specifies the type of token request (e.g., authorization_code, refresh_token).
- `client_id`: Registered client identifier.
- `client_secret`: Secret used to authenticate the client to the token endpoint.

### User Information

**Endpoint:** `POST /userinfo`

Retrieves user information using the access token provided in the Authorization header. This endpoint decodes the access token to fetch user attributes.

### Logout

**Endpoint:** `GET /logout`

Terminates the user's session and clears relevant cookies. Optionally redirects the user to a specified URI after logout.

## Security and Compliance

This project implements standard security protocols and complies with OAuth 2.0 and OpenID Connect specifications to ensure secure transmission of information. CORS is configured for cross-origin resource sharing, allowing the server to interact securely with resources from different domains.
Config cors and other security limitations through auth/configuration/SecurityConfiguration.

This service, based on Spring Security, performs authentication and authorization using the access token obtained from after successful login.
Check auth/configuration for customizing your needs. 
