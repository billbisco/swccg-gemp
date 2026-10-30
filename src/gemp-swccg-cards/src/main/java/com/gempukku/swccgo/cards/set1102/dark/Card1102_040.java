package com.gempukku.swccgo.cards.set1102.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.complete.ChoosePileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Character
 * Subtype: Alien
 * Title: Labria
 */
public class Card1102_040 extends AbstractAlien {
    public Card1102_040() {
        super(Side.DARK, 3, 2, 1, 1, 3, "Labria", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Information broker. Spy. Devaronian males instinctively have 'wanderlust.' Frustrated that he must be reclusive due to shady past. Suffers from prejudice due to devilish appearance.");
        setGameText("Once per game, may deploy Undercover on Labria from Reserve Deck; reshuffle. During your control phase, may reveal top card of Reserve Deck. Place revealed card on top of Force Pile, Reserve Deck, or Used Pile.");
        addKeywords(Keyword.INFORMATION_BROKER, Keyword.SPY);
        setSpecies(Species.DEVARONIAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId undercoverId = GameTextActionId.LEGACY__LABRIA_V__DEPLOY_UNDERCOVER;
        if (GameConditions.isOncePerGame(game, self, undercoverId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, undercoverId, Title.Undercover)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, undercoverId);
            action.setText("Deploy Undercover from Reserve Deck");
            action.setActionMsg("Deploy Undercover on " + GameUtils.getCardLink(self) + " from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Undercover, Filters.sameCardId(self), false, true));
            actions.add(action);
        }

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Reveal top card of Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new RevealTopCardOfReserveDeckEffect(action, playerId) {
                        @Override
                        protected void cardRevealed(final PhysicalCard revealedCard) {
                            action.appendEffect(
                                    new ChoosePileEffect(action, playerId, "Choose card pile to put revealed card on top of",
                                            playerId, new Zone[]{Zone.RESERVE_DECK, Zone.FORCE_PILE, Zone.USED_PILE}) {
                                        @Override
                                        public void pileChosen(SwccgGame game, String cardPileOwner, Zone cardPile) {
                                            action.appendEffect(
                                                    new PutCardFromReserveDeckOnTopOfCardPileEffect(action, revealedCard, cardPile, false));
                                        }
                                    }
                            );
                        }
                    });
            actions.add(action);
        }

        return actions;
    }
}
