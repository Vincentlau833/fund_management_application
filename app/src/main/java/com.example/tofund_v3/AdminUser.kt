package com.example.tofund_v3

data class AdminUser(
    var email: String? = null,
    var role:String? = null,
    var username:String? = null,
    var status: String? = null,
    var isButtonEnabled: Boolean = true
)
