package net.tuchnyak.bronotes.persistent

import com.intellij.openapi.ui.naturalSorted

/**
 * @author tuchnyak (George Shchennikov)
 */
fun DataState.addPlainNote(note: String) {
    plainNotes = plainNotes.copyAndAdd(note)
}

fun DataState.removePlainNote(note: String) {
    plainNotes = plainNotes.copyAndRemove(note)
}

fun DataState.addTodo(note: String) {
    todoNotes = todoNotes.copyAndAdd(note)
}

fun DataState.removeTodo(note: String) {
    plainNotes = todoNotes.copyAndRemove(note)
}

fun DataState.addDone(note: String) {
    doneNotes = doneNotes.copyAndAdd(note)
}

fun DataState.removeDone(note: String) {
    doneNotes = doneNotes.copyAndRemove(note)
}


private fun <T: Any> MutableList<T>.copyAndAdd(item: T): MutableList<T> {
    val tmpList = mutableListOf<T>()
    tmpList.addAll(this)
    tmpList.add(item)

    return tmpList
}

private fun <T: Any> MutableList<T>.copyAndRemove(item: T): MutableList<T> {
    val tmpList = mutableListOf<T>()
    tmpList.addAll(this)
    tmpList.remove(item)

    return tmpList
}
