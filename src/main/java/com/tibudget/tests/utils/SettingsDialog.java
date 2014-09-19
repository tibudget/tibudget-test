package com.tibudget.tests.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.tibudget.api.ICollectorPlugin;
import com.tibudget.api.Input;
import com.tibudget.api.exceptions.AccessDeny;
import com.tibudget.api.exceptions.CollectError;
import com.tibudget.api.exceptions.ConnectionFailure;
import com.tibudget.api.exceptions.ParameterError;
import com.tibudget.api.exceptions.TemporaryUnavailable;
import com.tibudget.dto.BankAccountDto;
import com.tibudget.dto.BankOperationDto;
import com.tibudget.dto.MessageDto;
import com.tibudget.dto.MessagesDto;

public class SettingsDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private static Logger LOG = LoggerFactory.getLogger(CollectorSettingsDialog.class);
	private JTextField textField;

	private static class InputElement implements Comparable<InputElement> {
		int order = -1;
		Component component;

		public InputElement(int order, Component component) {
			super();
			this.order = order;
			this.component = component;
		}

		@Override
		public int compareTo(InputElement o) {
			return getOrder() - o.getOrder();
		}

		public int getOrder() {
			return this.order;
		}

		public Component getComponent() {
			return this.component;
		}
	}

	private static class Fieldset implements Comparable<Fieldset> {
		List<InputElement> elements;
		boolean displayed;
		String name;

		public Fieldset(String name) {
			super();
			this.name = name;
			this.displayed = true;
			this.elements = new ArrayList<InputElement>();
		}

		@Override
		public int compareTo(Fieldset o) {
			return o != null ? getMinOrder() - o.getMinOrder() : -1;
		}

		@Override
		public int hashCode() {
			return this.name != null ? this.name.hashCode() : super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this.name != null && obj != null && obj instanceof Fieldset) {
				return this.name.equals(((Fieldset) obj).getName());
			}
			return super.equals(obj);
		}

		public List<InputElement> getElements() {
			return this.elements;
		}

		public int getMinOrder() {
			int min = Integer.MAX_VALUE;
			if (this.elements != null) {
				for (InputElement element : this.elements) {
					min = Math.min(min, element.order);
				}
			}
			return min;
		}

		public String getName() {
			return this.name;
		}

		public boolean isDisplayed() {
			return this.displayed;
		}

		public void setDisplayed(boolean displayed) {
			this.displayed = displayed;
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SettingsDialog dialog = new SettingsDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public enum Type {
		UN, DEUX, TROIS
	}

	/**
	 * Create the dialog.
	 */
	public SettingsDialog() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		Component form = generateForm("foo", new ICollectorPlugin() {
			@Input(required=true, order=7)
			private boolean enabled;
			
			@Input(required=true, fieldset="type_UN", order=5)
			private char separator;

			@Input(required=true, fieldset="type_DEUX", order=4)
			private File theFile;

			@Input(required=true, password=true, order=2)
			private String password;

			@Input(required=true, order=1)
			private String identifier;

			@Input(required=true, order=6)
			private BankAccountDto account;
			
			@Input(required=true, order=3)
			private Type type;

			@Override
			public Collection<MessageDto> validate() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getProgress() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Iterable<BankOperationDto> getBankOperations() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Iterable<BankAccountDto> getBankAccounts() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void collect(Iterable<BankAccountDto> existingBankAccounts)
					throws CollectError, AccessDeny, TemporaryUnavailable,
					ConnectionFailure, ParameterError {
				// TODO Auto-generated method stub
				
			}
		}, ResourceBundle.getBundle("messages"), new MessagesDto(), Collections.<BankAccountDto>emptyList());
		contentPanel.add(form, "4, 10, fill, fill");

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		pack();
	}

	public Component generateForm(String id, ICollectorPlugin collector, ResourceBundle bundle, MessagesDto messages,
			List<BankAccountDto> accounts) {
		Map<String, Fieldset> fieldsets = new HashMap<String, Fieldset>();
		for (Field field : collector.getClass().getDeclaredFields()) {
			Input inputAnnotation = field.getAnnotation(Input.class);
			if (inputAnnotation != null) {
				Component component = null;
				if (field.getType().isEnum()) {
					component = generateInputEnum(collector, field, inputAnnotation, bundle, fieldsets, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == int.class) {
					component = generateInputInt(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == boolean.class) {
					component = generateInputBoolean(collector, field, inputAnnotation, bundle, fieldsets, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == String.class) {
					component = generateInputString(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == char.class) {
					component = generateInputChar(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == File.class) {
					component = generateInputFile(field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == BankAccountDto.class) {
					component = generateInputBankAccount(field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()), accounts);
				} else {
					continue;
				}
				if (component != null) {
					Fieldset fieldset = getOrCreate(fieldsets, inputAnnotation.fieldset());
					fieldset.getElements().add(new InputElement(inputAnnotation.order(), component));
				}
			}
		}
		List<Fieldset> fsList = new ArrayList<Fieldset>(fieldsets.values());
		Collections.sort(fsList);
		JPanel formPanel = new JPanel(new GridLayout(fsList.size(), 1));
		for (Fieldset fieldset : fsList) {
			if (fieldset.getElements().size() > 0) {
				JPanel fieldsetPanel = new JPanel(new GridLayout(fieldset.getElements().size(), 1));
				if (fieldset.getName().length() > 0) {
					String title = null;
					try {
						title = getMessage(bundle, "form.fieldset." + fieldset.getName(), null);
					} catch (MissingResourceException e) {
						// Ignore
					}
					fieldsetPanel.setVisible(fieldset.isDisplayed());
					if (title != null && title.length() > 0) {
						fieldsetPanel.add(new JLabel(title));
					}
				}
				Collections.sort(fieldset.getElements());
				for (InputElement inputElement : fieldset.getElements()) {
					if (inputElement.getComponent() != null) {
						fieldsetPanel.add(inputElement.getComponent(), BorderLayout.NORTH);
					}
				}
				formPanel.add(fieldsetPanel, BorderLayout.CENTER);
			}
		}
		return formPanel;
	}

	private Object getHtmlGetterValue(ICollectorPlugin collector, Field field) {
		Object result = null;
		try {
			field.setAccessible(true);
			result = field.get(collector);
			if (result != null && result.getClass() == Boolean.class) {
				if ((Boolean) result) {
					result = " checked";
				} else {
					result = "";
				}
			}
		} catch (IllegalArgumentException e) {
			LOG.debug("Cannot get " + field.toGenericString() + ": IllegalArgumentException ->" + e.getMessage());
		} catch (IllegalAccessException e) {
			LOG.debug("Cannot get " + field.toGenericString() + ": IllegalAccessException ->" + e.getMessage());
		}
		return result != null ? result : "";
	}

	private String normalizeFieldsetName(String name) {
		String id = name.replaceAll("[^a-zA-Z_]", "").trim();
		if (id.length() == 0) {
			id = "fs" + name.hashCode();
		}
		return id;
	}

	private Fieldset getOrCreate(Map<String, Fieldset> fieldsets, String name) {
		Fieldset fieldset = fieldsets.get(name);
		if (fieldset == null) {
			fieldset = new Fieldset(name);
			fieldsets.put(fieldset.getName(), fieldset);
		}
		return fieldset;
	}

	private Color getColor(Iterable<MessageDto> messages, Color defaultColor) {
		Color color = defaultColor;
		if (messages != null) {
			for (MessageDto messageDto : messages) {
				if (messageDto.getType() == MessageDto.MessageType.ERROR) {
					color = Color.RED;
					break;
				} else if (messageDto.getType() == MessageDto.MessageType.WARN) {
					color = Color.ORANGE;
				} else if (messageDto.getType() == MessageDto.MessageType.SUCCESS) {
					if (!"warning".equals(color)) {
						color = Color.GREEN;
					}
				} else if (messageDto.getType() == MessageDto.MessageType.INFO) {
					if (!"success".equals(color) && !"warning".equals(color)) {
						color = Color.CYAN;
					}
				}
			}
		}
		return color;
	}

	private void addAlerts(JPanel panel, ResourceBundle bundle, Iterable<MessageDto> messages) {
		if (messages != null) {
			for (MessageDto messageDto : messages) {
				JLabel lblNewLabel;
				try {
					lblNewLabel = new JLabel(MessageFormat.format(getMessage(bundle, messageDto.getMessageKey()), messageDto.getMessageArguments()));
				} catch (NullPointerException e) {
					lblNewLabel = new JLabel("???null???");
				} catch (ClassCastException e) {
					lblNewLabel = new JLabel("!!"+messageDto.getMessageKey()+"!!");
				} catch (IllegalArgumentException e) {
					lblNewLabel = new JLabel("!!!"+messageDto.getMessageKey()+"!!!");
				}
				switch (messageDto.getType()) {
				case INFO:
					lblNewLabel.setBackground(Color.CYAN);
					break;
				case WARN:
					lblNewLabel.setBackground(Color.ORANGE);
					break;
				case SUCCESS:
					lblNewLabel.setBackground(Color.GREEN);
					break;
				case ERROR:
				default:
					lblNewLabel.setBackground(Color.RED);
					break;
				}
				panel.add(lblNewLabel, "2, 2");
			}
		}
	}

	private Component generateInputBoolean(ICollectorPlugin collector, Field field, Input inputAnnotation, ResourceBundle bundle,
			Map<String, Fieldset> fieldsets, Iterable<MessageDto> messages) {
		JPanel panel = new JPanel();
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		addAlerts(panel, bundle, messages);

		JCheckBox checkBox = new JCheckBox(getMessage(bundle, "form.label." + field.getName()));
		checkBox.setSelected(String.valueOf(getHtmlGetterValue(collector, field)).equals("true"));
		panel.add(checkBox, "4, 2");
		checkBox.setForeground(getColor(messages, Color.BLACK));

		JLabel lblNewLabel_1 = new JLabel(getMessage(bundle, "form.tooltip." + field.getName()));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.ITALIC, 11));
		panel.add(lblNewLabel_1, "4, 4");

		return panel;
	}

	private Component generateInputInt(ICollectorPlugin collector, Field field, Input inputAnnotation, ResourceBundle bundle,
			Iterable<MessageDto> messages) {
		JPanel panel = new JPanel();
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		addAlerts(panel, bundle, messages);
		
		JLabel lblNewLabel = new JLabel(getMessage(bundle, "form.label." + field.getName()));
		panel.add(lblNewLabel, "2, 2");
		lblNewLabel.setForeground(getColor(messages, Color.BLACK));

		JTextField textField = new JTextField(String.valueOf(getHtmlGetterValue(collector, field)));
		textField.setBackground(Color.WHITE);
		panel.add(textField, "4, 2");
		textField.setColumns(10);
		textField.setForeground(getColor(messages, Color.BLACK));

		JLabel lblNewLabel_1 = new JLabel(getMessage(bundle, "form.tooltip." + field.getName()));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.ITALIC, 11));
		panel.add(lblNewLabel_1, "4, 4");

		return panel;
	}

	private Component generateInputEnum(ICollectorPlugin collector, Field field, Input inputAnnotation, ResourceBundle bundle,
			Map<String, Fieldset> fieldsets, Iterable<MessageDto> messages) {
		
		JPanel panel = new JPanel();
		FormLayout layout = new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow")},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC});
		panel.setLayout(layout);
		
		addAlerts(panel, bundle, messages);
		
		JLabel lblNewLabel = new JLabel(getMessage(bundle, "form.label." + field.getName()));
		panel.add(lblNewLabel, "2, 2");
		lblNewLabel.setForeground(getColor(messages, Color.BLACK));

		ButtonGroup group = new ButtonGroup();
		int rowIndex = 2;
		for (Object enumConstant : field.getType().getEnumConstants()) {
			JRadioButton jRadioButton = new JRadioButton(getMessage(bundle, "form.label." + field.getName() + "." + enumConstant.toString()));
			jRadioButton.setSelected(enumConstant.equals(getHtmlGetterValue(collector, field)));
			group.add(jRadioButton);
			if (rowIndex > 2) {
				layout.appendRow(FormFactory.RELATED_GAP_ROWSPEC);
				layout.appendRow(FormFactory.DEFAULT_ROWSPEC);
			}
			panel.add(new JLabel(), "2, "+rowIndex);
			panel.add(jRadioButton, "4, "+rowIndex);
			rowIndex += 2;
		}

		layout.appendRow(FormFactory.RELATED_GAP_ROWSPEC);
		layout.appendRow(FormFactory.DEFAULT_ROWSPEC);
		JLabel lblNewLabel_1 = new JLabel(getMessage(bundle, "form.tooltip." + field.getName()));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.ITALIC, 11));
		panel.add(lblNewLabel_1, "4, "+rowIndex);

		return panel;
	}

	private Component generateInputString(ICollectorPlugin collector, Field field, Input inputAnnotation, ResourceBundle bundle, Iterable<MessageDto> messages) {
		JPanel panel = new JPanel();
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		addAlerts(panel, bundle, messages);
		
		JLabel lblNewLabel = new JLabel(getMessage(bundle, "form.label." + field.getName()));
		panel.add(lblNewLabel, "2, 2");
		lblNewLabel.setForeground(getColor(messages, Color.BLACK));

		JTextField textField;
		if (inputAnnotation.password()) {
			textField = new JPasswordField();
		} else {
			textField = new JTextField(String.valueOf(getHtmlGetterValue(collector, field)));
		}
		textField.setBackground(Color.WHITE);
		textField.setForeground(getColor(messages, Color.BLACK));
		panel.add(textField, "4, 2");

		JLabel lblNewLabel_1 = new JLabel(getMessage(bundle, "form.tooltip." + field.getName()));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.ITALIC, 11));
		panel.add(lblNewLabel_1, "4, 4");

		return panel;
	}

	private Component generateInputChar(ICollectorPlugin collector, Field field, Input inputAnnotation, ResourceBundle bundle,
			Iterable<MessageDto> messages) {
		JPanel panel = new JPanel();
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		addAlerts(panel, bundle, messages);
		
		JLabel lblNewLabel = new JLabel(getMessage(bundle, "form.label." + field.getName()));
		panel.add(lblNewLabel, "2, 2");
		lblNewLabel.setForeground(getColor(messages, Color.BLACK));

		JTextField textField = new JTextField(String.valueOf(getHtmlGetterValue(collector, field)));
		textField.setBackground(Color.WHITE);
		textField.setForeground(getColor(messages, Color.BLACK));
		panel.add(textField, "4, 2");

		JLabel lblNewLabel_1 = new JLabel(getMessage(bundle, "form.tooltip." + field.getName()));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.ITALIC, 11));
		panel.add(lblNewLabel_1, "4, 4");

		return panel;
	}

	private Component generateInputFile(Field field, Input inputAnnotation, ResourceBundle bundle, Iterable<MessageDto> messages) {
		JPanel panel = new JPanel();
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		addAlerts(panel, bundle, messages);
		
		JLabel lblNewLabel = new JLabel(getMessage(bundle, "form.label." + field.getName()));
		panel.add(lblNewLabel, "2, 2");
		lblNewLabel.setForeground(getColor(messages, Color.BLACK));

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar files", "jar");
				chooser.setFileFilter(filter);
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int returnVal = chooser.showOpenDialog(contentPanel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// TODO
				}
			}
		});
		panel.add(btnBrowse, "4, 2");

		JLabel lblNewLabel_1 = new JLabel(getMessage(bundle, "form.tooltip." + field.getName()));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.ITALIC, 11));
		panel.add(lblNewLabel_1, "4, 4");

		return panel;
	}

	private Component generateInputBankAccount(Field field, Input inputAnnotation, ResourceBundle bundle, Iterable<MessageDto> messages,
			List<BankAccountDto> accounts) {
		JPanel panel = new JPanel();
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		addAlerts(panel, bundle, messages);
		
		JLabel lblNewLabel = new JLabel(getMessage(bundle, "form.label." + field.getName()));
		panel.add(lblNewLabel, "2, 2");
		lblNewLabel.setForeground(getColor(messages, Color.BLACK));

		JComboBox<BankAccountDto> list = new JComboBox<BankAccountDto>(accounts.toArray(new BankAccountDto[0]));
		panel.add(list, "4, 2");

		JLabel lblNewLabel_1 = new JLabel(getMessage(bundle, "form.tooltip." + field.getName()));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.ITALIC, 11));
		panel.add(lblNewLabel_1, "4, 4");

		return panel;
	}

	private String getMessage(ResourceBundle bundle, String key, String defaultValue) {
		String msg = "";
		try {
			msg = bundle.getString(key);
		} catch (MissingResourceException e) {
			msg = defaultValue;
		}
		return msg;
	}

	private String getMessage(ResourceBundle bundle, String key) {
		return getMessage(bundle, key, "???" + key + "???");
	}

	public boolean hasFileInput(ICollectorPlugin collector) {
		for (Field field : collector.getClass().getDeclaredFields()) {
			Input inputAnnotation = field.getAnnotation(Input.class);
			if (inputAnnotation != null) {
				if (field.getType() == File.class) {
					return true;
				}
			}
		}
		return false;
	}

}
