package be.mbict.k8stestcontainer

import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.util.Config
import org.awaitility.kotlin.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.BindMode
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.k3s.K3sContainer
import org.testcontainers.shaded.org.awaitility.Awaitility
import org.testcontainers.utility.DockerImageName
import java.io.StringReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.TimeUnit

@Testcontainers
class MainTest {

    companion object {

        const val NAMESPACE = "echoserver"

        val httpClient = HttpClient.newHttpClient()

        @Container
        val k3sContainer: K3sContainer = K3sContainer(DockerImageName.parse("rancher/k3s:v1.23.3-k3s1"))
                .withClasspathResourceMapping("manifests", "/var/lib/rancher/k3s/server/manifests", BindMode.READ_ONLY)
                .apply { addExposedPort(32666) }
    }

    private val api = CoreV1Api(Config.fromConfig(StringReader(k3sContainer.kubeConfigYaml)))
    private val echoServerPod = await atMost Duration.ofMinutes(2) untilCallTo { pod("echoserver") } has { status!!.phase == "Running" }

    private fun pod(name: String) = api.listNamespacedPod(NAMESPACE, null, null, null, null, null, null, null, null, null, null)
            .items
            .first { it.metadata!!.name!!.startsWith(name) }

    @Test
    fun justATest() {

        println("-".repeat(80))

        // interact with the running K3s server, e.g.:
        api.listNode(null, null, null, null, null, null, null, null, null, null)
                .items.apply {
                    println("There are ${size} nodes")
                    forEach(::println)
                }

        println("-".repeat(80))

        // interact with the running K3s server, e.g.:
        api.listNamespacedPod(NAMESPACE, null, null, null, null, null, null, null, null, null, null)
                .items.apply {
                    println("There are ${size} pods")
                    forEach(::println)
                }

        println("-".repeat(80))

        api.listNamespacedService(NAMESPACE, null, null, null, null, null, null, null, null, null, null)
                .items.apply {
                    println("There are ${size} services")
                    forEach(::println)
                }

        println("-".repeat(80))

        val req = HttpRequest.newBuilder().uri(URI.create("http://${echoServerPod!!.status!!.hostIP}:32666")).GET().build()

        val response = httpClient.send(req, HttpResponse.BodyHandlers.ofString())

        println(response)
        println(response.body())
    }
}