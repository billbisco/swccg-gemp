package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Admiral's Order
 * Title: Taking Them With Us
 */
public class Card1230_062 extends AbstractAdmiralsOrder {
    public Card1230_062() {
        super(Side.LIGHT, Title.Taking_Them_With_Us, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Each player may, once per battle at a system, lose 1 Force to target two participating unique (•) piloted starships with immunity to attrition (one from each player); both starships lose all immunity to attrition this turn. When deploying to capital starships, your Mon Calamari pilots are deploy = 1.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetDeployCostToTargetModifier(self, Filters.and(Filters.your(playerId), Filters.pilot, Filters.species(Species.MON_CALAMARI)), 1, Filters.capital_starship));
        return modifiers;
    }

    private List<TopLevelGameTextAction> getLoseImmunityAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter uniquePilotedWithImmunity = Filters.and(Filters.unique, Filters.piloted, Filters.starship, Filters.participatingInBattle, Filters.hasAnyImmunityToAttrition);
        Filter yourStarship = Filters.and(Filters.your(playerId), uniquePilotedWithImmunity);
        Filter opponentsStarship = Filters.and(Filters.opponents(playerId), uniquePilotedWithImmunity);

        if (GameConditions.isDuringBattleAt(game, Filters.system)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTarget(game, self, yourStarship)
                && GameConditions.canTarget(game, self, opponentsStarship)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Cancel immunity to attrition");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose your unique piloted starship", yourStarship) {
                        @Override
                        protected void cardTargeted(final int yourGroupId, final PhysicalCard yourCard) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's unique piloted starship", opponentsStarship) {
                                        @Override
                                        protected void cardTargeted(final int oppGroupId, final PhysicalCard oppCard) {
                                            action.addAnimationGroup(yourCard, oppCard);
                                            action.allowResponses("Cancel immunity to attrition of " + GameUtils.getCardLink(yourCard) + " and " + GameUtils.getCardLink(oppCard),
                                                    new UnrespondableEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            PhysicalCard finalYours = action.getPrimaryTargetCard(yourGroupId);
                                                            PhysicalCard finalOpponents = action.getPrimaryTargetCard(oppGroupId);
                                                            action.appendEffect(
                                                                    new CancelImmunityToAttritionUntilEndOfTurnEffect(action, Filters.or(Filters.sameCardId(finalYours), Filters.sameCardId(finalOpponents)),
                                                                            "Cancels immunity to attrition"));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return getLoseImmunityAction(playerId, game, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return getLoseImmunityAction(playerId, game, self, gameTextSourceCardId);
    }
}
