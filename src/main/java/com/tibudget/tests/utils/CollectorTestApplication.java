package com.tibudget.tests.utils;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import com.tibudget.api.ICollectorPlugin;
import com.tibudget.api.exceptions.AccessDeny;
import com.tibudget.api.exceptions.CollectError;
import com.tibudget.api.exceptions.ConnectionFailure;
import com.tibudget.api.exceptions.ParameterError;
import com.tibudget.api.exceptions.TemporaryUnavailable;
import com.tibudget.dto.BankAccountDto;
import com.tibudget.dto.BankOperationDto;

public class CollectorTestApplication {

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
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CollectorTestApplication window = new CollectorTestApplication();
					window.frmTibudgetTestApplication.setVisible(true);
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
		frmTibudgetTestApplication.setBounds(100, 100, 450, 300);
		frmTibudgetTestApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		console = new JTextArea();
		
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
		
		accountsTable = new JTable();
		accountsTableModel = new DefaultTableModel(0, 2);
		accountsTableModel.setColumnIdentifiers(new Object[] {"Account", "Balance"});
		accountsTable.setModel(accountsTableModel);
		
		operationsTable = new JTable();
		operationsTableModel = new DefaultTableModel(0, 5);
		operationsTableModel.setColumnIdentifiers(new Object[] {"Type", "Date op", "Date value", "Label", "Amount"});
		operationsTable.setModel(operationsTableModel);
		
		progressBar = new JProgressBar();
		GroupLayout groupLayout = new GroupLayout(frmTibudgetTestApplication.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(console, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnOpen)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnReload)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnRun)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSettings)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(accountsTable, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(operationsTable, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)))
					.addGap(8))
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
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(accountsTable, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
						.addComponent(operationsTable, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(console, GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
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
		try {
			
			pluginInstance.collect(Collections.<BankAccountDto>emptyList());
			
			for (BankAccountDto account : pluginInstance.getBankAccounts()) {
				accountsTableModel.addRow(new Object[] {account.getTitle(), account.getCurrentBalance()});
			}
			
			for (BankOperationDto operation : pluginInstance.getBankOperations()) {
				operationsTableModel.addRow(new Object[] {
					operation.getType(),
					operation.getDateOperation(), 
					operation.getDateValue(),
					operation.getLabel(),
					operation.getValue()
				});
			}

		} catch (CollectError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessDeny e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemporaryUnavailable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionFailure e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParameterError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	private boolean loadPlugin(File archive) {
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
		}
		btnReload.setEnabled(isValid);
		return isValid;
	}

	public ICollectorPlugin getInstance() throws MalformedURLException {
		pluginLoader = ServiceLoader.load(ICollectorPlugin.class, getPluginClassLoader());
		return pluginLoader.iterator().next();
	}

	public URLClassLoader getPluginClassLoader() throws MalformedURLException {
		URLClassLoader classLoader = new URLClassLoader(new URL[]{pluginArchive.toURI().toURL()}, this.getClass().getClassLoader());
		return classLoader;
	}
}
