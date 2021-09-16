package com.salilvnair.packagemonitor.service.provider;

import com.intellij.json.psi.JsonElementGenerator;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.salilvnair.packagemonitor.event.provider.PackageMonitorEventEmitter;
import com.salilvnair.packagemonitor.event.type.MainFrameEvent;
import com.salilvnair.packagemonitor.model.NgLibInfo;
import com.salilvnair.packagemonitor.model.PackageInfo;
import com.salilvnair.packagemonitor.service.base.BasePackageMonitor;
import com.salilvnair.packagemonitor.service.context.DataContext;
import com.salilvnair.packagemonitor.service.context.PackageMonitorContext;
import com.salilvnair.packagemonitor.service.type.NgLibraryPackageMonitorType;
import com.salilvnair.packagemonitor.service.type.PackageMonitorTypeBase;
import com.salilvnair.packagemonitor.util.IntelliJPsiUtils;
import com.salilvnair.packagemonitor.util.IntellijAngularUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Salil V Nair
 */
public class NgLibraryPackageMonitor extends BasePackageMonitor {
    private PackageMonitorEventEmitter emitter;

    @Override
    public PackageMonitorContext monitor(PackageMonitorTypeBase monitorType, DataContext dataContext, Object... objects) {
        PackageMonitorContext packageMonitorContext = new PackageMonitorContext();
        this.dataContext = dataContext;
        if(emitter == null) {
            emitter = new PackageMonitorEventEmitter(this.eventEmitter);
        }
        initListeners();
        this.packageInfoConfiguration = dataContext.packageInfoConfiguration();
        if(NgLibraryPackageMonitorType.COMPARE_LIBRARY_PROJECT_VERSIONS.equals(monitorType)) {
            Map<String, NgLibInfo> ngLibInfoMap = IntellijAngularUtils.retrievePackageNameKeyedLibraryInfoMap(dataContext.project());
            dataContext.setNgLibInfoMap(ngLibInfoMap);
            Map<String, String> packageNameVersionMap = ngLibInfoMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, stringNgLibInfoEntry -> stringNgLibInfoEntry.getValue().getVersion(), (o, n) -> n));
            if(!packageNameVersionMap.isEmpty()) {
                compareNpmVersions(packageNameVersionMap, emitter);
            }
        }
        if(NgLibraryPackageMonitorType.UPDATE_LIBRARY_PROJECT_VERSIONS.equals(monitorType)) {
            updateLibraryVersions();
        }
        return packageMonitorContext;
    }

    private void updateLibraryVersions() {
        Map<String, NgLibInfo> ngLibInfoMap = dataContext.ngLibInfoMap().isEmpty() ? IntellijAngularUtils.retrievePackageNameKeyedLibraryInfoMap(dataContext.project()) : dataContext.ngLibInfoMap();
        List<PackageInfo> packageInfos = dataContext.packageInfos();
        List<PackageInfo> updatePackageInfos = new ArrayList<>();
        List<Integer> selectedRows = dataContext.selectedRows();
        if(selectedRows.size() == 0) {
            List<PackageInfo> unSyncedPackages = packageInfos.stream().filter(packageInfo -> !packageInfo.getYourVersion().equals(packageInfo.getLatestVersion())).collect(Collectors.toList());
            updatePackageInfos.addAll(unSyncedPackages);
        }
        else {
            for(int i : selectedRows) {
                updatePackageInfos.add(packageInfos.get(i));
            }
        }
        if (updateNpmWorker == null || updateNpmWorker.isDone() || updateNpmWorker.isCancelled()) {
            instantiateUpdateNpmVersionsSwingWorker(updatePackageInfos, ngLibInfoMap);
            updateNpmWorker.execute();
        }
        emitter.showLoading();
    }

    private void instantiateUpdateNpmVersionsSwingWorker(List<PackageInfo> updatePackageInfos, Map<String, NgLibInfo> ngLibInfoMap) {
        updateNpmWorker = new SwingWorker<>() {

            @Override
            protected void done() {
                emitter.disposeLoading();
                emitter.updatedNgLibVersion();
                JOptionPane.showMessageDialog(dataContext.packageMonitorMainFrame(), "Package(s) updated successfully!", "Updated Successfully", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            protected void process(List<String> packageNames) {
                boolean shouldContinue = !isCancelled();
                if(shouldContinue) {
                    PackageInfo packageInfo = packageNamePackageInfoMap.get(packageNames.get(0));
                    emitter.replaceTableData(packageInfo);
                }
            }

            @Override
            protected PackageInfo doInBackground() {
                boolean shouldContinue = !isCancelled();
                while (shouldContinue) {
                    if (isCancelled()) {
                        shouldContinue = false;
                        continue;
                    }
                    for (PackageInfo updatePackageInfo : updatePackageInfos) {
                        NgLibInfo ngLibInfo = ngLibInfoMap.get(updatePackageInfo.getPackageName());
                        PsiFile psiFile = IntelliJPsiUtils.findFileByPath(dataContext.project(), ngLibInfo.getPackageJsonPath());
                        if(psiFile != null ) {
                            if (psiFile instanceof JsonFile) {
                                if (((JsonFile) psiFile).getTopLevelValue() instanceof JsonObject) {
                                    JsonObject object = (JsonObject) ((JsonFile) psiFile).getTopLevelValue();
                                    assert object != null;
                                    JsonProperty version = object.findProperty("version");
                                    JsonElementGenerator generator = new JsonElementGenerator(dataContext.project());
                                    if(version != null) {
                                        WriteCommandAction.runWriteCommandAction(dataContext.project(), ()-> {
                                            Objects.requireNonNull(version.getValue()).replace(generator.createStringLiteral(updatePackageInfo.getLatestVersion()));
                                        });
                                        String name = updatePackageInfo.getPackageName();
                                        packageNamePackageInfoMap.get(name).setYourVersion(updatePackageInfo.getLatestVersion());
                                        publish(name);
                                    }
                                }
                            }
                            Document document = PsiDocumentManager.getInstance(dataContext.project()).getDocument(psiFile);
                            if(document!=null) {
                                FileDocumentManager.getInstance().saveDocument(document);
                                JOptionPane.showMessageDialog(dataContext.packageMonitorMainFrame(), "Package(s) updated successfully!", "Updated Successfully", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                    shouldContinue = false;
                }
                return null;
            }
        };
    }

    private Map<String, String> extractNgLibraryProjectNameVersionMap(DataContext dataContext) {
        return IntellijAngularUtils.retrievePackageNameKeyedVersionMap(dataContext.project());
    }

    private void initListeners() {
        this.dataContext.packageMonitorMainFrame().subscribe(event -> {
            MainFrameEvent mainFrameEvent = (MainFrameEvent) event;
            if(mainFrameEvent.windowClosed()) {
                cleanUpWorkers(compareNpmWorker);
            }
        });
    }
}
