package com.example.memorygame.utils

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.storage

object SupabaseStorage {
    lateinit var client: SupabaseClient

    fun init() {
        client = createSupabaseClient(
            supabaseUrl = "https://tffsmkjsmjpnhqvfeypa.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRmZnNta2pzbWpwbmhxdmZleXBhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTQ0NzI3NjQsImV4cCI6MjA3MDA0ODc2NH0.R0i12_Sk8X-ZfNebJwwhrcO1Tg3g7Kjqu2mwTTKDbCI"
        ) {
            install(io.github.jan.supabase.storage.Storage)
        }
    }
}
