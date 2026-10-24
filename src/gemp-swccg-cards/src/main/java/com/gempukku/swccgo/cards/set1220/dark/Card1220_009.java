package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardAndOrCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Imperial
 * Title: Corporal Vandolay
 */
public class Card1220_009 extends AbstractImperial {
    public Card1220_009() {
        super(Side.DARK, 2, 3, 2, 2, 5, "Corporal Vandolay", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("ISB attache to the Executor. Detention Officer. Political Liaison for COMPNOR. Responsible for all prisoner transfers. Fiercely loyal to the Emperor's New Order.");
        setGameText("Adds 2 to power of anything he pilots. Opponent's smugglers are forfeit -2. During any control phase, may take one Spice Mines Of Kessel into hand from Reserve Deck; reshuffle. Once per game, may take an Immediate Effect and/or a prison into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.opponents(self), Filters.smuggler), -2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId spiceActionId = GameTextActionId.LEGACY__VANDOLAY_V__TAKE_SPICE_MINES;
        if (GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, spiceActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, spiceActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, spiceActionId);
            action.setText("Take Spice Mines Of Kessel into hand");
            action.setActionMsg("Take Spice Mines Of Kessel into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Spice_Mines_Of_Kessel, true));
            actions.add(action);
        }

        GameTextActionId oncePerGameId = GameTextActionId.LEGACY__VANDOLAY_V__TAKE_IMMEDIATE_EFFECT_AND_OR_PRISON;
        if (GameConditions.isOncePerGame(game, self, oncePerGameId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, oncePerGameId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, oncePerGameId);
            action.setText("Take Immediate Effect and/or prison into hand");
            action.setActionMsg("Take an Immediate Effect and/or a prison into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new TakeCardAndOrCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Immediate_Effect, Filters.prison, true));
            actions.add(action);
        }

        return actions;
    }
}
