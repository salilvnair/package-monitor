package com.salilvnair.packagemonitor.frame;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import com.salilvnair.packagemonitor.event.core.EventEmitter;
import com.salilvnair.packagemonitor.event.core.EventPublisher;
import com.salilvnair.packagemonitor.event.type.MainFrameActionPanelEvent;
import com.salilvnair.packagemonitor.event.type.MainFrameEvent;
import com.salilvnair.packagemonitor.event.type.PackageMonitorEvent;
import com.salilvnair.packagemonitor.event.type.TableSelectionEvent;
import com.salilvnair.packagemonitor.icon.PackageMonitorIcon;
import com.salilvnair.packagemonitor.model.PackageInfo;
import com.salilvnair.packagemonitor.model.PackageInfoConfiguration;
import com.salilvnair.packagemonitor.panel.MainFrameActionPanel;
import com.salilvnair.packagemonitor.panel.TablePanel;
import com.salilvnair.packagemonitor.service.context.DataContext;
import com.salilvnair.packagemonitor.service.core.PackageMonitorService;
import com.salilvnair.packagemonitor.service.factory.PackageMonitorFactory;
import com.salilvnair.packagemonitor.service.type.PackageMonitorType;
import com.salilvnair.packagemonitor.toolbar.ToolbarPanel;
import com.salilvnair.packagemonitor.type.ToolbarEvent;
import com.salilvnair.packagemonitor.ui.SwingComponent;
import com.salilvnair.packagemonitor.util.IconUtils;
import com.salilvnair.packagemonitor.util.MacOsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Salil V Nair
 */
public class PackageMonitorMainFrame extends JFrame implements SwingComponent, EventPublisher {
    private TablePanel tablePanel;
    private MainFrameActionPanel mainFrameActionPanel;
    private ToolbarPanel toolbarPanel;
    private final Project project;
    private JDialog loading;
    JMenuItem exitItem;
    JMenuItem settingsItem;
    private final PackageMonitorService monitorService;
    private final DataContext dataContext;
    private EventEmitter emitter;
    private final PackageMonitorType packageMonitorType;

    public PackageMonitorMainFrame(Project project, PackageInfoConfiguration packageInfoConfiguration, PackageMonitorType packageMonitorType) {
        super("Package Monitor");
        this.project = project;
        this.packageMonitorType = packageMonitorType;
        monitorService = PackageMonitorFactory.generate(packageMonitorType.factory());
        init();
        setVisible(true);
        dataContext = new DataContext(project, this, packageInfoConfiguration);
        dataContext.setPackageMonitorType(packageMonitorType);
        monitorService.monitor(packageMonitorType.compareVersionCommand(), dataContext);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initStyle() {
        setSize(750,550);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        setIconImage(IconUtils.createIcon(AllIcons.Debugger.Watch).getImage());
    }

    @Override
    public void initComponents() {
        setJMenuBar(initMenuBar());
        toolbarPanel = new ToolbarPanel();
        tablePanel = new TablePanel(getRootPane());
        List<PackageInfo> data = new ArrayList<>();
        tablePanel.setData(data);
        mainFrameActionPanel = new MainFrameActionPanel(getRootPane());
        initLoadingPanel();
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
            else if(ToolbarEvent.FORCE_REFRESH.equals(toolbarEvent)) {
                tablePanel.clear();
                PackageInfoConfiguration packageInfoConfiguration = dataContext.packageInfoConfiguration();
                if(packageInfoConfiguration != null && !packageInfoConfiguration.getConfiguredPackageInfos().isEmpty()) {
                    monitorService.monitor(packageMonitorType.compareVersionCommand(), dataContext);
                }
            }
            else {
                tablePanel.showOnlyDiff();
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        mainFrameActionPanel.actionPerformed(event -> {
            MainFrameActionPanelEvent mainFrameActionPanelEvent = (MainFrameActionPanelEvent) event;
            if(mainFrameActionPanelEvent.updateClicked()) {
                dataContext.setPackageInfos(tablePanel.data());
                dataContext.setSelectedRows(tablePanel.selectedRows());
                monitorService.monitor(packageMonitorType.updateVersionCommand(), dataContext);
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
            new PackageMonitorConfigFrame(project, false, true, mainFrame, packageMonitorType);
            mainFrame.setVisible(false);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainFrameEvent event = new MainFrameEvent(this, true);
                emitter.emit(event);
                dispose();
                System.gc();
            }
        });

        if(SystemInfo.isMac) {
            MacOsUtil.closeOnQuit();
        }

        monitorService.subscribe(event -> {
            if( event instanceof PackageMonitorEvent) {
                PackageMonitorEvent packageMonitorEvent = (PackageMonitorEvent) event;
                switch (packageMonitorEvent.eventType()) {
                    case TABLE_PANEL_DATA:
                        tablePanel.addData(packageMonitorEvent.tablePanelData());
                        break;

                    case REPLACE_TABLE_DATA:
                        tablePanel.replace(packageMonitorEvent.replacedTableData());
                        break;

                    case ENABLE_TABLE_ROW_SELECTION:
                        tablePanel.enableRowSelection();
                        break;

                    case DISABLE_TABLE_ROW_SELECTION:
                        tablePanel.disableRowSelection();
                        break;

                    case HIDE_LOADING:
                        loading.setVisible(false);
                        break;

                    case SHOW_LOADING:
                        loading.setVisible(true);
                        break;

                    case DISPOSE_LOADING:
                        loading.dispose();
                        break;

                    case HIDE_UPDATE_BTN:
                        mainFrameActionPanel.hideUpdateButton();
                        break;

                    case SHOW_UPDATE_BTN:
                        mainFrameActionPanel.showUpdateButton();
                        break;

                    case HIDE_TOOLBAR_PANEL:
                        toolbarPanel.setVisible(false);
                        break;

                    case SHOW_TOOLBAR_PANEL:
                        toolbarPanel.setVisible(true);
                        break;

                    case SHOW_TOOLBAR_DIFF_PANEL:
                        toolbarPanel.showDiffPanel();
                        break;

                    case HIDE_TOOLBAR_DIFF_PANEL:
                        toolbarPanel.hideDiffPanel();
                        break;

                    case DISABLE_FORCE_REFRESH:
                        toolbarPanel.disableForceRefresh();
                        break;

                    case ENABLE_FORCE_REFRESH:
                        toolbarPanel.enableForceRefresh();
                        break;

                    case NG_LIB_UPDATED_EVENT:
                        if(toolbarPanel.showAllEnabled()) {
                            tablePanel.showOnlyDiff();
                        }
                        break;
                }
            }
        });

    }

    @Override
    public void initChildrenLayout() {
        add(toolbarPanel, BorderLayout.PAGE_START);
        add(tablePanel, BorderLayout.CENTER);
        add(mainFrameActionPanel, BorderLayout.SOUTH);
    }

    @Override
    public void eventEmitter(EventEmitter eventEmitter) {
        this.emitter = eventEmitter;
    }

    private void initLoadingPanel() {
        loading = new JDialog(this);
        JPanel loadingPanel = new JPanel(new BorderLayout());
        JPanel loadingInfoPanel = new JPanel(new FlowLayout());
        JLabel waitLabel = new JLabel("Please wait...",new AnimatedIcon.Default(), SwingConstants.LEFT);
        waitLabel.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        if(!UIUtil.isUnderDarcula()) {
            loading.setBackground(JBColor.WHITE);
            loadingPanel.setBackground(JBColor.WHITE);
            loadingInfoPanel.setBackground(JBColor.WHITE);
        }
        loadingInfoPanel.add(waitLabel);
        loadingPanel.add(loadingInfoPanel, BorderLayout.CENTER);
        loadingPanel.setSize(300,300);
        loading.setUndecorated(true);
        loading.getContentPane().add(loadingPanel);
        loading.pack();
        loading.setLocationRelativeTo(this);
        loading.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        loading.setModal(true);
    }

    private JMenuBar initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");


        exitItem = new JMenuItem("Exit");
        settingsItem = new JMenuItem("Settings...");
        settingsItem.setIcon(PackageMonitorIcon.GEAR_ICON);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        settingsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));

        fileMenu.add(settingsItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        return menuBar;
    }
}
