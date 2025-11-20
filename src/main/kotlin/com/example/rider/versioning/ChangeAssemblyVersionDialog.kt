package com.example.rider.versioning

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class ChangeAssemblyVersionDialog(project: Project, private val file: VirtualFile) : DialogWrapper(project) {

    private val fileVersionMajor = JBTextField("1", 5)
    private val fileVersionMinor = JBTextField("0", 5)
    private val fileVersionBuild = JBTextField("0", 5)
    private val fileVersionRevision = JBTextField("0", 5)

    private val assemblyVersionMajor = JBTextField("1", 5)
    private val assemblyVersionMinor = JBTextField("0", 5)
    private val assemblyVersionBuild = JBTextField("0", 5)
    private val assemblyVersionRevision = JBTextField("0", 5)

    init {
        title = "Change Assembly Version"
        // Load current version if possible (TODO)
        val currentVersions = VersionManager.getCurrentVersions(project, file)
        if (currentVersions != null) {
            setVersionFields(currentVersions.first, fileVersionMajor, fileVersionMinor, fileVersionBuild, fileVersionRevision)
            setVersionFields(currentVersions.second, assemblyVersionMajor, assemblyVersionMinor, assemblyVersionBuild, assemblyVersionRevision)
        }
        init()
    }
    
    private fun setVersionFields(version: String, major: JBTextField, minor: JBTextField, build: JBTextField, revision: JBTextField) {
        val parts = version.split(".")
        if (parts.isNotEmpty()) major.text = parts[0]
        if (parts.size > 1) minor.text = parts[1]
        if (parts.size > 2) build.text = parts[2]
        if (parts.size > 3) revision.text = parts[3]
    }

    override fun createCenterPanel(): JComponent {
        val fileVersionPanel = JPanel()
        fileVersionPanel.add(fileVersionMajor)
        fileVersionPanel.add(JBLabel("."))
        fileVersionPanel.add(fileVersionMinor)
        fileVersionPanel.add(JBLabel("."))
        fileVersionPanel.add(fileVersionBuild)
        fileVersionPanel.add(JBLabel("."))
        fileVersionPanel.add(fileVersionRevision)

        val assemblyVersionPanel = JPanel()
        assemblyVersionPanel.add(assemblyVersionMajor)
        assemblyVersionPanel.add(JBLabel("."))
        assemblyVersionPanel.add(assemblyVersionMinor)
        assemblyVersionPanel.add(JBLabel("."))
        assemblyVersionPanel.add(assemblyVersionBuild)
        assemblyVersionPanel.add(JBLabel("."))
        assemblyVersionPanel.add(assemblyVersionRevision)

        return FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("File Version:"), fileVersionPanel)
            .addLabeledComponent(JBLabel("Assembly Version:"), assemblyVersionPanel)
            .panel
    }

    val fileVersion: String
        get() = "${fileVersionMajor.text}.${fileVersionMinor.text}.${fileVersionBuild.text}.${fileVersionRevision.text}"

    val assemblyVersion: String
        get() = "${assemblyVersionMajor.text}.${assemblyVersionMinor.text}.${assemblyVersionBuild.text}.${assemblyVersionRevision.text}"
}
