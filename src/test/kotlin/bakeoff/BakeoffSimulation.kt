package bakeoff

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*

/**
 * This sample is based on our official tutorials:
 *
 * - [Gatling quickstart tutorial](https://gatling.io/docs/gatling/tutorials/quickstart)
 * - [Gatling advanced tutorial](https://gatling.io/docs/gatling/tutorials/advanced)
 */
class BakeoffSimulation : Simulation() {

//    val feeder = csv("search.csv").random()

    private val list =
        exec(
            http("list")
                .get("/")
                .check(status().shouldBe(200))
        )

    private val create =
        exec(
            http("create")
                .post("/")
                .body(StringBody("{\"name\":\"Prince\", \"genre\": \"Rock\"}"))
                .asJson()
                .check(status().shouldBe(200))
                .check(jsonPath("$..id").saveAs("artistId"))
        )

    private val get =
        exec(
            http("get")
                .get("/#{artistId}")
                .check(status().shouldBe(200))
        )

    private val delete =
        exec(
            http("delete")
                .delete("/#{artistId}")
                .check(status().shouldBe(200))
        )

    private val httpProtocol =
        http.baseUrl("http://localhost/api/artists")//"http://192.168.1.180/api/artists")
            .acceptHeader("application/json")
            .acceptEncodingHeader("gzip, deflate")

    private val users = scenario("Users").exec(create, get, list, delete)

    init {
        setUp(
            users.injectOpen(rampUsers(10000).during(60)),
        ).protocols(httpProtocol)
    }
}
