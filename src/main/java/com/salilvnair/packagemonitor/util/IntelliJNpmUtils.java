package com.salilvnair.packagemonitor.util;

import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.salilvnair.packagemonitor.model.PackageInfo;

import java.util.*;

/**
 * @author Salil V Nair
 */
public class IntelliJNpmUtils {

    public static Map<String, String> retrievePackageNameKeyedVersionMap(Project project) {
        Map<String, String> packageNameVersionMap = new HashMap<>();
        List<PackageInfo> data = new ArrayList<>();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        assert editor != null;
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        assert file != null;
        PsiFile psiFile = Objects.requireNonNull(PsiManager.getInstance(project).findFile(file));
        if (psiFile instanceof JsonFile) {
            if (((JsonFile) psiFile).getTopLevelValue() instanceof JsonObject) {
                JsonObject object = (JsonObject) ((JsonFile) psiFile).getTopLevelValue();
                assert object != null;
                JsonProperty dependencies = object.findProperty("dependencies");
                assert dependencies != null;
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
        return packageNameVersionMap;
    }
}
