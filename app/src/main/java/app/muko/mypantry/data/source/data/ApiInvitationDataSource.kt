package app.muko.mypantry.data.source.data

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation
import io.reactivex.Completable

interface ApiInvitationDataSource {
    fun create(box: Box, email: String): Completable
    fun remove(invitation: Invitation): Completable
}