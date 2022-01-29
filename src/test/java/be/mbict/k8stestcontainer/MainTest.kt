package be.mbict.k8stestcontainer

import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.Config
import org.junit.jupiter.api.Test
import org.testcontainers.containers.BindMode
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.k3s.K3sContainer
import org.testcontainers.utility.DockerImageName
import java.io.StringReader


@Testcontainers
class MainTest {

    companion object {

        @Container
        val k3sContainer: K3sContainer = K3sContainer(DockerImageName.parse("rancher/k3s:v1.23.3-k3s1"))
                .withClasspathResourceMapping("manifests", "/var/lib/rancher/k3s/server/manifests", BindMode.READ_ONLY)
    }

    @Test
    fun justATest() {
        val kubeConfigYaml: String = k3sContainer.kubeConfigYaml

        val client: ApiClient = Config.fromConfig(StringReader(kubeConfigYaml))
        val api = CoreV1Api(client)

        // interact with the running K3s server, e.g.:
        api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null)
                .items.apply {
                    println("There are ${size} pods")
                    forEach(::println)
                }
    }
}