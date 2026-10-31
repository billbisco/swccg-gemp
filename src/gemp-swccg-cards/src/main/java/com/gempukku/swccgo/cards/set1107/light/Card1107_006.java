package com.gempukku.swccgo.cards.set1107.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeBattledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 7: Shadows Of The Empire
 * Type: Character
 * Subtype: Droid
 * Title: LE-BO2D9 (Leebo) (V)
 */
public class Card1107_006 extends AbstractDroid {
    public Card1107_006() {
        super(Side.LIGHT, 3, 3, 3, 3, Title.Leebo, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_7, Rarity.V);
        setVirtualSuffix(true);
        setArmor(4);
        setLore("Smuggler. Heavily modified Cybot Galactica LE-series repair droid. Information broker. Provides information gained through underworld channels to the Alliance.");
        setGameText("May be battled. If with Dash Rendar, adds one destiny to attrition only. Once per turn, if at a battleground, you may peek at the top card of opponent's Reserve Deck; may place that card on top of opponent's Used Pile.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT);
        addKeywords(Keyword.SMUGGLER, Keyword.INFORMATION_BROKER);
        addModelTypes(ModelType.MAINTENANCE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayBeBattledModifier(self));
        modifiers.add(new AddsDestinyToAttritionModifier(self, new WithCondition(self, Filters.Dash), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isAtLocation(game, self, Filters.battleground)
                && GameConditions.hasReserveDeck(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at top of opponent's Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, opponent) {
                        @Override
                        protected void cardPeekedAt(final PhysicalCard peekedAtCard) {
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, playerId,
                                            new YesNoDecision("Do you want to place " + GameUtils.getCardLink(peekedAtCard) + " on Used Pile?") {
                                                @Override
                                                protected void yes() {
                                                    action.appendEffect(
                                                            new PutCardFromReserveDeckOnTopOfCardPileEffect(action, peekedAtCard, Zone.USED_PILE, true));
                                                }
                                                @Override
                                                protected void no() {
                                                    game.getGameState().sendMessage(playerId + " chooses to not place card on Used Pile");
                                                }
                                            }
                                    )
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
