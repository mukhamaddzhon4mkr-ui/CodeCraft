package com.example.codecraft.data.api

import kotlinx.coroutines.delay
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class NewsItem(
    val id: Int,
    val title: String,
    val body: String
)

interface ApiService {
    @GET("posts")
    suspend fun getNews(): List<NewsItem>

    companion object {
        private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

        fun create(): ApiService {
            // В академических целях мы возвращаем объект, который имитирует работу API,
            // предоставляя данные на русском языке. Это позволяет избежать проблем с ключами API
            // и иноязычным контентом для защиты проекта.
            return object : ApiService {
                override suspend fun getNews(): List<NewsItem> {
                    delay(500) // Имитируем задержку сети
                    return listOf(
                        NewsItem(
                            id = 1,
                            title = "Релиз Kotlin 2.0",
                            body = "Новая версия языка программирования принесла компилятор K2, который значительно ускоряет сборку Android-приложений."
                        ),
                        NewsItem(
                            id = 2,
                            title = "Новости Android 15",
                            body = "Google представила новые функции безопасности и улучшения производительности для следующего поколения мобильных устройств."
                        ),
                        NewsItem(
                            id = 3,
                            title = "ИИ в Android Studio",
                            body = "В среду разработки интегрировали Gemini — умного помощника, который помогает писать код и находить ошибки в реальном времени."
                        ),
                        NewsItem(
                            id = 4,
                            title = "Compose Multiplatform",
                            body = "Теперь создавать интерфейсы на Jetpack Compose можно не только для Android, но и для iOS, Desktop и Web одновременно."
                        )
                    )
                }
            }
        }
    }
}
