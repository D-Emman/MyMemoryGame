package com.example.memorygame.utils

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.storage

object SupabaseStorage {
    lateinit var client: SupabaseClient

    fun init() {
        client = createSupabaseClient(
            supabaseUrl = "https://tffsmkjsmjpnhqvfeypa.supabase.co",
//            supabaseKey = "sb_secret_bBjOr7P3zZDDrTNsFH-uhQ_O16DNSq9"
//            supabaseKey = "sb_publishable_YpotEpinEWsC2dI7FIKI"
//            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRmZnNta2pzbWpwbmhxdmZleXBhIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1NDQ3Mjc2NCwiZXhwIjoyMDcwMDQ4NzY0fQ.BxR8jWOKhbbFtW4fAnJ3Jianc_Ttck0az6VTmw3st6w"
        ) {
            install(io.github.jan.supabase.storage.Storage)
        }
    }
}
