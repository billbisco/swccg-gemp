package com.gempukku.swccgo.cards.set1111.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Starship
 * Subtype: Starfighter
 * Title: Green Squadron 1
 */
public class Card1111_005 extends AbstractStarfighter {
    public Card1111_005() {
        super(Side.LIGHT, 3, 2, 3, null, 5, 4, 4, "Green Squadron 1", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("Constructed at Chardaan Shipyards, a zero-gravity facility. Typically used as an escort for Y-wings or B-wings on critical attack missions.");
        setGameText("May add Green Leader as a pilot. Once per turn, may take Diversionary Tactics into hand from Reserve Deck; reshuffle. If with Executor at end of battle, may draw destiny; Executor and this starfighter lost if destiny > 5.");
        addPersona(Persona.GREEN_SQUADRON_1);
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.A_WING);
        addKeywords(Keyword.GREEN_SQUADRON);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Green_Leader);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__GREEN_SQUADRON_1__TAKE_DIVERSIONARY_TACTICS;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Diversionary Tactics into hand from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title("Diversionary Tactics"), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.battleEndingAt(game, effectResult, Filters.here(self))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Executor)
                && GameConditions.isInBattle(game, self)) {
            final PhysicalCard executor = Filters.findFirstActive(game, self, Filters.Executor);
            if (executor == null) {
                return null;
            }
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw destiny");
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (totalDestiny != null && totalDestiny > 5) {
                                action.appendEffect(new LoseCardFromTableEffect(action, self));
                                action.appendEffect(new LoseCardFromTableEffect(action, executor));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
