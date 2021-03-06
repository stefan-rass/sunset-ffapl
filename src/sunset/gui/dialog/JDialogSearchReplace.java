package sunset.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.JCheckBox;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.SwingConstants;

import sunset.gui.interfaces.IProperties;
import sunset.gui.listener.ActionListenerCloseWindow;
import sunset.gui.search.SearchReplaceCoordinator;
import sunset.gui.search.interfaces.ISearchReplaceDialog;
import sunset.gui.search.interfaces.ISearchReplaceCoordinator;
import sunset.gui.search.interfaces.ISearchReplaceShowDialog;
import sunset.gui.search.listener.ActionListenerFindString;
import sunset.gui.search.listener.ActionListenerReplaceAll;
import sunset.gui.search.listener.ActionListenerReplaceString;
import sunset.gui.search.listener.ActionListenerOpenSettingsDialog;
import sunset.gui.search.listener.ActionListenerRegexConverter;
import sunset.gui.tabbedpane.JTabbedPaneNamed;
import sunset.gui.util.DialogFocusTraversalPolicy;
import sunset.gui.util.TranslateGUIElements;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.awt.Font;
import java.awt.HeadlessException;
import java.util.Vector;
import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class JDialogSearchReplace extends JDialog implements ISearchReplaceDialog, ISearchReplaceShowDialog {

	private final JPanel contentPanel = new JPanel();
	private JTabbedPaneNamed jTabbedPaneNamed_main;
	private JTextField jTextField_searchtext;
	private JTextField jTextField_replacetext;
	private JButton jButton_find;
	private JButton jButton_replace;
	private JButton jButton_replaceall;
	private JButton jButton_converttoregex;
	private JButton jButton_settings;
	private JButton jButton_cancel;
	private JButton jButton_help;
	private JLabel jLabel_searchfor;
	private JLabel jLabel_replacewith;
	private Vector<Component> replaceComp = new Vector<Component>();
	private JCheckBox chckbxMatchCase;
	private JCheckBox chckbxWrapAround;
	private JCheckBox chckbxReplaceAllFromStart;
	private JRadioButton rdbtnStandardSearch;
	private JRadioButton rdbtnAdvancedSearch;
	private JRadioButton rdbtnRegularExpression;
	private JCheckBox chckbxUseSpecialSymbols;
	private JCheckBox chckbxShowBalancingErrors;
	private JCheckBox chckbxDotMatchNewLine;
	private JLabel jLabel_status;
	private JPanel panelOptions;
	private JPanel panelMode;

	/**
	 * Create the dialog.
	 */
	public JDialogSearchReplace(JFrame frame) {
		super(frame);
		setResizable(false);
		setFont(new Font("Dialog", Font.PLAIN, 10));
		initGUI();
		initListener();
		setInitialStatus();
	}
	
	private void initGUI() {
		setName("dialog_searchreplace");		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setPreferredSize(new Dimension(500, 350));
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			jTabbedPaneNamed_main = new JTabbedPaneNamed();
			contentPanel.add(jTabbedPaneNamed_main);
			{
				JPanel panelSearchReplace = new JPanel();				
				panelSearchReplace.setLayout(new BorderLayout(0, 0));
				
				JPanel panelSearchReplaceMain = new JPanel();
				panelSearchReplace.add(panelSearchReplaceMain, BorderLayout.CENTER);
				panelSearchReplaceMain.setLayout(null);
				
				panelOptions = new JPanel();
				
				panelMode = new JPanel();
				panelMode.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Search Mode", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
				panelMode.setName("panel_mode");
				panelMode.setLayout(null);
				
				panelMode.setBounds(10, 140, 148, 100);
				
				panelOptions.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
				panelOptions.setName("panel_options");
				panelOptions.setLayout(null);
				
				panelOptions.setBounds(160, 139, 190, 100);
				
				jLabel_searchfor = new JLabel("Search for:");
				jLabel_searchfor.setBounds(10, 10, 74, 26);
				jLabel_searchfor.setHorizontalAlignment(SwingConstants.LEFT);
				jLabel_searchfor.setName("label_searchfor");
				panelSearchReplaceMain.add(jLabel_searchfor);
				
				jTextField_searchtext = new JTextField();
				jTextField_searchtext.setBounds(90, 10, 260, 26);
				jTextField_searchtext.setColumns(10);
				panelSearchReplaceMain.add(jTextField_searchtext);
				
				jLabel_replacewith = new JLabel("Replace with:");
				jLabel_replacewith.setBounds(10, 46, 74, 26);
				jLabel_replacewith.setHorizontalAlignment(SwingConstants.LEFT);
				jLabel_replacewith.setName("label_replacewith");
				panelSearchReplaceMain.add(jLabel_replacewith);
				replaceComp.add(jLabel_replacewith);
				
				jTextField_replacetext = new JTextField();
				jTextField_replacetext.setBounds(90, 46, 260, 26);
				jTextField_replacetext.setColumns(10);
				panelSearchReplaceMain.add(jTextField_replacetext);

				replaceComp.add(jTextField_replacetext);
				
				{
					jButton_find = new JButton("Find Next");
					jButton_find.setBounds(360, 13, 101, 21);
					jButton_find.setName("button_find");
					panelSearchReplaceMain.add(jButton_find);
					getRootPane().setDefaultButton(jButton_find);
				}
				
				{
					jButton_replace = new JButton("Replace");
					jButton_replace.setBounds(360, 49, 101, 21);
					jButton_replace.setName("button_replace");
					panelSearchReplaceMain.add(jButton_replace);
					replaceComp.add(jButton_replace);
				}
				
				{					
					JPanel panelReplaceAll = new JPanel();
					panelReplaceAll.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
					panelReplaceAll.setBounds(260, 80, 210, 40);
					panelReplaceAll.setLayout(null);
					
					chckbxReplaceAllFromStart = new JCheckBox("From start");
					chckbxReplaceAllFromStart.setBounds(10, 10, 80, 21);
					chckbxReplaceAllFromStart.setName("chckbx_replaceallfromstart");
					panelReplaceAll.add(chckbxReplaceAllFromStart);
					
					jButton_replaceall = new JButton("Replace All");
					jButton_replaceall.setBounds(100, 9, 101, 21);
					jButton_replaceall.setName("button_replaceall");
					panelReplaceAll.add(jButton_replaceall);
					
					panelSearchReplaceMain.add(panelReplaceAll);
					replaceComp.add(panelReplaceAll);
				}
				
				{
					jButton_converttoregex = new JButton(">> RegEx");
					jButton_converttoregex.setBounds(360, 182, 101, 21);
					panelSearchReplaceMain.add(jButton_converttoregex);
				}
				
				{
					jButton_settings = new JButton("Settings");
					jButton_settings.setBounds(360, 218, 101, 21);
					jButton_settings.setName("button_settings");
					panelSearchReplaceMain.add(jButton_settings);
				}
				
				chckbxMatchCase = new JCheckBox("Match case");
				chckbxMatchCase.setBounds(10, 85, 174, 21);
				chckbxMatchCase.setName("chckbx_matchcase");
				panelSearchReplaceMain.add(chckbxMatchCase);
				
				chckbxWrapAround = new JCheckBox("Wrap around");
				chckbxWrapAround.setBounds(10, 107, 174, 21);
				chckbxWrapAround.setName("chckbx_wraparound");
				panelSearchReplaceMain.add(chckbxWrapAround);
				
				chckbxUseSpecialSymbols = new JCheckBox("Use symbols: \\r, \\n, \\t");
				chckbxUseSpecialSymbols.setBounds(10, 20, 174, 21);
				chckbxUseSpecialSymbols.setName("chckbx_handleescapes");
				panelOptions.add(chckbxUseSpecialSymbols);
				
				chckbxShowBalancingErrors = new JCheckBox("Show balancing errors");
				chckbxShowBalancingErrors.setBounds(10, 42, 174, 21);
				chckbxShowBalancingErrors.setName("chckbx_showbalancingerror");
				panelOptions.add(chckbxShowBalancingErrors);

				chckbxDotMatchNewLine = new JCheckBox(". matches newline");
				chckbxDotMatchNewLine.setBounds(10, 64, 174, 21);
				chckbxDotMatchNewLine.setName("chckbx_dotall");
				panelOptions.add(chckbxDotMatchNewLine);
				
				rdbtnStandardSearch = new JRadioButton("Standard search");
				rdbtnStandardSearch.setBounds(10, 20, 120, 21);
				rdbtnStandardSearch.setName("rdbtn_standardsearch");
				panelMode.add(rdbtnStandardSearch);
				
				rdbtnAdvancedSearch = new JRadioButton("Advanced search");
				rdbtnAdvancedSearch.setBounds(10, 42, 108, 21);
				rdbtnAdvancedSearch.setName("rdbtn_advancedsearch");
				panelMode.add(rdbtnAdvancedSearch);
				
				rdbtnRegularExpression = new JRadioButton("Regular expression");
				rdbtnRegularExpression.setBounds(10, 64, 120, 21);
				rdbtnRegularExpression.setName("rdbtn_regularexpression");
				panelMode.add(rdbtnRegularExpression);
				
				ButtonGroup bgModes = new ButtonGroup();
				bgModes.add(rdbtnStandardSearch);
				bgModes.add(rdbtnAdvancedSearch);
				bgModes.add(rdbtnRegularExpression);
				
				panelSearchReplaceMain.add(panelOptions);
				panelSearchReplaceMain.add(panelMode);
				
				jButton_help = new JButton("");
				jButton_help.setIcon((new ImageIcon(getClass().getClassLoader().getResource(
						IProperties.IMAGEPATH + "info16.png"))));
				jButton_help.setOpaque(false);
				jButton_help.setContentAreaFilled(false);
				jButton_help.setBorderPainted(false);
				jButton_help.setBounds(120, 42, 27, 21);
				panelMode.add(jButton_help);
				
				jTabbedPaneNamed_main.addTab("Search", null, 
						panelSearchReplace, "Search", 
						"tabbedPane_tabsearch");
				jTabbedPaneNamed_main.setEnabledAt(0, true);
				
				jTabbedPaneNamed_main.addTab("Replace", null, 
						jTabbedPaneNamed_main.getTabComponentAt(0), "Replace", 
						"tabbedPane_tabreplace");
				jTabbedPaneNamed_main.setEnabledAt(1, true);
			}
		}
		{
			JPanel panelButton = new JPanel();
			getContentPane().add(panelButton, BorderLayout.SOUTH);
			panelButton.setLayout(new BorderLayout(0, 0));
			
			JPanel panelControl = new JPanel();
			panelButton.add(panelControl, BorderLayout.EAST);
			
			jButton_cancel = new JButton("Cancel");
			jButton_cancel.setName("button_cancel");
			panelControl.add(jButton_cancel);
			
			JPanel panelStatus = new JPanel();
			panelButton.add(panelStatus, BorderLayout.WEST);
			
			jLabel_status = new JLabel("Status");
			jLabel_status.setHorizontalAlignment(SwingConstants.CENTER);
			panelStatus.add(jLabel_status);
		}
	}
	
	private void initListener() {
		jButton_cancel.addActionListener(new ActionListenerCloseWindow(this));
		
		ISearchReplaceCoordinator coordinator = new SearchReplaceCoordinator(this);
		jButton_find.addActionListener(new ActionListenerFindString(coordinator));
		jButton_replace.addActionListener(new ActionListenerReplaceString(coordinator));
		jButton_replaceall.addActionListener(new ActionListenerReplaceAll(coordinator));
		jButton_converttoregex.addActionListener(new ActionListenerRegexConverter(coordinator));
		jButton_settings.addActionListener(new ActionListenerOpenSettingsDialog(this));
		jButton_help.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream isHTML = getClass().getClassLoader().getResourceAsStream(IProperties.SEARCHHELPPATH + "SunsetSearchHelp.html");
					InputStream isCSS = getClass().getClassLoader().getResourceAsStream(IProperties.SEARCHHELPPATH + "SunsetSearchHelp.css");
					
					File htmlFile = new File(System.getProperty("java.io.tmpdir") + "/SunsetSearchHelp.html");
					File cssFile = new File(System.getProperty("java.io.tmpdir") + "/SunsetSearchHelp.css");
					
					byte[] buffer = new byte[isHTML.available()];
				    isHTML.read(buffer);
				    OutputStream outStream = new FileOutputStream(htmlFile);
				    outStream.write(buffer);
				    outStream.close();

				    buffer = new byte[isCSS.available()];
				    isCSS.read(buffer);
				    outStream = new FileOutputStream(cssFile);
				    outStream.write(buffer);
				    outStream.close();
				    
				    htmlFile.deleteOnExit();
				    cssFile.deleteOnExit();
					Desktop.getDesktop().browse(htmlFile.toURI());
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "Failed to open help file", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			
		});
		
		this.addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		    	jTextField_searchtext.requestFocusInWindow();
		    }
		});
		
		jTabbedPaneNamed_main.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int selectedIndex = jTabbedPaneNamed_main.getSelectedIndex();
				boolean bReplaceTabSelected = jTabbedPaneNamed_main
						.getTabNameAt(selectedIndex).equals("tabbedPane_tabreplace");
				
				for (Component comp : replaceComp) {
					comp.setVisible(bReplaceTabSelected);
				}
				
				setComponentFocusOrder(bReplaceTabSelected);
			}
	    });
		
		ActionListener radioButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chckbxUseSpecialSymbols.setEnabled(!rdbtnRegularExpression.isSelected());
				chckbxDotMatchNewLine.setEnabled(rdbtnRegularExpression.isSelected());
				chckbxShowBalancingErrors.setEnabled(rdbtnAdvancedSearch.isSelected());
				jButton_converttoregex.setEnabled(rdbtnAdvancedSearch.isSelected());
			}
		};
		
		rdbtnStandardSearch.addActionListener(radioButtonListener);
		rdbtnAdvancedSearch.addActionListener(radioButtonListener);
		rdbtnRegularExpression.addActionListener(radioButtonListener);
		
		this.addEscapeListener(this);
	}
	
	private void setInitialStatus() {
		rdbtnStandardSearch.setSelected(true);
		chckbxShowBalancingErrors.setEnabled(false);
		chckbxDotMatchNewLine.setEnabled(false);
		jButton_converttoregex.setEnabled(false);
	}
	
	private void addEscapeListener(final JDialog dialog) {
	    ActionListener escListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
	    };

	    dialog.getRootPane().registerKeyboardAction(escListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);

	}
	
	private void translate() {
		TranslateGUIElements.translateButton(jButton_find);
		TranslateGUIElements.translateButton(jButton_replace);
		TranslateGUIElements.translateButton(jButton_replaceall);
		TranslateGUIElements.translateButton(jButton_settings);
		TranslateGUIElements.translateButton(jButton_cancel);
		TranslateGUIElements.translateLabel(jLabel_searchfor);
		TranslateGUIElements.translateLabel(jLabel_replacewith);
		TranslateGUIElements.translateCheckbox(chckbxMatchCase);
		TranslateGUIElements.translateCheckbox(chckbxWrapAround);
		TranslateGUIElements.translateCheckbox(chckbxReplaceAllFromStart);
		TranslateGUIElements.translateRadioButton(rdbtnStandardSearch);
		TranslateGUIElements.translateRadioButton(rdbtnAdvancedSearch);
		TranslateGUIElements.translateRadioButton(rdbtnRegularExpression);
		TranslateGUIElements.translateCheckbox(chckbxUseSpecialSymbols);
		TranslateGUIElements.translateCheckbox(chckbxShowBalancingErrors);
		TranslateGUIElements.translateCheckbox(chckbxDotMatchNewLine);
		TranslateGUIElements.translatePanel(panelOptions);
		TranslateGUIElements.translatePanel(panelMode);
		TranslateGUIElements.translateTappedPane(jTabbedPaneNamed_main);
		TranslateGUIElements.translateDialog(this);
	}
	
	private void setComponentFocusOrder(boolean isReplace){
		Vector<Component> order = new Vector<Component>();
        order.add(jTextField_searchtext);
        if (isReplace) {
        	order.add(jTextField_replacetext);
        }
        order.add(chckbxMatchCase);
        order.add(chckbxWrapAround);
        order.add(rdbtnStandardSearch);
        order.add(chckbxUseSpecialSymbols);
        order.add(jButton_find);
        if (isReplace) {
        	order.add(jButton_replace);
        	order.add(chckbxReplaceAllFromStart);
            order.add(jButton_replaceall);
        }
        order.add(jButton_settings);
        order.add(jButton_cancel);
        
		FocusTraversalPolicy focusTraversalPolicy = new DialogFocusTraversalPolicy(order);
		this.setFocusTraversalPolicy(focusTraversalPolicy);
	}
	
	@Override
	public void prepareAndShowDialog(boolean isReplace, JFrame owner) {
		jTabbedPaneNamed_main.setSelectedIndex(isReplace ? 1 : 0);
		
		for (Component comp : replaceComp) {
			comp.setVisible(isReplace);
		}
		
		pack();
		this.setLocationRelativeTo(owner);
		jLabel_status.setText("");
		translate();
		setComponentFocusOrder(isReplace);
		setVisible(true);
	}

	@Override
	public String searchPattern() {
		return jTextField_searchtext.getText();
	}

	@Override
	public String replaceText() {
		return jTextField_replacetext.getText();
	}

	@Override
	public void setStatus(String status, Color color) {
		jLabel_status.setText(status);
		jLabel_status.setForeground(color);
		
		if (status.length() > 75) {
			JOptionPane.showMessageDialog(this, status, "Information", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public boolean matchCase() {
		return chckbxMatchCase.isSelected();
	}

	@Override
	public boolean wrapAround() {
		return chckbxWrapAround.isSelected();
	}

	@Override
	public boolean replaceAllFromStart() {
		return chckbxReplaceAllFromStart.isSelected();
	}

	@Override
	public boolean useRegEx() {
		return rdbtnRegularExpression.isSelected();
	}

	@Override
	public boolean dotMatchesNewLine() {
		return chckbxDotMatchNewLine.isSelected();
	}
	
	@Override
	public boolean useSpecialSymbols() {
		return chckbxUseSpecialSymbols.isSelected();
	}
	
	@Override
	public boolean showBalancingError() {
		return chckbxShowBalancingErrors.isSelected();
	}

	@Override
	public boolean useAdvancedSearch() {
		return rdbtnAdvancedSearch.isSelected();
	}

	@Override
	public void setSearchPattern(String pattern) {
		jTextField_searchtext.setText(pattern);
	}
	
	@Override
	public void changeToRegexSearch() {
		rdbtnRegularExpression.setSelected(true);
		chckbxUseSpecialSymbols.setEnabled(false);
		chckbxShowBalancingErrors.setEnabled(false);
		jButton_converttoregex.setEnabled(false);
		chckbxDotMatchNewLine.setEnabled(true);
		chckbxDotMatchNewLine.setSelected(true);
	}
	
	@Override
	public void enableDisableButtons(boolean enable) {
		jButton_find.setEnabled(enable);
		jButton_replace.setEnabled(enable);
		jButton_replaceall.setEnabled(enable);
	}

	public void setReplaceText(String text) {
		jTextField_replacetext.setText(text);
	}

	public void setMatchCase(boolean value) {
		chckbxMatchCase.setSelected(value);
	}

	public void setWrapAround(boolean value) {
		chckbxWrapAround.setSelected(value);
	}
	
	public void setReplaceAllFromStart(boolean value) {
		chckbxReplaceAllFromStart.setSelected(value);
	}

	public void setUseRegEx(boolean value) {
		rdbtnRegularExpression.setSelected(value);
	}

	public void setDotMatchesNewLine(boolean value) {
		chckbxDotMatchNewLine.setSelected(value);
	}
	
	public void setUseSpecialSymbols(boolean value) {
		chckbxUseSpecialSymbols.setSelected(value);
	}
	
	public void setShowBalancingError(boolean value) {
		chckbxShowBalancingErrors.setSelected(value);
	}

	public void setUseAdvancedSearch(boolean value) {
		rdbtnAdvancedSearch.setSelected(value);
	}
	
	public void setUseStandardSearch(boolean value) {
		rdbtnStandardSearch.setSelected(value);
	}
	
	public String getStatus() {
		return jLabel_status.getText();
	}
}