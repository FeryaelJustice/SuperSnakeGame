package com.feryaeljustice.supersnakegame.ui.navigation

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T : Parcelable> createNavType(): NavType<T> =
    object : NavType<T>(isNullableAllowed = true) {
        override fun get(
            bundle: Bundle,
            key: String,
        ): T? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(key, T::class.java)
            } else {
                bundle.getParcelable(key)
            }

        override fun parseValue(value: String): T = Json.decodeFromString<T>(value)

        override fun serializeAsValue(value: T): String = Uri.encode(Json.encodeToString(value))

        override fun put(
            bundle: Bundle,
            key: String,
            value: T,
        ) {
            bundle.putParcelable(key, value)
        }
    }
