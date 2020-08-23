package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.source.data.ApiInvitationDataSource
import app.muko.mypantry.data.source.remote.ApiRemoteInvitationSource
import app.muko.mypantry.data.source.remote.services.InvitationService
import io.reactivex.Completable

class ApiInvitationRepository(service: InvitationService) : ApiInvitationDataSource {

    private val remote = ApiRemoteInvitationSource(service)

    override fun create(box: Box, email: String): Completable {
        return remote.create(box, email)
    }

    override fun remove(invitation: Invitation): Completable {
        return remote.remove(invitation)
    }
}