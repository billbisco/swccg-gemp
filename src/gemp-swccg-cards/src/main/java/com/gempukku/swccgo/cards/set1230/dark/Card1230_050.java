package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.InsteadOfForceDrainingEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerCaptiveEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Set: Virtual Block 3
 * Type: Location
 * Subtype: Site
 * Title: Dungeon (Prison)
 */
public class Card1230_050 extends AbstractSite {
    public Card1230_050() {
        super(Side.DARK, Title.Dungeon_Prison, Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setGameText("Dark: May not deploy to Bespin, Dagobah, or a Rebel Base. Whenever you imprison a captive (without a bounty) here, may retrieve 2 Force (once per captive). Light: If you control, instead of Force draining, may release one imprisoned captive here.");
        setLocationDarkSideGameText("May not deploy to Bespin, Dagobah, or a Rebel Base. Whenever you imprison a captive (without a bounty) here, may retrieve 2 Force (once per captive).");
        setLocationLightSideGameText("If you control, instead of Force draining, may release one imprisoned captive here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.INTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.PRISON);
        addMayNotBePartOfSystem(Title.Bespin, Title.Dagobah, Title.Yavin_4, Title.Hoth, Title.Endor, Title.Dantooine);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__DUNGEON_V__RETRIEVE_FORCE;
        if (TriggerConditions.captiveDeliveredToPrison(game, effectResult, Filters.not(Filters.hasAttached(Filters.title(Title.Bounty))), self)
                && effectResult instanceof DeliveredCaptiveToPrisonResult) {
            final PhysicalCard captive = ((DeliveredCaptiveToPrisonResult) effectResult).getCaptive();
            if (captive != null
                    && GameConditions.isOncePerCaptive(game, self, captive, gameTextActionId)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
                action.setText("Retrieve 2 Force");
                action.appendUsage(
                        new OncePerCaptiveEffect(action, captive));
                action.appendEffect(
                        new RetrieveForceEffect(action, playerOnDarkSideOfLocation, 2) {
                            @Override
                            public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                return Collections.singletonList(captive);
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.imprisonedIn(self);
        Map<InactiveReason, Boolean> spotOverride = SpotOverride.INCLUDE_CAPTIVE;
        if (GameConditions.controls(game, playerOnLightSideOfLocation, self)
                && GameConditions.canTarget(game, self, spotOverride, targetFilter)
                && GameConditions.canInsteadOfForceDrainingAtLocation(game, playerOnLightSideOfLocation, self)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Release an imprisoned captive");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerOnLightSideOfLocation, "Target captive", spotOverride, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            action.allowResponses("Release " + cardTargeted.getTitle(),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new InsteadOfForceDrainingEffect(action, self,
                                                            new ReleaseCaptiveEffect(action, finalTarget)));
                                        }
                                    }
                            );
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
