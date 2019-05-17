package com.simonbaars.codearena.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.CollapsibleSectionPanel;
//import org.fife.rsta.ui.DocumentMap;
import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.SizeGripIcon;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.FindToolBar;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.ReplaceToolBar;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.common.TestingCommons;
import com.simonbaars.codearena.model.MetricProblem;
import com.simonbaars.codearena.thread.ProblemDetectionThread;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;

import net.minecraft.client.Minecraft;

public class CodeEditor extends JFrame implements SearchListener {

	private CollapsibleSectionPanel csp;
	private RSyntaxTextArea textArea;
	private FindDialog findDialog;
	private ReplaceDialog replaceDialog;
	private FindToolBar findToolBar;
	private ReplaceToolBar replaceToolBar;
	private StatusBar statusBar;
	private final File file;
	private final MetricProblem cloneClass;
	private final boolean isMetricProblem;
	public static boolean locked = true;
	private final String metric;
	
	public CodeEditor(MetricProblem cloneClass, File file, int markedRangeStart, int markedRangeEnd, int pos, int amount) {
		this(cloneClass, file, markedRangeStart, markedRangeEnd, pos, amount, true, null);
	}
	
	public CodeEditor(File file, boolean isMetricsProblem, String metric) {
		this(null, file, 0, 0, 0, 1, false, metric);
	}
	
	public CodeEditor(MetricProblem cloneClass, File file, int markedRangeStart, int markedRangeEnd, int pos, int amount, boolean isMetricProblem, String metric) {
		this.metric = metric;
		this.isMetricProblem = isMetricProblem;
		this.file = file;
		this.cloneClass = cloneClass;
		String content;
		try {
			content = TestingCommons.getFileAsString(file);

			initSearchDialogs();

			JPanel contentPane = new JPanel(new BorderLayout());
			setContentPane(contentPane);
			csp = new CollapsibleSectionPanel();
			contentPane.add(csp);

			setJMenuBar(createMenuBar());

			textArea = new RSyntaxTextArea(25, 80);
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
			textArea.setCodeFoldingEnabled(true);
			textArea.setMarkOccurrences(true);
			textArea.setText(content);

			RTextScrollPane sp = new RTextScrollPane(textArea);
			csp.add(sp);
			if(isMetricProblem) {
			for(int line = markedRangeStart; line<=markedRangeEnd && line>0; line++) {
				try {
					textArea.addLineHighlight(line-1, Color.CYAN);

					//sp.getVerticalScrollBar().set.scrollRectToVisible(new java.awt.Rectangle(0, textArea.getLineStartOffset(line-1),0,textArea.getLineStartOffset(line-1)));
					//sp.scrollRectToVisible(new java.awt.Rectangle(0, textArea.getLineStartOffset(line-1),0,0));
				} catch (BadLocationException e) {
					//e.printStackTrace(); Doesn't matter
				}
			}
			DefaultCaret caret = (DefaultCaret)textArea.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			int index = getLineStartIndex(textArea, markedRangeStart);
			if (index != -1) { 
				textArea.setCaretPosition(index);
			}
			index = getLineStartIndex(textArea, markedRangeEnd);
			if (index != -1) { 
				textArea.setCaretPosition(index);
			}
			}
			//Point pt = textArea.getCaret().getMagicCaretPosition();
			//Rectangle rect = new Rectangle(pt, new Dimension(1, 10));
			//textArea.scrollRectToVisible(rect);


			ErrorStrip errorStrip = new ErrorStrip(textArea);
			contentPane.add(errorStrip, BorderLayout.LINE_END);
			///org.fife.rsta.ui.DocumentMap docMap = new org.fife.rsta.ui.DocumentMap(textArea);
			//contentPane.add(docMap, BorderLayout.LINE_END);

			statusBar = new StatusBar();
			contentPane.add(statusBar, BorderLayout.SOUTH);

			setTitle(file.getName()+" - CodeArena IDE");
			//setDefaultCloseOperation(EXIT_ON_CLOSE);
			pack();
			setLocationRelativeTo(null);

			KeyStroke controlS = KeyStroke.getKeyStroke("control S");
			textArea.getActionMap().put("save", new SaveFile(file, textArea, statusBar.label));
			textArea.getInputMap().put(controlS, "save");
			textArea.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					if(!e.isControlDown())
						statusBar.setLabel("Ready");
				}
				
				@Override
				public void keyReleased(KeyEvent e) {}
				
				@Override
				public void keyPressed(KeyEvent e) {}
			});
			java.awt.Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
			int width2 = (int)maxBounds.getWidth()/amount;
			setBounds(width2*pos, 0, width2, (int)maxBounds.getHeight());
			
			
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					if(!locked) {
						locked = true;
						try {
							for(CodeEditor c : CloneDetection.get().openEditors)
								TestingCommons.writeStringToFile(c.file, c.textArea.getText());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						if(isMetricProblem) {
							ProblemDetectionThread.startWorker(Minecraft.getMinecraft().player, cloneClass, false);
						} else {
							CloneDetection.get().executeRascal("import "+metric+";", '>');
						}
						CloneDetection.get().closeAllEditorsExcept(this);
						CloneDetection.get().openEditors.clear();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public int getLineStartIndex(JTextComponent textComp, int lineNumber) {
	    if (lineNumber == 0) { return 0; }

	    // Gets the current line number start index value for 
	    // the supplied text line.
	    try {
	        JTextArea jta = (JTextArea) textComp;
	        return jta.getLineStartOffset(lineNumber-1);
	    } catch (BadLocationException ex) { return -1; }
	}

	
	public static void centerLineInScrollPane(JTextComponent component)
	{
	    Container container = SwingUtilities.getAncestorOfClass(JViewport.class, component);

	    if (container == null) return;

	    try
	    {
	        java.awt.Rectangle r = component.modelToView(component.getCaretPosition());
	        JViewport viewport = (JViewport)container;

	        int extentWidth = viewport.getExtentSize().width;
	        int viewWidth = viewport.getViewSize().width;

	        int x = Math.max(0, r.x - (extentWidth / 2));
	        x = Math.min(x, viewWidth - extentWidth);

	        int extentHeight = viewport.getExtentSize().height;
	        int viewHeight = viewport.getViewSize().height;

	        int y = Math.max(0, r.y - (extentHeight / 2));
	        y = Math.min(y, viewHeight - extentHeight);

	        viewport.setViewPosition(new Point(x, y));
	    }
	    catch(BadLocationException ble) {}
	}


	public CodeEditor(File file, int markedRangeStart, int markedRangeEnd) {
		this(null, file, markedRangeStart, markedRangeEnd, 0, 1);
	}

	public CodeEditor(File file) {
		this(file, 0, 0);
	}


	private void addItem(Action a, ButtonGroup bg, JMenu menu) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
		bg.add(item);
		menu.add(item);
	}


	private JMenuBar createMenuBar() {

		JMenuBar mb = new JMenuBar();
		
		JMenu menu = new JMenu("File");
		JMenuItem menuItem = new JMenuItem(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					TestingCommons.writeStringToFile(file, textArea.getText());
					statusBar.setLabel("File successfully saved!");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		menuItem.setText("Save");
		
		/*if(isMetricProblem) {
			JMenuItem fixedButton = new JMenuItem(new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					ProblemDetectionThread.startWorker(Minecraft.getMinecraft().player, cloneClass, false);
					CloneDetection.get().closeAllEditors();
				}
			});
			fixedButton.setText("I fixed it! :-)");
			menu.add(fixedButton);
		}*/
		
		menu.add(menuItem);
		mb.add(menu);
		
		
		
		
		menu = new JMenu("Search");
		menu.add(new JMenuItem(new ShowFindDialogAction()));
		menu.add(new JMenuItem(new ShowReplaceDialogAction()));
		menu.add(new JMenuItem(new GoToLineAction()));
		menu.addSeparator();
	
		

		int ctrl = getToolkit().getMenuShortcutKeyMask();
		int shift = InputEvent.SHIFT_MASK;
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrl|shift);
		Action a = csp.addBottomComponent(ks, findToolBar);
		a.putValue(Action.NAME, "Show Find Search Bar");
		menu.add(new JMenuItem(a));
		ks = KeyStroke.getKeyStroke(KeyEvent.VK_H, ctrl|shift);
		a = csp.addBottomComponent(ks, replaceToolBar);
		a.putValue(Action.NAME, "Show Replace Search Bar");
		menu.add(new JMenuItem(a));

		mb.add(menu);

		return mb;

	}


	@Override
	public String getSelectedText() {
		return textArea.getSelectedText();
	}


	/**
	 * Creates our Find and Replace dialogs.
	 */
	public void initSearchDialogs() {

		findDialog = new FindDialog(this, this);
		replaceDialog = new ReplaceDialog(this, this);

		// This ties the properties of the two dialogs together (match case,
		// regex, etc.).
		SearchContext context = findDialog.getSearchContext();
		replaceDialog.setSearchContext(context);

		// Create tool bars and tie their search contexts together also.
		findToolBar = new FindToolBar(this);
		findToolBar.setSearchContext(context);
		replaceToolBar = new ReplaceToolBar(this);
		replaceToolBar.setSearchContext(context);

	}


	/**
	 * Listens for events from our search dialogs and actually does the dirty
	 * work.
	 */
	@Override
	public void searchEvent(SearchEvent e) {

		SearchEvent.Type type = e.getType();
		SearchContext context = e.getSearchContext();
		SearchResult result = null;

		switch (type) {
		default: // Prevent FindBugs warning later
		case MARK_ALL:
			result = SearchEngine.markAll(textArea, context);
			break;
		case FIND:
			result = SearchEngine.find(textArea, context);
			if (!result.wasFound()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
			break;
		case REPLACE:
			result = SearchEngine.replace(textArea, context);
			if (!result.wasFound()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
			break;
		case REPLACE_ALL:
			result = SearchEngine.replaceAll(textArea, context);
			JOptionPane.showMessageDialog(null, result.getCount() +
					" occurrences replaced.");
			break;
		}

		String text = null;
		if (result.wasFound()) {
			text = "Text found; occurrences marked: " + result.getMarkedCount();
		}
		else if (type==SearchEvent.Type.MARK_ALL) {
			if (result.getMarkedCount()>0) {
				text = "Occurrences marked: " + result.getMarkedCount();
			}
			else {
				text = "";
			}
		}
		else {
			text = "Text not found";
		}
		statusBar.setLabel(text);

	}

	/**
	 * Opens the "Go to Line" dialog.
	 */
	private class GoToLineAction extends AbstractAction {

		GoToLineAction() {
			super("Go To Line...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			GoToDialog dialog = new GoToDialog(CodeEditor.this);
			dialog.setMaxLineNumberAllowed(textArea.getLineCount());
			dialog.setVisible(true);
			int line = dialog.getLineNumber();
			if (line>0) {
				try {
					textArea.setCaretPosition(textArea.getLineStartOffset(line-1));
				} catch (BadLocationException ble) { // Never happens
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
					ble.printStackTrace();
				}
			}
		}

	}


	/**
	 * Changes the Look and Feel.
	 */
	private class LookAndFeelAction extends AbstractAction {

		private LookAndFeelInfo info;

		LookAndFeelAction(LookAndFeelInfo info) {
			putValue(NAME, info.getName());
			this.info = info;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				UIManager.setLookAndFeel(info.getClassName());
				SwingUtilities.updateComponentTreeUI(CodeEditor.this);
				if (findDialog!=null) {
					findDialog.updateUI();
					replaceDialog.updateUI();
				}
				pack();
			} catch (RuntimeException re) {
				throw re; // FindBugs
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


	/**
	 * Shows the Find dialog.
	 */
	private class ShowFindDialogAction extends AbstractAction {

		ShowFindDialogAction() {
			super("Find...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, c));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			findDialog.setVisible(true);
		}

	}


	/**
	 * Shows the Replace dialog.
	 */
	private class ShowReplaceDialogAction extends AbstractAction {

		ShowReplaceDialogAction() {
			super("Replace...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, c));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			replaceDialog.setVisible(true);
		}

	}


	/**
	 * The status bar for this application.
	 */
	private static class StatusBar extends JPanel {

		private JLabel label;

		StatusBar() {
			label = new JLabel("Ready");
			setLayout(new BorderLayout());
			add(label, BorderLayout.LINE_START);
			add(new JLabel(new SizeGripIcon()), BorderLayout.LINE_END);
		}

		void setLabel(String label) {
			this.label.setText(label);
		}
	}


}