package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Rebel
 * Title: Ten Numb (V)
 */
public class Card1273_028 extends AbstractRebel {
    public Card1273_028() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, Title.Ten_Numb, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Sullustan gunner. Pilot of Blue 5. Ordered to head a B-wing attack against Imperial command ships at the Battle Of Endor.");
        setGameText("Adds 2 to the power of anything he pilots. Rogue Squadron pilot. While piloting a B-wing or X-wing, draws one battle destiny if unable to otherwise and during battle (unless Outrider on table), may reduce the power of one opponent's starfighter at same system by 4.");
        addIcons(Icon.PILOT);
        addKeywords(Keyword.ROGUE_SQUADRON, Keyword.GUNNER, Keyword.BLUE_SQUADRON);
        setSpecies(Species.SULLUSTAN);
        setMatchingStarshipFilter(Filters.Blue_Squadron_5);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.or(Filters.B_wing, Filters.X_wing)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__TEN_NUMB_V__REDUCE_STARFIGHTER_POWER;
        final Filter opponentsStarfighter = Filters.and(Filters.opponents(self), Filters.starfighter, Filters.atSameLocation(self), Filters.at(Filters.system));

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isPiloting(game, self, Filters.or(Filters.B_wing, Filters.X_wing))
                && !GameConditions.canSpot(game, self, Filters.Outrider)
                && GameConditions.canTarget(game, self, opponentsStarfighter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reduce power of opponent's starfighter by 4");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", opponentsStarfighter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard starfighter) {
                            action.addAnimationGroup(starfighter);
                            // Allow response(s)
                            action.allowResponses("Reduce power of " + GameUtils.getCardLink(starfighter) + " by 4",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action,
                                                            new PowerModifier(self, Filters.sameCardId(finalTarget), -4),
                                                            "Reduces power of " + GameUtils.getCardLink(finalTarget) + " by 4"));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
