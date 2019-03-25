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
    private var mId: Int? = null
    private var mName: String? = null
    private var mNotice: String? = null

    override fun takeView(view: BoxInfoContract.View) {
        mView = view
    }

    override fun getBox(id: Int) {
        mView?.onLoading()

        mBox = mApiRepository.getBox(id, object : ApiRepositoryCallback<Box> {
            override fun onNext(t: Box?) {
                mId = t?.id
                mName = t?.name
                mNotice = t?.notice
                mView?.setBox(t)
            }

            override fun onCompleted() {
                mView?.onLoaded()
            }

            override fun onError(e: Throwable?) {
                mView?.onLoaded()
                mView?.showToast(e?.message)
            }
        })

        mId = mBox?.id
        mName = mBox?.name
        mNotice = mBox?.notice
        mView?.setBox(mBox)
    }

    override fun updateBox() {
        mView?.onLoading()

        mId?.let {
            mApiRepository.updateBox(object : ApiRepositoryCallback<Box> {
                override fun onNext(t: Box?) {
                    mBox = t
                    mView?.setBox(t)
                    mView?.showSnackbar("Box ${t?.name} is updated successfully")
                }

                override fun onCompleted() {
                    mView?.onLoaded()
                }

                override fun onError(e: Throwable?) {
                    mView?.onLoaded()
                    mView?.showToast(e?.message)
                }
            }, it, mName, mNotice)
        }
    }

    override fun editSharedUsers() {
        mView?.showEditSharedUsersDialog(mBox?.invitedUsers)
    }

    override fun updateName(name: String) {
        mName = name
    }

    override fun updateNotice(notice: String) {
        mNotice = notice
    }

    override fun updateSharedUsers(users: List<User>) {
//        mBox?.invitedUsers = users
//        mView?.setBox(mBox)
    }
}