package com.salilvnair.packagemonitor.frame;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ScriptRunnerUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import com.salilvnair.packagemonitor.event.MainFrameActionPanelEvent;
import com.salilvnair.packagemonitor.event.TableSelectionEvent;
import com.salilvnair.packagemonitor.model.PackageInfo;
import com.salilvnair.packagemonitor.model.PackageInfoConfiguration;
import com.salilvnair.packagemonitor.panel.MainFrameActionPanel;
import com.salilvnair.packagemonitor.panel.TablePanel;
import com.salilvnair.packagemonitor.toolbar.ToolbarPanel;
import com.salilvnair.packagemonitor.type.ToolbarEvent;
import com.salilvnair.packagemonitor.ui.SwingComponent;
import com.salilvnair.packagemonitor.util.IconUtils;
import com.salilvnair.packagemonitor.util.IntelliJNpmUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Salil V Nair
 */
public class PackageMonitorMainFrame extends JFrame implements SwingComponent {

    private TablePanel tablePanel;
    private MainFrameActionPanel mainFrameActionPanel;
    private ToolbarPanel toolbarPanel;
    private Map<String, PackageInfo> packageNamePackageInfoMap;
    private final Project project;
    private JDialog loading;
    private final PackageInfoConfiguration packageInfoConfiguration;
    JMenuItem exitItem;
    JMenuItem settingsItem;
    private SwingWorker<PackageInfo, String> compareNpmWorker;
    SwingWorker<PackageInfo, String> updateNpmWorker;

    public PackageMonitorMainFrame(Project project, PackageInfoConfiguration packageInfoConfiguration) {
        super("Package Monitor");
        this.project = project;
        this.packageInfoConfiguration = packageInfoConfiguration;
        init();
        setVisible(true);
        compareNpmVersionsSwingWorker();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initStyle() {
        setSize(600,500);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        setIconImage(IconUtils.createIcon("/icon/package-json-blue.png").getImage());
    }

    @Override
    public void initComponents() {
        setJMenuBar(initMenuBar());
        toolbarPanel = new ToolbarPanel();
        toolbarPanel.setVisible(false);
        tablePanel = new TablePanel(getRootPane());
        List<PackageInfo> data = new ArrayList<>();
        tablePanel.setData(data);
        mainFrameActionPanel = new MainFrameActionPanel(getRootPane());
        loading = new JDialog(this);
        JPanel p1 = new JPanel(new BorderLayout());
        JLabel waitLabel = new JLabel("Please wait...");
        waitLabel.setFont(new Font("JetBrains Mono", Font.PLAIN, 16));
        p1.add(waitLabel, BorderLayout.CENTER);
        p1.setSize(300,300);
        if(!UIUtil.isUnderDarcula()) {
            loading.setBackground(JBColor.WHITE);
        }
        loading.setUndecorated(true);
        loading.getContentPane().add(p1);
        loading.pack();
        loading.setLocationRelativeTo(this);
        loading.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        loading.setModal(true);
    }

    @Override
    public void initListeners() {

        tablePanel.actionPerformed(event -> {
            if(event instanceof TableSelectionEvent) {
                TableSelectionEvent tableSelectionEvent = (TableSelectionEvent) event;
                mainFrameActionPanel.changeUpdateButtonText(tableSelectionEvent.isRowsSelected());
            }
        });

        toolbarPanel.addToolbarListener(toolbarEvent -> {
            if(ToolbarEvent.SHOW_ALL.equals(toolbarEvent)) {
                tablePanel.showAll();
            }
            else {
                tablePanel.showOnlyDiff();
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        mainFrameActionPanel.actionPerformed(event -> {
            MainFrameActionPanelEvent mainFrameActionPanelEvent = (MainFrameActionPanelEvent) event;
            if(mainFrameActionPanelEvent.updateClicked()) {
                updateNpmVersions();
            }
        });

        exitItem.addActionListener( actionEvent -> {
            int action = JOptionPane.showConfirmDialog(PackageMonitorMainFrame.this
                    , "Do you want to exit?", "Confirm Exit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
            if(action == JOptionPane.OK_OPTION) {
                WindowListener[] windowListeners = getWindowListeners();
                windowListeners[0].windowClosing(new WindowEvent(PackageMonitorMainFrame.this, 0));
            }
        });
        PackageMonitorMainFrame mainFrame = this;
        settingsItem.addActionListener(actionEvent -> {
            new PackageMonitorConfigFrame(project, false, true, mainFrame);
            mainFrame.setVisible(false);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanUpWorkers(compareNpmWorker);
                cleanUpWorkers(updateNpmWorker);
                dispose();
                System.gc();
            }
        });

    }

    @Override
    public void initChildrenLayout() {
        add(toolbarPanel, BorderLayout.PAGE_START);
        add(tablePanel, BorderLayout.CENTER);
        add(mainFrameActionPanel, BorderLayout.SOUTH);
    }

    private void updateNpmVersions() {
        List<PackageInfo> packageInfos = tablePanel.data();
        List<PackageInfo> updatePackageInfos = new ArrayList<>();
        if(tablePanel.selectedRows().size() == 0) {
            List<PackageInfo> unSyncedPackages = packageInfos.stream().filter(packageInfo -> !packageInfo.getYourVersion().equals(packageInfo.getLatestVersion())).collect(Collectors.toList());
            updatePackageInfos.addAll(unSyncedPackages);
        }
        else {
            for(int i : tablePanel.selectedRows()) {
                updatePackageInfos.add(packageInfos.get(i));
            }
        }
        if (updateNpmWorker == null || updateNpmWorker.isDone() || updateNpmWorker.isCancelled()) {
            updateNpmVersionsSwingWorker(updatePackageInfos);
            updateNpmWorker.execute();
        }
        loading.setVisible(true);
    }

    private void updateNpmVersionsSwingWorker(List<PackageInfo> updatePackageInfos) {
        updateNpmWorker = new SwingWorker<>() {

            @Override
            protected void done() {
                System.out.println("All Packages has been updated");
                loading.dispose();
                JOptionPane.showMessageDialog(PackageMonitorMainFrame.this, "Package(s) updated successfully!", "Updated Successfully", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            protected void process(List<String> packageNames) {
                boolean shouldContinue = !isCancelled();
                if(shouldContinue) {
                    JOptionPane.showMessageDialog(PackageMonitorMainFrame.this, "Updated package:" + packageNames.get(0), "Updated Successfully", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("Updated package:" + packageNames.get(0));
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
                        generalCommandLine.setWorkDirectory(project.getBasePath());
                        generalCommandLine.addParameters("install", packageInfo.getPackageName()+"@"+packageInfo.getLatestVersion());
                        try {
                            String output = ScriptRunnerUtil.getProcessOutput(generalCommandLine);
                            System.out.println(output);
                            publish(packageInfo.getPackageName());
                        }
                        catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    shouldContinue = false;
                }
                return null;
            }
        };
    }


    private void compareNpmVersionsSwingWorker() {
        packageNamePackageInfoMap = new HashMap<>();
        Map<String, String> packageNameVersionMap = IntelliJNpmUtils.retrievePackageNameKeyedVersionMap(project);
        if(!packageInfoConfiguration.getConfiguredPackageInfos().isEmpty()) {
            List<String> savedPackageNames = packageInfoConfiguration.getConfiguredPackageInfos().stream().map(PackageInfo::getPackageName).collect(Collectors.toList());
            packageNameVersionMap = packageNameVersionMap.entrySet().stream().filter(entry -> savedPackageNames.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        packageNameVersionMap.forEach((name, version) ->{
            PackageInfo disputeData = new PackageInfo();
            disputeData.setPackageName(name);
            disputeData.setYourVersion(version);
            disputeData.setLatestVersion(packageInfoConfiguration.isConfiguredPackagesInSync() ? version : null);
            packageNamePackageInfoMap.put(name, disputeData);
            tablePanel.addData(disputeData);
        });
        Map<String, String> finalPackageNameVersionMap = packageNameVersionMap;
        PackageMonitorMainFrame self = this;
        if (compareNpmWorker == null || compareNpmWorker.isDone() || compareNpmWorker.isCancelled()) {
            compareNpmVersionsSwingWorker(finalPackageNameVersionMap);
        }
        if(!packageInfoConfiguration.isConfiguredPackagesInSync()){
            compareNpmWorker.execute();
        }
        else {
            tablePanel.disableRowSelection();
        }
    }

    private void compareNpmVersionsSwingWorker(Map<String, String> finalPackageNameVersionMap) {
        compareNpmWorker = new SwingWorker<>() {
            @Override
            protected void done() {
                System.out.println("done has been called current entries"+ packageNamePackageInfoMap.size());
                boolean allPackagesInSync = packageNamePackageInfoMap.entrySet().stream().allMatch(entry -> entry.getValue().getYourVersion().equals(entry.getValue().getLatestVersion()));
                if(allPackagesInSync) {
                    tablePanel.disableRowSelection();
                    mainFrameActionPanel.hideUpdateButton();
                    toolbarPanel.setVisible(false);
                    packageInfoConfiguration.setConfiguredPackagesInSync(true);
                    PackageMonitorConfigFrame.saveConfigurationToUserHome(packageInfoConfiguration);
                }
                else {
                    tablePanel.enableRowSelection();
                    mainFrameActionPanel.showUpdateButton();
                    toolbarPanel.setVisible(true);
                }
            }

            @Override
            protected void process(List<String> packageNames) {
                boolean shouldContinue = !isCancelled();
                if(shouldContinue) {
                    int retrieved = packageNames.size();
                    System.out.println("Got " + retrieved+ " packageInfo." + packageNames);
                    PackageInfo packageInfo = packageNamePackageInfoMap.get(packageNames.get(0));
                    tablePanel.replace(packageInfo);
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
                        generalCommandLine.setWorkDirectory(project.getBasePath());
                        generalCommandLine.addParameters("show", name, "version");
                        try {
                            String latestVersionOnServer = ScriptRunnerUtil.getProcessOutput(generalCommandLine);
                            latestVersionOnServer = latestVersionOnServer.replace("\n", "").replace("\r", "");
                            PackageInfo packageInfo = packageNamePackageInfoMap.get(name);
                            packageInfo.setLatestVersion(latestVersionOnServer);
                            publish(name);
                        }
                        catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    shouldContinue = false;
                }
                return null;
            }
        };
    }

    private JMenuBar initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");


        exitItem = new JMenuItem("Exit");
        settingsItem = new JMenuItem("Settings...");
        if(UIUtil.isUnderDarcula()) {
            settingsItem.setIcon(IconUtils.createIcon("/icon/settings_dark.png"));
        }
        else {
            settingsItem.setIcon(IconUtils.createIcon("/icon/settings_white.png"));
        }
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        settingsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));

        fileMenu.add(settingsItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        return menuBar;
    }

    private <T, V>  void cleanUpWorkers(SwingWorker<T, V> worker) {
        if(worker != null && !worker.isCancelled() && !worker.isDone()) {
            worker.cancel(true);
        }
    }
}
