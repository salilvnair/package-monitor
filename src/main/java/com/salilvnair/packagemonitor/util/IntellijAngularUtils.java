package com.salilvnair.packagemonitor.util;

import com.intellij.json.psi.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.salilvnair.packagemonitor.model.NgLibInfo;
import com.salilvnair.packagemonitor.model.PackageInfo;

import java.io.File;
import java.util.*;

/**
 * @author Salil V Nair
 */
public class IntellijAngularUtils {

    public IntellijAngularUtils() {}

    public static Map<String, String> retrievePackageNameKeyedVersionMap(Project project) {
        Map<String, String> packageNameVersionMap = new HashMap<>();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        assert editor != null;
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        assert file != null;
        PsiFile psiFile = Objects.requireNonNull(PsiManager.getInstance(project).findFile(file));
        if (psiFile instanceof JsonFile) {
            if (((JsonFile) psiFile).getTopLevelValue() instanceof JsonObject) {
                JsonObject object = (JsonObject) ((JsonFile) psiFile).getTopLevelValue();
                assert object != null;
                JsonProperty projects = object.findProperty("projects");
                assert projects != null;
                if(projects.getValue() instanceof JsonObject) {
                    JsonObject projectInfoObjects = (JsonObject) projects.getValue();
                    List<JsonProperty> projectInfoObjectPropertyList = projectInfoObjects.getPropertyList();
                    List<JsonProperty> libraryProjects = new ArrayList<>();
                    for (JsonProperty projectInfoObject: projectInfoObjectPropertyList) {
                        if(projectInfoObject.getValue() instanceof JsonObject) {
                            JsonObject libraryInfoObject = (JsonObject) projectInfoObject.getValue();
                            boolean isLibrary = libraryInfoObject
                                                .getPropertyList()
                                                .stream()
                                                .anyMatch(property -> "projectType".equals(property.getName()) && "library".equals(((JsonStringLiteral) Objects.requireNonNull(property.getValue())).getValue()));
                            if(isLibrary) {
                                libraryProjects.add(projectInfoObject);
                            }
                        }
                    }
                    for (JsonProperty libraryProject: libraryProjects) {
                        JsonObject libraryProjectInfoObject = (JsonObject) libraryProject.getValue();
                        assert libraryProjectInfoObject != null;
                        String rootPath = ((JsonStringLiteral) Objects.requireNonNull(Objects.requireNonNull(libraryProjectInfoObject.findProperty("root")).getValue())).getValue();
                        String libraryPackageJsonFilePath = project.getBasePath() + "/" + rootPath + "/" + "package.json";
                        PsiFile libPsiFile = IntelliJPsiUtils.findFileByPath(project, libraryPackageJsonFilePath);
                        if (libPsiFile instanceof JsonFile) {
                            if (((JsonFile) libPsiFile).getTopLevelValue() instanceof JsonObject) {
                                JsonObject libObject = (JsonObject) ((JsonFile) libPsiFile).getTopLevelValue();
                                assert libObject != null;
                                String version = ((JsonStringLiteral) Objects.requireNonNull(Objects.requireNonNull(libObject.findProperty("version")).getValue())).getValue();
                                packageNameVersionMap.put(libraryProject.getName(), version);
                            }
                        }
                    }
                }
            }
        }
        return packageNameVersionMap;
    }

    public static Map<String, NgLibInfo> retrievePackageNameKeyedLibraryInfoMap(Project project) {
        Map<String, NgLibInfo> packageNameNgLibInfoMap = new HashMap<>();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        assert editor != null;
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        assert file != null;
        PsiFile psiFile = Objects.requireNonNull(PsiManager.getInstance(project).findFile(file));
        if (psiFile instanceof JsonFile) {
            if (((JsonFile) psiFile).getTopLevelValue() instanceof JsonObject) {
                JsonObject object = (JsonObject) ((JsonFile) psiFile).getTopLevelValue();
                assert object != null;
                JsonProperty projects = object.findProperty("projects");
                assert projects != null;
                if(projects.getValue() instanceof JsonObject) {
                    JsonObject projectInfoObjects = (JsonObject) projects.getValue();
                    List<JsonProperty> projectInfoObjectPropertyList = projectInfoObjects.getPropertyList();
                    List<JsonProperty> libraryProjects = new ArrayList<>();
                    for (JsonProperty projectInfoObject: projectInfoObjectPropertyList) {
                        if(projectInfoObject.getValue() instanceof JsonObject) {
                            JsonObject libraryInfoObject = (JsonObject) projectInfoObject.getValue();
                            boolean isLibrary = libraryInfoObject
                                    .getPropertyList()
                                    .stream()
                                    .anyMatch(property -> "projectType".equals(property.getName()) && "library".equals(((JsonStringLiteral) Objects.requireNonNull(property.getValue())).getValue()));
                            if(isLibrary) {
                                libraryProjects.add(projectInfoObject);
                            }
                        }
                    }
                    for (JsonProperty libraryProject: libraryProjects) {
                        JsonObject libraryProjectInfoObject = (JsonObject) libraryProject.getValue();
                        assert libraryProjectInfoObject != null;
                        String rootPath = ((JsonStringLiteral) Objects.requireNonNull(Objects.requireNonNull(libraryProjectInfoObject.findProperty("root")).getValue())).getValue();
                        String libraryPackageJsonFilePath = project.getBasePath() + "/" + rootPath + "/" + "package.json";
                        PsiFile libPsiFile = IntelliJPsiUtils.findFileByPath(project, libraryPackageJsonFilePath);
                        if (libPsiFile instanceof JsonFile) {
                            if (((JsonFile) libPsiFile).getTopLevelValue() instanceof JsonObject) {
                                JsonObject libObject = (JsonObject) ((JsonFile) libPsiFile).getTopLevelValue();
                                assert libObject != null;
                                String version = ((JsonStringLiteral) Objects.requireNonNull(Objects.requireNonNull(libObject.findProperty("version")).getValue())).getValue();
                                NgLibInfo libInfo = new NgLibInfo();
                                libInfo.setName(libraryProject.getName());
                                libInfo.setVersion(version);
                                libInfo.setPackageJsonPath(libraryPackageJsonFilePath);
                                packageNameNgLibInfoMap.put(libraryProject.getName(), libInfo);
                            }
                        }
                    }
                }
            }
        }
        return packageNameNgLibInfoMap;
    }
}
