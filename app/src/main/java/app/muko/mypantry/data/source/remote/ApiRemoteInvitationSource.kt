package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.source.data.ApiInvitationDataSource
import app.muko.mypantry.data.source.remote.services.InvitationService
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class ApiRemoteInvitationSource(
        private val service: InvitationService
) : ApiInvitationDataSource {

    override fun create(box: Box, email: String): Completable {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .build()

        return service.create(box.id, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun remove(invitation: Invitation): Completable {
        return service.remove(invitation.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
