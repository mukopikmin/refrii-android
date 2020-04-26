package app.muko.mypantry.noticelist

import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Notice
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiNoticeRepository
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
    private var mNotice: Notice? = null

    override fun init(view: NoticeListContract.View) {
        mView = view
    }

    override fun getFood(id: Int) {
        mApiFoodRepository.getFood(id)
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

    override fun removeNotice() {
        mNotice?.let {
            mApiNoticeRepository.remove(it.id)
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

    override fun confirmRemovingNotice(notice: Notice) {
        mNotice = notice

        mFood?.name?.let {
            mView?.showRemoveConfirmation(it, notice)
        }
    }
}