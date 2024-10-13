package net.tuchnyak.bronotes.view

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import net.tuchnyak.bronotes.persistent.PersistentService
import net.tuchnyak.bronotes.persistent.addItem
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * @author tuchnyak (George Shchennikov)
 */
class NotesToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        invokeLater{
            PersistentService.getInstance(project).state.addItem("WORLD")
            PersistentService.getInstance(project).state.testName += "-NEW"

            val panel = JPanel()
            panel.add(JLabel(PersistentService.getInstance(project).state.testList.joinToString(",", postfix = "!")))
            toolWindow.contentManager.addContent(
                ContentFactory.getInstance().createContent(panel, "contenttest", false)
            )
        }
    }

}
