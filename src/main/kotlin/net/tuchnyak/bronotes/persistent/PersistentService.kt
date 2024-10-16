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

        fun processNote(note: String, project: Project) {
            val stateInstance = getInstance(project).state
            when {
                note.isTodo() -> stateInstance.addTodo(note)
                note.isDone() -> stateInstance.addDone(note)
                else -> stateInstance.addPlainNote(note)
            }
        }

        fun doneTask(note: String, project: Project) {
            val stateInstance = getInstance(project).state
            stateInstance.removeTodo(note)
            stateInstance.addDone(note.closeTask())
        }

        fun undoneTask(note: String, project: Project) {
            val stateInstance = getInstance(project).state
            stateInstance.removeDone(note)
            stateInstance.addTodo(note.openTask())
        }
    }

}

private val toDoPrefix = "- [ ]"
private val donePrefix = "- [x]"

private  fun String.isTodo() = this.startsWith(toDoPrefix)
private  fun String.isDone() = this.startsWith(donePrefix)
private fun String.closeTask(): String = "$donePrefix ${this.removePrefix(toDoPrefix).trim()}"
private fun String.openTask(): String = "$toDoPrefix ${this.removePrefix(donePrefix).trim()}"