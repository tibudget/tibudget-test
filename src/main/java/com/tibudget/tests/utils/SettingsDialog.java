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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

	private final JPanel contentPanel = new JPanel(new GridLayout(1, 1));

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

	/**
	 * Create the dialog.
	 */
	public SettingsDialog() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		Component form = generateForm("foo", new ICollectorPlugin() {
			@Input(required=true)
			private boolean enabled;
			
			@Input(required=true)
			private char separator;

			@Input(required=true)
			private File theFile;

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
		contentPanel.add(form);

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

	}

	public Component generateForm(String id, ICollectorPlugin collector, ResourceBundle bundle, MessagesDto messages,
			List<BankAccountDto> accounts) {
		Map<String, Fieldset> fieldsets = new HashMap<String, Fieldset>();
		for (Field field : collector.getClass().getDeclaredFields()) {
			Input inputAnnotation = field.getAnnotation(Input.class);
			if (inputAnnotation != null) {
				Component component = null;
				if (field.getType().isEnum()) {
					generateInputEnum(collector, field, inputAnnotation, bundle, fieldsets, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == int.class) {
					generateInputInt(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == boolean.class) {
					generateInputBoolean(collector, field, inputAnnotation, bundle, fieldsets, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == String.class) {
					generateInputString(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == char.class) {
					component = generateInputChar(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == File.class) {
					component = generateInputFile(field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == BankAccountDto.class) {
					generateInputBankAccount(field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()), accounts);
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
				formPanel.add(fieldsetPanel, BorderLayout.NORTH);
			}
		}
		return formPanel;
	}

	private static Object getHtmlGetterValue(ICollectorPlugin collector, Field field) {
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

	private static String normalizeFieldsetName(String name) {
		String id = name.replaceAll("[^a-zA-Z_]", "").trim();
		if (id.length() == 0) {
			id = "fs" + name.hashCode();
		}
		return id;
	}

	private static Fieldset getOrCreate(Map<String, Fieldset> fieldsets, String name) {
		Fieldset fieldset = fieldsets.get(name);
		if (fieldset == null) {
			fieldset = new Fieldset(name);
			fieldsets.put(fieldset.getName(), fieldset);
		}
		return fieldset;
	}

	private static String getCssClass(Iterable<MessageDto> messages) {
		String cssClass = "";
		if (messages != null) {
			for (MessageDto messageDto : messages) {
				if (messageDto.getType() == MessageDto.MessageType.ERROR) {
					cssClass = "error";
					break;
				} else if (messageDto.getType() == MessageDto.MessageType.WARN) {
					cssClass = "warning";
				} else if (messageDto.getType() == MessageDto.MessageType.SUCCESS) {
					if (!"warning".equals(cssClass)) {
						cssClass = "success";
					}
				} else if (messageDto.getType() == MessageDto.MessageType.INFO) {
					if (!"success".equals(cssClass) && !"warning".equals(cssClass)) {
						cssClass = "info";
					}
				}
			}
		}
		return cssClass;
	}

	private static String getHtmlAlerts(ResourceBundle bundle, Iterable<MessageDto> messages) {
		StringBuffer buffer = new StringBuffer();
		if (messages != null) {
			for (MessageDto messageDto : messages) {
				buffer.append("<div class=\"alert alert-");
				switch (messageDto.getType()) {
				case INFO:
					buffer.append("info");
					break;
				case WARN:
					buffer.append("warning");
					break;
				case SUCCESS:
					buffer.append("success");
					break;
				case ERROR:
				default:
					buffer.append("error");
					break;
				}
				buffer.append("\"><button type=\"button\" class=\"close\" data-dismiss=\"alert\">×</button>");
				try {
					buffer.append(MessageFormat.format(getMessage(bundle, messageDto.getMessageKey()), messageDto.getMessageArguments()));
				} catch (NullPointerException e) {
					buffer.append("???null???");
				} catch (ClassCastException e) {
					buffer.append("!!");
					buffer.append(messageDto.getMessageKey());
					buffer.append("!!");
				} catch (IllegalArgumentException e) {
					buffer.append("!!!");
					buffer.append(messageDto.getMessageKey());
					buffer.append("!!!");
				}
				buffer.append("</div>");
			}
		}
		return buffer.toString();
	}

	private static String generateInputBoolean(ICollectorPlugin collector, Field field, Input inputAnnotation, ResourceBundle bundle,
			Map<String, Fieldset> fieldsets, Iterable<MessageDto> messages) {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(getHtmlAlerts(bundle, messages));
		buffer.append("<div class=\"form-group ");
		buffer.append(getCssClass(messages));
		buffer.append("\"><label class=\"control-label");
		if (inputAnnotation.required()) {
			buffer.append(" required");
		}
		buffer.append("\" for=\"_id");
		buffer.append(field.getName());
		buffer.append("\">");
		buffer.append(getMessage(bundle, "form.label." + field.getName()));
		buffer.append("</label><input class=\"form-control\" type=\"checkbox\" name=\"");
		buffer.append(field.getName());
		buffer.append("\" id=\"_id");
		buffer.append(field.getName());
		buffer.append("\" value=\"checked\"");
		Object resultValue = getHtmlGetterValue(collector, field);
		buffer.append(resultValue);
		if (inputAnnotation.hideFieldset().length > 0 || inputAnnotation.showFieldset().length > 0) {
			buffer.append(" onclick=\"");
			for (String fs : inputAnnotation.hideFieldset()) {
				Fieldset fieldset = getOrCreate(fieldsets, fs);
				fieldset.setDisplayed(!" checked".equals(resultValue));
				if (fs.length() > 0) {
					buffer.append("$('#idfs");
					buffer.append(normalizeFieldsetName(fs));
					buffer.append("').fadeToggle();");
				}
			}
			for (String fs : inputAnnotation.showFieldset()) {
				Fieldset fieldset = getOrCreate(fieldsets, fs);
				fieldset.setDisplayed(" checked".equals(resultValue));
				if (fs.length() > 0) {
					buffer.append("$('#idfs");
					buffer.append(normalizeFieldsetName(fs));
					buffer.append("').fadeToggle();");
				}
			}
			buffer.append("\"");
		}
		buffer.append("/>");
		if (bundle.containsKey("form.tooltip." + field.getName())) {
			buffer.append("<p class=\"help-block\">");
			buffer.append(getMessage(bundle, "form.tooltip." + field.getName()));
			buffer.append("</p>");
		}
		buffer.append("</div>");
		return buffer.toString();
	}

	private static String generateInputInt(ICollectorPlugin collector, Field field, Input inputAnnotation, ResourceBundle bundle,
			Iterable<MessageDto> messages) {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(getHtmlAlerts(bundle, messages));
		buffer.append("<div class=\"form-group ");
		buffer.append(getCssClass(messages));
		buffer.append("\"><label class=\"control-label");
		if (inputAnnotation.required()) {
			buffer.append(" required");
		}
		buffer.append("\" for=\"_id");
		buffer.append(field.getName());
		buffer.append("\">");
		buffer.append(getMessage(bundle, "form.label." + field.getName()));
		buffer.append("</label><input class=\"form-control\" type=\"text\" name=\"");
		buffer.append(field.getName());
		buffer.append("\" id=\"_id");
		buffer.append(field.getName());
		buffer.append("\" value=\"");
		buffer.append(getHtmlGetterValue(collector, field));
		buffer.append("\"/>");
		if (bundle.containsKey("form.tooltip." + field.getName())) {
			buffer.append("<p class=\"help-block\">");
			buffer.append(getMessage(bundle, "form.tooltip." + field.getName()));
			buffer.append("</p>");
		}
		buffer.append("</div>");
		return buffer.toString();
	}

	private static String generateInputEnum(ICollectorPlugin collector, Field field, Input inputAnnotation, ResourceBundle bundle,
			Map<String, Fieldset> fieldsets, Iterable<MessageDto> messages) {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(getHtmlAlerts(bundle, messages));
		buffer.append("<div class=\"form-group ");
		buffer.append(getCssClass(messages));
		buffer.append("\"><label class=\"control-label");
		if (inputAnnotation.required()) {
			buffer.append(" required");
		}
		buffer.append("\" for=\"_id");
		buffer.append(field.getName());
		buffer.append("\">");
		buffer.append(getMessage(bundle, "form.label." + field.getName()));
		buffer.append("</label>");

		for (Object enumConstant : field.getType().getEnumConstants()) {
			buffer.append("<div class=\"radio col-md-offset-1\"><label><input type=\"radio\" name=\"");
			buffer.append(field.getName());
			buffer.append("\" id=\"_id");
			buffer.append(field.getName());
			buffer.append("\" value=\"");
			buffer.append(enumConstant.toString());
			buffer.append("\"");
			Fieldset fieldset = getOrCreate(fieldsets, normalizeFieldsetName(field.getName() + "_" + enumConstant.toString()));
			try {
				field.setAccessible(true);
				if (enumConstant.equals(field.get(collector))) {
					buffer.append(" checked");
					fieldset.setDisplayed(true);
				} else {
					fieldset.setDisplayed(false);
				}
			} catch (IllegalArgumentException e) {
				LOG.debug("Cannot get " + field.toGenericString() + ": IllegalArgumentException ->" + e.getMessage());
			} catch (IllegalAccessException e) {
				LOG.debug("Cannot get " + field.toGenericString() + ": IllegalAccessException ->" + e.getMessage());
			}
			buffer.append(" onclick=\"");
			for (Object enumConstant2 : field.getType().getEnumConstants()) {
				if (!enumConstant.equals(enumConstant2)) {
					buffer.append("$('#idfs");
					buffer.append(normalizeFieldsetName(field.getName() + "_" + enumConstant2.toString()));
					buffer.append("').fadeOut();");
				}
			}
			buffer.append("$('#idfs");
			buffer.append(normalizeFieldsetName(field.getName() + "_" + enumConstant.toString()));
			buffer.append("').fadeIn();");
			buffer.append("\"/>");
			buffer.append(getMessage(bundle, "form.label." + field.getName() + "." + enumConstant.toString()));
			buffer.append("</label></div>");
		}
		if (bundle.containsKey("form.tooltip." + field.getName())) {
			buffer.append("<p class=\"help-block\">");
			buffer.append(getMessage(bundle, "form.tooltip." + field.getName()));
			buffer.append("</p>");
		}
		buffer.append("</div>");
		return buffer.toString();
	}

	private static String generateInputString(ICollectorPlugin collector, Field field, Input inputAnnotation, ResourceBundle bundle,
			Iterable<MessageDto> messages) {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(getHtmlAlerts(bundle, messages));
		buffer.append("<div class=\"form-group ");
		buffer.append(getCssClass(messages));
		buffer.append("\"><label class=\"control-label");
		if (inputAnnotation.required()) {
			buffer.append(" required");
		}
		buffer.append("\" for=\"_id");
		buffer.append(field.getName());
		buffer.append("\">");
		buffer.append(getMessage(bundle, "form.label." + field.getName()));
		buffer.append("</label><input class=\"form-control\" type=\"");
		if (inputAnnotation.password()) {
			buffer.append("password");
		} else {
			buffer.append("text");
		}
		buffer.append("\" name=\"");
		buffer.append(field.getName());
		buffer.append("\" id=\"_id");
		buffer.append(field.getName());
		buffer.append("\" value=\"");
		buffer.append(getHtmlGetterValue(collector, field));
		buffer.append("\"/>");
		if (bundle.containsKey("form.tooltip." + field.getName())) {
			buffer.append("<p class=\"help-block\">");
			buffer.append(getMessage(bundle, "form.tooltip." + field.getName()));
			buffer.append("</p>");
		}
		buffer.append("</div>");
		return buffer.toString();
	}

	private static Component generateInputChar(ICollectorPlugin collector, Field field, Input inputAnnotation, ResourceBundle bundle,
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
		
		JLabel lblNewLabel = new JLabel(getMessage(bundle, "form.label." + field.getName()));
		panel.add(lblNewLabel, "2, 2, right, default");

		JTextField textField = new JTextField(String.valueOf(getHtmlGetterValue(collector, field)));
		textField.setBackground(Color.WHITE);
		panel.add(textField, "4, 2, fill, default");
		textField.setColumns(10);

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
		
		JLabel lblNewLabel = new JLabel(getMessage(bundle, "form.label." + field.getName()));
		panel.add(lblNewLabel, "2, 2, right, default");

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
		panel.add(btnBrowse, "4, 2, fill, default");

		JLabel lblNewLabel_1 = new JLabel(getMessage(bundle, "form.tooltip." + field.getName()));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.ITALIC, 11));
		panel.add(lblNewLabel_1, "4, 4");

		return panel;
	}

	private static String generateInputBankAccount(Field field, Input inputAnnotation, ResourceBundle bundle, Iterable<MessageDto> messages,
			List<BankAccountDto> accounts) {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(getHtmlAlerts(bundle, messages));
		buffer.append("<div class=\"form-group ");
		buffer.append(getCssClass(messages));
		buffer.append("\"><label class=\"control-label");
		if (inputAnnotation.required()) {
			buffer.append(" required");
		}
		buffer.append("\" for=\"_id");
		buffer.append(field.getName());
		buffer.append("\">");
		buffer.append(getMessage(bundle, "form.label." + field.getName()));
		buffer.append("</label><select class=\"form-control\" name=\"");
		buffer.append(field.getName());
		buffer.append("\" id=\"_id");
		buffer.append(field.getName());
		buffer.append("\">");
		for (BankAccountDto bankAccountDto : accounts) {
			buffer.append("<option value=\"");
			buffer.append(bankAccountDto.getId());
			buffer.append("\">");
			buffer.append(bankAccountDto.getTitle());
			buffer.append("</option>");
		}
		buffer.append("<option value=\"\">--</option>");
		buffer.append("</select>");
		if (bundle.containsKey("form.tooltip." + field.getName())) {
			buffer.append("<p class=\"help-block\">");
			buffer.append(getMessage(bundle, "form.tooltip." + field.getName()));
			buffer.append("</p>");
		}
		buffer.append("</div>");
		return buffer.toString();
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

	public static boolean hasFileInput(ICollectorPlugin collector) {
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
