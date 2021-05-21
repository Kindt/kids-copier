package ru.kids.copier;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import ru.kids.copier.process.FilesProcess;

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
					"Входная папка не была найдена!\nПапка создана!\nДобавьте файлы клише и повторите попытку!\nПуть к созданной папке:"
							+ inFolder.getAbsolutePath(),
					"Ошибка!", JOptionPane.ERROR_MESSAGE);
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
					"Входная папка не содержит файлов \".cliche\"!\nДобавьте файлы клише и повторите попытку!\nПуть к папке:"
							+ inFolder.getAbsolutePath(),
					"Ошибка!", JOptionPane.ERROR_MESSAGE);
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
						"Ошибка очистки папки выходных файлов!\nПуть к папке:" + outFolder.getAbsolutePath(), "Ошибка!",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

		FilesProcess proc = new FilesProcess(fArray, outFolder);

		try {
			proc.process();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Ошибка работы приложения", "Ошибка!", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JOptionPane.showMessageDialog(null,
				"Работа завершена.\nГотоые файлы находятся в папке: " + outFolder.getAbsolutePath(), "Сообщение!",
				JOptionPane.INFORMATION_MESSAGE);
	}
}
