package com.visionagent.apptest.model.request

data class QueryList(var page: Int?,
                     var rows: Int?,
                     var name: String?) {
    constructor() : this(1, 10, null)
}