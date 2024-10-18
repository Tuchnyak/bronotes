package net.tuchnyak.bronotes.view.panel

import com.intellij.openapi.project.Project
import net.tuchnyak.bronotes.persistent.PersistentService
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.Insets
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JScrollPane
import javax.swing.JTextArea

/**
 * @author tuchnyak (George Shchennikov)
 */
class MainPanel(val project: Project) : JPanel()

private const val H_GAP = 0
private const val V_GAP = 0

private val mixedBtn = JRadioButton("Mixed")
private val toDoBtn = JRadioButton("To-Do")

fun MainPanel.init(): JPanel {
    layout = GridLayout(5, 1, H_GAP, V_GAP)
    // add input panel
    add(PanelFactory.getInputPanel(project), 0)
    // add radio panel
    add(PanelFactory.getRadioPanel(), 1)
    // add taskPanel
    // add notePanel
    // add donePanel

    return this
}

private object PanelFactory {
    fun getInputPanel(project: Project): JPanel = initPanel {
        it.layout = BorderLayout(5, 0)

        val btn = JButton("+")
        it.add(btn, BorderLayout.WEST)

        val textBox = JTextArea()
        val scroll = JScrollPane(textBox)
        it.add(scroll, BorderLayout.CENTER)
        textBox.background = Color.LIGHT_GRAY
        textBox.foreground = Color.BLACK
        textBox.margin = Insets(5, 10, 5, 10)

        btn.addActionListener {
            val note = textBox.text
            if (note.isNotBlank()) {
                PersistentService.processNote(note, project, toDoBtn.isSelected)
                textBox.text = ""
            }
        }
    }

    fun getRadioPanel(): JPanel = initPanel {
        it.layout = FlowLayout(FlowLayout.CENTER)
        it.background = Color.ORANGE

        val grButton = ButtonGroup()
        grButton.add(mixedBtn)
        grButton.add(toDoBtn)

        it.add(JLabel("input mode: "))
        it.add(mixedBtn)
        it.add(toDoBtn)
        mixedBtn.isSelected = true
    }

    private fun initPanel(block: (panel: JPanel) -> Unit): JPanel = with(JPanel()) {
        block(this)
        return this
    }

}

//private fun JPanel.initInputPanel(project: Project): JPanel {
//    val inputBox = JTextArea(3, 1)
////    inputBox.fon
//    return this
//}