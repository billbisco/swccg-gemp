package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Effect
 * Title: Rogue Squadron Tactics
 */
public class Card1272_024 extends AbstractNormalEffect {
    public Card1272_024() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Rogue Squadron Tactics", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        addIcons(Icon.SPECIAL_EDITION);
        setLore("'I'm going to cut across the axis and try and draw their fire.'");
        setGameText("Deploy on table. Once per game, may take a non-[Virtual Block 1] Alderaan or Dressel, and/or a [Death Star II] Rogue Squadron pilot (except Tycho) into hand from Reserve Deck; reshuffle. Once per turn, if you just Force drained at a battleground system, may draw top card of Reserve Deck. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    static Filter alderaanOrDressel() {
        return Filters.and(Filters.or(Filters.title(Title.Alderaan), Filters.title("Dressel")), Filters.not(Icon.LEGACY_BLOCK_1));
    }

    static Filter rogueSquadronPilot() {
        return Filters.and(Icon.DEATH_STAR_II, Filters.Rogue_Squadron_pilot, Filters.not(Filters.Tycho));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ROGUE_SQUADRON_TACTICS__TAKE_CARDS;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take cards into hand from Reserve Deck");
            action.setActionMsg("Take a non-[Virtual Block 1] Alderaan or Dressel and/or a [Death Star II] Rogue Squadron pilot into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new YesNoDecision("Take a non-[Virtual Block 1] Alderaan or Dressel into hand from Reserve Deck?") {
                                @Override
                                protected void yes() {
                                    action.appendEffect(
                                            new TakeCardIntoHandFromReserveDeckEffect(action, playerId, alderaanOrDressel(), false));
                                    askForPilot(action, playerId);
                                }

                                @Override
                                protected void no() {
                                    askForPilot(action, playerId);
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }

    private static void askForPilot(final TopLevelGameTextAction action, final String playerId) {
        action.appendEffect(
                new PlayoutDecisionEffect(action, playerId,
                        new YesNoDecision("Take a [Death Star II] Rogue Squadron pilot (except Tycho) into hand from Reserve Deck?") {
                            @Override
                            protected void yes() {
                                action.appendEffect(
                                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, rogueSquadronPilot(), true));
                            }

                            @Override
                            protected void no() {
                                action.appendEffect(
                                        new ShuffleReserveDeckEffect(action, playerId));
                            }
                        }));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ROGUE_SQUADRON_TACTICS__DRAW_TOP_CARD;

        // Check condition(s)
        if (TriggerConditions.forceDrainCompleted(game, effectResult, playerId, Filters.battleground_system)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw top card of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
