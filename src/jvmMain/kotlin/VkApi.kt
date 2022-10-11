import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.httpclient.HttpTransportClient

object VkApi {
    val vk = VkApiClient(HttpTransportClient.getInstance())
}