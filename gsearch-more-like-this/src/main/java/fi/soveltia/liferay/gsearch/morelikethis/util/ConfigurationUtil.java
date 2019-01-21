package fi.soveltia.liferay.gsearch.morelikethis.util;

import com.liferay.portal.kernel.util.StringBundler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Takes care of injecting default configuration to portlet instance.
 * 
 * @author Petteri Karttunen
 *
 */
public class ConfigurationUtil {

	public static String getDefaultConfigurationValue(String name) {

		StringBundler filePath = new StringBundler();

		filePath.append("configs/").append(name).append(".json");

		String content = null;

		BufferedReader br = null;
		InputStreamReader isr = null;
		InputStream is = null;

		try {

			is = ConfigurationUtil.class.getClassLoader().getResourceAsStream(filePath.toString());
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

			StringBuilder sb = new StringBuilder();

			String line = null;

			while ((line = br.readLine()) != null) {

				sb.append(line).append("\n");
			}

			content = sb.toString();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return content;
	}
}
