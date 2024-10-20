package net.tuchnyak.bronotes.view.panel

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.JBScrollBar
import com.intellij.ui.components.JBScrollPane
import com.squareup.wire.internal.toUnmodifiableList
import net.tuchnyak.bronotes.persistent.PersistentService
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Cursor
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Insets
import java.util.stream.IntStream
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
private const val V_GAP = 0

private val mixedBtn = JBRadioButton("Mixed")
private val toDoBtn = JBRadioButton("To-Do")

fun MainPanel.init(): JPanel {
    layout = BorderLayout()

    // Input and radio panels
    val inputPanel = PanelFactory.getInputPanel(project)
    val radioPanel = PanelFactory.getRadioPanel()

// Create a container for vertical note panels with BoxLayout (Y_AXIS)
    val container = JPanel()
    container.layout = BoxLayout(container, BoxLayout.Y_AXIS)

    // Add note panels (example: 1 or more notes)
//    IntStream.range(0, 10).forEach { i ->
    PersistentService.getDataStateInstance(project).todoNotes.toUnmodifiableList().forEach { note ->
        val notePanel = JPanel(BorderLayout())
//        val note = """
//        This is a test message: $i
//        lorem ipsum dolor onum asdhals, asasd hsdkasd a.
//        lorem ipsum dolor onum asdhals, asasd hsdkasd a.
//        lorem ipsum dolor onum asdhals, asasd hsdkasd a.
//        lorem ipsum dolor onum asdhals, asasd hsdkasd a.
//        sd asldkjh asd sjdhasjdh sdha sldkashdsd hsdjalsdh lkasd!
//        sd asldkjh asd sjdhasjdh sdha sldkashdsd hsdjalsdh lkasd!
//    """.trimIndent()

        val text = JTextArea(note)
        text.lineWrap = true
        text.wrapStyleWord = true
        text.isEditable = false
//        text.background = Color.WHITE
        text.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)  // Padding for aesthetics

        // Allow the text area to grow in height as needed
        text.preferredSize = Dimension(notePanel.preferredSize.width, text.preferredSize.height)

        // Add the text area to the panel
        notePanel.add(JScrollPane(text), BorderLayout.CENTER)
        notePanel.border = BorderFactory.createLineBorder(Color.GRAY)

        // Only set the maximum width, allow height to adjust based on content
        notePanel.maximumSize = Dimension(Int.MAX_VALUE, text.preferredSize.height)
//        notePanel.alignmentX = Component.LEFT_ALIGNMENT  // Align to the left

        // Add note panel to the container
        container.add(notePanel)
    }

    // Add vertical glue only if more space is available than needed
    container.add(Box.createVerticalGlue())  // Will keep notes at the top if extra space exists

    // Wrap the container inside a scroll pane
    val scrollPane = JBScrollPane(container)
    scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
    scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

    // Ensure panels stick to the top
    container.alignmentY = Component.TOP_ALIGNMENT
    scrollPane.alignmentY = Component.TOP_ALIGNMENT

    // Add the input panel and the scrollable note panels to the main layout
    add(inputPanel, BorderLayout.NORTH)
    add(radioPanel, BorderLayout.SOUTH)
    add(scrollPane, BorderLayout.CENTER)

    return this
}
/*
fun MainPanel.init(): JPanel {
    layout = VerticalFlowLayout(VerticalFlowLayout.TOP)
    add(PanelFactory.getInputPanel(project), -1)
    add(PanelFactory.getRadioPanel(), -1)

    val verticalScrollPanel = PanelFactory.initCustomPanel {
        it.layout = BorderLayout()

        // Container panel to hold the note panels vertically
        val container = JPanel()
        container.layout = BoxLayout(container, BoxLayout.Y_AXIS)

        // Adding example panels (replace with actual note data)
        IntStream.range(0, 40).forEach { i ->
            val p = JPanel(BorderLayout())
            p.add(JLabel("This is a test message: $i"), BorderLayout.CENTER)
            p.maximumSize = Dimension(Int.MAX_VALUE, p.preferredSize.height)  // Panels can grow vertically
            container.add(p)
        }

        // Scroll pane to wrap the container panel
        val scroll = JScrollPane(container)
        scroll.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        scroll.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

        // Fix for making scrollable container behave as expected
        scroll.viewport.view = container
        container.revalidate()
        container.repaint()

        it.add(scroll, BorderLayout.CENTER)  // Add scroll pane to the main panel
    }

    add(verticalScrollPanel, -1)

//    if (PersistentService.getDataStateInstance(project).todoNotes.isNotEmpty()) {
//        add(
//            PanelFactory.getTaskPanel(
//                project,
//                PersistentService.getDataStateInstance(project).todoNotes.toUnmodifiableList()
//            ), gridPosition++
//        )
//    }
//    if (PersistentService.getDataStateInstance(project).plainNotes.isNotEmpty()) {
//        add(
//            PanelFactory.getNotesPanel(
//                project,
//                PersistentService.getDataStateInstance(project).plainNotes.toUnmodifiableList()
//            ), gridPosition++
//        )
//    }
//    if (PersistentService.getDataStateInstance(project).doneNotes.isNotEmpty()) {
//        add(
//            PanelFactory.getDonePanel(
//                project,
//                PersistentService.getDataStateInstance(project).doneNotes.toUnmodifiableList()
//            ), gridPosition++
//        )
//    }

    return this
}
*/

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

    fun getTaskPanel(project: Project, notes: List<String>): JPanel = initCustomPanel {
        it.layout = VerticalFlowLayout(VerticalFlowLayout.TOP)
        notes.forEach { note -> it.add(getNotePanel(note, NoteType.TODO, project)) }
    }

    fun getNotesPanel(project: Project, notes: List<String>): JPanel = initCustomPanel {
        it.layout = VerticalFlowLayout(VerticalFlowLayout.TOP)
        notes.forEach { note -> it.add(getNotePanel(note, NoteType.PLAIN, project)) }
    }

    fun getDonePanel(project: Project, notes: List<String>): JPanel = initCustomPanel {
        it.layout = VerticalFlowLayout(VerticalFlowLayout.TOP)
        notes.forEach { note -> it.add(getNotePanel(note, NoteType.DONE, project)) }
    }

    private fun getNotePanel(note: String, type: NoteType, project: Project): JPanel = initCustomPanel {
        it.layout = BorderLayout(2, 5)

        val checkBox = JCheckBox()
        checkBox.isEnabled = NoteType.PLAIN != type
        checkBox.isVisible = NoteType.PLAIN != type
        val text = JTextArea(note)
        text.lineWrap = true
        text.isEditable = false
        val deleteButton = JButton("X")

        when (type) {
            NoteType.PLAIN -> checkBox.isSelected = false
            NoteType.TODO -> checkBox.isSelected = false
            NoteType.DONE -> {
                checkBox.isSelected = true
                text.foreground = Color.lightGray
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

        it.add(deleteButton, BorderLayout.EAST)
        it.add(text, BorderLayout.CENTER)
        it.add(checkBox, BorderLayout.WEST)         // TODO("Stub for plain notes")
    }

    fun initCustomPanel(block: (panel: JPanel) -> Unit): JPanel = with(JPanel()) {
        block(this)
        return this
    }

    private enum class NoteType {
        PLAIN, TODO, DONE
    }
}
