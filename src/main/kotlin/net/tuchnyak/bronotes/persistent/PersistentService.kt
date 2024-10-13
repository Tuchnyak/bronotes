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
    var plainNotes by list<String>()
    var todoNotes by list<String>()
    var doneNotes by list<String>()
}

@Service(Service.Level.PROJECT)
@State(
    name = "BroNotes",
    storages = [Storage("bronotes_data_state.xml")],
    reloadable = true
)
class PersistentService : SimplePersistentStateComponent<DataState>(DataState()) {

    companion object {
        fun getInstance(project: Project) = project.getService<PersistentService>(PersistentService::class.java)
    }

}
