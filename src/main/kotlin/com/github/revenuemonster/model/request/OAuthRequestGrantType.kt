package com.github.revenuemonster.model.request

import com.fasterxml.jackson.annotation.JsonValue

enum class OAuthRequestGrantType(@JsonValue val id: String) {
    CLIENT_CREDENTIALS("client_credentials"),
    REFRESH_TOKEN("refresh_token");
}