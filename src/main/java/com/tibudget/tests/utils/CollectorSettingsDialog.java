package com.tibudget.tests.utils;

import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tibudget.api.ICollectorPlugin;
import com.tibudget.api.Input;
import com.tibudget.dto.BankAccountDto;
import com.tibudget.dto.MessageDto;
import com.tibudget.dto.MessagesDto;

public class CollectorSettingsDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton cancelButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JPanel panel;
	private javax.swing.JButton saveButton;
	// End of variables declaration//GEN-END:variables

	private static Logger LOG = LoggerFactory.getLogger(CollectorSettingsDialog.class);

	private static class InputElement implements Comparable<InputElement> {
		int order = -1;
		String html;

		public InputElement(int order, String html) {
			super();
			this.order = order;
			this.html = html;
		}

		@Override
		public int compareTo(InputElement o) {
			return getOrder() - o.getOrder();
		}

		public int getOrder() {
			return this.order;
		}

		public String getHtml() {
			return this.html;
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

	public CollectorSettingsDialog(java.awt.Frame parent, boolean modal) {

		super(parent, modal);

		initComponents();

		// setLocationRelativeTo must be called only AFTER setting your dialog
		// size, or the dialog will appear with its top left corner centered on
		// the parent window.
		super.setLocationRelativeTo(parent);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		cancelButton = new javax.swing.JButton();
		saveButton = new javax.swing.JButton();
		panel = new javax.swing.JPanel();
		jTextField1 = new javax.swing.JTextField();
		jLabel1 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		cancelButton.setText("Cancel");

		saveButton.setText("Save");

		jTextField1.setText("fieldName");
		jTextField1.setToolTipText("field tooltip");

		jLabel1.setText("jLabel1");

		javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
		panel.setLayout(panelLayout);
		panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				panelLayout
						.createSequentialGroup()
						.addComponent(jLabel1)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(266, Short.MAX_VALUE)));
		panelLayout.setVerticalGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				panelLayout
						.createSequentialGroup()
						.addGroup(
								panelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel1)
										.addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(220, Short.MAX_VALUE)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(
												javax.swing.GroupLayout.Alignment.TRAILING,
												layout.createSequentialGroup().addComponent(saveButton)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(cancelButton))
										.addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(cancelButton)
										.addComponent(saveButton)).addContainerGap()));

		pack();
	}// </editor-fold>
	//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				CollectorSettingsDialog dialog = new CollectorSettingsDialog(new javax.swing.JFrame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}

	public static StringBuffer generateHtmlForm(String id, ICollectorPlugin collector, ResourceBundle bundle, MessagesDto messages,
			List<BankAccountDto> accounts) {
		StringBuffer buffer = new StringBuffer("<input type=\"hidden\" name=\"_id\" value=\"");
		buffer.append(id);
		buffer.append("\"/>");
		Map<String, Fieldset> fieldsets = new HashMap<String, Fieldset>();
		for (Field field : collector.getClass().getDeclaredFields()) {
			Input inputAnnotation = field.getAnnotation(Input.class);
			if (inputAnnotation != null) {
				String html;
				if (field.getType().isEnum()) {
					html = generateInputEnum(collector, field, inputAnnotation, bundle, fieldsets, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == int.class) {
					html = generateInputInt(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == boolean.class) {
					html = generateInputBoolean(collector, field, inputAnnotation, bundle, fieldsets, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == String.class) {
					html = generateInputString(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == char.class) {
					html = "";
					//html = generateInputChar(collector, field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == File.class) {
					html = generateInputFile(field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()));
				} else if (field.getType() == BankAccountDto.class) {
					html = generateInputBankAccount(field, inputAnnotation, bundle, messages.getFieldMessages(field.getName()), accounts);
				} else {
					continue;
				}
				Fieldset fieldset = getOrCreate(fieldsets, inputAnnotation.fieldset());
				fieldset.getElements().add(new InputElement(inputAnnotation.order(), html));
			}
		}
		List<Fieldset> fsList = new ArrayList<Fieldset>(fieldsets.values());
		Collections.sort(fsList);
		for (Fieldset fieldset : fsList) {
			if (fieldset.getElements().size() > 0) {
				if (fieldset.getName().length() > 0) {
					String title = null;
					try {
						title = getMessage(bundle, "form.fieldset." + fieldset.getName(), null);
					} catch (MissingResourceException e) {
						// Ignore
					}
					buffer.append("<fieldset id =\"idfs");
					buffer.append(normalizeFieldsetName(fieldset.getName()));
					if (!fieldset.isDisplayed()) {
						buffer.append("\" style=\"display:none");
					}
					buffer.append("\">");
					if (title != null && title.length() > 0) {
						buffer.append("<legend>");
						buffer.append(getMessage(bundle, "form.fieldset." + fieldset.getName()));
						buffer.append("</legend>");
					}
				}
				Collections.sort(fieldset.getElements());
				for (InputElement inputElement : fieldset.getElements()) {
					buffer.append(inputElement.getHtml());
				}
				if (fieldset.getName().length() > 0) {
					buffer.append("</fieldset>");
				}
			}
		}
		return buffer;
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
				buffer.append("\"><button type=\"button\" class=\"close\" data-dismiss=\"alert\">�</button>");
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
		GridLayout layout = new GridLayout(2, 1);
		panel.setLayout(layout);
		JTextField input = new JTextField((String) getHtmlGetterValue(collector, field));
		layout.addLayoutComponent("lbl"+field.getName(), new JLabel(getMessage(bundle, "form.label." + field.getName())));
		layout.addLayoutComponent("txt"+field.getName(), input);
		layout.addLayoutComponent("empty"+field.getName(), new JLabel());
		layout.addLayoutComponent("empty"+field.getName(), new JLabel(getMessage(bundle, "form.tooltip." + field.getName())));
		return panel;
	}

	private static String generateInputFile(Field field, Input inputAnnotation, ResourceBundle bundle, Iterable<MessageDto> messages) {
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
		buffer.append("</label><input class=\"form-control\" type=\"file\" name=\"");
		buffer.append(field.getName());
		buffer.append("\" id=\"_id");
		buffer.append(field.getName());
		buffer.append("\"/>");
		if (bundle.containsKey("form.tooltip." + field.getName())) {
			buffer.append("<p class=\"help-block\">");
			buffer.append(getMessage(bundle, "form.tooltip." + field.getName()));
			buffer.append("</p>");
		}
		buffer.append("</div>");
		return buffer.toString();
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

	//	public static void populateParameters(IStorageService store, Map<String, BankAccountDto> accounts, String userId, ICollectorPlugin collector,
	//			MultipartHttpServletRequest request) {
	//		try {
	//			for (Field field : collector.getClass().getDeclaredFields()) {
	//				Input inputAnnotation = field.getAnnotation(Input.class);
	//				if (inputAnnotation != null) {
	//					field.setAccessible(true);
	//					if (field.getType() == int.class) {
	//						String paramValue = request.getParameter(normalizeFieldsetName(field.getName()));
	//						if (paramValue != null && paramValue.trim().length() > 0) {
	//							try {
	//								int value = Integer.parseInt(paramValue);
	//								field.set(collector, value);
	//							} catch (NumberFormatException e) {
	//								LOG.debug(field.getName() + " has incorrect value: " + paramValue);
	//							}
	//						}
	//					} else if (field.getType() == boolean.class) {
	//						String paramValue = request.getParameter(normalizeFieldsetName(field.getName()));
	//						// Whatever the value is, if the parameter is present then it's true
	//						boolean value = paramValue != null;
	//						field.set(collector, value);
	//					} else if (field.getType() == String.class) {
	//						String value = request.getParameter(normalizeFieldsetName(field.getName()));
	//						field.set(collector, value);
	//					} else if (field.getType() == BankAccountDto.class) {
	//						String value = request.getParameter(normalizeFieldsetName(field.getName()));
	//						field.set(collector, accounts.get(value));
	//					} else if (field.getType() == char.class) {
	//						String paramValue = request.getParameter(normalizeFieldsetName(field.getName()));
	//						if (paramValue != null && paramValue.length() > 0) {
	//							if (paramValue.trim().equals("\\t")) {
	//								field.set(collector, '\t');
	//							} else if (paramValue.trim().equals("\\n")) {
	//								field.set(collector, '\n');
	//							} else {
	//								field.set(collector, paramValue.charAt(0));
	//							}
	//						} else {
	//							field.set(collector, Character.MIN_VALUE);
	//						}
	//					} else if (field.getType() == File.class) {
	//						MultipartFile paramFile = request.getFile(normalizeFieldsetName(field.getName()));
	//						field.set(collector, new File(store.put(userId, paramFile.getInputStream())));
	//					} else if (field.getType().isEnum()) {
	//						String paramValue = request.getParameter(normalizeFieldsetName(field.getName()));
	//						for (Object enumConstant : field.getType().getEnumConstants()) {
	//							if (enumConstant.toString().equals(paramValue)) {
	//								field.set(collector, enumConstant);
	//								break;
	//							}
	//						}
	//					}
	//				}
	//			}
	//		} catch (IllegalArgumentException e) {
	//			LOG.warn(e.getMessage());
	//		} catch (IllegalAccessException e) {
	//			LOG.warn(e.getMessage());
	//		} catch (IOException e) {
	//			LOG.error(e.getMessage());
	//		}
	//	}

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