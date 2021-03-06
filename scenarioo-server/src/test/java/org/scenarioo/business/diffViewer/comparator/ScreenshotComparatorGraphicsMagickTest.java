package org.scenarioo.business.diffViewer.comparator;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.NullAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.scenarioo.dao.diffViewer.GraphicsMagickConfiguration;
import org.scenarioo.dao.diffViewer.impl.DiffFiles;
import org.scenarioo.repository.RepositoryLocator;
import org.scenarioo.utils.TestFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.scenarioo.business.diffViewer.comparator.ConfigurationFixture.*;

public class ScreenshotComparatorGraphicsMagickTest {
	private ScreenshotComparator screenshotComparator;

	private static final String FILEPATH = "src/test/resources/org/scenarioo/business/diffViewer/";
	private static final File BASE_SCREENSHOT = new File(FILEPATH + "baseScreenshot.png");
	private static final File COMPARISON_SCREENSHOT_SAME_SIZE = new File(FILEPATH + "comparisonScreenshot.png");
	private static final File COMPARISON_SCREENSHOT_LARGE = new File(FILEPATH + "comparisonScreenshotLarge.png");
	private static final File DIFF_SCREENSHOT = new File(FILEPATH + "diffScreenshot.png");
	private static final File NON_EXISTENT_SCREENSHOT = new File(FILEPATH + "nonExistentScreenshot.png");
	private static final double SCREENSHOT_DIFFERENCE_SAME_SIZE = 14.11;
	private static final double SCREENSHOT_DIFFERENCE_LARGE = 17.81;
	private static final double DOUBLE_TOLERANCE = 0.01;
	private static final boolean IS_GRAPHICS_MAGICK_INSTALLED = GraphicsMagickConfiguration.isAvailable();

	@Rule
	public TemporaryFolder rootFolder = new TemporaryFolder();

	@Before
	public void setUpClass() throws IOException {
		TestFileUtils.createFolderAndSetItAsRootInConfigurationForUnitTest(rootFolder.newFolder());
		assertTrue(DiffFiles.getDiffViewerDirectory().mkdirs());
		RepositoryLocator.INSTANCE.getConfigurationRepository().updateConfiguration(getTestConfiguration());
		screenshotComparator = new ScreenshotComparator(BASE_BRANCH_NAME, BASE_BUILD_NAME,
			getComparisonConfiguration());
	}

	// Avoids massive logging which is expected
	private Enumeration allAppenders;

	@Before
	public void removeAppenders() {
		allAppenders = Logger.getRootLogger().getAllAppenders();
		Logger.getRootLogger().removeAllAppenders();
		Logger.getRootLogger().addAppender(new NullAppender());
	}

	@After
	public void restoreAppenders() {
		for (Object appender : Collections.list(allAppenders)) {
			Logger.getRootLogger().addAppender((Appender) appender);
		}
	}

	@Test
	public void compareEqualScreenshots() {
		final double difference = screenshotComparator.compareScreenshots(BASE_SCREENSHOT,
				BASE_SCREENSHOT, DIFF_SCREENSHOT);
		assertEquals("Difference of screenshots", 0, difference, DOUBLE_TOLERANCE);
		assertTrue("No DiffScreenshot is saved", !DIFF_SCREENSHOT.exists());
	}

	@Test
	public void compareDifferentScreenshots() {
		final double difference = screenshotComparator.compareScreenshots(BASE_SCREENSHOT,
				COMPARISON_SCREENSHOT_SAME_SIZE, DIFF_SCREENSHOT);
		if (IS_GRAPHICS_MAGICK_INSTALLED) {
			assertEquals("Difference of screenshots", SCREENSHOT_DIFFERENCE_SAME_SIZE, difference, DOUBLE_TOLERANCE);
		} else {
			assertEquals("Difference of screenshots", 0, difference, DOUBLE_TOLERANCE);
		}

	}

	@Test
	public void compareDifferentSizedScreenshots() {
		final double difference = screenshotComparator.compareScreenshots(BASE_SCREENSHOT,
				COMPARISON_SCREENSHOT_LARGE, DIFF_SCREENSHOT);
		if (IS_GRAPHICS_MAGICK_INSTALLED) {
			assertEquals("Difference of screenshots", SCREENSHOT_DIFFERENCE_LARGE, difference, DOUBLE_TOLERANCE);
		} else {
			assertEquals("Difference of screenshots", 0, difference, DOUBLE_TOLERANCE);
		}

	}

	@Test
	public void compareNonExistentScreenshots() {
		Logger LOGGER = ScreenshotComparator.getLogger();
		TestAppender appender = new TestAppender();
		LOGGER.addAppender(appender);

		final double difference = screenshotComparator.compareScreenshots(BASE_SCREENSHOT,
				NON_EXISTENT_SCREENSHOT, DIFF_SCREENSHOT);

		final List<LoggingEvent> log = appender.getLog();
		final LoggingEvent firstLogEntry = log.get(0);

		assertEquals("Difference of screenshots", 0.0D, difference, DOUBLE_TOLERANCE);
		assertEquals("Log Level", Level.WARN, firstLogEntry.getLevel());
		assertTrue("Assert log message is correct",
				firstLogEntry.getMessage().toString().contains("Graphics Magick operation failed"));
		LOGGER.removeAppender(appender);
	}

	private class TestAppender extends AppenderSkeleton {
		private List<LoggingEvent> log = new ArrayList<LoggingEvent>();

		@Override
		public boolean requiresLayout() {
			return false;
		}

		@Override
		protected void append(final LoggingEvent loggingEvent) {
			log.add(loggingEvent);
		}

		@Override
		public void close() {
			// Not test relevant
		}

		public List<LoggingEvent> getLog() {
			return new ArrayList<LoggingEvent>(log);
		}
	}
}
