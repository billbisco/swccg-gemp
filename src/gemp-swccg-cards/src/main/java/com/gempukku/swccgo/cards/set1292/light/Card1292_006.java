package com.gempukku.swccgo.cards.set1292.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.UndercoverSpyDeployRestriction;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnBottomOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerByExistingCardPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.2
 * Type: Character
 * Subtype: Rebel
 * Title: Boushh (V)
 */
public class Card1292_006 extends AbstractRebel implements UndercoverSpyDeployRestriction {
    public Card1292_006() {
        super(Side.LIGHT, 1, 4, 3, 4, 7, Title.Boushh, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_2, Rarity.V);
        setVirtualSuffix(true);
        setArmor(4);
        setLore("Leia obtained the armor of a notorious mercenary to sneak onto Coruscant. She later assumed the same role to spy on Jabba. Fearless and inventive. Jabba's kind of scum.");
        setGameText("May deploy as an Undercover spy to Coruscant, Tatooine, or same site as a gangster. Power +2 while alone or with a scoundrel. During battle, may peek at top card of a Reserve Deck; may place that card under that deck. Immune to attrition < 3.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.PREMIUM);
        addKeywords(Keyword.SPY, Keyword.FEMALE);
        addPersona(Persona.LEIA);
        setSpecies(Species.ALDERAANIAN);
        setMayDeployAsUndercoverSpy(true);
    }

    @Override
    public Filter getMayDeployAsUndercoverSpyToFilter(SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.site, Filters.or(Filters.Coruscant_site, Filters.Tatooine_site, Filters.sameSiteAs(self, Filters.gangster)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new OrCondition(new AloneCondition(self), new WithCondition(self, Filters.scoundrel)), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, game.getOpponent(playerId)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at top card of a Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ChoosePlayerByExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                        @Override
                        protected void playerChosen(final SwccgGame game, final String pileOwner) {
                            action.appendEffect(
                                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, pileOwner) {
                                        @Override
                                        protected void cardPeekedAt(final PhysicalCard peekedAtCard) {
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, playerId,
                                                            new YesNoDecision("Place " + GameUtils.getCardLink(peekedAtCard) + " under that Reserve Deck?") {
                                                                @Override
                                                                protected void yes() {
                                                                    action.appendEffect(
                                                                            new PutCardFromReserveDeckOnBottomOfCardPileEffect(action, peekedAtCard, pileOwner, Zone.RESERVE_DECK, true));
                                                                }

                                                                @Override
                                                                protected void no() {
                                                                    game.getGameState().sendMessage(playerId + " chooses not to place card under Reserve Deck");
                                                                }
                                                            }));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
