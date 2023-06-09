package kr.hqservice.framework.database.component.datasource.handler

import kr.hqservice.framework.global.core.component.handler.ComponentHandler
import kr.hqservice.framework.global.core.component.handler.HQComponentHandler
import kr.hqservice.framework.coroutine.component.handler.CoroutineScopeComponentHandler
import kr.hqservice.framework.database.component.datasource.HQDataSource

@ComponentHandler(depends = [CoroutineScopeComponentHandler::class])
class DataSourceComponentHandler : HQComponentHandler<HQDataSource> {
    override fun setup(element: HQDataSource) {
        element.setupDatabase()
    }

    override fun teardown(element: HQDataSource) {
        element.teardown()
    }
}