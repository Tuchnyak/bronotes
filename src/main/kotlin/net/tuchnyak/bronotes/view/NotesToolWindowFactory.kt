package net.tuchnyak.bronotes.view

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.naturalSorted
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import kotlinx.datetime.Clock
import net.tuchnyak.bronotes.persistent.PersistentService
import net.tuchnyak.bronotes.persistent.addPlainNote
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * @author tuchnyak (George Shchennikov)
 */
class NotesToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        invokeLater{
            PersistentService.getInstance(project).state.addPlainNote("- [ ] New todo ${Clock.System.now()}")
            PersistentService.getInstance(project).state.addPlainNote("New note ${Clock.System.now()}")
            PersistentService.getInstance(project).state.addPlainNote("- [x] done ${Clock.System.now()}")

            val panel = JPanel()
            PersistentService.getInstance(project).state.plainNotes
//                .naturalSorted()
                .forEachIndexed {i, pn ->
                panel.add(JLabel("$i: $pn"))
            }

            toolWindow.contentManager.addContent(
                ContentFactory.getInstance().createContent(panel, "Notes", false)
            )
        }
    }

}
