package mx.com.nitrostudio.animechannel.models.entities.servers.hoster

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import mx.com.nitrostudio.animechannel.services.jbro.Jbro

class YourUpload : IHoster {
    override fun directLink(link: String): Deferred<String?> = async {
        try {
            val response = Jbro.getInstance().getContents(link).await()
            val regex = """jwplayerOptions.*?file:.*?['"]+(.*?)['"]+""".toRegex()
            val match = regex.find(response)
            val (url) = match!!.destructured
            url
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }
}

fun Jbro.getContents(url: String): Deferred<String> = async {
    val auxCache = isSkipCache
    isSkipCache = false
    val result = connect(url).get().toString()
    isSkipCache = auxCache
    result
}