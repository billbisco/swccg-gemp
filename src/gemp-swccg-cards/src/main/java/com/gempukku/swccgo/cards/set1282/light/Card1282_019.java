package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyIKnowThatLaugh;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ResetForfeitUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.StackOneCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Subtype: Alien
 * Title: Rennek (V)
 */
public class Card1282_019 extends AbstractAlien {
    public Card1282_019() {
        super(Side.LIGHT, 2, 2, 3, 1, 3, Title.Rennek, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Nerf herder. Personally oversees the herding of Jabba's nerfs. Scruffy-looking appearance conceals his expertise in unusual combat techniques.");
        setGameText("May be stacked on I Know That Laugh from hand, using 2 Force. While stacked on I Know That Laugh, stack face-up and during any battle may draw destiny. If destiny < number of your aliens in battle, reset a participating character's forfeit to 0.");
        addIcons(Icon.WARRIOR);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final PhysicalCard iKnowThatLaugh = Filters.findFirstActive(game, self, LegacyIKnowThatLaugh.yourIKnowThatLaugh(playerId));
        if (iKnowThatLaugh != null
                && GameConditions.canUseForce(game, playerId, 2)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Stack on I Know That Laugh");
            action.setActionMsg("Stack " + GameUtils.getCardLink(self) + " on " + GameUtils.getCardLink(iKnowThatLaugh));
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new StackOneCardFromHandEffect(action, self, iKnowThatLaugh, false));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelWhileStackedActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Stacked face up (I Know That Laugh and Under Jabba's Protection stack this card face up)
        if (self.getStackedOn() != null
                && LegacyIKnowThatLaugh.iKnowThatLaugh().accepts(game, self.getStackedOn())
                && GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canDrawDestiny(game, playerId)) {
            final Filter participatingCharacter = Filters.and(Filters.character, Filters.participatingInBattle);
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Draw destiny");
            action.setActionMsg("Draw destiny to reset a participating character's forfeit to 0");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            int aliens = Filters.countActive(game, self, Filters.and(Filters.your(playerId), Filters.alien, Filters.participatingInBattle));
                            if (totalDestiny != null && totalDestiny < aliens) {
                                game.getGameState().sendMessage("Result: Succeeded");
                                action.appendEffect(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose character to reset forfeit to 0", participatingCharacter) {
                                            @Override
                                            protected void cardSelected(PhysicalCard selectedCard) {
                                                action.appendEffect(
                                                        new ResetForfeitUntilEndOfTurnEffect(action, selectedCard, 0));
                                            }
                                        });
                            }
                            else {
                                game.getGameState().sendMessage("Result: Failed");
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
