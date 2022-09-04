/*
 * SedView.java
 */
package sed;

import java.io.File;
import java.util.logging.LogRecord;
import javax.swing.Icon;
import org.jdesktop.application.Action;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Font;
import java.awt.TrayIcon.MessageType;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import pt.unl.fct.di.tsantos.util.app.AppUtils;
import pt.unl.fct.di.tsantos.util.app.DefaultTrayedFrameView;
import pt.unl.fct.di.tsantos.util.swing.JStreamedTextArea;
import pt.unl.fct.di.tsantos.util.download.subtitile.Language;
import pt.unl.fct.di.tsantos.util.time.Ticker;

/**
 * The application's main frame.
 */
public class SedView extends DefaultTrayedFrameView<SedApp> {

    private static final Logger logger =
            Logger.getLogger(SedView.class.getName());

    protected Level current = Level.INFO;
    protected StreamHandler sh;
    protected Handler trayHandler;

    public SedView(SedApp app) {
        super(app);
        initComponents();
        initMyComponents();
    }    

    private void initMyComponents() {
        logTextArea.setFont(new Font(null, 0, 11));

        // Redirect err and out to text area
        OutputStream outs = ((JStreamedTextArea)logTextArea).getOutputStream();
        sh = new StreamHandler(outs, new SimpleFormatter()) {

            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                flush();
            }

        };

        logFrame.setIconImage(getResourceMap().
                getImageIcon("Application.trayIcon").getImage());
        advancedSettingsDialog.setIconImage(getResourceMap().
                getImageIcon("Application.trayIcon").getImage());

        // next check empty
        nextCheckLabel.setText("");

        updateLevel(Level.INFO);

        // if windows 7 cover java bug and change icons
        if (AppUtils.USER_OS != null &&
                AppUtils.USER_OS.toLowerCase().equals("windows 7")) {
            Icon homeFolderIcon =
                    getResourceMap().getIcon("Application.homeFolderIcon");
            Icon upFolderIcon =
                    getResourceMap().getIcon("Application.upFolderIcon");
            Icon newFolderIcon =
                    getResourceMap().getIcon("Application.newFolderIcon");
            Icon listViewIcon =
                    getResourceMap().getIcon("Application.listViewIcon");
            Icon detailsViewIcon =
                    getResourceMap().getIcon("Application.detailsViewIcon");
            AppUtils.useWindows7FileChooserIcons(homeFolderIcon, upFolderIcon,
                    newFolderIcon, listViewIcon, detailsViewIcon);
        }
    
        DefaultComboBoxModel dcbm = (DefaultComboBoxModel)
                    langsComboBox.getModel();
        for (Language lang : SedApp.getLanguages()) {
            dcbm.addElement(lang);
        }

        DefaultTableModel dtm =
                        (DefaultTableModel) langsTable.getModel();
        for (Language lang : getTheApplication().getSelectedLanguages()) {
            dtm.addRow(new Object[]{ lang });
        }

        File directory = getTheApplication().getSearchDirectory();
        if (directory != null)
            searchDirectoryTextField.setText(directory.toString());
        directory = getTheApplication().getSaveDirectory();
        if (directory != null)
            saveToTextField.setText(directory.toString());
        searchAtStartupCheckBox.setSelected(
                getTheApplication().isSearchAtStartup());
        searchMinutesTextField.setText(
                getTheApplication().getSearchInterval() + "");
        tvsubsRadioButton.setSelected(
                getTheApplication().isTvsubsEnabled());
        opensubsRadioButton.setSelected(
                getTheApplication().isOpensubsEnabled());
        legtvRadioButton.setSelected(
                getTheApplication().isLegtvEnabled());
        String u = getTheApplication().getLegtvUser();
        String p = getTheApplication().getLegtvPwd();
        if (u != null && !u.isEmpty() && p != null && !p.isEmpty()) {
            legtvUserField.setText(u);
            legtvPasswordField.setText(p);
        }
        addic7edRadioButton.setSelected(
                getTheApplication().isAddic7edEnabled());
        u = getTheApplication().getAddic7edUser();
        p = getTheApplication().getAddic7edPwd();
        if (u != null && !u.isEmpty() && p != null && !p.isEmpty()) {
            addic7edUserField.setText(u);
            addic7edPasswordField.setText(p);
        }
        firstMatchCheckBox.setSelected(getTheApplication().isFirstMatch());         

        priorityTextField.setText(getPriorities());

        Observer observer = new Observer() {
            public void update(Observable o, Object arg) {
                Ticker t = (Ticker)arg;
                if (t != null) {
                    if (t.isRunning()) {
                        nextCheckLabel.setText("Checking for subtitiles");
                    } else {
                        nextCheckLabel.setText("Next check for new " +
                                "episodes in " + t.nextRun() + " minutes");
                    }
                }
            }
        };

        trayHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                Object[] params = record.getParameters();
                if (params != null && params.length == 1) {
                    MessageType mt = MessageType.NONE;
                    Level level = record.getLevel();
                    if (level == Level.INFO) {
                        mt = MessageType.INFO;
                    } else if (level == Level.WARNING) {
                        mt = MessageType.WARNING;
                    } else if (level == Level.SEVERE) {
                        mt = MessageType.ERROR;
                    }
                    trayIcon.displayMessage("Subtitle Downloader",
                            record.getMessage(), mt);
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };
        getTheApplication().addSubtitleHandler(sh);
        getTheApplication().addSubtitleTaskHandler(sh);
        getTheApplication().addSubtitleHandler(trayHandler);
        getTheApplication().addObserver(observer);

        //getFrame().setPreferredSize(null);
        //getFrame().pack();
        // do not resize window
        getFrame().setResizable(false);
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = SedApp.getApplication().getMainFrame();
            aboutBox = new SedAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SedApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        searchDirectoryLabel = new javax.swing.JLabel();
        searchDirectoryTextField = new javax.swing.JTextField();
        searchBrowseButton = new javax.swing.JButton();
        searchEpisodesLabel = new javax.swing.JLabel();
        searchMinutesTextField = new javax.swing.JTextField();
        searchMinutesLabel = new javax.swing.JLabel();
        searchAtStartupCheckBox = new javax.swing.JCheckBox();
        findLabel = new javax.swing.JLabel();
        saveToLabel = new javax.swing.JLabel();
        saveToTextField = new javax.swing.JTextField();
        saveBrowseButton = new javax.swing.JButton();
        nextCheckLabel = new javax.swing.JLabel();
        searchLangsLabel = new javax.swing.JLabel();
        langsScrollPane = new javax.swing.JScrollPane();
        langsTable = new javax.swing.JTable();
        langsComboBox = new javax.swing.JComboBox();
        addLangButton = new javax.swing.JButton();
        removeLangButton = new javax.swing.JButton();
        checkButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        extraMenu = new javax.swing.JMenu();
        advancedSettingsMenuItem = new javax.swing.JMenuItem();
        logMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        logFrame = new javax.swing.JFrame();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new pt.unl.fct.di.tsantos.util.swing.JStreamedTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileLogFrameMenu = new javax.swing.JMenu();
        closeLogFrameMenuItem = new javax.swing.JMenuItem();
        editLogFrameMenu = new javax.swing.JMenu();
        levelLogFrameMenu = new javax.swing.JMenu();
        severeLevelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        warningLevelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        infoLevelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        configLevelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        fineLevelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        finerLevelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        finestLevelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        clearLogFrameMenuItem = new javax.swing.JMenuItem();
        advancedSettingsDialog = new javax.swing.JDialog();
        searchersLabel = new javax.swing.JLabel();
        tvsubsRadioButton = new javax.swing.JRadioButton();
        legtvRadioButton = new javax.swing.JRadioButton();
        opensubsRadioButton = new javax.swing.JRadioButton();
        legtvUserField = new javax.swing.JTextField();
        legtvPasswordField = new javax.swing.JPasswordField();
        legtvUserLabel = new javax.swing.JLabel();
        legtvPassLabel = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        noteLabel = new javax.swing.JLabel();
        addic7edRadioButton = new javax.swing.JRadioButton();
        addic7edUserField = new javax.swing.JTextField();
        addic7edPasswordField = new javax.swing.JPasswordField();
        addic7edUserLabel = new javax.swing.JLabel();
        addic7edPassLabel = new javax.swing.JLabel();
        firstMatchCheckBox = new javax.swing.JCheckBox();
        priorityLabel = new javax.swing.JLabel();
        priorityTextField = new javax.swing.JTextField();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(sed.SedApp.class).getContext().getResourceMap(SedView.class);
        searchDirectoryLabel.setText(resourceMap.getString("searchDirectoryLabel.text")); // NOI18N
        searchDirectoryLabel.setName("searchDirectoryLabel"); // NOI18N

        searchDirectoryTextField.setEditable(false);
        searchDirectoryTextField.setText(resourceMap.getString("searchDirectoryTextField.text")); // NOI18N
        searchDirectoryTextField.setName("searchDirectoryTextField"); // NOI18N

        searchBrowseButton.setIcon(resourceMap.getIcon("searchBrowseButton.icon")); // NOI18N
        searchBrowseButton.setText(resourceMap.getString("searchBrowseButton.text")); // NOI18N
        searchBrowseButton.setName("searchBrowseButton"); // NOI18N
        searchBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBrowseButtonActionPerformed(evt);
            }
        });

        searchEpisodesLabel.setText(resourceMap.getString("searchEpisodesLabel.text")); // NOI18N
        searchEpisodesLabel.setName("searchEpisodesLabel"); // NOI18N

        searchMinutesTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        searchMinutesTextField.setText(resourceMap.getString("searchMinutesTextField.text")); // NOI18N
        searchMinutesTextField.setName("searchMinutesTextField"); // NOI18N
        searchMinutesTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchMinutesTextFieldActionPerformed(evt);
            }
        });

        searchMinutesLabel.setText(resourceMap.getString("searchMinutesLabel.text")); // NOI18N
        searchMinutesLabel.setName("searchMinutesLabel"); // NOI18N

        searchAtStartupCheckBox.setText(resourceMap.getString("searchAtStartupCheckBox.text")); // NOI18N
        searchAtStartupCheckBox.setName("searchAtStartupCheckBox"); // NOI18N
        searchAtStartupCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchAtStartupCheckBoxActionPerformed(evt);
            }
        });

        findLabel.setText(resourceMap.getString("findLabel.text")); // NOI18N
        findLabel.setName("findLabel"); // NOI18N

        saveToLabel.setText(resourceMap.getString("saveToLabel.text")); // NOI18N
        saveToLabel.setName("saveToLabel"); // NOI18N

        saveToTextField.setEditable(false);
        saveToTextField.setText(resourceMap.getString("saveToTextField.text")); // NOI18N
        saveToTextField.setName("saveToTextField"); // NOI18N

        saveBrowseButton.setIcon(resourceMap.getIcon("saveBrowseButton.icon")); // NOI18N
        saveBrowseButton.setText(resourceMap.getString("saveBrowseButton.text")); // NOI18N
        saveBrowseButton.setName("saveBrowseButton"); // NOI18N
        saveBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBrowseButtonActionPerformed(evt);
            }
        });

        nextCheckLabel.setText(resourceMap.getString("nextCheckLabel.text")); // NOI18N
        nextCheckLabel.setName("nextCheckLabel"); // NOI18N

        searchLangsLabel.setText(resourceMap.getString("searchLangsLabel.text")); // NOI18N
        searchLangsLabel.setName("searchLangsLabel"); // NOI18N

        langsScrollPane.setName("langsScrollPane"); // NOI18N

        langsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        langsTable.setName("langsTable"); // NOI18N
        langsScrollPane.setViewportView(langsTable);

        langsComboBox.setModel(new javax.swing.DefaultComboBoxModel());
        langsComboBox.setName("langsComboBox"); // NOI18N

        addLangButton.setText(resourceMap.getString("addLangButton.text")); // NOI18N
        addLangButton.setName("addLangButton"); // NOI18N
        addLangButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLangButtonActionPerformed(evt);
            }
        });

        removeLangButton.setText(resourceMap.getString("removeLangButton.text")); // NOI18N
        removeLangButton.setName("removeLangButton"); // NOI18N
        removeLangButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLangButtonActionPerformed(evt);
            }
        });

        checkButton.setIcon(resourceMap.getIcon("checkButton.icon")); // NOI18N
        checkButton.setText(resourceMap.getString("checkButton.text")); // NOI18N
        checkButton.setName("checkButton"); // NOI18N
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });

        jSeparator1.setName("jSeparator1"); // NOI18N

        jSeparator2.setName("jSeparator2"); // NOI18N

        jSeparator3.setName("jSeparator3"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(langsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(nextCheckLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                        .addGap(99, 99, 99))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(searchMinutesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(searchMinutesLabel))
                                    .addComponent(searchEpisodesLabel)
                                    .addComponent(searchAtStartupCheckBox)
                                    .addComponent(searchDirectoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(searchBrowseButton))
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(checkButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addComponent(searchDirectoryLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(searchLangsLabel, javax.swing.GroupLayout.Alignment.LEADING))
                        .addContainerGap())
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(langsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(87, 87, 87)
                        .addComponent(addLangButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeLangButton)
                        .addContainerGap(11, Short.MAX_VALUE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(findLabel)
                        .addContainerGap(210, Short.MAX_VALUE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(saveToLabel)
                        .addContainerGap(297, Short.MAX_VALUE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(saveToTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveBrowseButton)
                        .addContainerGap())))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchDirectoryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchDirectoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(searchEpisodesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchMinutesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(searchMinutesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchAtStartupCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(checkButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchLangsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(langsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeLangButton)
                    .addComponent(addLangButton)
                    .addComponent(langsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveToLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveToTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveBrowseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(nextCheckLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(sed.SedApp.class).getContext().getActionMap(SedView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        extraMenu.setText(resourceMap.getString("extraMenu.text")); // NOI18N
        extraMenu.setName("extraMenu"); // NOI18N

        advancedSettingsMenuItem.setText(resourceMap.getString("advancedSettingsMenuItem.text")); // NOI18N
        advancedSettingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedSettingsMenuItemActionPerformed(evt);
            }
        });
        extraMenu.add(advancedSettingsMenuItem);

        logMenuItem.setText(resourceMap.getString("logMenuItem.text")); // NOI18N
        logMenuItem.setName("logMenuItem"); // NOI18N
        logMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logMenuItemActionPerformed(evt);
            }
        });
        extraMenu.add(logMenuItem);

        menuBar.add(extraMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        logFrame.setTitle(resourceMap.getString("logFrame.title")); // NOI18N
        logFrame.setName("logFrame"); // NOI18N

        logScrollPane.setName("logScrollPane"); // NOI18N

        logTextArea.setColumns(20);
        logTextArea.setEditable(false);
        logTextArea.setRows(5);
        logTextArea.setName("logTextArea"); // NOI18N
        logScrollPane.setViewportView(logTextArea);

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        fileLogFrameMenu.setText(resourceMap.getString("fileLogFrameMenu.text")); // NOI18N
        fileLogFrameMenu.setName("fileLogFrameMenu"); // NOI18N

        closeLogFrameMenuItem.setText(resourceMap.getString("closeLogFrameMenuItem.text")); // NOI18N
        closeLogFrameMenuItem.setName("closeLogFrameMenuItem"); // NOI18N
        closeLogFrameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeLogFrameMenuItemActionPerformed(evt);
            }
        });
        fileLogFrameMenu.add(closeLogFrameMenuItem);

        jMenuBar1.add(fileLogFrameMenu);

        editLogFrameMenu.setText(resourceMap.getString("editLogFrameMenu.text")); // NOI18N
        editLogFrameMenu.setName("editLogFrameMenu"); // NOI18N

        levelLogFrameMenu.setText(resourceMap.getString("levelLogFrameMenu.text")); // NOI18N
        levelLogFrameMenu.setName("levelLogFrameMenu"); // NOI18N

        severeLevelCheckBoxMenuItem.setSelected(true);
        severeLevelCheckBoxMenuItem.setText(resourceMap.getString("severeLevelCheckBoxMenuItem.text")); // NOI18N
        severeLevelCheckBoxMenuItem.setName("severeLevelCheckBoxMenuItem"); // NOI18N
        severeLevelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                severeLevelCheckBoxMenuItemActionPerformed(evt);
            }
        });
        levelLogFrameMenu.add(severeLevelCheckBoxMenuItem);

        warningLevelCheckBoxMenuItem.setSelected(true);
        warningLevelCheckBoxMenuItem.setText(resourceMap.getString("warningLevelCheckBoxMenuItem.text")); // NOI18N
        warningLevelCheckBoxMenuItem.setName("warningLevelCheckBoxMenuItem"); // NOI18N
        warningLevelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warningLevelCheckBoxMenuItemActionPerformed(evt);
            }
        });
        levelLogFrameMenu.add(warningLevelCheckBoxMenuItem);

        infoLevelCheckBoxMenuItem.setSelected(true);
        infoLevelCheckBoxMenuItem.setText(resourceMap.getString("infoLevelCheckBoxMenuItem.text")); // NOI18N
        infoLevelCheckBoxMenuItem.setName("infoLevelCheckBoxMenuItem"); // NOI18N
        infoLevelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoLevelCheckBoxMenuItemActionPerformed(evt);
            }
        });
        levelLogFrameMenu.add(infoLevelCheckBoxMenuItem);

        configLevelCheckBoxMenuItem.setSelected(true);
        configLevelCheckBoxMenuItem.setText(resourceMap.getString("configLevelCheckBoxMenuItem.text")); // NOI18N
        configLevelCheckBoxMenuItem.setName("configLevelCheckBoxMenuItem"); // NOI18N
        configLevelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configLevelCheckBoxMenuItemActionPerformed(evt);
            }
        });
        levelLogFrameMenu.add(configLevelCheckBoxMenuItem);

        fineLevelCheckBoxMenuItem.setSelected(true);
        fineLevelCheckBoxMenuItem.setText(resourceMap.getString("fineLevelCheckBoxMenuItem.text")); // NOI18N
        fineLevelCheckBoxMenuItem.setName("fineLevelCheckBoxMenuItem"); // NOI18N
        fineLevelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fineLevelCheckBoxMenuItemActionPerformed(evt);
            }
        });
        levelLogFrameMenu.add(fineLevelCheckBoxMenuItem);

        finerLevelCheckBoxMenuItem.setSelected(true);
        finerLevelCheckBoxMenuItem.setText(resourceMap.getString("finerLevelCheckBoxMenuItem.text")); // NOI18N
        finerLevelCheckBoxMenuItem.setName("finerLevelCheckBoxMenuItem"); // NOI18N
        finerLevelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finerLevelCheckBoxMenuItemActionPerformed(evt);
            }
        });
        levelLogFrameMenu.add(finerLevelCheckBoxMenuItem);

        finestLevelCheckBoxMenuItem.setSelected(true);
        finestLevelCheckBoxMenuItem.setText(resourceMap.getString("finestLevelCheckBoxMenuItem.text")); // NOI18N
        finestLevelCheckBoxMenuItem.setName("finestLevelCheckBoxMenuItem"); // NOI18N
        finestLevelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finestLevelCheckBoxMenuItemActionPerformed(evt);
            }
        });
        levelLogFrameMenu.add(finestLevelCheckBoxMenuItem);

        editLogFrameMenu.add(levelLogFrameMenu);

        clearLogFrameMenuItem.setText(resourceMap.getString("clearLogFrameMenuItem.text")); // NOI18N
        clearLogFrameMenuItem.setName("clearLogFrameMenuItem"); // NOI18N
        clearLogFrameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearLogFrameMenuItemActionPerformed(evt);
            }
        });
        editLogFrameMenu.add(clearLogFrameMenuItem);

        jMenuBar1.add(editLogFrameMenu);

        logFrame.setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout logFrameLayout = new javax.swing.GroupLayout(logFrame.getContentPane());
        logFrame.getContentPane().setLayout(logFrameLayout);
        logFrameLayout.setHorizontalGroup(
            logFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
        );
        logFrameLayout.setVerticalGroup(
            logFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
        );

        advancedSettingsDialog.setTitle(resourceMap.getString("advancedSettingsDialog.title")); // NOI18N
        advancedSettingsDialog.setModal(true);
        advancedSettingsDialog.setName("advancedSettingsDialog"); // NOI18N
        advancedSettingsDialog.setResizable(false);

        searchersLabel.setText(resourceMap.getString("searchersLabel.text")); // NOI18N
        searchersLabel.setName("searchersLabel"); // NOI18N

        tvsubsRadioButton.setText(resourceMap.getString("tvsubsRadioButton.text")); // NOI18N
        tvsubsRadioButton.setName("tvsubsRadioButton"); // NOI18N

        legtvRadioButton.setText(resourceMap.getString("legtvRadioButton.text")); // NOI18N
        legtvRadioButton.setName("legtvRadioButton"); // NOI18N

        opensubsRadioButton.setText(resourceMap.getString("opensubsRadioButton.text")); // NOI18N
        opensubsRadioButton.setName("opensubsRadioButton"); // NOI18N

        legtvUserField.setText(resourceMap.getString("legtvUserField.text")); // NOI18N
        legtvUserField.setName("legtvUserField"); // NOI18N

        legtvPasswordField.setText(resourceMap.getString("legtvPasswordField.text")); // NOI18N
        legtvPasswordField.setName("legtvPasswordField"); // NOI18N

        legtvUserLabel.setText(resourceMap.getString("legtvUserLabel.text")); // NOI18N
        legtvUserLabel.setName("legtvUserLabel"); // NOI18N

        legtvPassLabel.setText(resourceMap.getString("legtvPassLabel.text")); // NOI18N
        legtvPassLabel.setName("legtvPassLabel"); // NOI18N

        saveButton.setText(resourceMap.getString("saveButton.text")); // NOI18N
        saveButton.setName("saveButton"); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(resourceMap.getString("cancelButton.text")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        noteLabel.setForeground(resourceMap.getColor("noteLabel.foreground")); // NOI18N
        noteLabel.setText(resourceMap.getString("noteLabel.text")); // NOI18N
        noteLabel.setName("noteLabel"); // NOI18N

        addic7edRadioButton.setText(resourceMap.getString("addic7edRadioButton.text")); // NOI18N
        addic7edRadioButton.setName("addic7edRadioButton"); // NOI18N

        addic7edUserField.setName("addic7edUserField"); // NOI18N

        addic7edPasswordField.setName("addic7edPasswordField"); // NOI18N

        addic7edUserLabel.setText(resourceMap.getString("addic7edUserLabel.text")); // NOI18N
        addic7edUserLabel.setName("addic7edUserLabel"); // NOI18N

        addic7edPassLabel.setText(resourceMap.getString("addic7edPassLabel.text")); // NOI18N
        addic7edPassLabel.setName("addic7edPassLabel"); // NOI18N

        firstMatchCheckBox.setText(resourceMap.getString("firstMatchCheckBox.text")); // NOI18N
        firstMatchCheckBox.setName("firstMatchCheckBox"); // NOI18N

        priorityLabel.setText(resourceMap.getString("priorityLabel.text")); // NOI18N
        priorityLabel.setName("priorityLabel"); // NOI18N

        priorityTextField.setText(resourceMap.getString("priorityTextField.text")); // NOI18N
        priorityTextField.setName("priorityTextField"); // NOI18N
        priorityTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                priorityTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout advancedSettingsDialogLayout = new javax.swing.GroupLayout(advancedSettingsDialog.getContentPane());
        advancedSettingsDialog.getContentPane().setLayout(advancedSettingsDialogLayout);
        advancedSettingsDialogLayout.setHorizontalGroup(
            advancedSettingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(advancedSettingsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(advancedSettingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tvsubsRadioButton)
                    .addComponent(searchersLabel)
                    .addComponent(opensubsRadioButton)
                    .addGroup(advancedSettingsDialogLayout.createSequentialGroup()
                        .addComponent(legtvRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                        .addComponent(legtvUserLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(legtvUserField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(legtvPassLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(legtvPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, advancedSettingsDialogLayout.createSequentialGroup()
                        .addComponent(saveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 300, Short.MAX_VALUE)
                        .addComponent(cancelButton))
                    .addComponent(noteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(advancedSettingsDialogLayout.createSequentialGroup()
                        .addGroup(advancedSettingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, advancedSettingsDialogLayout.createSequentialGroup()
                                .addComponent(firstMatchCheckBox)
                                .addGap(59, 59, 59)
                                .addComponent(priorityLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(priorityTextField))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, advancedSettingsDialogLayout.createSequentialGroup()
                                .addComponent(addic7edRadioButton)
                                .addGap(18, 18, 18)
                                .addComponent(addic7edUserLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addic7edUserField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addic7edPassLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addic7edPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        advancedSettingsDialogLayout.setVerticalGroup(
            advancedSettingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(advancedSettingsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchersLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tvsubsRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(opensubsRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedSettingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(legtvRadioButton)
                    .addComponent(legtvUserLabel)
                    .addComponent(legtvUserField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(legtvPassLabel)
                    .addComponent(legtvPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedSettingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addic7edRadioButton)
                    .addComponent(addic7edUserLabel)
                    .addComponent(addic7edUserField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addic7edPassLabel)
                    .addComponent(addic7edPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(advancedSettingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstMatchCheckBox)
                    .addComponent(priorityLabel)
                    .addComponent(priorityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(noteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(advancedSettingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cancelButton)
                    .addComponent(saveButton))
                .addContainerGap())
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void searchBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBrowseButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int showOpenDialog = fileChooser.showOpenDialog(getFrame());

        if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
            File dir = fileChooser.getSelectedFile();
            getTheApplication().setSearchDirectory(dir);
            searchDirectoryTextField.setText(dir.toString());
        }
    }//GEN-LAST:event_searchBrowseButtonActionPerformed

    private void saveBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBrowseButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int showOpenDialog = fileChooser.showOpenDialog(getFrame());

        if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
            File dir = fileChooser.getSelectedFile();
            getTheApplication().setSaveDirectory(dir);
            saveToTextField.setText(dir.toString());
        }
    }//GEN-LAST:event_saveBrowseButtonActionPerformed

    private void searchMinutesTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchMinutesTextFieldActionPerformed
        String text = searchMinutesTextField.getText();
        try {
            int mins = Integer.parseInt(text);
            if (mins <= 0) {
                JOptionPane.showMessageDialog(getFrame(),
                        "Number must be a positive integer.",
                        "Warning!", JOptionPane.WARNING_MESSAGE);
            } else {
                getTheApplication().setSearchInterval(mins);
            }
            searchMinutesTextField.setText(
                    getTheApplication().getSearchInterval() + "");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(getFrame(),
                    "Invalid number. Number must be an integer.",
                    "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_searchMinutesTextFieldActionPerformed

    private void searchAtStartupCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchAtStartupCheckBoxActionPerformed
        getTheApplication().setSearchAtStartup(
                searchAtStartupCheckBox.isSelected());
    }//GEN-LAST:event_searchAtStartupCheckBoxActionPerformed

    private void logMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logMenuItemActionPerformed
        logFrame.pack();
        logFrame.setLocationRelativeTo(getFrame());
        logFrame.setVisible(true);
    }//GEN-LAST:event_logMenuItemActionPerformed

    private void addLangButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLangButtonActionPerformed
        Language lang = (Language) langsComboBox.getSelectedItem();
        if (!getTheApplication().isSelectedLanguage(lang)) {
            DefaultTableModel dtm = (DefaultTableModel) langsTable.getModel();
            dtm.addRow(new Object[]{ lang });
            getTheApplication().addSelectedLanguage(lang);
        }
    }//GEN-LAST:event_addLangButtonActionPerformed

    private void removeLangButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLangButtonActionPerformed
        int index = langsTable.getSelectedRow();
        if (index < 0) return;
        DefaultTableModel dtm = (DefaultTableModel) langsTable.getModel();
        Language lang = (Language) dtm.getValueAt(index, 0);
        dtm.removeRow(index);
        getTheApplication().removeSelectedLanguage(lang);
    }//GEN-LAST:event_removeLangButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        boolean failed = false;
        boolean invalidateLegtv = false;
        boolean invalidateAddic7ed = false;

        if (legtvRadioButton.isSelected()) {
            String user = legtvUserField.getText();
            if (user == null || (user = user.trim()).isEmpty()) {
                JOptionPane.showMessageDialog(advancedSettingsDialog,
                        "User name must be non empty.",
                        "Warning!", JOptionPane.ERROR_MESSAGE);
                invalidateLegtv = true;
                failed = true;
            } else {
                char[] passArr = legtvPasswordField.getPassword();
                if (passArr == null ||
                        (new String(passArr).trim()).isEmpty()) {
                    JOptionPane.showMessageDialog(advancedSettingsDialog,
                            "Password must be non empty.",
                            "Warning!", JOptionPane.ERROR_MESSAGE);
                    invalidateLegtv = true;
                    failed = true;
                }
            }
        } else {
            invalidateLegtv = true;
        }

        if (addic7edRadioButton.isSelected()) {
            String user = addic7edUserField.getText();
            if (user == null || (user = user.trim()).isEmpty()) {
                JOptionPane.showMessageDialog(advancedSettingsDialog,
                        "User name must be non empty.",
                        "Warning!", JOptionPane.ERROR_MESSAGE);
                invalidateAddic7ed = true;
                failed = true;
            } else {
                char[] passArr = addic7edPasswordField.getPassword();
                if (passArr == null ||
                        (new String(passArr).trim()).isEmpty()) {
                    JOptionPane.showMessageDialog(advancedSettingsDialog,
                            "Password must be non empty.",
                            "Warning!", JOptionPane.ERROR_MESSAGE);
                    invalidateAddic7ed = true;
                    failed = true;
                }
            }
        } else {
            invalidateAddic7ed = true;
        }

        if (invalidateAddic7ed) {
            addic7edRadioButton.setSelected(false);
            addic7edUserField.setText("");
            addic7edPasswordField.setText("");
            getTheApplication().invalidateAddic7ed();
        }
        
        if (invalidateLegtv) {
            legtvRadioButton.setSelected(false);
            legtvUserField.setText("");
            legtvPasswordField.setText("");
            getTheApplication().invalidateLegtv();
        }

        String text = priorityTextField.getText();
        int subd = 0;
        if (tvsubsRadioButton.isSelected()) subd++;
        if (opensubsRadioButton.isSelected()) subd++;
        if (legtvRadioButton.isSelected() &&
                legtvUserField.getText() != null &&
                !legtvUserField.getText().isEmpty() &&
                legtvPasswordField.getPassword() != null) subd++;
        if (addic7edRadioButton.isSelected() &&
                addic7edUserField.getText() != null &&
                !addic7edUserField.getText().isEmpty() &&
                addic7edPasswordField.getPassword() != null) subd++;
        if (subd > 0) {
            if (!text.matches("\\d+(,\\d+){" + (subd - 1) + "}")) {
                JOptionPane.showMessageDialog(getFrame(),
                "Invalid order. String must be on the form i,j,...,n",
                "Error!", JOptionPane.ERROR_MESSAGE);
                failed = true;
            }
        } else {
            if (!text.isEmpty()) {
                JOptionPane.showMessageDialog(getFrame(),
                "Invalid order. Order must be empty because "
                        + "no subtitle downloaders were selected",
                "Error!", JOptionPane.ERROR_MESSAGE);
                failed = true;
            }
        }
        
        if (failed) {
            rollbackSettings();
        } else {
            frozeSettings();
            advancedSettingsDialog.setVisible(false);
            advancedSettingsDialog.dispose();
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        rollbackSettings();
        advancedSettingsDialog.setVisible(false);
        advancedSettingsDialog.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void advancedSettingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancedSettingsMenuItemActionPerformed
        advancedSettingsDialog.pack();
        advancedSettingsDialog.setLocationRelativeTo(getFrame());
        advancedSettingsDialog.setVisible(true);
    }//GEN-LAST:event_advancedSettingsMenuItemActionPerformed

    private void closeLogFrameMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeLogFrameMenuItemActionPerformed
        logFrame.setVisible(false);
        logFrame.dispose();
    }//GEN-LAST:event_closeLogFrameMenuItemActionPerformed

    private void clearLogFrameMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearLogFrameMenuItemActionPerformed
        logTextArea.setText("");
        logTextArea.repaint();
    }//GEN-LAST:event_clearLogFrameMenuItemActionPerformed

    private void severeLevelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_severeLevelCheckBoxMenuItemActionPerformed
        updateLevel(Level.SEVERE);
    }//GEN-LAST:event_severeLevelCheckBoxMenuItemActionPerformed

    private void warningLevelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warningLevelCheckBoxMenuItemActionPerformed
        updateLevel(Level.WARNING);
    }//GEN-LAST:event_warningLevelCheckBoxMenuItemActionPerformed

    private void infoLevelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoLevelCheckBoxMenuItemActionPerformed
        updateLevel(Level.INFO);
    }//GEN-LAST:event_infoLevelCheckBoxMenuItemActionPerformed

    private void configLevelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configLevelCheckBoxMenuItemActionPerformed
        updateLevel(Level.CONFIG);
    }//GEN-LAST:event_configLevelCheckBoxMenuItemActionPerformed

    private void fineLevelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fineLevelCheckBoxMenuItemActionPerformed
        updateLevel(Level.FINE);
    }//GEN-LAST:event_fineLevelCheckBoxMenuItemActionPerformed

    private void finerLevelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finerLevelCheckBoxMenuItemActionPerformed
        updateLevel(Level.FINER);
    }//GEN-LAST:event_finerLevelCheckBoxMenuItemActionPerformed

    private void finestLevelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finestLevelCheckBoxMenuItemActionPerformed
        updateLevel(Level.FINEST);
    }//GEN-LAST:event_finestLevelCheckBoxMenuItemActionPerformed

    private void priorityTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_priorityTextFieldActionPerformed
        String text = priorityTextField.getText();
        int subd = 0;
        if (tvsubsRadioButton.isSelected()) subd++;
        if (opensubsRadioButton.isSelected()) subd++;
        if (legtvRadioButton.isSelected() &&
                legtvUserField.getText() != null &&
                !legtvUserField.getText().isEmpty() &&
                legtvPasswordField.getPassword() != null) subd++;
        if (addic7edRadioButton.isSelected() &&
                addic7edUserField.getText() != null &&
                !addic7edUserField.getText().isEmpty() &&
                addic7edPasswordField.getPassword() != null) subd++;
        if (subd > 0) {
            if (!text.matches("\\d+(,\\d+){" + (subd - 1) + "}"))
                JOptionPane.showMessageDialog(getFrame(),
                "Invalid order. String must be on the form i,j,...,n",
                "Error!", JOptionPane.ERROR_MESSAGE);
            else {
                setPriorities(text);
            }
        } else {
            if (!text.isEmpty())
                JOptionPane.showMessageDialog(getFrame(),
                "Invalid order. Order must be empty because "
                        + "no subtitle downloaders were selected",
                "Error!", JOptionPane.ERROR_MESSAGE);
        }
        rollbackSettings();
    }//GEN-LAST:event_priorityTextFieldActionPerformed

    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
        getTheApplication().checkSubtitles();
    }//GEN-LAST:event_checkButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addLangButton;
    private javax.swing.JLabel addic7edPassLabel;
    private javax.swing.JPasswordField addic7edPasswordField;
    private javax.swing.JRadioButton addic7edRadioButton;
    private javax.swing.JTextField addic7edUserField;
    private javax.swing.JLabel addic7edUserLabel;
    private javax.swing.JDialog advancedSettingsDialog;
    private javax.swing.JMenuItem advancedSettingsMenuItem;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton checkButton;
    private javax.swing.JMenuItem clearLogFrameMenuItem;
    private javax.swing.JMenuItem closeLogFrameMenuItem;
    private javax.swing.JCheckBoxMenuItem configLevelCheckBoxMenuItem;
    private javax.swing.JMenu editLogFrameMenu;
    private javax.swing.JMenu extraMenu;
    private javax.swing.JMenu fileLogFrameMenu;
    private javax.swing.JLabel findLabel;
    private javax.swing.JCheckBoxMenuItem fineLevelCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem finerLevelCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem finestLevelCheckBoxMenuItem;
    private javax.swing.JCheckBox firstMatchCheckBox;
    private javax.swing.JCheckBoxMenuItem infoLevelCheckBoxMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JComboBox langsComboBox;
    private javax.swing.JScrollPane langsScrollPane;
    private javax.swing.JTable langsTable;
    private javax.swing.JLabel legtvPassLabel;
    private javax.swing.JPasswordField legtvPasswordField;
    private javax.swing.JRadioButton legtvRadioButton;
    private javax.swing.JTextField legtvUserField;
    private javax.swing.JLabel legtvUserLabel;
    private javax.swing.JMenu levelLogFrameMenu;
    private javax.swing.JFrame logFrame;
    private javax.swing.JMenuItem logMenuItem;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel nextCheckLabel;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JRadioButton opensubsRadioButton;
    private javax.swing.JLabel priorityLabel;
    private javax.swing.JTextField priorityTextField;
    private javax.swing.JButton removeLangButton;
    private javax.swing.JButton saveBrowseButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel saveToLabel;
    private javax.swing.JTextField saveToTextField;
    private javax.swing.JCheckBox searchAtStartupCheckBox;
    private javax.swing.JButton searchBrowseButton;
    private javax.swing.JLabel searchDirectoryLabel;
    private javax.swing.JTextField searchDirectoryTextField;
    private javax.swing.JLabel searchEpisodesLabel;
    private javax.swing.JLabel searchLangsLabel;
    private javax.swing.JLabel searchMinutesLabel;
    private javax.swing.JTextField searchMinutesTextField;
    private javax.swing.JLabel searchersLabel;
    private javax.swing.JCheckBoxMenuItem severeLevelCheckBoxMenuItem;
    private javax.swing.JRadioButton tvsubsRadioButton;
    private javax.swing.JCheckBoxMenuItem warningLevelCheckBoxMenuItem;
    // End of variables declaration//GEN-END:variables

    private JDialog aboutBox;

    private void frozeSettings() {
        getTheApplication().setTvsubsEnabled(tvsubsRadioButton.isSelected());
        getTheApplication().setOpensubsEnabled(
                opensubsRadioButton.isSelected());
        getTheApplication().setLegtvEnabled(legtvRadioButton.isSelected());
        if (legtvUserField.getText() != null &&
                !legtvUserField.getText().isEmpty())
            getTheApplication().setLegtvUser(legtvUserField.getText());
        if (legtvPasswordField.getPassword() != null &&
                !new String(legtvPasswordField.getPassword()).isEmpty())
            getTheApplication().setLegtvPwd(
                new String(legtvPasswordField.getPassword()));
        getTheApplication().setAddic7edEnabled(
                addic7edRadioButton.isSelected());
        if (addic7edUserField.getText() != null &&
                !addic7edUserField.getText().isEmpty())
            getTheApplication().setAddic7edUser(addic7edUserField.getText());
        if (addic7edPasswordField.getPassword() != null &&
                !new String(addic7edPasswordField.getPassword()).isEmpty())
            getTheApplication().setAddic7edPwd(
                new String(addic7edPasswordField.getPassword()));
        getTheApplication().setFirstMatch(firstMatchCheckBox.isSelected());
    }

    private void rollbackSettings() {
        tvsubsRadioButton.setSelected(
                getTheApplication().isTvsubsEnabled());
        opensubsRadioButton.setSelected(
                getTheApplication().isOpensubsEnabled());
        legtvRadioButton.setSelected(
                getTheApplication().isLegtvEnabled());
        String u = getTheApplication().getLegtvUser();
        String p = getTheApplication().getLegtvPwd();
        if (u != null && !u.isEmpty() && p != null && !p.isEmpty()) {
            legtvUserField.setText(u);
            legtvPasswordField.setText(p);
        }
        addic7edRadioButton.setSelected(
                getTheApplication().isAddic7edEnabled());
        u = getTheApplication().getAddic7edUser();
        p = getTheApplication().getAddic7edPwd();
        if (u != null && !u.isEmpty() && p != null && !p.isEmpty()) {
            addic7edUserField.setText(u);
            addic7edPasswordField.setText(p);
        }
        firstMatchCheckBox.setSelected(getTheApplication().isFirstMatch());

        priorityTextField.setText(getPriorities());
    }

    //////////////////////////////////////////////////////////////

    private void updateLevel(Level level) {        
        current = level;
        sh.setLevel(current);
        logger.setLevel(current);
        boolean /*all = (level == Level.ALL),*/
                severe = (level == Level.SEVERE),
                warning = (level == Level.WARNING),
                info = (level == Level.INFO),
                config = (level == Level.CONFIG),
                fine = (level == Level.FINE),
                finer = (level == Level.FINER),
                finest = (level == Level.FINEST),
                off = (level == Level.OFF);
        severeLevelCheckBoxMenuItem.setSelected(severe);
        warningLevelCheckBoxMenuItem.setSelected(warning);
        infoLevelCheckBoxMenuItem.setSelected(info);
        configLevelCheckBoxMenuItem.setSelected(config);
        fineLevelCheckBoxMenuItem.setSelected(fine);
        finerLevelCheckBoxMenuItem.setSelected(finer);
        finestLevelCheckBoxMenuItem.setSelected(finest);
        getTheApplication().setSubtitleLoggerLevel(current);
    }

    private int[] getPriorities(String text) {
        if (text.isEmpty()) return new int[0];
        String[] ss = text.split(",");
        int[] res = new int[ss.length];
        for (int i = 0; i < ss.length ; i++) res[i] = Integer.parseInt(ss[i]);
        return res;
    }

    private void setPriorities(String text) {
        int[] arr = getPriorities(text);
        int curr = 0;
        getTheApplication().setTvsubsPriority(arr[curr++]);
        getTheApplication().setOpensubsPriority(arr[curr++]);
        getTheApplication().setLegtvPriority(arr[curr++]);
        getTheApplication().setAddic7edPriority(arr[curr++]);
    }

    private String getPriorities() {
        StringBuilder builder = new StringBuilder();
        boolean append = false;
        if (getTheApplication().isTvsubsEnabled()) {
            builder.append(append ? "," : "").
                    append(getTheApplication().getTvsubsPriority());
            append = true;
        }
        if (getTheApplication().isOpensubsEnabled()) {
            builder.append(append ? "," : "").
                    append(getTheApplication().getOpensubsPriority());
            append = true;
        }
        if (getTheApplication().isLegtvEnabled()) {
            builder.append(append ? "," : "").
                    append(getTheApplication().getLegtvPriority());
            append = true;
        }
        if (getTheApplication().isAddic7edEnabled()) {
            builder.append(append ? "," : "").
                    append(getTheApplication().getAddic7edPriority());
            append = true;
        }
        return builder.toString();
    }
}
