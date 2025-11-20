package com.example.rider.versioning

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rider.projectView.workspace.ProjectModelEntity
import com.jetbrains.rider.projectView.workspace.getProjectModelEntity

class ChangeAssemblyVersionAction : AnAction() {

    override fun update(e: AnActionEvent) {
        // Only show if a project is selected
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        
        // Simple check: is it a file/directory in the project view?
        // Ideally we check if it's a C# project, but for now let's just check if it's a valid selection.
        // In Rider, we often check for ProjectModelEntity
        // val entity = e.dataContext.getData(com.jetbrains.rider.projectView.workspace.ProjectModelEntity.DATA_KEY)
        
        e.presentation.isEnabledAndVisible = project != null && file != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        // Show Dialog
        val dialog = ChangeAssemblyVersionDialog(project, file)
        if (dialog.showAndGet()) {
            // Update versions
            val newFileVersion = dialog.fileVersion
            val newAssemblyVersion = dialog.assemblyVersion
            
            VersionManager.updateVersion(project, file, newFileVersion, newAssemblyVersion)
        }
    }
}
