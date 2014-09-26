package com.tibudget.tests.utils;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
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
							.addComponent(accountsScrollPane, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(operationsScrollPane, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)))
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
						.addComponent(accountsScrollPane, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
						.addComponent(operationsScrollPane, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
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
							accounts.add(account);
						}
					}

					pluginInstance.collect(accounts);
					
					btnRun.setEnabled(true);
					btnOpen.setEnabled(true);
					btnReload.setEnabled(true);
					btnSettings.setEnabled(true);

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

				for (BankAccountDto account : pluginInstance.getBankAccounts()) {
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

				for (BankOperationDto operation : pluginInstance.getBankOperations()) {
					operationsTableModel.addRow(new Object[] { operation.getType(),
							operation.getDateOperation(), operation.getDateValue(),
							operation.getLabel(), operation.getValue() });
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
		URLClassLoader classLoader = new URLClassLoader(new URL[]{pluginArchive.toURI().toURL()}, this.getClass().getClassLoader());
		return classLoader;
	}
	
	static class DateRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;
		
		SimpleDateFormat formatter;
	    
		public DateRenderer() { super(); }

	    public void setValue(Object value) {
	        if (formatter==null) {
	            formatter = new SimpleDateFormat("yyyy - MM - dd");
	        }
	        setText((value == null) ? "" : formatter.format(value));
	    }
	}
	
	static class AmountRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;
		
		NumberFormat formatter;
	    
		public AmountRenderer() { super(); }

	    public void setValue(Object value) {
	        if (formatter==null) {
	        	formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
	        }
	        if (value instanceof Double) {
		        Double amount = (Double) value;
		        setText((value == null) ? "" : formatter.format(amount));
		        setHorizontalAlignment(RIGHT);
		        if (amount < 0.0) {
		        	setForeground(Color.RED);
		        }
		        else {
		        	setForeground(new Color(0, 83, 0));
		        }
	        }
	        else {
	        	setText(String.valueOf(value));
	        }
	    }
	}
}
