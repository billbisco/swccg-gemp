package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayInterruptFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Rebel
 * Title: General Solo
 */
public class Card1230_068 extends AbstractRebel {
    public Card1230_068() {
        super(Side.LIGHT, 1, 4, 4, 3, 8, Title.General_Solo, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Scout. Leader of the Rebel strike team on Endor. Needed the cleverness of a Corellian to destroy the Endor control bunker. Discovered the truth about Luke and Leia.");
        setGameText("Adds 3 to power of anything he pilots. During battle, may use 1 Force to play an Interrupt from your Reserve Deck (then place that card out of play). Once per turn, may take a non-Interrupt card with 'door' in game text into hand from Reserve Deck; reshuffle.");
        addPersona(Persona.HAN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.ENDOR);
        addKeywords(Keyword.SCOUT, Keyword.LEADER, Keyword.GENERAL);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__GENERAL_SOLO_V__PLAY_INTERRUPT;

        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canPlayInterruptFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Play an Interrupt from Reserve Deck");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new PlayInterruptFromReserveDeckEffect(action, true, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LEGACY__GENERAL_SOLO_V__TAKE_DOOR;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a non-Interrupt card with 'door' in game text into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.non_Interrupt, Filters.or(Filters.gameTextContains("door"), Filters.gameTextContains("doors"))), true));
            actions.add(action);
        }

        return actions;
    }
}
