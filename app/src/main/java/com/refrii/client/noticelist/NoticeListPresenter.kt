package com.refrii.client.noticelist

import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Notice
import com.refrii.client.data.source.ApiFoodRepository
import com.refrii.client.data.source.ApiNoticeRepository
import rx.Subscriber
import javax.inject.Inject

class NoticeListPresenter
@Inject
constructor(
        private val mApiFoodRepository: ApiFoodRepository,
        private val mApiNoticeRepository: ApiNoticeRepository
) : NoticeListContract.Presenter {

    private var mView: NoticeListContract.View? = null
    private var mFood: Food? = null

    override fun init(view: NoticeListContract.View) {
        mView = view
    }

    override fun getFood(id: Int) {
        mApiFoodRepository.getFoodFromCache(id)
                .flatMap {
                    onGetFoodCompleted(it)

                    mApiFoodRepository.getFood(id)
                }
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food?) {
                        onGetFoodCompleted(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        e?.message?.let {
                            mView?.showToast(it)
                        }
                    }
                })
    }

    private fun onGetFoodCompleted(food: Food?) {
        food?.let {
            mFood = it
            mView?.setFood(it)

            food.notices?.let {
                mView?.setNotices(it)
            }
        }

    }

    override fun createNotice(text: String) {
        mFood?.let { food ->
            mApiFoodRepository.createNotice(food.id, text)
                    .subscribe(object : Subscriber<Food>() {
                        override fun onNext(t: Food?) {}

                        override fun onCompleted() {
                            mView?.resetForm()
                            getFood(food.id)
                        }

                        override fun onError(e: Throwable?) {
                            e?.message?.let {
                                mView?.showToast(it)
                            }
                        }
                    })
        }
    }

    override fun removeNotice(notice: Notice) {
        mApiNoticeRepository.remove(notice.id)
                .subscribe(object : Subscriber<Void>() {
                    override fun onNext(t: Void?) {}

                    override fun onCompleted() {
                        mView?.onRemoveCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        e?.message?.let {
                            mView?.showToast(it)
                        }
                    }
                })
    }
}