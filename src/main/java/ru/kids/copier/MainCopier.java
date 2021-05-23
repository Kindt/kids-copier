package ru.kids.copier;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;

import ru.kids.copier.process.FilesProcess;
import ru.kids.copier.ui.ProgressDialog;

public class MainCopier {

	public static void main(String[] args) {
		String inFolderName = "in";
		String outFolderName = "out";
		boolean clearOutFolder = true;
		for (String arg : args) {
			String argName = arg.substring(0, arg.indexOf('='));
			String argValue = arg.substring(arg.indexOf('=') + 1);
			switch (argName) {
			case ("inFolder"):
				inFolderName = argValue;
				break;
			case ("outFolder"):
				outFolderName = argValue;
				break;
			case ("clearOutFolder"):
				clearOutFolder = Boolean.parseBoolean(argValue);
				break;
			default:
				break;
			}
		}

		File inFolder = new File(inFolderName);
		File outFolder = new File(outFolderName);

		if (!inFolder.exists()) {
			inFolder.mkdirs();
			JOptionPane.showMessageDialog(null,
					"The input folder was not found!\nFolder created!\nAdd the cliche files and try again!\nPath to the created folder:"
							+ inFolder.getAbsolutePath(),
					"Error!", JOptionPane.ERROR_MESSAGE);
			return;
		}

		File[] fArray = inFolder.listFiles(new FilenameFilter() {
			private String ext = ".cliche";

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(ext);
			}
		});

		if (fArray.length == 0) {
			JOptionPane.showMessageDialog(null,
					"The input folder does not contain \".cliche\" files!\nAdd the cliche files and try again!\nFolder path:"
							+ inFolder.getAbsolutePath(),
					"Error!", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!outFolder.exists())
			outFolder.mkdirs();

		if (clearOutFolder)
			try {
				FileUtils.deleteDirectory(outFolder);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Error clearing the output file folder!\nFolder path:" + outFolder.getAbsolutePath(), "Error!",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

		boolean isError = false;
		try {
			ProgressDialog pd = new ProgressDialog(null, "Kids copier in progress...");
			pd.runWork(new SwingWorker<Boolean, Object>() {

				@Override
				protected Boolean doInBackground() throws Exception {
					FilesProcess proc = new FilesProcess(fArray, outFolder, pd);
					proc.process();
					return true;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Application error:\n"+e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			isError = true;
		}

		if (!isError)
			JOptionPane.showMessageDialog(null,
					"The job is finished.\nThe finished files are located in the folder: " + outFolder.getAbsolutePath(), "Message!",
					JOptionPane.INFORMATION_MESSAGE);
	}
}
