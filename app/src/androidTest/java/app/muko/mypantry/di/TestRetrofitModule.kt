package app.muko.mypantry.di

import android.content.Context
import app.muko.mypantry.BuildConfig
import app.muko.mypantry.data.models.*
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.remote.services.BoxService
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.util.*
import javax.inject.Singleton


@Module
class TestRetrofitModule {

    @Singleton
    @Provides
    fun provideRetrofit(context: Context): Retrofit {
        val gson = GsonBuilder()
                .setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        return Retrofit.Builder()
                .baseUrl(getApiEndpoint())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(provideHttpClient(context))
                .build()
    }

    @Singleton
    @Provides
    fun provideBoxService(retrofit: Retrofit): BoxService {
        val behavior = NetworkBehavior.create()
        val mockRetrofit = MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build()
        val delegate = mockRetrofit.create(BoxService::class.java)

        return MockBoxService(delegate)
    }

    @Singleton
    @Provides
    fun provideHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor(context))
                .addInterceptor(ApiErrorInterceptor(context))
                .build()
    }

    private fun getApiEndpoint(): String {
        val version = "v1"

        return if (BuildConfig.FLAVOR == "development") {
            "https://staging.api.mypantry.muko.app/$version/"
        } else {
            "https://api.mypantry.muko.app/$version/"
        }
    }

    class MockBoxService(private val delegate: BehaviorDelegate<BoxService>) : BoxService {

        private val users = listOf(
                User(
                        id = 1,
                        name = "test user",
                        avatarUrl = "",
                        createdAt = Date(),
                        updatedAt = Date(),
                        email = "test@example.com",
                        provider = "google"
                )
        )
        private val user = users[0]
        private val boxes = listOf(
                Box(
                        id = 1,
                        name = "test box",
                        notice = "test",
                        imageUrl = null,
                        isInvited = false,
                        createdAt = Date(),
                        updatedAt =
                        Date(),
                        owner = user,
                        invitations = listOf()
                ),
                Box(
                        id = 2,
                        name = "test",
                        notice = "test",
                        imageUrl = null,
                        isInvited = false,
                        createdAt = Date(),
                        updatedAt =
                        Date(),
                        owner = user,
                        invitations = listOf()
                )
        )
        private val units = listOf(
                Unit(
                        id = 1,
                        label = "test",
                        step = 1.0,
                        createdAt = Date(),
                        updatedAt = Date(),
                        user = user
                )
        )
        private val foods = listOf(
                Food(
                        id = 1,
                        name = "test",
                        amount = 10.0,
                        expirationDate = Date(),
                        imageUrl = null,
                        createdAt = Date(),
                        updatedAt = Date(),
                        unit = units[0],
                        box = boxes[0],
                        createdUser = user,
                        updatedUser = user,
                        notices = listOf()
                )
        )

        override fun getBoxes(): Flowable<List<Box>> {
            return delegate.returningResponse(boxes).getBoxes()
        }

        override fun getBox(id: Int): Flowable<Box> {
            val box = boxes.find { it.id == id }

            return delegate.returningResponse(box).getBox(id)
        }

        override fun createBox(body: RequestBody): Flowable<Box> {
            TODO("Not yet implemented")
        }

        override fun updateBox(id: Int, body: RequestBody): Flowable<Box> {
            TODO("Not yet implemented")
        }

        override fun invite(id: Int, body: RequestBody): Flowable<Invitation> {
            TODO("Not yet implemented")
        }

        override fun getFoodsInBox(id: Int): Flowable<List<Food>> {
            val box = boxes.find { it.id == id }
            val foods = foods.filter { it.box.id == id }

            return delegate.returningResponse(foods).getFoodsInBox(id)
        }

        override fun getUnitsForBox(id: Int): Flowable<List<Unit>> {
            val box = boxes.find { it.id == id }
            val units = units.filter { it.user.id == box?.owner?.id }

            return delegate.returningResponse(units).getUnitsForBox(id)
        }

        override fun removeBox(id: Int): Flowable<Void> {
            TODO("Not yet implemented")
        }
    }
}
