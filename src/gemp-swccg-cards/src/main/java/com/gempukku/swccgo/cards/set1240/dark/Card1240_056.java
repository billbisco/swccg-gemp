package com.gempukku.swccgo.cards.set1240.dark;

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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptivesEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Location
 * Subtype: Site
 * Title: Kessel: Spice Mines - Prison
 */
public class Card1240_056 extends AbstractSite {
    public Card1240_056() {
        super(Side.DARK, "Kessel: Spice Mines - Prison", Title.Kessel, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLocationLightSideGameText("If you control, may release any captives imprisoned here (may retrieve 1 Force for each).");
        setLocationDarkSideGameText("Whenever a droid is imprisoned here, may retrieve 3 Force (once per captive).");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.PRISON);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__DUNGEON_V__RETRIEVE_FORCE;
        PhysicalCard captive = null;
        if (effectResult instanceof DeliveredCaptiveToPrisonResult delivered
                && TriggerConditions.captiveDeliveredToPrison(game, effectResult, Filters.droid, self)) {
            captive = delivered.getCaptive();
        } else if (effectResult instanceof CaptureCharacterResult captured && self.equals(captured.getPrison())
                && Filters.droid.accepts(game, captured.getCapturedCard())) {
            captive = captured.getCapturedCard();
        }
        if (captive != null && GameConditions.isOncePerCaptive(game, self, captive, gameTextActionId)) {
            final PhysicalCard imprisoned = captive;
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 3 Force");
            action.appendUsage(new OncePerCaptiveEffect(action, imprisoned));
            action.appendEffect(new RetrieveForceEffect(action, playerOnDarkSideOfLocation, 3) {
                @Override
                public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                    return Collections.singletonList(imprisoned);
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        Filter captiveHere = Filters.imprisonedIn(self);
        if (!GameConditions.controls(game, playerId, self)) {
            return null;
        }
        Collection<PhysicalCard> captives = Filters.filterActive(game, self, SpotOverride.INCLUDE_CAPTIVE, captiveHere);
        if (captives.isEmpty()) {
            return null;
        }

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
        action.setText("Release imprisoned captives");
        action.appendEffect(new ChooseCardsOnTableEffect(action, playerId, "Choose captives to release", 1, captives.size(), captives) {
            @Override
            protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                List<PhysicalCard> chosen = List.copyOf(selectedCards);
                action.appendEffect(new ReleaseCaptivesEffect(action, chosen));
                action.appendEffect(new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        List<PhysicalCard> released = chosen.stream().filter(card -> !card.isCaptive()).toList();
                        if (!released.isEmpty() && !game.getGameState().getLostPile(playerId).isEmpty()) {
                            action.appendEffect(new PlayoutDecisionEffect(action, playerId,
                                    new YesNoDecision("Retrieve " + released.size() + " Force?") {
                                        @Override
                                        public void yes() {
                                            action.appendEffect(new RetrieveForceEffect(action, playerId, released.size()) {
                                                @Override
                                                public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                                    return released;
                                                }
                                            });
                                        }
                                    }));
                        }
                    }
                });
            }
        });
        return Collections.singletonList(action);
    }
}
