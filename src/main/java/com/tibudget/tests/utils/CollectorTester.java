package com.tibudget.tests.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.tibudget.api.ICollectorPlugin;

public class CollectorTester extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;

	private File pluginArchive;

	private ServiceLoader<ICollectorPlugin> pluginLoader;
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JMenuItem aboutMenuItem;
	private javax.swing.JTable accountsTable;
	private javax.swing.JTextArea console;
	private javax.swing.JMenuItem contentsMenuItem;
	private javax.swing.JMenuItem exitMenuItem;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane5;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JMenuItem openMenuItem;
	private javax.swing.JTable operationsTable;
	private javax.swing.JProgressBar progressBar;
	private javax.swing.JButton reloadButton;
	private javax.swing.JButton runButton;
	private javax.swing.JButton settingsButton;
	private javax.swing.JMenuItem settingsMenuItem;
	// End of variables declaration
	//GEN-END:variables

	public CollectorTester() {
		initComponents();
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		operationsTable = new javax.swing.JTable();
		jScrollPane2 = new javax.swing.JScrollPane();
		accountsTable = new javax.swing.JTable();
		reloadButton = new javax.swing.JButton();
		runButton = new javax.swing.JButton();
		progressBar = new javax.swing.JProgressBar();
		jScrollPane5 = new javax.swing.JScrollPane();
		console = new javax.swing.JTextArea();
		settingsButton = new javax.swing.JButton();
		menuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		openMenuItem = new javax.swing.JMenuItem();
		settingsMenuItem = new javax.swing.JMenuItem();
		exitMenuItem = new javax.swing.JMenuItem();
		helpMenu = new javax.swing.JMenu();
		contentsMenuItem = new javax.swing.JMenuItem();
		aboutMenuItem = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("ti'Budget collector tester");

		operationsTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][] { { null, null, null, null }, { null, null, null, null },
				{ null, null, null, null }, { null, null, null, null } }, new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
		jScrollPane1.setViewportView(operationsTable);

		accountsTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][] { { null, null, null, null }, { null, null, null, null },
				{ null, null, null, null }, { null, null, null, null } }, new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
		jScrollPane2.setViewportView(accountsTable);

		reloadButton.setText("Reload");
		reloadButton.setEnabled(false);

		runButton.setText("Run");
		runButton.setEnabled(false);

		progressBar.setFocusable(false);

		console.setColumns(20);
		console.setEditable(false);
		console.setRows(5);
		jScrollPane5.setViewportView(console);

		settingsButton.setText("Settings");
		settingsButton.setEnabled(false);
		settingsButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				settingsButtonActionPerformed(evt);
			}
		});

		fileMenu.setText("File");

		openMenuItem.setText("Open");
		openMenuItem.setToolTipText("Open a collector directory or jar");
		openMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(openMenuItem);

		settingsMenuItem.setText("Settings");
		settingsMenuItem.setEnabled(false);
		settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				settingsMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(settingsMenuItem);

		exitMenuItem.setText("Exit");
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exitMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

		helpMenu.setText("Help");

		contentsMenuItem.setText("Contents");
		helpMenu.add(contentsMenuItem);

		aboutMenuItem.setText("About");
		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 692,
												Short.MAX_VALUE)
										.addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 692,
												Short.MAX_VALUE)
										.addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 692,
												Short.MAX_VALUE)
										.addGroup(
												javax.swing.GroupLayout.Alignment.LEADING,
												layout.createSequentialGroup()
														.addComponent(reloadButton)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(runButton)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 335, Short.MAX_VALUE)
														.addComponent(settingsButton))).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(reloadButton).addComponent(runButton)
										.addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(settingsButton)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE).addContainerGap()));

		pack();
	}// </editor-fold>
	//GEN-END:initComponents

	private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
		System.exit(0);
	}//GEN-LAST:event_exitMenuItemActionPerformed

	private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar files", "jar");
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			loadPlugin(chooser.getSelectedFile());
		}
	}

	private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		CollectorSettingsDialog settingsDialog = new CollectorSettingsDialog(this, true);
		settingsDialog.setVisible(true);
	}

	private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {
		CollectorSettingsDialog settingsDialog = new CollectorSettingsDialog(this, true);
		settingsDialog.setVisible(true);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new CollectorTester().setVisible(true);
			}
		});
	}

	private boolean loadPlugin(File archive) {
		boolean isValid = true;
		if (isValid) {
			pluginArchive = archive;

			// Enable / disable UI elements
			reloadButton.setEnabled(false);
			runButton.setEnabled(false);
			settingsButton.setEnabled(true);
			settingsMenuItem.setEnabled(true);

			// Clear console
			console.setText("");
		}
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