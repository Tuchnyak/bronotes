package net.tuchnyak.bronotes.persistent

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import net.tuchnyak.bronotes.view.panel.NoteType

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
        fun getDataStateInstance(project: Project) = project.getService<PersistentService>(PersistentService::class.java).state

        fun processNote(note: String, project: Project, isToDoInputMode: Boolean) {
            val stateInstance = getDataStateInstance(project)
            when {
                isToDoInputMode && !note.isTask()   -> stateInstance.addTodo(note)
                note.isTodo()                       -> stateInstance.addTodo(note.removeTodoPrefix())
                note.isDone()                       -> stateInstance.addDone(note.removeTodoPrefix())
                else                                -> stateInstance.addPlainNote(note)
            }
        }

        fun doneTask(note: String, project: Project) {
            val stateInstance = getDataStateInstance(project)
            stateInstance.removeTodo(note)
            stateInstance.addDone(note)
        }

        fun undoneTask(note: String, project: Project) {
            val stateInstance = getDataStateInstance(project)
            stateInstance.removeDone(note)
            stateInstance.addTodo(note)
        }

        fun deleteTask(note: String, project: Project, type: NoteType) {
            val stateInstance = getDataStateInstance(project)
            when (type) {
                NoteType.PLAIN -> stateInstance.plainNotes.remove(note)
                NoteType.TODO -> stateInstance.todoNotes.remove(note)
                NoteType.DONE -> stateInstance.doneNotes.remove(note)
            }
        }
    }

}

private const val taskPrefix = "- ["
private const val toDoPrefix = "$taskPrefix ]"
private const val donePrefix = "${taskPrefix}x]"

private  fun String.isTask() = this.startsWith(taskPrefix)
private  fun String.isTodo() = this.startsWith(toDoPrefix)
private  fun String.isDone() = this.startsWith(donePrefix)

fun String.removeTodoPrefix(): String = this.substring(5).trim()
fun String.rebuildIfTask(type: NoteType): String = when (type) {
    NoteType.TODO -> toDoPrefix + this
    NoteType.DONE -> donePrefix + this
    else -> this
}
