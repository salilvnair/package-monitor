package com.salilvnair.packagemonitor.frame;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.salilvnair.packagemonitor.event.type.ConfigFrameActionPanelEvent;
import com.salilvnair.packagemonitor.event.type.TableSelectionEvent;
import com.salilvnair.packagemonitor.icon.PackageMonitorIcon;
import com.salilvnair.packagemonitor.model.PackageInfo;
import com.salilvnair.packagemonitor.model.PackageInfoConfiguration;
import com.salilvnair.packagemonitor.panel.ConfigFrameActionPanel;
import com.salilvnair.packagemonitor.panel.ConfigurationTablePanel;
import com.salilvnair.packagemonitor.service.type.PackageMonitorType;
import com.salilvnair.packagemonitor.ui.SwingComponent;
import com.salilvnair.packagemonitor.util.FileUtils;
import com.salilvnair.packagemonitor.util.IconUtils;
import com.salilvnair.packagemonitor.util.IntelliJNpmUtils;
import com.salilvnair.packagemonitor.util.IntellijAngularUtils;
import org.jetbrains.annotations.NotNull;

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
    private ComboBox<PackageMonitorType> monitorTypes;
    private JPanel monitorTypePanel;
    PackageMonitorType packageMonitorType;

    public PackageMonitorConfigFrame(Project project, boolean showAllPackages, boolean fromMenu, PackageMonitorType packageMonitorType) {
        super("Package Monitor Configuration");
        this.showAllPackages = showAllPackages;
        this.fromMenu = fromMenu;
        this.project = project;
        this.packageMonitorType = packageMonitorType;
        init();
        showPackageDataInfo();
        setVisible(true);
    }

    public PackageMonitorConfigFrame(Project project, boolean showAllPackages, boolean fromMenu, PackageMonitorMainFrame mainFrame, PackageMonitorType packageMonitorType) {
        super("Package Monitor Configuration");
        this.mainFrame = mainFrame;
        this.packageMonitorType = packageMonitorType;
        this.showAllPackages = showAllPackages;
        this.fromMenu = fromMenu;
        this.project = project;
        init();
        showPackageDataInfo();
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
        initMonitorTypes();
        monitorTypePanel = new JPanel();
    }

    private void showPackageDataInfo() {
        addPackageInfoData();
    }

    @Override
    public void initChildrenLayout() {
        initMonitorTypeLayout();
        add(monitorTypePanel, BorderLayout.NORTH);
        add(configurationTablePanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void initMonitorTypeLayout() {
        monitorTypePanel.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(monitorTypes);
        monitorTypePanel.add(panel, BorderLayout.EAST);
    }

    @Override
    public void initStyle() {
        setSize(380,500);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        setIconImage(IconUtils.createIcon(AllIcons.Actions.Preview).getImage());
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
                PackageMonitorType monitorType = (PackageMonitorType) monitorTypes.getSelectedItem();
                if(monitorType == null) {
                    monitorType = PackageMonitorType.NODE_JS;
                }
                PackageInfoConfiguration packageInfoConfiguration = loadConfigurationFromUserHome(monitorType);
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
                    new PackageMonitorMainFrame(project, configuration, packageMonitorType);
                }
                else if(mainFrame!=null) {
                    mainFrame.setVisible(true);
                }
            }
        });
    }

    private void saveConfigurationToUserHome() {
        String userHomePath = System.getProperty("user.home");
        List<PackageInfo> packageInfos = configurationTablePanel.data();
        List<PackageInfo> configuredPackageInfos = new ArrayList<>();
        for(int i : configurationTablePanel.selectedRows()) {
            configuredPackageInfos.add(packageInfos.get(i));
        }
        try {
            int typeIndex = 1;
            if(packageMonitorType != null) {
                typeIndex = packageMonitorType.typeIndex();
            }
            File file = new File(userHomePath + File.separator + ".salilvnair" + File.separator + "package-monitor"+ File.separator + "config"+typeIndex+".data");
            configuration = new PackageInfoConfiguration(configuredPackageInfos, false, typeIndex);
            PackageInfoConfiguration[] configurations = {configuration};
            FileUtils.saveToFile(file, configurations);
            new PackageMonitorMainFrame(project, configuration, packageMonitorType);
        }
        catch (IOException ignore) {}
    }

    public static void saveConfigurationToUserHome(PackageInfoConfiguration configuration) {
        String userHomePath = System.getProperty("user.home");
        File file = new File(userHomePath + File.separator + ".salilvnair" + File.separator + "package-monitor"+ File.separator + "config"+configuration.getSelectedMonitorType()+".data");
        try {
            PackageInfoConfiguration[] configurations = {configuration};
            FileUtils.saveToFile(file, configurations);
        }
        catch (IOException ignore) {}
    }

    public static PackageInfoConfiguration loadConfigurationFromUserHome(PackageMonitorType packageMonitorType) {
        String userHomePath = System.getProperty("user.home");
        File file = new File(userHomePath + File.separator + ".salilvnair" + File.separator + "package-monitor"+ File.separator + "config"+packageMonitorType.typeIndex()+".data");
        try {
            List<PackageInfoConfiguration> packageInfoConfigurations = FileUtils.loadFromFile(file, PackageInfoConfiguration.class);
            if(packageInfoConfigurations!=null && !packageInfoConfigurations.isEmpty()) {
                return packageInfoConfigurations.get(0);
            }
        }
        catch (IOException | ClassNotFoundException ignore) {}
        return null;
    }

    private void addPackageInfoData() {
        PackageMonitorType monitorType = (PackageMonitorType) monitorTypes.getSelectedItem();
        if(monitorType == null) {
            monitorType = PackageMonitorType.NODE_JS;
        }
        configuration = loadConfigurationFromUserHome(monitorType);
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
            Map<String, String> packageNameVersionMap = retrievePackageNameKeyedVersionMapByMonitorType();
            packageNameVersionMap.forEach((k,v) -> {
                PackageInfo packageInfo = new PackageInfo(k, v, null);
                packageInfos.add(packageInfo);
            });
        }
        configurationTablePanel.setData(packageInfos);
        configurationTablePanel.refresh();
    }

    @NotNull
    private Map<String, String> retrievePackageNameKeyedVersionMapByMonitorType() {
        PackageMonitorType monitorType = (PackageMonitorType) monitorTypes.getSelectedItem();
        if(monitorType == null) {
            monitorType = PackageMonitorType.NODE_JS;
        }
        if(monitorType == PackageMonitorType.ANGULAR_LIB) {
            return IntellijAngularUtils.retrievePackageNameKeyedVersionMap(project);
        }
        else {
            return IntelliJNpmUtils.retrievePackageNameKeyedVersionMap(project);
        }

    }

    public void setShowAllPackages(boolean showAllPackages) {
        this.showAllPackages = showAllPackages;
    }

    private void initMonitorTypes() {
        monitorTypes = new ComboBox<>();
        DefaultComboBoxModel<PackageMonitorType> monitorType = new DefaultComboBoxModel<>();
        monitorType.addElement(packageMonitorType);
        monitorTypes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                PackageMonitorType packageMonitorType = (PackageMonitorType) value;
                label.setIcon(packageMonitorType.icon());
                return label;
            }
        });
        monitorTypes.setModel(monitorType);
        monitorTypes.setSelectedIndex(0);
    }
}
