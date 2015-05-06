/* scenarioo-server
 * Copyright (C) 2014, scenarioo.org Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.scenarioo.dao.design.aggregates;

import java.io.File;
import java.text.NumberFormat;

import org.scenarioo.dao.design.entities.DesignFiles;
import org.scenarioo.rest.base.BuildIdentifier;
import org.scenarioo.rest.base.design.ProposalIdentifier;

/**
 * Defines locations of aggregated files containing aggregated (=derived) data from design data.
 */
public class DesignAggregateFiles {

	private static NumberFormat THREE_DIGIT_NUM_FORMAT = createNumberFormatWithMinimumIntegerDigits(3);

	private static final String FILENAME_VERSION_PROPERTIES = "version.derived.properties";
	private static final String FILENAME_ISSUES_XML = "issues.derived.xml";
	private static final String FILENAME_PROPOSALS_XML = "proposals.derived.xml";
	private static final String FILENAME_PROPOSAL_STEPS_XML = "proposalSteps.derived.xml";
	private static final String FILENAME_LONG_OBJECT_NAMES_INDEX = "longObjectNamesIndex.derived.xml";

	private final DesignFiles designFiles;

	public DesignAggregateFiles(final File rootDirectory) {
		designFiles = new DesignFiles(rootDirectory);
	}

	public File getRootDirectory() {
		return designFiles.getRootDirectory();
	}

	public File getVersionFile(final BuildIdentifier buildIdentifier) {
		return new File(designFiles.getBranchDirectory(buildIdentifier.getBranchName()),
				FILENAME_VERSION_PROPERTIES);
	}

	public File getIssuesAndProposalsFile(final BuildIdentifier buildIdentifier) {
		File branchDir = designFiles.getBranchDirectory(buildIdentifier.getBranchName());
		return new File(branchDir, FILENAME_ISSUES_XML);
	}

	public File getIssueProposalsFile(final BuildIdentifier buildIdentifier, final String issueName) {
		File issueDir = designFiles.getIssueDirectory(buildIdentifier.getBranchName(), issueName);
		return new File(issueDir, FILENAME_PROPOSALS_XML);
	}

	public File getProposalStepsFile(final ProposalIdentifier proposalIdentifier) {
		File scenarioDir = designFiles.getProposalDirectory(proposalIdentifier.getBuildIdentifier().getBranchName(),
				proposalIdentifier.getIssueName(),
				proposalIdentifier.getProposalName());
		return new File(scenarioDir, FILENAME_PROPOSAL_STEPS_XML);
	}

	/**
	 * File to store short name aliases for file names for long object names.
	 */
	public File getLongObjectNamesIndexFile(final BuildIdentifier buildIdentifier) {
		return new File(designFiles.getBranchDirectory(buildIdentifier.getBranchName()),
				FILENAME_LONG_OBJECT_NAMES_INDEX);
	}

	/**
	 * Directory to store additional step navigation details inside
	 */
	public File getStepNavigationsDirectory(final ProposalIdentifier proposalIdentifier) {
		File stepsDir = designFiles.getSketchStepsDirectory(proposalIdentifier.getBranchName(),
				proposalIdentifier.getIssueName(), proposalIdentifier.getProposalName());
		return new File(stepsDir, "navigation.derived");
	}

	/**
	 * File to store navigation details of a step.
	 */
	public File getStepNavigationFile(final ProposalIdentifier proposalIdentifier, final int stepIndex) {
		File stepNavigationsDir = getStepNavigationsDirectory(proposalIdentifier);
		return new File(stepNavigationsDir, THREE_DIGIT_NUM_FORMAT.format(stepIndex) + ".navigation.xml");
	}

	private static NumberFormat createNumberFormatWithMinimumIntegerDigits(final int minimumIntegerDigits) {
		final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
		numberFormat.setMinimumIntegerDigits(minimumIntegerDigits);
		return numberFormat;
	}

	public File getBranchDirectory(final BuildIdentifier buildIdentifier) {
		return designFiles.getBranchDirectory(buildIdentifier.getBranchName());
	}

}