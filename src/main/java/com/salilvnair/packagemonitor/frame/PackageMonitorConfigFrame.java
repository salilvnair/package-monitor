package com.salilvnair.packagemonitor.frame;

import com.intellij.openapi.project.Project;
import com.salilvnair.packagemonitor.event.ConfigFrameActionPanelEvent;
import com.salilvnair.packagemonitor.event.TableSelectionEvent;
import com.salilvnair.packagemonitor.model.PackageInfo;
import com.salilvnair.packagemonitor.model.PackageInfoConfiguration;
import com.salilvnair.packagemonitor.panel.ConfigFrameActionPanel;
import com.salilvnair.packagemonitor.panel.ConfigurationTablePanel;
import com.salilvnair.packagemonitor.ui.SwingComponent;
import com.salilvnair.packagemonitor.util.FileUtils;
import com.salilvnair.packagemonitor.util.IconUtils;
import com.salilvnair.packagemonitor.util.IntelliJNpmUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Salil V Nair
 */
public class PackageMonitorConfigFrame extends JFrame implements SwingComponent {
    private final Project project;
    private ConfigurationTablePanel configurationTablePanel;
    private ConfigFrameActionPanel actionPanel;
    private boolean showAllPackages;
    private final boolean fromMenu;
    private PackageInfoConfiguration configuration;
    private PackageMonitorMainFrame mainFrame;
    private boolean saveButtonClicked;

    public PackageMonitorConfigFrame(Project project, boolean showAllPackages, boolean fromMenu) {
        super("Package Monitor Configuration");
        this.showAllPackages = showAllPackages;
        this.fromMenu = fromMenu;
        this.project = project;
        init();
        setVisible(true);
    }

    public PackageMonitorConfigFrame(Project project, boolean showAllPackages, boolean fromMenu, PackageMonitorMainFrame mainFrame) {
        super("Package Monitor Configuration");
        this.mainFrame = mainFrame;
        this.showAllPackages = showAllPackages;
        this.fromMenu = fromMenu;
        this.project = project;
        init();
        setVisible(true);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        configurationTablePanel = new ConfigurationTablePanel(getRootPane());
        actionPanel = new ConfigFrameActionPanel(getRootPane());
        addPackageInfoData();
    }

    @Override
    public void initChildrenLayout() {
        add(configurationTablePanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    @Override
    public void initStyle() {
        setSize(350,500);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        setIconImage(IconUtils.createIcon("/icon/package-json-blue.png").getImage());
    }

    @Override
    public void initListeners() {
        configurationTablePanel.actionPerformed(event -> {
            if(event instanceof TableSelectionEvent) {
                TableSelectionEvent tableSelectionEvent = (TableSelectionEvent) event;
                if(tableSelectionEvent.isRowsSelected()) {
                    actionPanel.enableSaveButton();
                }
                else {
                    actionPanel.disableSaveButton();
                }
            }
        });
        actionPanel.actionPerformed(event -> {
            ConfigFrameActionPanelEvent configFrameActionPanelEvent = (ConfigFrameActionPanelEvent) event;
            if (configFrameActionPanelEvent.saveClicked()) {
                saveButtonClicked = true;
                saveConfigurationToUserHome();
                this.dispose();
            }
            if (configFrameActionPanelEvent.showAllClicked()) {
                PackageInfoConfiguration packageInfoConfiguration = loadConfigurationFromUserHome();
                assert packageInfoConfiguration != null;
                packageInfoConfiguration.setConfiguredPackagesInSync(false);
                saveConfigurationToUserHome(packageInfoConfiguration);
                showPackages(new ArrayList<>());
                actionPanel.disableShowAllButton();
                configurationTablePanel.enableRowSelection();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(fromMenu && configuration!=null && saveButtonClicked) {
                    if(mainFrame!=null) {
                        mainFrame.dispose();
                    }
                    new PackageMonitorMainFrame(project, configuration);
                }
                else if(mainFrame!=null) {
                    mainFrame.setVisible(true);
                }
            }
        });
    }

    private void saveConfigurationToUserHome() {
        String userHomePath = System.getProperty("user.home");
        File file = new File(userHomePath + File.separator + ".salilvnair" + File.separator + "package-monitor"+ File.separator + "config.data");
        List<PackageInfo> packageInfos = configurationTablePanel.data();
        List<PackageInfo> configuredPackageInfos = new ArrayList<>();
        for(int i : configurationTablePanel.selectedRows()) {
            configuredPackageInfos.add(packageInfos.get(i));
        }
        try {
            configuration = new PackageInfoConfiguration(configuredPackageInfos, false);
            PackageInfoConfiguration[] configurations = {configuration};
            FileUtils.saveToFile(file, configurations);
            new PackageMonitorMainFrame(project, configuration);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfigurationToUserHome(PackageInfoConfiguration configuration) {
        String userHomePath = System.getProperty("user.home");
        File file = new File(userHomePath + File.separator + ".salilvnair" + File.separator + "package-monitor"+ File.separator + "config.data");
        try {
            PackageInfoConfiguration[] configurations = {configuration};
            FileUtils.saveToFile(file, configurations);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PackageInfoConfiguration loadConfigurationFromUserHome() {
        String userHomePath = System.getProperty("user.home");
        File file = new File(userHomePath + File.separator + ".salilvnair" + File.separator + "package-monitor"+ File.separator + "config.data");
        try {
            List<PackageInfoConfiguration> packageInfoConfigurations = FileUtils.loadFromFile(file, PackageInfoConfiguration.class);
            if(packageInfoConfigurations!=null && !packageInfoConfigurations.isEmpty()) {
                return packageInfoConfigurations.get(0);
            }
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addPackageInfoData() {
        configuration = loadConfigurationFromUserHome();
        if(configuration!=null && !configuration.getConfiguredPackageInfos().isEmpty()) {
            configurationTablePanel.disableRowSelection();
            actionPanel.enableShowAllButton();
            showPackages(configuration.getConfiguredPackageInfos());
        }
        else {
            showPackages(new ArrayList<>());
        }
    }

    private void showPackages(List<PackageInfo> packageInfos) {
        if(packageInfos.isEmpty() || showAllPackages) {
            Map<String, String> packageNameVersionMap = IntelliJNpmUtils.retrievePackageNameKeyedVersionMap(project);
            packageNameVersionMap.forEach((k,v) -> {
                PackageInfo packageInfo = new PackageInfo(k, v, null);
                packageInfos.add(packageInfo);
            });
        }
        configurationTablePanel.setData(packageInfos);
        configurationTablePanel.refresh();
    }

    public void setShowAllPackages(boolean showAllPackages) {
        this.showAllPackages = showAllPackages;
    }
}
