/*
 * Responsabilidad: Configura y expone una única instancia del cliente Supabase utilizada por la
 * capa de datos.
 */

package com.miguel.portomuinos.data.remote

import com.miguel.portomuinos.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {

    val client: SupabaseClient by lazy {

        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Postgrest)

            install(Auth) {
                scheme = "portomuinos"
                host = "auth"
            }

            install(Functions)
        }
    }
}