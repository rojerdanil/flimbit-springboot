package com.riseup.flimbit.constant;

import java.util.Arrays;
import java.util.Optional;

public enum MovieStatusEnum {

	   IDEA_STAGE("Idea Stage"),
	   PRE_PRODUCTION("Pre-Production"),
	   FUNDING_OPEN("Funding Open"),
	   FUNDING_CLOSED("Funding Closed"),
	   PRODUCTION("Production"),
	   POST_PRODUCTION("Post-Production"),
	   TRAILER_RELEASED("Trailer Released"),
	   COMING_SOON("Coming Soon"),
	   RELEASED("Released"),
	   BOX_OFFICE_RUNNING("Box Office Running"),
	   PROFIT_DISTRIBUTION("Profit Distribution"),
	   ARCHIVED("Archived"),
	   ON_HOLD("On Hold"),
	   CANCELLED("Cancelled");

	   private final String displayName;

	   MovieStatusEnum(String displayName) {
	       this.displayName = displayName;
	   }

	   public String getDisplayName() {
	       return displayName;
	   }

	   public static Optional<MovieStatusEnum> fromDisplayName(String displayName) {
	       return Arrays.stream(values())
	               .filter(e -> e.displayName.equalsIgnoreCase(displayName))
	               .findFirst();
	   }
}
