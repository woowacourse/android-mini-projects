package a4.dogsignal

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
