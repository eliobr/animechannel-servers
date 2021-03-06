package mx.com.nitrostudio.animechannel.models.entities.servers.hosts

import kotlinx.coroutines.experimental.runBlocking
import mx.com.nitrostudio.animechannel.models.entities.servers.GenericServer
import mx.com.nitrostudio.animechannel.models.entities.servers.hoster.YourUpload
import mx.com.nitrostudio.animechannel.models.entities.servers.hoster.getContents
import mx.com.nitrostudio.animechannel.models.webservice.ICallback
import mx.com.nitrostudio.animechannel.services.jbro.Jbro
import kotlin.concurrent.thread

class YourUploadServer : GenericServer() {

    override fun getName(): String? {
        return "YourUpload"
    }

    override fun isProcessable(): Boolean {
        return true
    }

    override fun process(callback: ICallback<String?>?): Thread? {
        return thread(start = true) {
            callback?.onStart()
            val http = Jbro.getInstance()
            if (getDirectURL() == null) {
                try {
                    runBlocking {
                        val response = http.getContents(getURL() ?: "").await()
                        val regex = """var.*?redir.*?['"]+(.*?)['"]+""".toRegex()
                        val match = regex.find(response)
                        val (link) = match!!.destructured

                        setDirectUrl(YourUpload().directLink(link).await())
                    }
                } catch (exception: Exception) { /* LOG */
                }
            }
            if (getDirectURL() != null)
                callback?.onSuccess(getDirectURL())
            else
                callback?.onError("No se pudo procesar el enlace")
        }
    }
}