package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnBottomOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerByExistingCardPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Rebel Scout
 */
public class Card1104_016 extends AbstractRebel {
    public Card1104_016() {
        super(Side.LIGHT, 2, 2, 2, 1, 3, "Rebel Scout", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("A Rebel Scout such as Vidar Blin is usually assigned to recon missions. Trained in first-response tactics, many come to the Alliance with prior special forces experience.");
        setGameText("May deploy -1 as a 'react' to same site as a Rebel. When deployed, may peek at the top card of a Reserve Deck; may place that card on bottom of that Reserve Deck.");
        addIcons(Icon.WARRIOR, Icon.HOTH);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.sameSiteAs(self, Filters.Rebel), -1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, game.getOpponent(playerId)))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Peek at top of a Reserve Deck");
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
                                                            new YesNoDecision("Place " + GameUtils.getCardLink(peekedAtCard) + " on bottom of that Reserve Deck?") {
                                                                @Override
                                                                protected void yes() {
                                                                    action.appendEffect(
                                                                            new PutCardFromReserveDeckOnBottomOfCardPileEffect(action, peekedAtCard, pileOwner, Zone.RESERVE_DECK, true));
                                                                }

                                                                @Override
                                                                protected void no() {
                                                                    game.getGameState().sendMessage(playerId + " chooses not to place card on bottom of Reserve Deck");
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
