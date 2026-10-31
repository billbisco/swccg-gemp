package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.InitiateBattleCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Character
 * Subtype: Imperial
 * Title: Officer Evax
 */
public class Card1112_040 extends AbstractImperial {
    public Card1112_040() {
        super(Side.DARK, 2, 2, 2, 2, 5, "Officer Evax", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setInfluence(1);
        setLore("This Imperial Intelligence leader has a proven track record for predicting Rebel fleet movements. His coordination of starship maneuvers has saved many vulnerable bases.");
        setGameText("Imperial Council Member. Agenda: logistics. Once per turn, if at Conference Room, may target an opponent's starship on table with power < total influence here. Opponent must use +2 Force to move target (or initiate battle there) this turn.");
        addIcons(Icon.PILOT, Icon.A_NEW_HOPE);
        addKeywords(Keyword.IMPERIAL_COUNCIL_MEMBER, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.LOGISTICS));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = opponentStarshipWithPowerLessThanInfluenceHere(self);

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isAtLocation(game, self, Filters.Death_Star_Conference_Room)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Make opponent use +2 Force to move or battle");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's starship", targetFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Opponent must use +2 Force to move " + GameUtils.getCardLink(targetedCard) + " or initiate battle there",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            String opponent = game.getOpponent(playerId);
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new MoveCostModifier(self, Filters.sameCardId(targetedCard), 2),
                                                            "Opponent uses +2 Force to move " + GameUtils.getCardLink(targetedCard)));
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new InitiateBattleCostModifier(self, Filters.sameLocation(targetedCard), 2, opponent),
                                                            "Opponent uses +2 Force to initiate battle at " + GameUtils.getCardLink(targetedCard) + "'s location"));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    private Filter opponentStarshipWithPowerLessThanInfluenceHere(final PhysicalCard self) {
        return Filters.and(Filters.opponents(self), Filters.starship, Filters.in_play, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard here = modifiersQuerying.getLocationThatCardIsAt(gameState, self);
                if (here == null)
                    return false;
                float influence = modifiersQuerying.getTotalInfluenceAtLocation(gameState, self.getOwner(), here);
                return modifiersQuerying.getPower(gameState, physicalCard) < influence;
            }
        });
    }
}
