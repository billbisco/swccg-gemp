package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ExchangedCardsInCardPileResult;
import java.util.List;

/** Virtual Block6.3: Brangus Glee(V). */
public class Card1263_002 extends AbstractAlien {
    public Card1263_002() {
        super(Side.DARK, 3, 3, 2, 4, 4, Title.Brangus_Glee, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Elder of a dextrous race of renowned travelers. Gambler. Frequents casinos, bars and spaceports. Originally from distant planet Dor Nameth.");
        setGameText("Adds 2 to power of anything he pilots. Deploys -1 and forfeit +1 at a docking bay or aboard a starship. During your control phase, if at a battleground, may use 1 Force (free if at Casino) to exchange a card in hand with a random card in Lost Pile. Immune to attrition < 3.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_6);
        addKeyword(Keyword.GAMBLER);
        setAsLegacy(true);
    }
    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new DeployCostToTargetModifier(self, -1, Filters.or(
                Filters.locationAndCardsAtLocation(Filters.docking_bay), Filters.starship,
                Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(Filters.starship)))));
    }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 2),
                new ForfeitModifier(self, new OrCondition(new AtCondition(self, Filters.docking_bay), new AboardCondition(self, Filters.starship)), 1),
                new ImmuneToAttritionLessThanModifier(self, 3));
    }
    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        int cost = GameConditions.isAtLocation(game, self, Filters.titleContains("Casino")) ? 0 : 1;
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && GameConditions.isAtLocation(game, self, Filters.battleground)
                && GameConditions.canUseForce(game, playerId, cost)
                && GameConditions.hasInHand(game, playerId, Filters.any)
                && GameConditions.hasLostPile(game, playerId)) {
            var action = new TopLevelGameTextAction(self, sourceId);
            action.setText("Exchange hand card with random Lost Pile card");
            action.appendTargeting(new ChooseCardFromHandEffect(action, playerId, Filters.any) {
                @Override
                protected void cardSelected(SwccgGame game, PhysicalCard handCard) {
                    action.appendEffect(new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            var state = game.getGameState();
                            if (handCard.getZone() != Zone.HAND || !handCard.getOwner().equals(playerId)) return;
                            var cards = GameUtils.getRandomCards(game, state.getLostPile(playerId), 1);
                            if (cards.isEmpty()) return;
                            var lostCard = cards.getFirst();
                            int position = state.getLostPile(playerId).indexOf(lostCard);
                            state.sendMessage(playerId + " exchanges " + GameUtils.getCardLink(handCard) + " with " + GameUtils.getCardLink(lostCard) + " in Lost Pile");
                            state.removeCardFromZone(handCard);
                            state.addCardToSpecificPositionInZone(handCard, Zone.LOST_PILE, playerId, position);
                            state.removeCardFromZone(lostCard);
                            state.addCardToZone(lostCard, Zone.HAND, playerId);
                            game.getActionsEnvironment().emitEffectResult(new ExchangedCardsInCardPileResult(action));
                        }
                    });
                }
            });
            if (cost > 0) action.appendCost(new UseForceEffect(action, playerId, cost));
            return List.of(action);
        }
        return null;
    }
}
