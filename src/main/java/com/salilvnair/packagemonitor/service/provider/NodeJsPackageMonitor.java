package com.salilvnair.packagemonitor.service.provider;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ScriptRunnerUtil;
import com.salilvnair.packagemonitor.event.provider.PackageMonitorEventEmitter;
import com.salilvnair.packagemonitor.event.type.MainFrameEvent;
import com.salilvnair.packagemonitor.model.PackageInfo;
import com.salilvnair.packagemonitor.service.base.BasePackageMonitor;
import com.salilvnair.packagemonitor.service.context.DataContext;
import com.salilvnair.packagemonitor.service.context.PackageMonitorContext;
import com.salilvnair.packagemonitor.service.type.NodeJsPackageMonitorType;
import com.salilvnair.packagemonitor.service.type.PackageMonitorTypeBase;
import com.salilvnair.packagemonitor.util.IntelliJNpmUtils;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Salil V Nair
 */
public class NodeJsPackageMonitor extends BasePackageMonitor {
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
        if(NodeJsPackageMonitorType.COMPARE_NPM_VERSIONS.equals(monitorType)) {
            Map<String, String> packageNameVersionMap = IntelliJNpmUtils.retrievePackageNameKeyedVersionMap(dataContext.project());
            if(!packageNameVersionMap.isEmpty()) {
                compareNpmVersions(packageNameVersionMap, emitter);
            }
            else {
                this.emitter.disableForceRefresh();
            }
        }
        if(NodeJsPackageMonitorType.UPDATE_NPM_VERSIONS.equals(monitorType)) {
            updateNpmVersions();
        }
        return packageMonitorContext;
    }

    private void initListeners() {
        this.dataContext.packageMonitorMainFrame().subscribe(event -> {
            MainFrameEvent mainFrameEvent = (MainFrameEvent) event;
            if(mainFrameEvent.windowClosed()) {
                cleanUpWorkers(compareNpmWorker);
                cleanUpWorkers(updateNpmWorker);
            }
        });
    }

    private void updateNpmVersions() {
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
            instantiateUpdateNpmVersionsSwingWorker(updatePackageInfos);
            updateNpmWorker.execute();
        }

        emitter.showLoading();
    }

    private void instantiateUpdateNpmVersionsSwingWorker(List<PackageInfo> updatePackageInfos) {
        updateNpmWorker = new SwingWorker<>() {

            @Override
            protected void done() {
                emitter.disposeLoading();
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
                    for(PackageInfo packageInfo : updatePackageInfos) {
                        List<String> cmds = new ArrayList<>();
                        cmds.add(IntelliJNpmUtils.npm());
                        GeneralCommandLine generalCommandLine = new GeneralCommandLine(cmds);
                        generalCommandLine.setCharset(StandardCharsets.UTF_8);
                        generalCommandLine.setWorkDirectory(dataContext.project().getBasePath());
                        generalCommandLine.addParameters("install", packageInfo.getPackageName()+"@"+packageInfo.getLatestVersion());
                        try {
                            ScriptRunnerUtil.getProcessOutput(generalCommandLine);
                            String name = packageInfo.getPackageName();
                            packageNamePackageInfoMap.get(name).setYourVersion(packageInfo.getLatestVersion());
                            publish(name);
                        }
                        catch (ExecutionException ignored) {}
                    }
                    shouldContinue = false;
                }
                return null;
            }
        };
    }
}
