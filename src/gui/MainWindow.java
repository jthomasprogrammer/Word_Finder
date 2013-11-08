package gui;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import classes.InvertedIndex;
import classes.TextLocation;

public class MainWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2886798694986593005L;
	private InvertedIndex invertedIndex;
	JPanel panel = new JPanel();
	public MainWindow(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
		initUI();
	}

	private void initUI() {

		panel = new JPanel();

		//Changes the flow layout to nothing. Allows absolute positioning. 
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(4,1));
		final JTextField searchBox = new JTextField(24);
		searchBox.setText("test");
		JButton searchButton = new JButton("Search files for a word/words (Comma to seperate words)");
		JButton addFileButton = new JButton("Add file to search");
		JButton clearFilesButton = new JButton("Clear the file list");
		leftPanel.add(searchBox);
		leftPanel.add(searchButton);
		leftPanel.add(addFileButton);
		leftPanel.add(clearFilesButton);

		JPanel menu = new JPanel();
		final JTextArea foundBox = new JTextArea("Area where information about the found words is listed.");
		foundBox.setEditable(false);
		JScrollPane scroll = new JScrollPane (foundBox);
		scroll.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

		menu.setLayout(new BoxLayout(menu, BoxLayout.X_AXIS));
		menu.add(leftPanel);
		menu.add(scroll);

		panel.add(menu);

		final JPanel filePanel = new JPanel();
		filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));
		final JLabel label = new JLabel("Searching the following files:");
		final JTextField fileBox = new JTextField(24);
		fileBox.setEditable(false);
		filePanel.add(label);
		filePanel.add(fileBox);

		panel.add(filePanel);
		add(panel);

		/*
		 * Searches for the word in the given files. Adds any relevant data to the foundBox text area.
		 */
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String wordArr[] = searchBox.getText().split(",");
				//Clear the text box.
				foundBox.setText("");
				for(int index = 0; index < wordArr.length; index++){
					String word = wordArr[index].trim();
					Set<TextLocation> results = invertedIndex.get(word);
					//Put the results of the search into foundbox. If there are no results, simply put in no results.
					if(results != null && !results.isEmpty()){
						List<TextLocation> list = new ArrayList<TextLocation>(results);
						//Sort the text locations.
						Collections.sort(list, new Comparator<TextLocation>() {
							public int compare(TextLocation o1, TextLocation o2) {
								int nameCompare = o1.getFilename().compareTo(o2.getFilename());
								if(nameCompare == 0){
									if(o1.getLineNumber() < o2.getLineNumber()){
										return -1;
									}else if(o1.getLineNumber() > o2.getLineNumber()){
										return 1;
									}else{
										return 0;
									}
								}else{
									return nameCompare;
								}
							}
						});
						//Handles the foundBox result formatting. 
						String previous = "";
						for(int i = 0; i < list.size(); i++){
							TextLocation textLocation = list.get(i);
							if(i == 0){
								previous = textLocation.getFilename();
								foundBox.setText(foundBox.getText()+word+" found in "+previous+"\n"+textLocation.toString()+"\n");
							}else{
								String currentFileName = textLocation.getFilename();
								if(previous.equals(currentFileName)){
									foundBox.setText(foundBox.getText()+textLocation.toString()+"\n");
								}else{
									previous = currentFileName;
									foundBox.setText(foundBox.getText()+word+" found in "+previous+"\n"+textLocation.toString()+"\n");
								}
							}
						}
					}else{
						//Clear the text box.
						foundBox.setText(foundBox.getText()+"None of the documents contain "+word+".\n");
					}
				}
			}
		});

		/*
		 * Adds a files or files to the list of files were searching a word in.
		 */
		addFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fc = new JFileChooser();
				//Lets the user select multiple files.
				fc.setMultiSelectionEnabled(true);
				int returnVal = fc.showOpenDialog(MainWindow.this);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File files[] = fc.getSelectedFiles();
					for(int i = 0; i < files.length; i++){
						File file = files[i];
						try{
							BufferedReader br = new BufferedReader(new FileReader(file));
							String filename = file.getName();
							String line = br.readLine();
							int lineNumber = 1;
							while(line != null){
								String arr[] = line.split(" ");
								for(int a = 0; a < arr.length; a++){
									String word = arr[a];
									TextLocation textLocation = new TextLocation(filename, lineNumber, line);
									invertedIndex.add(word, textLocation);
								}
								lineNumber++;
								line = br.readLine();
							}
							//Update the searching the following files file box. 
							String currentText = fileBox.getText();
							if(currentText.length() != 0){
								fileBox.setText(currentText+", "+filename);
							}else{
								fileBox.setText(filename);
							}
						}catch(FileNotFoundException e){
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		});

		/*
		 * Clears the invertedIndex and the loaded files.
		 */
		clearFilesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				//Clears the file box.
				fileBox.setText("");
				//Clears the invertedIndex.
				invertedIndex.clear();
			}
		});

		pack();
		setTitle("Word Finder");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public JPanel getPanel(){
		return panel;
	}

}
