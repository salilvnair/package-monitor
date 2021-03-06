package com.salilvnair.packagemonitor.util;

import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Salil V Nair
 */
public class IntelliJNpmUtils {

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
                if(object != null ) {
                    JsonProperty dependencies = object.findProperty("dependencies");
                    if(dependencies != null ) {
                        if(dependencies.getValue() instanceof JsonObject) {
                            JsonObject dependenciesObject = (JsonObject) dependencies.getValue();
                            List<JsonProperty> dependenciesObjectPropertyList = dependenciesObject.getPropertyList();
                            for (JsonProperty jsonProperty: dependenciesObjectPropertyList) {
                                if(jsonProperty.getValue() instanceof JsonStringLiteral) {
                                    String currentVersion = ((JsonStringLiteral) jsonProperty.getValue()).getValue();
                                    currentVersion = currentVersion.replace("~","").replace("^","");
                                    packageNameVersionMap.put(jsonProperty.getName(), currentVersion);
                                }
                            }
                        }
                    }
                }
            }
        }
        return packageNameVersionMap;
    }

    public static String npm() {
        if(SystemInfo.isWindows) {
            return "npm.cmd";
        }
        return "npm";
    }
}
