package com.refrii.client.data.source.remote.services

import retrofit2.http.DELETE
import retrofit2.http.Path
import rx.Observable

interface NoticeService {

    @DELETE("/notices/{id}")
    fun remove(@Path("id") id: Int): Observable<Void>
}