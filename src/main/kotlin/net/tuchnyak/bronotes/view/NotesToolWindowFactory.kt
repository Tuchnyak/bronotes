package net.tuchnyak.bronotes.view

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import kotlinx.datetime.Clock
import net.tuchnyak.bronotes.persistent.PersistentService
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * @author tuchnyak (George Shchennikov)
 */
class NotesToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        invokeLater{
            val incr = 10
            val newTask = "- [ ] New todo SHOULD CLOSE $incr: ${Clock.System.now()}"
            val undoneTask = "- [x] done SHOULD OPEN $incr: ${Clock.System.now()}"
            PersistentService.processNote(newTask, project)
            PersistentService.processNote("- [ ] simple todo $incr: ${Clock.System.now()}", project)
            PersistentService.processNote("New note $incr: ${Clock.System.now()}", project)
            PersistentService.processNote("- [x] done $incr: ${Clock.System.now()}", project)
            PersistentService.processNote(undoneTask, project)

            PersistentService.doneTask(newTask, project)
            PersistentService.undoneTask(undoneTask, project)

            val panel = JPanel()
            PersistentService.getInstance(project).state.plainNotes
                .forEachIndexed {i, pn ->
                panel.add(JLabel("$i: $pn"))
            }

            toolWindow.contentManager.addContent(
                ContentFactory.getInstance().createContent(panel, "Notes", false)
            )
        }
    }

}
