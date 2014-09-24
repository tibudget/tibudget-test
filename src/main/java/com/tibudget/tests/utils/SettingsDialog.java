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
import java.util.Locale;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
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

	private static final long serialVersionUID = 1L;

	private static Logger LOG = LoggerFactory.getLogger(SettingsDialog.class);

	private final Map<String, Fieldset> fieldsets = new HashMap<String, Fieldset>();
	
	private final ICollectorPlugin pluginInstance;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SettingsDialog dialog = new SettingsDialog(new ICollectorPlugin() {
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
					return null;
				}
				
				@Override
				public int getProgress() {
					return 0;
				}
				
				@Override
				public Iterable<BankOperationDto> getBankOperations() {
					return null;
				}
				
				@Override
				public Iterable<BankAccountDto> getBankAccounts() {
					return null;
				}
				
				@Override
				public void collect(Iterable<BankAccountDto> existingBankAccounts)
						throws CollectError, AccessDeny, TemporaryUnavailable,
						ConnectionFailure, ParameterError {
				}
			});
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
	public SettingsDialog(final ICollectorPlugin pluginInstance) {
		this.pluginInstance = pluginInstance;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel(); 
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		ResourceBundle bundle;
		try {
			bundle = ResourceBundle.getBundle("messages", Locale.ENGLISH, this.pluginInstance.getClass().getClassLoader());
		}
		catch (MissingResourceException e) {
			bundle = ResourceBundle.getBundle("test_collector_messages", Locale.ENGLISH, this.pluginInstance.getClass().getClassLoader());
		}
		
		Component form = generateForm("foo", this.pluginInstance, bundle, new MessagesDto(), Collections.<BankAccountDto>emptyList());

		contentPanel.add(form, "4, 10, fill, fill");

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				validatePluginInstance();
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

		pack();
	}
	
	public void validatePluginInstance() {
		Collection<MessageDto> msg = pluginInstance.validate();
		for (MessageDto messageDto : msg) {
			
		}
	}

	public Component generateForm(String id, ICollectorPlugin collector, ResourceBundle bundle, MessagesDto messages,
			List<BankAccountDto> accounts) {
		for (Field field : collector.getClass().getDeclaredFields()) {
			Input inputAnnotation = field.getAnnotation(Input.class);
			if (inputAnnotation != null) {
				InputElement component = null;
				if (field.getType().isEnum()) {
					component = generateInputEnum(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == int.class) {
					component = generateInputInt(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == boolean.class) {
					component = generateInputBoolean(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == String.class) {
					component = generateInputString(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == char.class) {
					component = generateInputChar(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == File.class) {
					component = generateInputFile(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == BankAccountDto.class) {
					component = generateInputBankAccount(field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()), accounts);
				} else {
					continue;
				}
				if (component != null) {
					Fieldset fieldset = getOrCreateFieldset(inputAnnotation.fieldset());
					component.setOrder(inputAnnotation.order());
					fieldset.getElements().add(component);
				}
			}
		}
		List<Fieldset> fsList = new ArrayList<Fieldset>(fieldsets.values());
		Collections.sort(fsList);
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(""));
        builder.appendColumn("right:pref");
        builder.appendColumn("5dlu");
        builder.appendColumn("fill:max(pref; 100px)");
        builder.appendColumn("5dlu");
        
		for (Fieldset fieldset : fsList) {
			if (fieldset.getElements().size() > 0) {
				Collections.sort(fieldset.getElements());
				fieldset.setSeparator(builder.appendSeparator(normalizeFieldsetName(fieldset.getName())));
				for (InputElement inputElement : fieldset.getElements()) {
					if (inputElement.getComponent() != null) {
						builder.append(inputElement.getLabel(), inputElement.getComponent());
						builder.nextLine();
						if (inputElement.getTooltip() != null) {
							JLabel tooltip = inputElement.getTooltip();
							tooltip.setFont(new Font("Tahoma", Font.ITALIC, 11));
							builder.append("", tooltip);
							builder.nextLine();
						}
					}
				}
				// Init display state
				showFieldSet(normalizeFieldsetName(fieldset.getName()), fieldset.isDisplayed());
			}
		}
		
		return builder.getPanel();
	}
	
	private void showFieldSet(String fieldsetName, boolean show) {
		Fieldset fieldset = fieldsets.get(fieldsetName);
		if (fieldset != null) {
			for (InputElement element: fieldset.getElements()) {
				if (element.getLabel() != null) element.getLabel().setVisible(show);
				if (element.getComponent() != null) element.getComponent().setVisible(show);
				if (element.getTooltip() != null) element.getTooltip().setVisible(show);
			}
			if (fieldset.getSeparator() != null) {
				fieldset.getSeparator().setVisible(show);
			}
			pack();
		}
	}

	private <T> T getValue(ICollectorPlugin collector, Field field, T defaultValue) {
		T result = null;
		try {
			field.setAccessible(true);
			result = (T) field.get(collector);
		} catch (IllegalArgumentException e) {
			LOG.debug("Cannot get " + field.toGenericString() + ": IllegalArgumentException -> " + e.getMessage());
		} catch (IllegalAccessException e) {
			LOG.debug("Cannot get " + field.toGenericString() + ": IllegalAccessException -> " + e.getMessage());
		}
		return result != null ? result : defaultValue;
	}
	
	private <T> void setValue(ICollectorPlugin collector, Field field, T value) {
		try {
			field.setAccessible(true);
			if (value != null && field.getType() == String.class && !(value instanceof String)) {
				field.set(collector, String.valueOf(value));
			}
			else if (value != null && field.getType() == char.class && value instanceof String) {
				if (((String)value).length() > 0) {
					field.set(collector, ((String)value).charAt(0));
				}
				else {
					field.set(collector, '\u0000');
				}
			}
			else {
				field.set(collector, value);
			}
			LOG.debug(field.toGenericString() + " = " + getValue(collector, field, null));
		} catch (IllegalArgumentException e) {
			LOG.debug("Cannot set " + field.toGenericString() + ": IllegalArgumentException -> " + e.getMessage());
		} catch (IllegalAccessException e) {
			LOG.debug("Cannot set " + field.toGenericString() + ": IllegalAccessException -> " + e.getMessage());
		}
	}

	private Fieldset getOrCreateFieldset(String name) {
		Fieldset fieldset = fieldsets.get(name);
		if (fieldset == null) {
			fieldset = new Fieldset(name);
			fieldsets.put(fieldset.getName(), fieldset);
		}
		return fieldset;
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

	private InputElement generateInputBoolean(final ICollectorPlugin collector, final Field field, Input inputAnnotation, ResourceBundle bundle,
			Iterable<MessageDto> messages) {

		JCheckBox checkBox = new JCheckBox(getMessage(bundle, "form.label." + field.getName()));
		checkBox.setSelected(getValue(collector, field, false));
		checkBox.setForeground(getColor(messages, Color.BLACK));
		checkBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setValue(collector, field, ((JCheckBox) e.getSource()).isSelected());
			}
		});

		InputElement element = new InputElement(checkBox);
		element.setLabel(new JLabel(""));
		element.setTooltip(new JLabel(getMessage(bundle, "form.tooltip." + field.getName(), null)));
		return element;
	}

	private InputElement generateInputInt(final ICollectorPlugin collector, final Field field, Input inputAnnotation, ResourceBundle bundle,
			Iterable<MessageDto> messages) {

		JTextField textField = new JTextField(getValue(collector, field, 0));
		textField.setBackground(Color.WHITE);
		textField.setColumns(10);
		textField.setForeground(getColor(messages, Color.BLACK));
		textField.getDocument().addDocumentListener(new DocumentListener() {

			private void update(DocumentEvent e) {
				try {
					setValue(collector, field, Integer.parseInt(e.getDocument().getText(0, e.getDocument().getLength())));
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update(e);
			}
		});

		InputElement element = new InputElement(textField);
		element.setLabel(new JLabel(getMessage(bundle, "form.label." + field.getName())));
		element.setTooltip(new JLabel(getMessage(bundle, "form.tooltip." + field.getName(), null)));
		return element;
	}

	private InputElement generateInputEnum(final ICollectorPlugin collector, final Field field, Input inputAnnotation, ResourceBundle bundle, Iterable<MessageDto> messages) {
		
		final JPanel panel = new JPanel(new GridLayout(field.getType().getEnumConstants().length, 1));
		ButtonGroup group = new ButtonGroup();
		int rowIndex = 2;
		for (final Object enumConstant : field.getType().getEnumConstants()) {
			JRadioButton jRadioButton = new JRadioButton(getMessage(bundle, "form.label." + field.getName() + "." + enumConstant.toString()));
			Fieldset fieldset = getOrCreateFieldset(normalizeFieldsetName(field.getName() + "_" + enumConstant.toString()));
			try {
				field.setAccessible(true);
				if (enumConstant.equals(field.get(collector))) {
					jRadioButton.setSelected(true);
					fieldset.setDisplayed(true);
				} else {
					fieldset.setDisplayed(false);
				}
			} catch (IllegalArgumentException e) {
				LOG.debug("Cannot get " + field.toGenericString() + ": IllegalArgumentException ->" + e.getMessage());
			} catch (IllegalAccessException e) {
				LOG.debug("Cannot get " + field.toGenericString() + ": IllegalAccessException ->" + e.getMessage());
			}
			group.add(jRadioButton);
			panel.add(new JLabel(), "2, "+rowIndex);
			panel.add(jRadioButton, "4, "+rowIndex);
			rowIndex += 2;
			jRadioButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					for (Object enumConstant2 : field.getType().getEnumConstants()) {
						if (!enumConstant.equals(enumConstant2)) {
							showFieldSet(normalizeFieldsetName(field.getName() + "_" + enumConstant2.toString()), false);
						}
					}
					showFieldSet(normalizeFieldsetName(field.getName() + "_" + enumConstant.toString()), true);
					setValue(collector, field, enumConstant);
				}
			});
		}

		InputElement element = new InputElement(panel);
		element.setLabel(new JLabel(getMessage(bundle, "form.label." + field.getName())));
		element.setTooltip(new JLabel(getMessage(bundle, "form.tooltip." + field.getName(), null)));
		return element;
	}

	private InputElement generateInputString(final ICollectorPlugin collector, final Field field, Input inputAnnotation, ResourceBundle bundle, Iterable<MessageDto> messages) {

		JTextField textField;
		if (inputAnnotation.password()) {
			textField = new JPasswordField();
		} else {
			textField = new JTextField(getValue(collector, field, ""));
		}
		textField.setBackground(Color.WHITE);
		textField.setForeground(getColor(messages, Color.BLACK));
		textField.getDocument().addDocumentListener(new DocumentListener() {

			private void update(DocumentEvent e) {
				try {
					setValue(collector, field, e.getDocument().getText(0, e.getDocument().getLength()));
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update(e);
			}
		});

		InputElement element = new InputElement(textField);
		element.setLabel(new JLabel(getMessage(bundle, "form.label." + field.getName())));
		element.setTooltip(new JLabel(getMessage(bundle, "form.tooltip." + field.getName(), null)));
		return element;
	}

	private InputElement generateInputChar(final ICollectorPlugin collector, final Field field, Input inputAnnotation, ResourceBundle bundle,
			Iterable<MessageDto> messages) {

		JTextField textField = new JTextField(this.<Character>getValue(collector, field, null));
		textField.setBackground(Color.WHITE);
		textField.setForeground(getColor(messages, Color.BLACK));
		textField.getDocument().addDocumentListener(new DocumentListener() {

			private void update(DocumentEvent e) {
				try {
					setValue(collector, field, e.getDocument().getText(0, e.getDocument().getLength()));
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update(e);
			}
		});
		InputElement element = new InputElement(textField);
		element.setLabel(new JLabel(getMessage(bundle, "form.label." + field.getName())));
		element.setTooltip(new JLabel(getMessage(bundle, "form.tooltip." + field.getName(), null)));
		return element;
	}

	private InputElement generateInputFile(final ICollectorPlugin collector, final Field field, Input inputAnnotation, ResourceBundle bundle, Iterable<MessageDto> messages) {

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar files", "jar");
				chooser.setFileFilter(filter);
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int returnVal = chooser.showOpenDialog(getContentPane());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					setValue(collector, field, chooser.getSelectedFile());
				}
			}
		});

		InputElement element = new InputElement(btnBrowse);
		element.setLabel(new JLabel(getMessage(bundle, "form.label." + field.getName())));
		element.setTooltip(new JLabel(getMessage(bundle, "form.tooltip." + field.getName(), null)));
		return element;
	}

	private InputElement generateInputBankAccount(Field field, Input inputAnnotation, ResourceBundle bundle, Iterable<MessageDto> messages,
			List<BankAccountDto> accounts) {

		JComboBox<BankAccountDto> list = new JComboBox<BankAccountDto>(accounts.toArray(new BankAccountDto[0]));

		InputElement element = new InputElement(list);
		element.setLabel(new JLabel(getMessage(bundle, "form.label." + field.getName())));
		element.setTooltip(new JLabel(getMessage(bundle, "form.tooltip." + field.getName(), null)));
		return element;
	}

	private static String getMessage(ResourceBundle bundle, String key, String defaultValue) {
		String msg = "";
		try {
			msg = bundle.getString(key);
		} catch (MissingResourceException e) {
			msg = defaultValue;
		}
		return msg;
	}

	private static String getMessage(ResourceBundle bundle, String key) {
		return getMessage(bundle, key, "???" + key + "???");
	}

	private static String normalizeFieldsetName(String name) {
		String id = name.replaceAll("[^a-zA-Z_]", "").trim();
		if (id.length() == 0) {
			id = "fs" + name.hashCode();
		}
		return id;
	}

	private static Color getColor(Iterable<MessageDto> messages, Color defaultColor) {
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

}
