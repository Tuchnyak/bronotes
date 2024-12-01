package net.tuchnyak.bronotes.view.panel

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import net.tuchnyak.bronotes.persistent.PersistentService
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

/**
 * @author tuchnyak (George Shchennikov)
 */
class MainPanel(val project: Project, var isTaskModeEnabled: Boolean = false) : JPanel()

private const val H_GAP = 0
private const val V_GAP = 20

fun MainPanel.init(): JPanel {
    layout = BorderLayout(H_GAP, V_GAP)

    // Input and radio panels
    val inputPanel = PanelFactory.getInputPanel(this, project)
    val radioPanel = PanelFactory.getRadioPanel(this)

    // Create a container for vertical note panels with BoxLayout (Y_AXIS)
    val scrollContainer = JPanel()
    scrollContainer.layout = BoxLayout(scrollContainer, BoxLayout.Y_AXIS)

    PersistentService.getDataStateInstance(project).todoNotes.forEach { note ->
        scrollContainer.add(PanelFactory.getNotePanel(this, note, NoteType.TODO, project))
    }
    PersistentService.getDataStateInstance(project).plainNotes.forEach { note ->
        scrollContainer.add(PanelFactory.getNotePanel(this, note, NoteType.PLAIN, project))
    }
    PersistentService.getDataStateInstance(project).doneNotes.forEach { note ->
        scrollContainer.add(PanelFactory.getNotePanel(this, note, NoteType.DONE, project))
    }

    scrollContainer.add(Box.createVerticalGlue())  // Will keep notes at the top if extra space exists

    val scrollPane = JBScrollPane(scrollContainer)
    scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
    scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

    scrollContainer.alignmentY = Component.TOP_ALIGNMENT
    scrollPane.alignmentY = Component.TOP_ALIGNMENT

    add(inputPanel, BorderLayout.NORTH)
    add(scrollPane, BorderLayout.CENTER)
    add(radioPanel, BorderLayout.SOUTH)

    return this
}

fun MainPanel.redraw() {
    this.removeAll()
    this.init()
    this.revalidate()
    this.repaint()
}

enum class NoteType {
    PLAIN, TODO, DONE
}
