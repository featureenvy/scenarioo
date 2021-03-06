package org.scenarioo.business.aggregator;

import java.util.List;

import org.apache.log4j.Logger;
import org.scenarioo.model.docu.entities.Step;

/**
 * With version 2.0 of Scenarioo, slashes and backslashes are not allowed anymore in all identifier fields. To not break
 * Scenarioo installations that use these characters in page names, they are replaced by underscores.
 */
public class PageNameSanitizer {
	
	private static final Logger LOGGER = Logger.getLogger(PageNameSanitizer.class);
	
	public static void sanitizePageNames(final List<Step> steps) {
		if (steps == null) {
			return;
		}
		
		for (Step step : steps) {
			sanitizePageName(step);
		}
	}
	
	public static void sanitizePageName(final Step step) {
		if (step == null || step.getPage() == null) {
			return;
		}
		step.getPage().setName(sanitize(step.getPage().getName()));
	}
	
	private static String sanitize(final String name) {
		if (name == null) {
			return null;
		}
		
		String sanitizedName = name.replace("/", "_").replace("\\", "_");
		if (!sanitizedName.equals(name)) {
			LOGGER.warn("Illegal identifier " + name + " was sanitized to " + sanitizedName);
		}
		return sanitizedName;
	}
	
}
