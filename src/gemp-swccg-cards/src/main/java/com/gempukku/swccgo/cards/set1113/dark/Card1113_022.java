package com.gempukku.swccgo.cards.set1113.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerCaptiveEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Location
 * Subtype: Site
 * Title: Kessel: Spice Mines - Prison
 */
public class Card1113_022 extends AbstractSite {
    public Card1113_022() {
        super(Side.DARK, "Kessel: Spice Mines - Prison", Title.Kessel, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("If you control, may release any captives imprisoned here (may retrieve 1 Force for each).");
        setLocationDarkSideGameText("Whenever a droid is imprisoned here, may retrieve 3 Force (once per captive).");
        setGameText("Light: If you control, may release any captives imprisoned here (may retrieve 1 Force for each). Dark: Whenever a droid is imprisoned here, may retrieve 3 Force (once per captive).");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.PRISON);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__DUNGEON_V__RETRIEVE_FORCE;
        if (TriggerConditions.captiveDeliveredToPrison(game, effectResult, Filters.droid, self)
                && effectResult instanceof DeliveredCaptiveToPrisonResult) {
            final PhysicalCard captive = ((DeliveredCaptiveToPrisonResult) effectResult).getCaptive();
            if (captive != null && GameConditions.isOncePerCaptive(game, self, captive, gameTextActionId)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
                action.setText("Retrieve 3 Force");
                action.appendUsage(new OncePerCaptiveEffect(action, captive));
                action.appendEffect(new RetrieveForceEffect(action, playerOnDarkSideOfLocation, 3) {
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
        final Filter captiveHere = Filters.imprisonedIn(self);
        if (GameConditions.controls(game, playerOnLightSideOfLocation, self)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, captiveHere)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Release an imprisoned captive");
            action.appendTargeting(new ChooseCardOnTableEffect(action, playerOnLightSideOfLocation, "Choose captive", SpotOverride.INCLUDE_CAPTIVE, captiveHere) {
                @Override
                protected void cardSelected(final PhysicalCard selectedCard) {
                    action.appendEffect(new UnrespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new ReleaseCaptiveEffect(action, selectedCard));
                            action.appendEffect(new RetrieveForceEffect(action, playerOnLightSideOfLocation, 1));
                        }
                    });
                    action.setActionMsg("Release " + GameUtils.getCardLink(selectedCard));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
