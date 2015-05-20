package org.scenarioo.rest.base.design;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Contains all the properties needed to identify a step unambiguously.
 */
public class SketchStepIdentifier {

	private static final String URI_ENCODING = "UTF-8";

	private final ScenarioSketchIdentifier scenarioSketchIdentifier;
	final String pageName;
	final int pageOccurrence;
	final int sketchStepInPageOccurrence;
	private final Set<String> labels;

	private enum RedirectType {
		STEP, SCREENSHOT
	}

	public SketchStepIdentifier(final String branchName, final String issueName,
			final String scenarioName,
			final String pageName, final int pageOccurrence, final int sketchStepInPageOccurrence) {
		this(new ScenarioSketchIdentifier(branchName, issueName, scenarioName), pageName, pageOccurrence,
				sketchStepInPageOccurrence);
	}

	public SketchStepIdentifier(final String branchName, final String issueName,
			final String proposalName,
			final String pageName, final int pageOccurrence, final int stepInPageOccurrence, final Set<String> labels) {
		this(new ScenarioSketchIdentifier(branchName, issueName, proposalName), pageName, pageOccurrence,
				stepInPageOccurrence, labels);
	}

	public SketchStepIdentifier(final ScenarioSketchIdentifier scenarioSketchIdentifier, final String pageName,
			final int pageOccurrence,
			final int sketchStepInPageOccurrence) {
		this(scenarioSketchIdentifier, pageName, pageOccurrence, sketchStepInPageOccurrence, null);
	}

	public SketchStepIdentifier(final ScenarioSketchIdentifier proposalIdentifier, final String pageName,
			final int pageOccurrence,
			final int sketchStepInPageOccurrence, final Set<String> labels) {
		this.scenarioSketchIdentifier = proposalIdentifier;
		this.pageName = pageName;
		this.pageOccurrence = pageOccurrence;
		this.sketchStepInPageOccurrence = sketchStepInPageOccurrence;
		this.labels = labels;
	}

	public static SketchStepIdentifier withDifferentSketchStepInPageOccurrence(
			final SketchStepIdentifier sketchStepIdentifier,
			final int sketchStepInPageOccurrence) {
		return new SketchStepIdentifier(sketchStepIdentifier.getScenarioSketchIdentifier(),
				sketchStepIdentifier.getPageName(),
				sketchStepIdentifier.getPageOccurrence(), sketchStepInPageOccurrence);
	}

	public static SketchStepIdentifier withDifferentIds(final SketchStepIdentifier sketchStepIdentifier,
			final int pageOccurrence,
			final int sketchStepInPageOccurrence) {
		return new SketchStepIdentifier(sketchStepIdentifier.getScenarioSketchIdentifier(),
				sketchStepIdentifier.getPageName(), pageOccurrence,
				sketchStepInPageOccurrence);
	}

	/**
	 * Returns the same page in a different scenario of the same use case.
	 */
	public static SketchStepIdentifier forFallBackScenario(final SketchStepIdentifier originalSketchStepIdentifier,
			final String fallbackIssueName, final String fallbackProposalName, final int pageOccurrence,
			final int sketchStepInPageOccurrence) {
		return new SketchStepIdentifier(originalSketchStepIdentifier.getBranchName(), fallbackIssueName,
				fallbackProposalName, originalSketchStepIdentifier.getPageName(), pageOccurrence,
				sketchStepInPageOccurrence);
	}

	/*
	 * public SketchStepIdentifier withDifferentBuildIdentifier(final BuildIdentifier
	 * buildIdentifierBeforeAliasResolution) {
	 * return new SketchStepIdentifier(getScenarioSketchIdentifier().withDifferentBuildIdentifier(
	 * buildIdentifierBeforeAliasResolution), pageName, pageOccurrence, sketchStepInPageOccurrence);
	 * }
	 */

	@JsonIgnore
	public ScenarioSketchIdentifier getScenarioSketchIdentifier() {
		return scenarioSketchIdentifier;
	}

	public String getBranchName() {
		return scenarioSketchIdentifier.getBranchName();
	}

	public String getIssueName() {
		return scenarioSketchIdentifier.getIssueName();
	}

	public String getScenarioSketchName() {
		return scenarioSketchIdentifier.getScenarioSketchName();
	}

	public String getPageName() {
		return pageName;
	}

	public int getPageOccurrence() {
		return pageOccurrence;
	}

	public int getSketchStepInPageOccurrence() {
		return sketchStepInPageOccurrence;
	}

	public Set<String> getLabels() {
		return labels;
	}

	/**
	 * File name extension, e.g. "png" or "jpeg". Always without a dot.
	 */
	@JsonIgnore
	public URI getScreenshotUriForRedirect(final String screenshotFileNameExtension) {
		return createUriForRedirect(RedirectType.SCREENSHOT, screenshotFileNameExtension);
	}

	@JsonIgnore
	public URI getSketchStepUriForRedirect() {
		return createUriForRedirect(RedirectType.STEP, null);
	}

	private URI createUriForRedirect(final RedirectType redirectType, final String screenshotFileNameExtension) {
		try {
			return getSketchStepUri(redirectType, screenshotFileNameExtension);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Redirect failed, can't create URI", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Encoding " + URI_ENCODING + " for Uri is not supported", e);
		}
	}

	private URI getSketchStepUri(final RedirectType redirectType, final String screenshotFileNameExtension)
			throws UnsupportedEncodingException, URISyntaxException {
		StringBuilder uriBuilder = new StringBuilder();

		uriBuilder.append("/rest/branch/").append(encode(getBranchName()));
		uriBuilder.append("/issue/").append(encode(getIssueName()));
		uriBuilder.append("/proposal/").append(encode(getScenarioSketchName()));
		uriBuilder.append("/pageName/").append(encode(getPageName()));
		uriBuilder.append("/pageOccurrence/").append(encode(Integer.toString(getPageOccurrence())));
		uriBuilder.append("/sketchStepInPageOccurrence/").append(
				encode(Integer.toString(getSketchStepInPageOccurrence())));

		if (RedirectType.SCREENSHOT.equals(redirectType)) {
			uriBuilder.append("/image." + screenshotFileNameExtension);
		}

		uriBuilder.append("?fallback=true");

		return new URI(uriBuilder.toString());
	}

	private String encode(final String urlParameter) throws UnsupportedEncodingException {
		String encoded = URLEncoder.encode(urlParameter, URI_ENCODING);
		return encoded.replace("+", "%20");
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(scenarioSketchIdentifier).append(pageName).append(pageOccurrence)
				.append(sketchStepInPageOccurrence).append(labels).build();
	}

}
