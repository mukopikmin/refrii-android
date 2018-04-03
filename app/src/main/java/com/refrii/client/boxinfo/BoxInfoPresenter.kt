package com.refrii.client.boxinfo

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.User
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import javax.inject.Inject

class BoxInfoPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : BoxInfoContract.Presenter {

    private var mView: BoxInfoContract.View? = null
    private var mBox: Box? = null

    override fun takeView(view: BoxInfoContract.View) {
        mView = view
    }

    override fun getBox(id: Int) {
        mView?.onLoading()

        mApiRepository.getBox(id, object : ApiRepositoryCallback<Box> {
            override fun onNext(t: Box?) {
                mBox = t
                mView?.setBox(t)
            }

            override fun onCompleted() {
                mView?.onLoaded()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })
    }

    override fun updateBox() {
        mView?.onLoading()

        mBox?.let {
            mApiRepository.updateBox(it, object : ApiRepositoryCallback<Box> {
                override fun onNext(t: Box?) {
                    mBox = t
                    mView?.setBox(t)
                }

                override fun onCompleted() {
                    mView?.onLoaded()
                    mView?.showSnackbar("Update completed")
                }

                override fun onError(e: Throwable?) {
                    mView?.showToast(e?.message)
                }
            })
        }
    }

    override fun editName() {
        mView?.showEditNameDialog(mBox?.name)
    }

    override fun editNotice() {
        mView?.showEditNoticeDialog(mBox?.notice)
    }

    override fun editSharedUsers() {
        mView?.showEditSharedUsersDialog(mBox?.invitedUsers)
    }

    override fun updateName(name: String) {
        mBox?.name = name
        mView?.setBox(mBox)
    }

    override fun updateNotice(notice: String) {
        mBox?.notice = notice
        mView?.setBox(mBox)
    }

    override fun updateSharedUsers(users: List<User>) {
//        mBox?.invitedUsers = users
//        mView?.setBox(mBox)
    }
}