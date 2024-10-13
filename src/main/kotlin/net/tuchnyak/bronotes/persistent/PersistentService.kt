package net.tuchnyak.bronotes.persistent

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

/**
 * @author tuchnyak (George Shchennikov)
 */
class DataState : BaseState() {
    var testName by string("asd")
    var testList by list<String>()
}

fun DataState.addItem(item: String) {
    testList.add(item)
    intIncrementModificationCount()
}


@Service(Service.Level.PROJECT)
@State(
    name = "PersistentService",
    storages = [Storage("bronotes_data_state.xml")],
    reloadable = true
)
class PersistentService : SimplePersistentStateComponent<DataState>(DataState()) {

    companion object {
        fun getInstance(project: Project) = project.getService<PersistentService>(PersistentService::class.java)
    }

}
