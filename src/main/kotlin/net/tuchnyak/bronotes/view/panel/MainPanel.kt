package net.tuchnyak.bronotes.view.panel

import com.intellij.openapi.project.Project
import net.tuchnyak.bronotes.persistent.PersistentService
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField

/**
 * @author tuchnyak (George Shchennikov)
 */
class MainPanel(val project: Project) : JPanel()

private const val H_GAP = 0
private const val V_GAP = 0

fun MainPanel.init(): JPanel {
    layout = GridLayout(5, 1, H_GAP, V_GAP)
//    font = Font()

    // add input panel
    add(Panel.initPanel(project) { panel, prj ->
        panel.layout = BorderLayout(5, 0)

        val btn = JButton("+")
        panel.add(btn, BorderLayout.WEST)

        val textBox = JTextArea()
        val scroll = JScrollPane(textBox)
        panel.add(scroll, BorderLayout.CENTER)
        textBox.background = Color.LIGHT_GRAY
        textBox.foreground = Color.BLACK
        textBox.margin = Insets(5, 10, 5, 10)

        btn.addActionListener {
            val note = textBox.text
            if (note.isNotBlank()) {
                PersistentService.processNote(note, prj)
                textBox.text = ""
            }
        }
    }, 0)
    // add radio panel
    // add taskPanel
    // add notePanel
    // add donePanel

    return this
}

private object Panel {
    fun initPanel(project: Project, block: (panel: JPanel, project: Project) -> Unit): JPanel {
        val panel = JPanel()
        block(panel, project)
        return panel
    }
}

//private fun JPanel.initInputPanel(project: Project): JPanel {
//    val inputBox = JTextArea(3, 1)
////    inputBox.fon
//    return this
//}