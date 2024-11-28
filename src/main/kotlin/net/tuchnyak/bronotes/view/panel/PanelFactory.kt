package net.tuchnyak.bronotes.view.panel

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.RadioButton
import com.jediterm.core.input.KeyEvent
import net.tuchnyak.bronotes.persistent.PersistentService
import net.tuchnyak.bronotes.persistent.rebuildIfTask
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.AbstractAction
import javax.swing.BorderFactory
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.KeyStroke

private val mixedBtn = JBRadioButton("Mixed")
private val toDoBtn = JBRadioButton("To-Do")

object PanelFactory {

    fun getInputPanel(mainPanel: MainPanel, project: Project): JPanel = initCustomPanel {
        it.layout = BorderLayout(5, 0)

        val btn = JButton("+")
        it.add(btn, BorderLayout.WEST)

        val textBox = JTextArea()
        val scroll: JScrollPane = JScrollPane(textBox)
        scroll.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        it.add(scroll, BorderLayout.CENTER)
        textBox.background = if (JBColor.isBright()) Color.WHITE else Color.BLACK
        textBox.foreground = if (JBColor.isBright()) Color.BLACK else Color.WHITE
        textBox.margin = Insets(5, 10, 5, 10)
        textBox.lineWrap = true
        textBox.rows = 3
        textBox.requestFocusInWindow()

        var addAction : () -> Unit = {
            val note = textBox.text
            if (note.isNotBlank()) {
                PersistentService.processNote(note, project, toDoBtn.isSelected)
                textBox.text = ""
            }
            mainPanel.redraw()
        }

        btn.addActionListener {
            addAction()
        }
        val actionKey = "addNoteMapKey"
        textBox.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), actionKey)
        textBox.actionMap.put(actionKey, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                addAction()
            }
        })
    }

    fun getRadioPanel(mainPanel: MainPanel): JPanel = initCustomPanel {
        it.layout = FlowLayout(FlowLayout.CENTER)

        val grButton = ButtonGroup()
        grButton.add(mixedBtn)
        grButton.add(toDoBtn)

        it.add(JLabel("input mode: "))
        it.add(mixedBtn)
        it.add(toDoBtn)
        mixedBtn.isSelected = !mainPanel.isTaskModeEnabled
        toDoBtn.isSelected = mainPanel.isTaskModeEnabled

        mixedBtn.addActionListener {
            mainPanel.isTaskModeEnabled = false
        }
        toDoBtn.addActionListener {
            mainPanel.isTaskModeEnabled = true
        }
    }

    fun getNotePanel(mainPanel: MainPanel, note: String, type: NoteType, project: Project): JPanel = initCustomPanel {
        it.layout = BorderLayout(5, 10)

        val checkBox = JCheckBox()
        checkBox.isEnabled = NoteType.PLAIN != type
        checkBox.isVisible = NoteType.PLAIN != type
        checkBox.addActionListener {
            when (checkBox.isSelected) {
                true -> PersistentService.doneTask(note, project)
                false -> PersistentService.undoneTask(note, project)
            }
            mainPanel.redraw()
        }

        val text = JTextArea()
        text.lineWrap = true
        text.wrapStyleWord = true
        text.isEditable = false

        text.border = BorderFactory.createLoweredSoftBevelBorder()
        text.text = note
        text.alignmentY = Component.CENTER_ALIGNMENT
        text.background = JBColor.WHITE

        text.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e?.clickCount == 2) {
                    val newNote = Messages.showMultilineInputDialog(
                        project,
                        "Edit note:",
                        "Edit note",
                        text.text.rebuildIfTask(type),
                        Messages.getInformationIcon(),
                        null
                    )
                    if (newNote != null && newNote.isNotBlank()) {
//                        text.text = if (type != NoteType.PLAIN) newNote.removeTodoPrefix() else newNote   //TODO: leave previous order
                        PersistentService.deleteTask(note, project, type)
                        PersistentService.processNote(newNote, project, toDoBtn.isSelected)
                        mainPanel.redraw();
                    }
                }
            }
        })

        val deleteButton = JButton("X")
        deleteButton.addActionListener {
            PersistentService.deleteTask(note, project, type)
            mainPanel.redraw()
        }

        when (type) {
            NoteType.PLAIN -> checkBox.isSelected = false
            NoteType.TODO -> {
                checkBox.isSelected = false
            }
            NoteType.DONE -> {
                checkBox.isSelected = true
                text.foreground = JBColor.DARK_GRAY
            }
        }
        it.maximumSize = Dimension(Int.MAX_VALUE, text.preferredSize.height)

        it.add(deleteButton, BorderLayout.EAST)
        it.add(text, BorderLayout.CENTER)
        it.add(
            if (checkBox.isEnabled)
                checkBox
            else {
                val rb = RadioButton("")
                rb.isSelected = false
                rb.isEnabled = false
                rb
            },
            BorderLayout.WEST
        )
        it.alignmentY = Component.TOP_ALIGNMENT
    }

    private fun initCustomPanel(block: (panel: JPanel) -> Unit): JPanel = with(JPanel()) {
        block(this)
        return this
    }

}

