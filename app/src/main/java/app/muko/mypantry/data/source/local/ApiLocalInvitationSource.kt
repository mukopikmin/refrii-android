package app.muko.mypantry.data.source.local

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.source.data.ApiInvitationDataSource
import io.reactivex.Completable

class ApiLocalInvitationSource : ApiInvitationDataSource {

    override fun create(box: Box, email: String): Completable {
        TODO("Not yet implemented")
    }

    override fun remove(invitation: Invitation): Completable {
        TODO("Not yet implemented")
    }
}