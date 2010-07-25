package weka.core.converters;

import it.polimi.utils.TextStripper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import weka.core.DenseInstance;
import weka.core.Instances;

public class DocumentLoader extends TextDirectoryLoader {

	private static final long serialVersionUID = -7996748074510351666L;

	@SuppressWarnings("unchecked")
	@Override
	public Instances getDataSet() throws IOException {
		if (getDirectory() == null)
			throw new IOException("No directory/source has been specified");

		String directoryPath = getDirectory().getAbsolutePath();
		ArrayList<String> classes = new ArrayList<String>();
		Enumeration enm = getStructure().classAttribute().enumerateValues();
		while (enm.hasMoreElements())
			classes.add((String) enm.nextElement());

		Instances data = getStructure();
		int fileCount = 0;
		for (int k = 0; k < classes.size(); k++) {
			String subdirPath = (String) classes.get(k);
			File subdir = new File(directoryPath + File.separator + subdirPath);
			String[] files = subdir.list();
			for (int j = 0; j < files.length; j++) {
				try {
					File txt = new File(directoryPath + File.separator
							+ subdirPath + File.separator + files[j]);
					if (files[j].startsWith(".") || !txt.isFile()) {
						continue;
					}
					fileCount++;
					if (getDebug())
						System.err.println("processing " + fileCount + " : "
								+ subdirPath + " : " + files[j]);

					double[] newInst = null;
					if (m_OutputFilename)
						newInst = new double[3];
					else
						newInst = new double[2];

					System.out.println(txt);

					String content = null;
					if (txt.getAbsolutePath().endsWith(".pdf")) {
						TextStripper textStripper = new TextStripper();
						content = textStripper.getContent(txt);
						content = textStripper.cleanContent(content);
					}
					if (txt.getAbsolutePath().endsWith(".txt")) {
						FileReader reader = new FileReader(txt);
						BufferedReader fileReader = new BufferedReader(reader);
						StringBuffer buffer = new StringBuffer();
						String temp;
						while((temp = fileReader.readLine()) != null){
							buffer.append(temp+"\n");
						}
						content = buffer.toString();
						reader.close();
						fileReader.close();
					}


					newInst[0] = (double) data.attribute(0).addStringValue(
							content);
					if (m_OutputFilename)
						newInst[1] = (double) data.attribute(1).addStringValue(
								subdirPath + File.separator + files[j]);
					newInst[data.classIndex()] = (double) k;
					data.add(new DenseInstance(1.0, newInst));
					// is.close();
				} catch (Exception e) {
					System.err.println("failed to convert file: "
							+ directoryPath + File.separator + subdirPath
							+ File.separator + files[j]);
				}
			}
		}

		return data;
	}
}
