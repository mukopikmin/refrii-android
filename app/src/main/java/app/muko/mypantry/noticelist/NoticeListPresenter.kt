package app.muko.mypantry.noticelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Notice
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiNoticeRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class NoticeListPresenter
@Inject
constructor(
        private val apiFoodRepository: ApiFoodRepository,
        private val apiNoticeRepository: ApiNoticeRepository
) : NoticeListContract.Presenter {

    private lateinit var view: NoticeListContract.View

    //    private var mFood: Food? = null
    private var notice: Notice? = null
    private lateinit var foodLiveData: LiveData<Food>

    override fun init(view: NoticeListContract.View, foodId: Int) {
        this.view = view as NoticeListActivity
        foodLiveData = apiFoodRepository.dao.getLiveData(foodId)

        foodLiveData.observe(view, Observer {
            view.setFood(it)
            view.setNotices(it.notices)
        })
    }

    override fun getFood(id: Int) {
        apiFoodRepository.get(id)
                .subscribe(object : DisposableSubscriber<Food>() {
                    override fun onNext(t: Food?) {}
                    override fun onComplete() {}
                    override fun onError(e: Throwable?) {
                        e?.message?.let {
                            view.showToast(it)
                        }
                    }
                })
    }

    override fun createNotice(text: String) {
        val food = foodLiveData.value ?: return

        apiFoodRepository.createNotice(food, text)
                .subscribe(object : DisposableSubscriber<Food>() {
                    override fun onNext(t: Food?) {}

                    override fun onComplete() {
                        view.resetForm()
                        getFood(food.id)
                    }

                    override fun onError(e: Throwable?) {
                        e?.message?.let {
                            view.showToast(it)
                        }
                    }
                })
    }

    override fun removeNotice() {
        notice?.let {
            apiNoticeRepository.remove(it)
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            view.onRemoveCompleted()
                        }

                        override fun onSubscribe(d: Disposable) {}

                        override fun onError(e: Throwable) {
                            e.message?.let {
                                view.showToast(it)
                            }
                        }
                    })
        }
    }


    override fun confirmRemovingNotice(notice: Notice) {
        val food = foodLiveData.value ?: return

        this.notice = notice
        view.showRemoveConfirmation(food.name, notice)
    }
}