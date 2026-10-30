package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Alien
 * Title: Bib Fortuna
 */
public class Card1230_002 extends AbstractAlien {
    public Card1230_002() {
        super(Side.DARK, 1, 3, 3, 1, 4, Title.Bib, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Twi'lek leader and majordomo of Jabba's palace. Succeeded Jabba's last majordomo, Naroon Cuthus. Plotting to kill Jabba.");
        setGameText("Opponent's non-Jedi characters are deploy +1 to [Jabba's Palace] sites. Once during your control phase, you may place three cards from hand in Used Pile to reveal top card of Reserve Deck; if it is an alien, take it into hand.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(com.gempukku.swccgo.common.Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.character, Filters.not(Filters.Jedi)), 1, Filters.Jabbas_Palace_site));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BIB_FORTUNA_V__REVEAL_TOP_OF_RESERVE;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.numCardsInHand(game, playerId) >= 3
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal top card of Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new PutCardsFromHandOnUsedPileEffect(action, playerId, 3, 3));
            action.appendEffect(
                    new RevealTopCardOfReserveDeckEffect(action, playerId) {
                        @Override
                        protected void cardRevealed(PhysicalCard revealedCard) {
                            if (Filters.alien.accepts(game, revealedCard)) {
                                action.appendEffect(
                                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, revealedCard, false));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
