package net.tuchnyak.bronotes.view.panel

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.JBScrollPane
import com.squareup.wire.internal.toUnmodifiableList
import net.tuchnyak.bronotes.persistent.PersistentService
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Insets
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

/**
 * @author tuchnyak (George Shchennikov)
 */
class MainPanel(val project: Project) : JPanel()

private const val H_GAP = 0
private const val V_GAP = 20

private val mixedBtn = JBRadioButton("Mixed")
private val toDoBtn = JBRadioButton("To-Do")

fun MainPanel.init(): JPanel {
    layout = BorderLayout(H_GAP,V_GAP)

    // Input and radio panels
    val inputPanel = PanelFactory.getInputPanel(project)
    val radioPanel = PanelFactory.getRadioPanel()

    // Create a container for vertical note panels with BoxLayout (Y_AXIS)
    val container = JPanel()
    container.layout = BoxLayout(container, BoxLayout.Y_AXIS)

    PersistentService.getDataStateInstance(project).todoNotes.toUnmodifiableList().forEach { note ->
        container.add(PanelFactory.getNotePanel(note, PanelFactory.NoteType.TODO, project))
    }
    PersistentService.getDataStateInstance(project).plainNotes.toUnmodifiableList().forEach { note ->
        container.add(PanelFactory.getNotePanel(note, PanelFactory.NoteType.PLAIN, project))
    }
    PersistentService.getDataStateInstance(project).doneNotes.toUnmodifiableList().forEach { note ->
        container.add(PanelFactory.getNotePanel(note, PanelFactory.NoteType.DONE, project))
    }

    container.add(Box.createVerticalGlue())  // Will keep notes at the top if extra space exists

    val scrollPane = JBScrollPane(container)
    scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
    scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

    container.alignmentY = Component.TOP_ALIGNMENT
    scrollPane.alignmentY = Component.TOP_ALIGNMENT

    add(inputPanel, BorderLayout.NORTH)
    add(scrollPane, BorderLayout.CENTER)
    add(radioPanel, BorderLayout.SOUTH)

    return this
}


private object PanelFactory {
    fun getInputPanel(project: Project): JPanel = initCustomPanel {
        it.layout = BorderLayout(5, 0)

        val btn = JButton("+")
        it.add(btn, BorderLayout.WEST)

        val textBox = JTextArea()
        val scroll = JScrollPane(textBox)
        scroll.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        it.add(scroll, BorderLayout.CENTER)
        textBox.background = Color.BLACK
        textBox.foreground = Color.WHITE
        textBox.margin = Insets(5, 10, 5, 10)
        textBox.lineWrap = true
        textBox.rows = 3

        btn.addActionListener {
            val note = textBox.text
            if (note.isNotBlank()) {
                PersistentService.processNote(note, project, toDoBtn.isSelected)
                textBox.text = ""
            }
        }
    }

    fun getRadioPanel(): JPanel = initCustomPanel {
        it.layout = FlowLayout(FlowLayout.CENTER)

        val grButton = ButtonGroup()
        grButton.add(mixedBtn)
        grButton.add(toDoBtn)

        it.add(JLabel("input mode: "))
        it.add(mixedBtn)
        it.add(toDoBtn)
        mixedBtn.isSelected = true
    }

    fun getNotePanel(note: String, type: NoteType, project: Project): JPanel = initCustomPanel {
        it.layout = BorderLayout(5, 10)

        val checkBox = JCheckBox()
        checkBox.isEnabled = NoteType.PLAIN != type
        checkBox.isVisible = NoteType.PLAIN != type

        val text = JTextArea()
        text.lineWrap = true
        text.wrapStyleWord = true
        text.isEditable = false
        val gap = 0
        text.border = BorderFactory.createEmptyBorder(gap, gap, gap, gap)
        text.preferredSize = Dimension(it.preferredSize.width, it.preferredSize.height)
        text.text = note
        text.alignmentY = Component.CENTER_ALIGNMENT

        val deleteButton = JButton("X")
        when (type) {
            NoteType.PLAIN -> checkBox.isSelected = false
            NoteType.TODO -> checkBox.isSelected = false
            NoteType.DONE -> {
                checkBox.isSelected = true
                text.foreground = Color.darkGray
            }
        }
        checkBox.addActionListener {
            when (checkBox.isSelected) {
                true -> PersistentService.doneTask(note, project)
                false -> PersistentService.undoneTask(note, project)
            }
            TODO("Redraw note panels")
        }
        deleteButton.addActionListener {
            TODO("Implement note deletion")
        }

        it.maximumSize = Dimension(Int.MAX_VALUE, text.preferredSize.height)

        it.add(deleteButton, BorderLayout.EAST)
        it.add(text, BorderLayout.CENTER)
        it.add(checkBox, BorderLayout.WEST)         // TODO("Stub for plain notes")
    }

    enum class NoteType {
        PLAIN, TODO, DONE
    }

    private fun initCustomPanel(block: (panel: JPanel) -> Unit): JPanel = with(JPanel()) {
        block(this)
        return this
    }
}
