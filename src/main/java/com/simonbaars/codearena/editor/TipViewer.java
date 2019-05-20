package com.simonbaars.codearena.editor;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.codearena.model.MetricProblem;

public class TipViewer implements GetTip {
	public static void main(String[] args) {
		new TipViewer().createHTMLView(new MetricProblem(ProblemType.DUPLICATION, 10, new Sequence()));
	}
	
	public void createHTMLView(MetricProblem p) {
		// create jeditorpane
        JEditorPane jEditorPane = new JEditorPane();
        
        // make it read-only
        jEditorPane.setEditable(false);
        
        // create a scrollpane; modify its attributes as desired
        JScrollPane scrollPane = new JScrollPane(jEditorPane);
        
        // add an html editor kit
        HTMLEditorKit kit = new HTMLEditorKit();
        jEditorPane.setEditorKit(kit);
        jEditorPane.addHyperlinkListener(new HyperlinkListener() {
			
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
					    try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException | URISyntaxException e1) {
							e1.printStackTrace();
						}
					}else{
			            Runtime runtime = Runtime.getRuntime();
			            try {
			                runtime.exec("xdg-open " + e.getURL().toString());
			            } catch (IOException e2) {
			                e2.printStackTrace();
			            }
			        }
				}
			}
		});

        // create some simple html as a string
        String htmlString = getTip(p);
        
        // create a document, set it on the jeditorpane, then add the html
        Document doc = kit.createDefaultDocument();
        jEditorPane.setDocument(doc);
        jEditorPane.setText(htmlString);

        // now add it all to a frame
        JFrame j = new JFrame("How to solve this problem");
        j.getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        // display the frame
        j.setSize(new Dimension(1280,720));
        
        // pack it, if you prefer
        //j.pack();
        
        // center the jframe, then make it visible
        j.setLocationRelativeTo(null);
        j.setVisible(true);
	}
}
