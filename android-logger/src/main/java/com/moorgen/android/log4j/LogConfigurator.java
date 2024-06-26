/* 
   Copyright 2011 Rolf Kulemann, Pascal Bockhorn

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/   
package com.moorgen.android.log4j;
import java.io.IOException;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.LogLog;

/**
 * Configures the Log4j logging framework. 
 * See <a href="http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html">Patterns</a> for pattern layout.
 *
 * @author Rolf Kulemann
 */
public class LogConfigurator {
	private Level rootLevel = Level.DEBUG;
	private String filePattern = "%d - [%p::%c::%C] - %m%n";
	private String logCatPattern = "%m%n";
	private String fileName = "android-log4j.log";
	private int maxBackupSize = 5;
	private long maxFileSize = 512 * 1024;
	private boolean immediateFlush = true;
	private boolean useLogCatAppender = true;
	private boolean useFileAppender = true;
	private boolean resetConfiguration = true;
	private boolean internalDebugging = false;

	/**
	 * Should location info be included in dispatched messages.
	 */
	private boolean locationInfo = false;
	public LogConfigurator() {
	}

	/**
	 * @param fileName Name of the log file
	 */
	public LogConfigurator(final String fileName) {
		setFileName(fileName);
	}

	/**
	 * @param fileName  Name of the log file
	 * @param rootLevel Log level for the root logger
	 */
	public LogConfigurator(final String fileName, final Level rootLevel) {
		this(fileName);
		setRootLevel(rootLevel);
	}

	/**
	 * @param fileName Name of the log file
	 * @param rootLevel Log level for the root logger
	 * @param filePattern Log pattern for the file appender
	 */
	public LogConfigurator(final String fileName, final Level rootLevel, final String filePattern) {
		this(fileName);
		setRootLevel(rootLevel);
		setFilePattern(filePattern);
	}

	/**
	 * @param fileName Name of the log file
	 * @param maxBackupSize Maximum number of backed up log files
	 * @param maxFileSize Maximum size of log file until rolling
	 * @param filePattern  Log pattern for the file appender
	 * @param rootLevel Log level for the root logger
	 */
	public LogConfigurator(final String fileName, final int maxBackupSize,
			final long maxFileSize, final String filePattern, final Level rootLevel) {
		this(fileName, rootLevel, filePattern);
		setMaxBackupSize(maxBackupSize);
		setMaxFileSize(maxFileSize);
	}

	public void configure() {
		final Logger root = Logger.getRootLogger();
		
		if(isResetConfiguration()) {
		  	org.apache.log4j.LogManager.getLoggerRepository().resetConfiguration();
		}

		LogLog.setInternalDebugging(isInternalDebugging());
		
		if(isUseFileAppender()) {
			configureFileAppender();
		}
		
		if(isUseLogCatAppender()) {
			configureLogCatAppender();
		}
		
		root.setLevel(getRootLevel());
	}
	
	/**
	 * Sets the level of logger with name <code>loggerName</code>.
	 * Corresponds to log4j.properties <code>log4j.logger.org.apache.what.ever=ERROR</code>
	 * @param loggerName
	 * @param level
	 */
	public void setLevel(final String loggerName, final Level level) {
		Logger.getLogger(loggerName).setLevel(level);
	}

	private void configureFileAppender() {
		final Logger root = Logger.getRootLogger();
		final RollingFileAppender rollingFileAppender;
		final Layout fileLayout = new PatternLayout(getFilePattern());


		try {
			rollingFileAppender = new RollingFileAppender(fileLayout, getFileName());
		} catch (final IOException e) {
			throw new RuntimeException("Exception configuring log system", e);
		}

		rollingFileAppender.setMaxBackupIndex(getMaxBackupSize());
		rollingFileAppender.setMaximumFileSize(getMaxFileSize());
		rollingFileAppender.setImmediateFlush(isImmediateFlush());

		AsyncAppender asyncAppender  = new  AsyncAppender();
		asyncAppender.setLocationInfo(getLocationInfo());
		asyncAppender.setName("file");
		asyncAppender.addAppender(rollingFileAppender);

		root.addAppender(asyncAppender);
	}
	
	private void configureLogCatAppender() {
		final Logger root = Logger.getRootLogger();
		final Layout logCatLayout = new PatternLayout(getLogCatPattern());
		final LogCatAppender logCatAppender = new LogCatAppender(logCatLayout);
		logCatAppender.setName("logcat");

		root.addAppender(logCatAppender);
	}
	
	/**
	 * Return the log level of the root logger
	 * @return Log level of the root logger
	 */
	public Level getRootLevel() {
		return rootLevel;
	}

	/**
	 * Sets log level for the root logger
	 * @param level Log level for the root logger
	 */
	public void setRootLevel(final Level level) {
		this.rootLevel = level;
	}

	public String getFilePattern() {
		return filePattern;
	}

	public void setFilePattern(final String filePattern) {
		this.filePattern = filePattern;
	}

	public String getLogCatPattern() {
		return logCatPattern;
	}

	public void setLogCatPattern(final String logCatPattern) {
		this.logCatPattern = logCatPattern;
	}

	/**
	 * Returns the name of the log file
	 * @return the name of the log file
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the name of the log file
	 * @param fileName Name of the log file
	 */
	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Returns the maximum number of backed up log files
	 * @return Maximum number of backed up log files
	 */
	public int getMaxBackupSize() {
		return maxBackupSize;
	}

	/**
	 * Sets the maximum number of backed up log files
	 * @param maxBackupSize Maximum number of backed up log files
	 */
	public void setMaxBackupSize(final int maxBackupSize) {
		this.maxBackupSize = maxBackupSize;
	}

	/**
	 * Returns the maximum size of log file until rolling
	 * @return Maximum size of log file until rolling
	 */
	public long getMaxFileSize() {
		return maxFileSize;
	}

	/**
	 * Sets the maximum size of log file until rolling
	 * @param maxFileSize Maximum size of log file until rolling
	 */
	public void setMaxFileSize(final long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public boolean isImmediateFlush() {
		return immediateFlush;
	}

	public void setImmediateFlush(final boolean immediateFlush) {
		this.immediateFlush = immediateFlush;
	}

	/**
	 * Returns true, if FileAppender is used for logging
	 * @return True, if FileAppender is used for logging
	 */
	public boolean isUseFileAppender() {
		return useFileAppender;
	}

	/**
	 * @param useFileAppender the useFileAppender to set
	 */
	public void setUseFileAppender(final boolean useFileAppender) {
		this.useFileAppender = useFileAppender;
	}

	/**
	 * Returns true, if LogcatAppender should be used
	 * @return True, if LogcatAppender should be used
	 */
	public boolean isUseLogCatAppender() {
		return useLogCatAppender;
	}

	/**
	 * If set to true, LogCatAppender will be used for logging
	 * @param useLogCatAppender If true, LogCatAppender will be used for logging
	 */
	public void setUseLogCatAppender(final boolean useLogCatAppender) {
		this.useLogCatAppender = useLogCatAppender;
	}

	public void setResetConfiguration(boolean resetConfiguration) {
		this.resetConfiguration = resetConfiguration;
	}

	/**
	 * Resets the log4j configuration before applying this configuration. Default is true.
	 * @return True, if the log4j configuration should be reset before applying this configuration. 
	 */
	public boolean isResetConfiguration() {
		return resetConfiguration;
	}

	public void setInternalDebugging(boolean internalDebugging) {
		this.internalDebugging = internalDebugging;
	}

	public boolean isInternalDebugging() {
		return internalDebugging;
	}

	/**
	 * Gets whether the location of the logging request call should be captured.
	 *
	 * @return the current value of the <b>LocationInfo</b> option.
	 */
	public boolean getLocationInfo() {
	    	return locationInfo;
	}

	/**
	 * The <b>LocationInfo</b> option takes a boolean value. By default, it is set
	 * to false which means there will be no effort to extract the location
	 * information related to the event. As a result, the event that will be
	 * ultimately logged will likely to contain the wrong location information (if
	 * present in the log format).
	 * <p/>
	 * <p/>
	 * Location information extraction is comparatively very slow and should be
	 * avoided unless performance is not a concern.
	 * </p>
	 *
	 * @param flag true if location information should be extracted.
	 */
	public void setLocationInfo(final boolean flag) {
		locationInfo = flag;
	}
}
