package com.gempukku.swccgo.cards.set1106.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PlayersTurnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Bewil (V)
 */
public class Card1106_031 extends AbstractImperial {
    public Card1106_031() {
        super(Side.DARK, 3, 3, 2, 2, 4, "Captain Bewil", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("Tactical officer from Dentaal. Leader. Familiar with utilizing computer controls to lure an invading enemy into a tactically weak position.");
        setGameText("During your turn, opponent's characters may not move away from same site. During your deploy phase, may ▼ one Restricted Access. During any control phase, may search your Force Pile and take an Interrupt into hand; reshuffle.");
        addIcons(Icon.CLOUD_CITY, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.CAPTAIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveFromLocationModifier(self, Filters.and(Filters.opponents(self), Filters.character),
                new PlayersTurnCondition(playerId), Filters.sameSite(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OVS__CAPTAIN_BEWIL__DEPLOY_RESTRICTED_ACCESS;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Restricted Access from Reserve Deck");
            action.setActionMsg("Deploy Restricted Access from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Restricted_Access, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OVS__CAPTAIN_BEWIL__SEARCH_FORCE_PILE;
        if (GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Interrupt into hand from Force Pile");
            action.setActionMsg("Take an Interrupt into hand from Force Pile");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromForcePileEffect(action, playerId, Filters.Interrupt, true));
            actions.add(action);
        }

        return actions;
    }
}
