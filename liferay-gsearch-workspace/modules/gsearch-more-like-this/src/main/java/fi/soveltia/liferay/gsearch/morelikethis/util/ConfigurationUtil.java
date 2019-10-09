package fi.soveltia.liferay.gsearch.morelikethis.util;

import com.liferay.portal.kernel.util.StringBundler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Loads default configuration to the portlet instance.
 *
 * @author Petteri Karttunen
 */
public class ConfigurationUtil {

	/**
	 * Gets default configuration value for given property name.
	 * 
	 * @param name
	 * @return
	 */
	public static String getDefaultConfigurationValue(String name) {
		StringBundler filePath = new StringBundler();

		filePath.append("configs/");
		filePath.append(name);
		filePath.append(".json");

		String content = null;

		BufferedReader br = null;
		InputStreamReader isr = null;
		InputStream is = null;

		try {
			is = ConfigurationUtil.class.getClassLoader(
			).getResourceAsStream(
				filePath.toString()
			);
			
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

			StringBuilder sb = new StringBuilder();

			String line = null;

			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}

			content = sb.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}

			if (isr != null) {
				try {
					isr.close();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}

			if (is != null) {
				try {
					is.close();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}

		return content;
	}

}