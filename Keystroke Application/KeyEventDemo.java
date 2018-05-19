/**
 * IBM Machine Learning Service : predictive-modeling-zl
 * IBM Spark instance : spark-wb
 * Project Name : Keystroke Analysis 2
 * Model name: Test Model 2
 * Creation Date: 7th May
 * Dataset : WatsonData458906
 * Train-Test-Hold Split : Train: 50%, Test: 30%, Holdout: 20%
 *
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.swing.*;
import java.util.List;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;


public class KeyEventDemo extends JFrame
		implements KeyListener,
		ActionListener
{
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	private static final String FILE_HEADER = "User,H.period,DD.period.t,UD.period.t,H.t,DD.t.i,UD.t.i,H.i,DD.i.e,UD.i.e," +
			"H.e,DD.e.five,UD.e.five,H.five,DD.five.Shift.r,UD.five.Shift.r,H.Shift.r,DD.Shift.r.o,UD.Shift.r.o,H.o," +
			"DD.o.a,UD.o.a,H.a,DD.a.n,UD.a.n,H.n,DD.n.l,UD.n.l,H.l,DD.l.Return,UD.l.Return,H.Return";
	private String URL = "https://ibm-watson-ml.mybluemix.net";
	private String USERNAME = "4ef88571-1cd9-43b5-a933-fe997a593ed9";
	private String PASSWORD = "5dadc6ee-3af1-4d26-82a3-e48c4762e189";
	private String SCORING_URL = "https://ibm-watson-ml.mybluemix.net/v3/wml_instances/971d9861-82af-44dc-8a16-3ce2f55511f0/published_models/a0c06b16-3a61-48bd-916f-daa0842522f7/deployments/690075a6-636e-4896-b612-f158fe9f82e9/online";


	JTextArea displayArea;
	JTextField typingArea;

	static final String newline = System.getProperty("line.separator");

	public static void main(String[] args) {

        /* Use an appropriate Look and Feel */
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
        /* Turn off metal's use of bold fonts */
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		//Schedule a job for event dispatch thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		KeyEventDemo frame = new KeyEventDemo(".tie5Roanl");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Set up the content pane.
		frame.addComponentsToPane();


		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	private void addComponentsToPane()  {

		JButton button = new JButton("Clear");
		button.addActionListener(this);
		typingArea = new JTextField(20);
		typingArea.addKeyListener(this);
		displayArea = new JTextArea();
		displayArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(displayArea);
		scrollPane.setPreferredSize(new Dimension(375, 125));
		getContentPane().add(typingArea, BorderLayout.PAGE_START);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(button, BorderLayout.PAGE_END);
	}

	KeyDataStore store;
    int pressIndex = 0;
    int releaseIndex = 0;

	public KeyEventDemo(String name) {
	    super(name);
        this.store = new KeyDataStore();
	}

	/** Handle the key typed event from the text field. */
	public void keyTyped(KeyEvent e) {


	}

	/** Handle the key pressed event from the text field. */
	public void keyPressed(KeyEvent e) {
		displayArea.setText("");
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
			typingArea.setText("");
			this.store.removeContainsOfMap();
			releaseIndex=0;
			pressIndex = 0;
		}


		if(e.getKeyCode() == KeyEvent.VK_ENTER){

			String str = typingArea.getText();
			System.out.println(str);

			if( str.equals(".tie5Roanl")){
				List<Double> keyStoreParams = this.store.process();
				List<Long> keyStoreParamsInNano = this.store.processInNano();
				System.out.println(keyStoreParamsInNano.toString());

				System.out.println(keyStoreParams.toString());
				System.out.println(keyStoreParams.size());
				this.store.initialize();
				pressIndex = 0;
				typingArea.setText("");

				//generateCsv(keyStoreParams,"Nikhil");
				//generateCsvInNano(keyStoreParamsInNano,"Nikhil");

				sendRequest(keyStoreParams,URL,SCORING_URL,USERNAME,PASSWORD);
				return;

			}else{
				displayArea.setText(str);
				displayArea.setText("Please insert correct password");
				typingArea.setText("");
				this.store.removeContainsOfMap();
				releaseIndex=0;
				pressIndex = 0;
				return;

			}


        }

		TypedKeyObject typedKeyObject = new TypedKeyObject();
		typedKeyObject.pressTime = System.currentTimeMillis();
		typedKeyObject.pressTimeinNano = System.nanoTime();
		this.store.storeTypedObject(pressIndex,typedKeyObject);
		pressIndex++;
	}

	/** Handle the key released event from the text field. */
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
		    releaseIndex = 0;
		    return;
        }
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
			typingArea.setText("");
			this.store.removeContainsOfMap();
			pressIndex = 0;
			releaseIndex=0;
			return;
		}
		if(this.store.getKey(releaseIndex)!=null) {
          this.store.getKey(releaseIndex).releaseTime = System.currentTimeMillis();
          this.store.getKey(releaseIndex).releaseTimeinNano = System.nanoTime();

			releaseIndex++;
        }
	}

	/**
	 * Handle the button click.
	 */
	public void actionPerformed(ActionEvent e) {
		//Clear the text components.
		displayArea.setText("");
		typingArea.setText("");
		//Return the focus to the typing area.
		typingArea.requestFocusInWindow();
	}

	/**
	 *  This function create csv which stores your keystroke eachtime after
	 *  you enter valid password.
	 *  Function is used data collection. This data will be used for
	 *  training and tesing machine learning model.
	 * @param keyStoreParams : list of keyhold,kyup,keydown timings
	 */
	public void generateCsv(List<Double> keyStoreParams,String userName) {

		FileWriter fileWriter = null;
		try {

			String filename = userName+"Keystrokes.csv";
			File file = new File(filename);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			fileWriter = new FileWriter(file.getAbsoluteFile(), true);


			BufferedReader reader = new BufferedReader(new FileReader(filename));

			if (reader.readLine() == null) {
				fileWriter.append(FILE_HEADER.toString());
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			fileWriter.append(userName);
			fileWriter.append(COMMA_DELIMITER);
			for (int j = 0; j < keyStoreParams.size() - 1; j++) {
				fileWriter.append("" + keyStoreParams.get(j));
				fileWriter.append(COMMA_DELIMITER);
			}
			fileWriter.append("" + keyStoreParams.get(keyStoreParams.size() - 1));

			fileWriter.append(NEW_LINE_SEPARATOR);
			System.out.println("CSV file was created successfully !!!");

			typingArea.setText("");
			//Return the focus to the typing area.
			typingArea.requestFocusInWindow();

		} catch (Exception e2) {
			System.out.println("Error in CsvFileWriter !!!");
			e2.printStackTrace();
		} finally {

			try {
				fileWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void generateCsvInNano(List<Long> keyStoreParams,String userName) {

		FileWriter fileWriter = null;
		try {
			String fileName = userName+"KeystrokesInNano.csv";
			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			fileWriter = new FileWriter(file.getAbsoluteFile(), true);
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			if (reader.readLine() == null) {
				fileWriter.append(FILE_HEADER.toString());
				fileWriter.append(NEW_LINE_SEPARATOR);
			}

			fileWriter.append(userName);
			fileWriter.append(COMMA_DELIMITER);

			for (int j = 0; j < keyStoreParams.size() - 1; j++) {
				fileWriter.append("" + keyStoreParams.get(j));
				fileWriter.append(COMMA_DELIMITER);
			}

			fileWriter.append("" + keyStoreParams.get(keyStoreParams.size() - 1));
			fileWriter.append(NEW_LINE_SEPARATOR);
			System.out.println("CSV file was created successfully !!!");
			typingArea.setText("");
			//Return the focus to the typing area.
			typingArea.requestFocusInWindow();

		} catch (Exception e2) {
			System.out.println("Error in CsvFileWriter !!!");
			e2.printStackTrace();
		} finally {

			try {
				fileWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}


	/**
	 *
	 * @param list : list of keystrokes to be sent
	 */
	public void sendRequest(List<Double> list,String url,String scoringurl,String username,String password){

		JSONObject ob = new JSONObject();
		JSONArray arrayOfFields = new JSONArray();
		arrayOfFields.add("H.period");
		arrayOfFields.add("DD.period.t");
		arrayOfFields.add("UD.period.t");
		arrayOfFields.add("H.t");
		arrayOfFields.add("DD.t.i");
		arrayOfFields.add("UD.t.i");
		arrayOfFields.add("H.i");
		arrayOfFields.add("DD.i.e");
		arrayOfFields.add("UD.i.e");
		arrayOfFields.add("H.e");
		arrayOfFields.add("DD.e.five");
		arrayOfFields.add("UD.e.five");
		arrayOfFields.add("H.five");
		arrayOfFields.add("DD.five.Shift.r");
		arrayOfFields.add("UD.five.Shift.r");
		arrayOfFields.add("H.Shift.r");
		arrayOfFields.add("DD.Shift.r.o");
		arrayOfFields.add("UD.Shift.r.o");
		arrayOfFields.add("H.o");
		arrayOfFields.add("DD.o.a");
		arrayOfFields.add("UD.o.a");
		arrayOfFields.add("H.a");
		arrayOfFields.add("DD.a.n");
		arrayOfFields.add("UD.a.n");
		arrayOfFields.add("H.n");
		arrayOfFields.add("DD.n.l");
		arrayOfFields.add("UD.n.l");
		arrayOfFields.add("H.l");
		arrayOfFields.add("DD.l.Return");
		arrayOfFields.add("UD.l.Return");
		arrayOfFields.add("H.Return");

		ob.put("fields",arrayOfFields);

		JSONArray ar2 = new JSONArray();

		for(int i =0 ; i < 31 ; i++){
			ar2.add(list.get(i));
		}
		JSONArray ar3 = new JSONArray();
		ar3.add(ar2);
		ob.put("values",ar3);
		Map<String, String> wml_credentials = new HashMap<String, String>()
		{{
			put("url",url);
			put("username",username);
			put("password",password);
		}};

		String wml_auth_header = "Basic " +
				Base64.getEncoder().encodeToString((wml_credentials.get("username") + ":" +
						wml_credentials.get("password")).getBytes(StandardCharsets.UTF_8));
		String wml_url = wml_credentials.get("url") + "/v3/identity/token";
		HttpURLConnection tokenConnection = null;
		HttpURLConnection scoringConnection = null;
		BufferedReader tokenBuffer = null;
		BufferedReader scoringBuffer = null;
		try {
			// Getting WML token
			URL tokenUrl = new URL(wml_url);
			tokenConnection = (HttpURLConnection) tokenUrl.openConnection();
			tokenConnection.setDoInput(true);
			tokenConnection.setDoOutput(true);
			tokenConnection.setRequestMethod("GET");
			tokenConnection.setRequestProperty("Authorization", wml_auth_header);
			tokenBuffer = new BufferedReader(new InputStreamReader(tokenConnection.getInputStream()));
			StringBuffer jsonString = new StringBuffer();
			String line;
			while ((line = tokenBuffer.readLine()) != null) {
				jsonString.append(line);
			}
			// Scoring request
			URL scoringUrl = new URL(scoringurl);
			String wml_token = "Bearer " +
					jsonString.toString()
							.replace("\"","")
							.replace("}", "")
							.split(":")[1];
			scoringConnection = (HttpURLConnection) scoringUrl.openConnection();
			scoringConnection.setDoInput(true);
			scoringConnection.setDoOutput(true);
			scoringConnection.setRequestMethod("POST");
			scoringConnection.setRequestProperty("Accept", "application/json");
			scoringConnection.setRequestProperty("Authorization", wml_token);
			scoringConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(scoringConnection.getOutputStream(), "UTF-8");
			String payload =ob.toJSONString();
			System.out.println("Payload is:" +payload);
			writer.write(payload);
			writer.close();

			scoringBuffer = new BufferedReader(new InputStreamReader(scoringConnection.getInputStream()));
			Object obj = new JSONParser().parse(scoringBuffer);
			JSONObject jo = (JSONObject) obj;
			JSONArray ja = (JSONArray) jo.get("values");
			JSONArray ja_1 = (JSONArray) ja.get(0);
			System.out.println("User is : " + ja_1.get(35) + "\n");
			System.out.println(ja);

			displayArea.setText("");
			displayArea.setText("User is: " +ja_1.get(35));


		} catch (IOException e4) {
			System.out.println("The URL is not valid.");
			System.out.println(e4.getMessage());
		}
		catch (ParseException e5) {
			//some exception handler code.
			System.out.println(e5);
		}
		finally {
			if (tokenConnection != null) {
				tokenConnection.disconnect();
			}
			if (tokenBuffer != null) {
				try {
					tokenBuffer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (scoringConnection != null) {
				scoringConnection.disconnect();
			}
			if (scoringBuffer != null) {
				try {
					scoringBuffer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

	}


}

