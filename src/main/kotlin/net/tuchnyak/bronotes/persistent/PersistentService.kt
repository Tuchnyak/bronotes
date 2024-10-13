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
    val todoNotes by list<String>()
    val doneNotes by list<String>()
}

fun DataState.addPlainNote(note: String) {
    plainNotes = plainNotes.copyAndAdd(note)
}

private fun <T: Any> MutableList<T>.copyAndAdd(item: T): MutableList<T> {
    val tmpList = mutableListOf<T>()
    tmpList.addAll(this)
    tmpList.add(item)

    return tmpList
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
