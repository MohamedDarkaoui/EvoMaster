package org.evomaster.core.output.service

import org.evomaster.client.java.controller.SutHandler
import org.evomaster.client.java.controller.api.dto.database.operations.InsertionDto
import org.evomaster.client.java.controller.api.dto.database.operations.InsertionResultsDto


class FakeController : SutHandler {
    override fun startSut(): String {
        return ""
    }

    override fun stopSut() {
    }

    override fun resetStateOfSUT() {
    }

    override fun execInsertionsIntoDatabase(insertions: MutableList<InsertionDto>?, previous: Array<InsertionResultsDto>): InsertionResultsDto? {
        return null
    }

    override fun getRPCClient(interfaceName: String?): Any {
        throw RuntimeException("ERROR: should not be executed")
    }

    override fun executeRPCEndpoint(json: String?): Any {
        throw RuntimeException("ERROR: should not be executed")
    }
}