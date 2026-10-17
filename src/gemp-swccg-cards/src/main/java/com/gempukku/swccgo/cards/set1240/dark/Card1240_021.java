package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractPoliticalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Subtype: Political
 * Title: Blown Clear
 */
public class Card1240_021 extends AbstractPoliticalEffect {
    public Card1240_021() {
        super(Side.DARK, 2, "Blown Clear", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Vader was nearly killed when Han damaged his TIE fighter during a surprise attack in the Death Star trench.");
        setGameText("Deploy on table. May place an Imperial Council Member here from hand. Once per turn, if an admiral or logistics agenda here, may subtract X from a starship's forfeit and immunity to attrition for remainder of turn, where X = total influence at Conference Room.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        if (GameConditions.hasInHand(game, playerId, Filters.and(Filters.your(self), Keyword.IMPERIAL_COUNCIL_MEMBER))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place an Imperial Council Member here");
            action.appendEffect(
                    new StackCardFromHandEffect(action, playerId, self, Filters.and(Filters.your(self), Keyword.IMPERIAL_COUNCIL_MEMBER)));
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter starship = Filters.and(Filters.starship, Filters.in_play);
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && (GameConditions.hasStackedCards(game, self, Filters.admiral)
                    || GameConditions.hasStackedCards(game, self, Agenda.LOGISTICS))
                && GameConditions.canTarget(game, self, starship)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Subtract influence from a starship");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            final float amount = totalInfluenceAtConferenceRoom(game, playerId);
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a starship", starship) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses("Subtract " + amount + " from " + GameUtils.getCardLink(targetedCard) + "'s forfeit and immunity to attrition",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new ForfeitModifier(self, Filters.sameCardId(targetedCard), -amount),
                                                            "Subtract " + amount + " from forfeit"));
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new ImmunityToAttritionChangeModifier(self, Filters.sameCardId(targetedCard), -amount),
                                                            "Subtract " + amount + " from immunity to attrition"));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    private float totalInfluenceAtConferenceRoom(SwccgGame game, String owner) {
        PhysicalCard room = Filters.findFirstActive(game, null, Filters.Death_Star_Conference_Room);
        if (room == null) {
            return 0;
        }
        return game.getModifiersQuerying().getTotalInfluenceAtLocation(game.getGameState(), owner, room);
    }
}
