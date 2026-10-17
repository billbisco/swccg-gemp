package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelReactEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Bewil
 */
public class Card1220_004 extends AbstractImperial {
    public Card1220_004() {
        super(Side.DARK, 3, 2, 2, 2, 4, "Captain Bewil", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setInfluence(1);
        setLore("Tactical officer from Dentaal. Leader. Familiar with utilizing computer controls to lure an invading enemy into a tactically weak position.");
        setGameText("Agenda: logistics. While at Conference Room, opponent must lose 3 Force to 'react' away from a battle, and, if your total Influence here is > 3, during your turn you may take an Interrupt into hand from Force Pile; reshuffle.");
        addIcons(Icon.WARRIOR, Icon.CLOUD_CITY);
        addKeywords(Keyword.LEADER, Keyword.CAPTAIN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.LOGISTICS));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        GameState gameState = game.getGameState();

        if (GameConditions.isAtLocation(game, self, Filters.Death_Star_Conference_Room)
                && TriggerConditions.isMovingAsReact(game, effect, Filters.and(Filters.opponents(self), Filters.at(Filters.battleLocation)))
                && gameState.getMoveAsReactState() != null
                && gameState.getMoveAsReactState().getReactActionOption() != null
                && gameState.getMoveAsReactState().getReactActionOption().isReactAway()) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 3 Force to 'react' away");
            int available = gameState.getForcePileSize(opponent) + gameState.getReserveDeckSize(opponent);
            if (available >= 3) {
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 3, true));
            }
            else {
                action.appendEffect(
                        new CancelReactEffect(action));
            }
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CAPTAIN_BEWIL__UPLOAD_INTERRUPT_FROM_FORCE_PILE;

        if (GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.isAtLocation(game, self, Filters.Death_Star_Conference_Room)
                && game.getModifiersQuerying().getTotalInfluenceAtLocation(game.getGameState(), playerId, game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self)) > 3
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Interrupt into hand from Force Pile");
            action.setActionMsg("Take an Interrupt into hand from Force Pile");
            action.appendEffect(
                    new TakeCardIntoHandFromForcePileEffect(action, playerId, Filters.Interrupt, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
