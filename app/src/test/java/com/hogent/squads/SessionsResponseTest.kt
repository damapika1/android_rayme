package com.hogent.squads

import com.hogent.squads.model.network.rest.resources.SessionsData
import com.hogent.squads.model.network.rest.resources.SessionsResponse
import com.hogent.squads.model.network.rest.resources.TraineesData
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class SessionsResponseTest {

    @Test
    fun shouldMapToDomainSessions() {
        val sessionsResponse = SessionsResponse(
            listOf(
                SessionsData(
                    1,
                    "titel",
                    "some description",
                    LocalDateTime.now().toString(),
                    LocalDateTime.now().toString(),
                    0,
                    listOf(TraineesData(1), TraineesData(2), TraineesData(11)),
                    2
                )
            )
        )

        val sessionsList = sessionsResponse.toSessionList("11")
        assertEquals(sessionsList.size, 1)

        val firstSession = sessionsList[0]
        assertEquals("titel", firstSession.title)
        assertEquals(3, firstSession.trainees)
        assertEquals(11, firstSession.userFK)

        firstSession.leaveSession()
        assertEquals(2, firstSession.trainees)
        assertEquals(null, firstSession.userFK)
    }
}
