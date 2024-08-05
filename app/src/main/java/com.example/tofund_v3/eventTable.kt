package com.example.tofund_v3

data class eventTable(
    var EVENT_KEY: String? = null,
    var evName: String? = null,
    var evCollectedAmount: Long? = 0,
    var evTargetAmount: Long? = 0,
    var condition: String? = null,
    var isButtonEnabled: Boolean = true
)

