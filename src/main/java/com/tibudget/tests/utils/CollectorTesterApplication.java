package com.tibudget.tests.utils;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JProgressBar;

public class CollectorTesterApplication {

	private JFrame frmTibudgetTestApplication;
	private JTable accountsTable;
	private JTable operationsTable;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CollectorTesterApplication window = new CollectorTesterApplication();
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
	public CollectorTesterApplication() {
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
		
		JTextArea console = new JTextArea();
		
		JButton btnOpen = new JButton("Open");
		
		JButton btnReload = new JButton("Reload");
		btnReload.setEnabled(false);
		btnReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		JButton btnSettings = new JButton("Settings");
		btnSettings.setEnabled(false);
		
		accountsTable = new JTable();
		
		operationsTable = new JTable();
		
		JButton btnRun = new JButton("Run");
		btnRun.setEnabled(false);
		
		JProgressBar progressBar = new JProgressBar();
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
}
