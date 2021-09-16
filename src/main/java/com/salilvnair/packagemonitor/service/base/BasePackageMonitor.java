package com.salilvnair.packagemonitor.service.base;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ScriptRunnerUtil;
import com.salilvnair.packagemonitor.event.core.EventEmitter;
import com.salilvnair.packagemonitor.event.provider.PackageMonitorEventEmitter;
import com.salilvnair.packagemonitor.model.PackageInfo;
import com.salilvnair.packagemonitor.model.PackageInfoConfiguration;
import com.salilvnair.packagemonitor.service.context.DataContext;
import com.salilvnair.packagemonitor.service.core.PackageMonitorService;
import com.salilvnair.packagemonitor.service.type.PackageMonitorType;
import com.salilvnair.packagemonitor.util.IntelliJNpmUtils;

import javax.swing.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Salil V Nair
 */
public abstract class BasePackageMonitor implements PackageMonitorService {

    protected EventEmitter eventEmitter;
    protected Map<String, PackageInfo> packageNamePackageInfoMap;
    protected PackageInfoConfiguration packageInfoConfiguration;
    protected SwingWorker<PackageInfo, String> compareNpmWorker;
    protected SwingWorker<PackageInfo, String> updateNpmWorker;
    protected DataContext dataContext;

    protected void compareNpmVersions(Map<String, String> packageNameVersionMap, PackageMonitorEventEmitter emitter) {
        packageNamePackageInfoMap = new HashMap<>();
        if(!packageInfoConfiguration.getConfiguredPackageInfos().isEmpty()) {
            List<String> savedPackageNames = packageInfoConfiguration.getConfiguredPackageInfos().stream().map(PackageInfo::getPackageName).collect(Collectors.toList());
            packageNameVersionMap = packageNameVersionMap.entrySet().stream().filter(entry -> savedPackageNames.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        packageNameVersionMap.forEach((name, version) ->{
            PackageInfo disputeData = new PackageInfo();
            disputeData.setPackageName(name);
            disputeData.setYourVersion(version);
            disputeData.setLatestVersion(null);
            packageNamePackageInfoMap.put(name, disputeData);
            emitter.emitTablePanelData(disputeData);
        });
        Map<String, String> finalPackageNameVersionMap = packageNameVersionMap;
        if (compareNpmWorker == null || compareNpmWorker.isDone() || compareNpmWorker.isCancelled()) {
            instantiateCompareNpmVersionsSwingWorker(finalPackageNameVersionMap, emitter);
            compareNpmWorker.execute();
        }
        emitter.showLoading();
    }

    protected void instantiateCompareNpmVersionsSwingWorker(Map<String, String> finalPackageNameVersionMap, PackageMonitorEventEmitter emitter) {
        compareNpmWorker = new SwingWorker<>() {
            @Override
            protected void done() {
                emitter.disposeLoading();
                emitter.enableTableRowSelection();
                emitter.showUpdateButton();
                emitter.showToolbarDiffPanel();
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
                    for(String name : finalPackageNameVersionMap.keySet()) {
                        List<String> cmds = new ArrayList<>();
                        cmds.add(IntelliJNpmUtils.npm());
                        GeneralCommandLine generalCommandLine = new GeneralCommandLine(cmds);
                        generalCommandLine.setCharset(StandardCharsets.UTF_8);
                        generalCommandLine.setWorkDirectory(dataContext.project().getBasePath());
                        generalCommandLine.addParameters("view", name, "versions", "--json");
                        try {
                            Gson converter = new Gson();
                            Type type = new TypeToken<List<String>>() {}.getType();
                            String commandLineOutputStr = ScriptRunnerUtil.getProcessOutput(generalCommandLine);
                            List<String> result = converter.fromJson(commandLineOutputStr, type);
                            Collections.reverse(result);
                            PackageInfo packageInfo = packageNamePackageInfoMap.get(name);
                            packageInfo.setLatestVersion(result.get(0));
                            packageInfo.setPackageVersions(result);
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


    protected  <T, V>  void cleanUpWorkers(SwingWorker<T, V> worker) {
        if(worker != null && !worker.isCancelled() && !worker.isDone()) {
            System.out.println("cleaning up.....");
            worker.cancel(true);
        }
    }

    @Override
    public void eventEmitter(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }
}
