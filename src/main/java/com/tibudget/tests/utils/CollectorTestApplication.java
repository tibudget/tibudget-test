package com.tibudget.tests.utils;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tibudget.api.ICollectorPlugin;
import com.tibudget.api.exceptions.AccessDeny;
import com.tibudget.api.exceptions.CollectError;
import com.tibudget.api.exceptions.ConnectionFailure;
import com.tibudget.api.exceptions.ParameterError;
import com.tibudget.api.exceptions.TemporaryUnavailable;
import com.tibudget.dto.BankAccountDto;
import com.tibudget.dto.BankOperationDto;

public class CollectorTestApplication {

	private static Logger LOG = LoggerFactory.getLogger(CollectorTestApplication.class);

	private JFrame frmTibudgetTestApplication;
	private JTable accountsTable;
	private JTable operationsTable;
	private DefaultTableModel accountsTableModel, operationsTableModel; 
	private JTextArea console;
	private JButton btnOpen;
	private JButton btnReload;
	private JButton btnSettings;
	private JButton btnRun;
	private JProgressBar progressBar;

	private File pluginArchive;
	
	private ICollectorPlugin pluginInstance;

	private ServiceLoader<ICollectorPlugin> pluginLoader;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CollectorTestApplication window = new CollectorTestApplication();
					window.frmTibudgetTestApplication.setVisible(true);
					if (args.length > 0) {
						LOG.info("Openning " + args[0]);
						window.loadPlugin(new File(args[0]));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CollectorTestApplication() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTibudgetTestApplication = new JFrame();
		frmTibudgetTestApplication.setTitle("ti'Budget test application");
		frmTibudgetTestApplication.setBounds(0, 0, 800, 600);
		frmTibudgetTestApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		console = new JTextArea(8, 0);
		console.append("Welcome to the collector tester, please click Open to select your collector jar (with all dependencies) or directory");
		JScrollPane scrollConsole = new JScrollPane(console);
		MessageConsole msgConsole = new MessageConsole(console);
		msgConsole.redirectOut();
		msgConsole.redirectErr(Color.RED, null);
        
		btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnOpenActionPerformed(e);
			}
		});
		
		btnReload = new JButton("Reload");
		btnReload.setEnabled(false);
		btnReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnReloadActionPerformed(e);
			}
		});
		
		btnSettings = new JButton("Settings");
		btnSettings.setEnabled(false);
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSettingsActionPerformed(e);
			}
		});
		
		btnRun = new JButton("Run");
		btnRun.setEnabled(false);
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRunActionPerformed(e);
			}
		});
		
		AmountRenderer amountRenderer = new AmountRenderer();
		DateRenderer dateRenderer = new DateRenderer();
		
		accountsTable = new JTable();
		accountsTableModel = new DefaultTableModel(0, 2);
		accountsTableModel.setColumnIdentifiers(new Object[] {"Account", "Balance"});
		accountsTable.setModel(accountsTableModel);
		JScrollPane accountsScrollPane = new JScrollPane(accountsTable);
		accountsTable.setFillsViewportHeight(true);
		accountsTable.getColumn("Balance").setCellRenderer(amountRenderer);
		
		operationsTable = new JTable();
		operationsTableModel = new DefaultTableModel(0, 5);
		operationsTableModel.setColumnIdentifiers(new Object[] {"Type", "Date op", "Date value", "Label", "Amount"});
		operationsTable.setModel(operationsTableModel);
		JScrollPane operationsScrollPane = new JScrollPane(operationsTable);
		operationsTable.setFillsViewportHeight(true);
		operationsTable.getColumn("Date op").setCellRenderer(dateRenderer);
		operationsTable.getColumn("Date value").setCellRenderer(dateRenderer);
		operationsTable.getColumn("Amount").setCellRenderer(amountRenderer);
		
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		
		JSplitPane horizSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		horizSplit.setDividerSize(5);
		horizSplit.setResizeWeight(0.3);
		horizSplit.setBorder(null);
		horizSplit.setLeftComponent(accountsScrollPane);
		horizSplit.setRightComponent(operationsScrollPane);

		JSplitPane vertSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		vertSplit.setDividerSize(5);
		vertSplit.setResizeWeight(0.7);
		vertSplit.setBorder(null);
		vertSplit.setTopComponent(horizSplit);
		vertSplit.setBottomComponent(scrollConsole);

		GroupLayout groupLayout = new GroupLayout(frmTibudgetTestApplication.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnOpen)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnReload)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnRun)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSettings)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
							.addGap(12))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(vertSplit, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
							.addContainerGap())))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnOpen, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnSettings, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnReload, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnRun, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(vertSplit, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addContainerGap())
		);
		frmTibudgetTestApplication.getContentPane().setLayout(groupLayout);
	}
	
	private void btnOpenActionPerformed(ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar files", "jar");
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = chooser.showOpenDialog(frmTibudgetTestApplication);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			loadPlugin(chooser.getSelectedFile());
		}
	}

	private void btnReloadActionPerformed(ActionEvent evt) {
		loadPlugin(pluginArchive);
	}
	
	private void btnRunActionPerformed(ActionEvent evt) {
		Runnable collectRun = new Runnable() {
			public void run() {
				try {
					btnRun.setEnabled(false);
					btnOpen.setEnabled(false);
					btnReload.setEnabled(false);
					btnSettings.setEnabled(false);
					
					List<BankAccountDto> accounts = new ArrayList<BankAccountDto>();
					if (pluginInstance.getBankAccounts() != null) {
						for (BankAccountDto account : pluginInstance.getBankAccounts()) {
							if (account != null) {
								accounts.add(account);
							}
						}
					}

					pluginInstance.collect(accounts);

				} catch (CollectError e) {
					console.append("Exception "+e.getClass().getName() +" thrown: " + e.getMessage() + "\n");
					printStacktrace(e.getCause() != null ? e.getCause() : e);
				} catch (AccessDeny e) {
					console.append("Exception "+e.getClass().getName() +" thrown: " + e.getMessage() + "\n");
					printStacktrace(e.getCause() != null ? e.getCause() : e);
				} catch (TemporaryUnavailable e) {
					console.append("Exception "+e.getClass().getName() +" thrown: " + e.getMessage() + "\n");
					printStacktrace(e.getCause() != null ? e.getCause() : e);
				} catch (ConnectionFailure e) {
					console.append("Exception "+e.getClass().getName() +" thrown: " + e.getMessage() + "\n");
					printStacktrace(e.getCause() != null ? e.getCause() : e);
				} catch (ParameterError e) {
					console.append("Exception "+e.getClass().getName() +" thrown: " + e.getMessage() + "\n");
					printStacktrace(e.getCause() != null ? e.getCause() : e);
				} finally {
					btnRun.setEnabled(true);
					btnOpen.setEnabled(true);
					btnReload.setEnabled(true);
					btnSettings.setEnabled(true);
				}
			}
		};

		final Thread collectThread = new Thread(collectRun);

		Runnable collectDisplay = new Runnable() {
			public void run() {
				
				progressBar.setValue(0);
				progressBar.setIndeterminate(true);
				progressBar.setStringPainted(true);

				while (collectThread.isAlive()) {
					progressBar.setValue(pluginInstance.getProgress());
					progressBar.setIndeterminate(false);
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						// ignore
					}
				}

				progressBar.setValue(pluginInstance.getProgress());

				if (pluginInstance.getBankAccounts() != null) {
					for (BankAccountDto account : pluginInstance.getBankAccounts()) {
						if (account == null) {
							console.append("ERROR: your collector returned a null account in getBankAccounts()\n");
							continue;
						}
						// Find account in already fetched accounts
						int rowIndex = -1;
						for (int i = 0; i < accountsTableModel.getRowCount(); i++) {
							Object name = accountsTableModel.getValueAt(i, 0);
							if (name != null && name.equals(account.getTitle())) {
								rowIndex = i;
								break;
							}
						}
						if (rowIndex < 0) {
							// Add account if not already in the list
							accountsTableModel.addRow(new Object[] {account.getTitle(), account.getCurrentBalance()});
						}
						else {
							// Update account balance if already in the list
							accountsTableModel.setValueAt(account.getCurrentBalance(), rowIndex, 1);
						}
					}
				}
				else {
					console.append("ERROR: your collector returned a null in getBankAccounts()\n");
				}

				if (pluginInstance.getBankOperations() != null) {
					for (BankOperationDto operation : pluginInstance.getBankOperations()) {
						if (operation == null) {
							console.append("ERROR: your collector returned a null operation in getBankOperations()\n");
							continue;
						}
						operationsTableModel.addRow(new Object[] { operation.getType(),
								operation.getDateOperation(), operation.getDateValue(),
								operation.getLabel(), operation.getValue() });
					}
				}
				else {
					console.append("ERROR: your collector returned a null in getBankOperations()\n");
				}
			}
		};

		final Thread displayThread = new Thread(collectDisplay);

		collectThread.start();
		displayThread.start();
	}

	private void btnSettingsActionPerformed(java.awt.event.ActionEvent evt) {
		if (pluginInstance != null) {
			SettingsDialog settingsDialog = new SettingsDialog(pluginInstance);
			settingsDialog.setListener(new SettingsDialog.SettingsChangeListener() {
				
				@Override
				public void onValueChange(boolean pluginInstanceValidated) {
					btnRun.setEnabled(pluginInstanceValidated);
				}
			});
			settingsDialog.setVisible(true);
		}
	}

	public boolean loadPlugin(File archive) {
		boolean isValid = false;
		pluginArchive = archive;
		try {
			pluginInstance = getInstance();
			isValid = true;
		} catch (MalformedURLException e) {
			console.append(e.getMessage());
			console.append("\n");
		}
		if (isValid) {

			// Enable / disable UI elements
			btnReload.setEnabled(false);
			btnRun.setEnabled(false);
			btnSettings.setEnabled(true);

			// Clear console
			console.setText("");
			
			// Clear tables
			int rowCount = accountsTableModel.getRowCount();
			//Remove rows one by one from the end of the table
			for (int i = rowCount - 1; i >= 0; i--) {
				accountsTableModel.removeRow(i);
			}
			rowCount = operationsTableModel.getRowCount();
			//Remove rows one by one from the end of the table
			for (int i = rowCount - 1; i >= 0; i--) {
				operationsTableModel.removeRow(i);
			}
			
			// Clear progress bar
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
		}
		btnReload.setEnabled(isValid);
		return isValid;
	}

	public ICollectorPlugin getInstance() throws MalformedURLException {
		pluginLoader = ServiceLoader.load(ICollectorPlugin.class, getPluginClassLoader());
		return pluginLoader.iterator().next();
	}

	public URLClassLoader getPluginClassLoader() throws MalformedURLException {
		URLClassLoader thisClassloader = (URLClassLoader)this.getClass().getClassLoader();
		List<URL> urlsToKeep = new ArrayList<URL>();
		for (URL url : thisClassloader.getURLs()) {
			if (url.getFile().endsWith(".jar")) {
				urlsToKeep.add(url);
			}
		}
		urlsToKeep.add(pluginArchive.toURI().toURL());
		for (URL url : urlsToKeep) {
			LOG.info("Adding " + url.toString() + " to classpath");
		}
		URLClassLoader classLoader = new URLClassLoader(urlsToKeep.toArray(new URL[0]), getClass().getClassLoader());
		return classLoader;
	}
	
	public void printStacktrace(Throwable e) {
		console.append(e.getClass().getName() + ": " + e.getMessage() + "\n");
		for (StackTraceElement element : e.getStackTrace()) {
			console.append("   " + element.toString() + "\n");
		}
	}
}
