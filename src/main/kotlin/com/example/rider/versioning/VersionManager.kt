package com.example.rider.versioning

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VfsUtil
import java.io.IOException

object VersionManager {

    fun getCurrentVersions(project: Project, file: VirtualFile): Pair<String, String>? {
        // 1. Try to find AssemblyInfo.cs
        val assemblyInfo = findAssemblyInfo(file)
        if (assemblyInfo != null) {
            val content = VfsUtil.loadText(assemblyInfo)
            val fv = Regex("""\[assembly:\s*(?:System\.Reflection\.)?AssemblyFileVersion\s*\(\s*"([\d\.]+)"\s*\)\s*\]""").find(content)?.groupValues?.get(1) ?: "1.0.0.0"
            val av = Regex("""\[assembly:\s*(?:System\.Reflection\.)?AssemblyVersion\s*\(\s*"([\d\.]+)"\s*\)\s*\]""").find(content)?.groupValues?.get(1) ?: "1.0.0.0"
            return Pair(fv, av)
        }
        
        // 2. Try .csproj
        val csproj = if (file.extension == "csproj") file else null
        if (csproj != null) {
             val content = VfsUtil.loadText(csproj)
             val fv = Regex("""<FileVersion>([\d\.]+)</FileVersion>""").find(content)?.groupValues?.get(1) ?: "1.0.0.0"
             val av = Regex("""<AssemblyVersion>([\d\.]+)</AssemblyVersion>""").find(content)?.groupValues?.get(1) ?: "1.0.0.0"
             return Pair(fv, av)
        }
        
        return null
    }

    fun updateVersion(project: Project, file: VirtualFile, fileVersion: String, assemblyVersion: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            try {
                val assemblyInfo = findAssemblyInfo(file)
                if (assemblyInfo != null) {
                    var content = VfsUtil.loadText(assemblyInfo)
                    // Use regex that matches the whole line to replace it safely
                    content = content.replace(
                        Regex("""\[assembly:\s*(?:System\.Reflection\.)?AssemblyFileVersion\s*\(\s*".*"\s*\)\s*\]"""), 
                        """[assembly: AssemblyFileVersion("$fileVersion")]"""
                    )
                    content = content.replace(
                        Regex("""\[assembly:\s*(?:System\.Reflection\.)?AssemblyVersion\s*\(\s*".*"\s*\)\s*\]"""), 
                        """[assembly: AssemblyVersion("$assemblyVersion")]"""
                    )
                    VfsUtil.saveText(assemblyInfo, content)
                } else if (file.extension == "csproj") {
                     var content = VfsUtil.loadText(file)
                     
                     if (content.contains("<FileVersion>")) {
                        content = content.replace(Regex("""<FileVersion>.*</FileVersion>"""), """<FileVersion>$fileVersion</FileVersion>""")
                     } else {
                        // If missing, we really should insert it, but for now let's stick to updating if present
                        // or maybe append to the first PropertyGroup
                     }
                     
                     if (content.contains("<AssemblyVersion>")) {
                        content = content.replace(Regex("""<AssemblyVersion>.*</AssemblyVersion>"""), """<AssemblyVersion>$assemblyVersion</AssemblyVersion>""")
                     }
                     
                     VfsUtil.saveText(file, content)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun findAssemblyInfo(file: VirtualFile): VirtualFile? {
        // If file is .csproj, look in its parent's Properties folder
        val baseDir = if (file.isDirectory) file else file.parent
        
        if (baseDir != null) {
            val properties = baseDir.findChild("Properties")
            if (properties != null && properties.isDirectory) {
                val info = properties.findChild("AssemblyInfo.cs")
                if (info != null) return info
            }
        }
        return null
    }
}
