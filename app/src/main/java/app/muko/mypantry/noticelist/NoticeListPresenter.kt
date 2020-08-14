package app.muko.mypantry.noticelist

import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Notice
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiNoticeRepository
import io.reactivex.subscribers.DisposableSubscriber
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
        mApiFoodRepository.get(id)
                .flatMap {
                    onGetFoodCompleted(it)

                    mApiFoodRepository.get(id)
                }
                .subscribe(object : DisposableSubscriber<Food>() {
                    override fun onNext(t: Food?) {
                        onGetFoodCompleted(t)
                    }

                    override fun onComplete() {}

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
//        TODO
//        mFood?.let { food ->
//            mApiFoodRepository.createNotice(food.id, text)
//                    .subscribe(object : DisposableSubscriber<Food>() {
//                        override fun onNext(t: Food?) {}
//
//                        override fun onComplete() {
//                            mView?.resetForm()
//                            getFood(food.id)
//                        }
//
//                        override fun onError(e: Throwable?) {
//                            e?.message?.let {
//                                mView?.showToast(it)
//                            }
//                        }
//                    })
//        }
    }

    override fun removeNotice() {
        mNotice?.let {
            mApiNoticeRepository.remove(it.id)
                    .subscribe(object : DisposableSubscriber<Void>() {
                        override fun onNext(t: Void?) {}

                        override fun onComplete() {
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