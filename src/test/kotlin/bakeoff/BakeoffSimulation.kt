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

//    private val list =
//        exec(
//            http("list")
//                .get("/")
//                .check(status().shouldBe(200))
//        )

    private val create =
        exec(
            http("create")
                .post("/")
                .body(StringBody("{\"name\":\"Prince\", \"genre\": \"Rock\"}"))
                .asJson()
                .check(status().shouldBe(200))
                .check(jsonPath("$..id").saveAs("artistId"))
        )

    private val update =
        exec(
            http("update")
                .put("/#{artistId}")
                .body(StringBody("{\"name\":\"Madonna\", \"genre\": \"Rock\"}"))
                .asJson()
                .check(status().shouldBe(200))
                .check(jsonPath("$..name").shouldBe("Madonna"))
        )

    private val retrieve =
        exec(
            http("retrieve")
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
        http.baseUrl("http://192.168.1.180/api/artists")
            .acceptHeader("application/json")
            .shareConnections()
            .acceptEncodingHeader("gzip, deflate")

    private val users = scenario("Users").exec(create, retrieve, update, delete)

    init {
        setUp(
            users.injectOpen(
                incrementUsersPerSec(100.0)
                    .times(20)
                    .eachLevelLasting(30)
                    .startingFrom(100.0)
                    .separatedByRampsLasting(10)

//                incrementConcurrentUsers(50)
//                    .times(2)
//                    .eachLevelLasting(30)
//                    .separatedByRampsLasting(15)
//                    .startingFrom(50)
            ),
        ).protocols(httpProtocol)
    }
}
