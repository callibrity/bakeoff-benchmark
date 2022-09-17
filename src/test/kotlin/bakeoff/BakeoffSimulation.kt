package bakeoff

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*

class BakeoffSimulation : Simulation() {

    private val create =
        exec(
            http("create")
                .post("/")
                .body(StringBody("{\"name\":\"Prince\", \"genre\": \"Rock\"}"))
                .asJson()
                .check(status().`in`(201, 200))
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
                .check(status().`in`(200, 204))
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
                incrementUsersPerSec(25.0)
                    .times(20)
                    .eachLevelLasting(15)
                    .startingFrom(100.0)
                    .separatedByRampsLasting(5)
            ),
        ).protocols(httpProtocol)
    }
}
