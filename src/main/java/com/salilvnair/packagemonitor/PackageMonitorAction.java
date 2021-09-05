package com.salilvnair.packagemonitor;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.salilvnair.packagemonitor.frame.PackageMonitorConfigFrame;
import com.salilvnair.packagemonitor.frame.PackageMonitorMainFrame;
import com.salilvnair.packagemonitor.model.PackageInfo;
import com.salilvnair.packagemonitor.model.PackageInfoConfiguration;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Salil V Nair
 */
public class PackageMonitorAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PackageInfoConfiguration packageInfoConfiguration = PackageMonitorConfigFrame.loadConfigurationFromUserHome();
        if(packageInfoConfiguration==null || packageInfoConfiguration.getConfiguredPackageInfos().isEmpty()) {
            new PackageMonitorConfigFrame(e.getProject(), true, false);
        }
        else {
            new PackageMonitorMainFrame(e.getProject(), packageInfoConfiguration);
        }

    }



    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        assert psiFile != null;
        String filename = psiFile.getVirtualFile().getName();
        Presentation presentation = e.getPresentation();
        if (!"package.json".equals(filename)) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        presentation.setEnabledAndVisible(true);
    }
}
