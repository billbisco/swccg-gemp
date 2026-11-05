package com.gempukku.swccgo.logic.actions;

/**
 * An Objective's deployment (setup) action. swccg-guild/work#142 R3 ("Objectives: deploy from hand"): when the
 * Objective was deployed from hand, its setup deploys the required cards from hand instead of Reserve Deck, and a
 * required card already on the table counts as deployed.
 */
public interface ObjectiveSetupAction {
    /**
     * @return true if the Objective was deployed from hand, so its setup takes cards from hand
     */
    boolean isSetupFromHand();
}
