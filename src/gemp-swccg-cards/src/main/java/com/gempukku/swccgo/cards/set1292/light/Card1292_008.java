package com.gempukku.swccgo.cards.set1292.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.BreakCoverEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Set: Virtual Block 9.2
 * Type: Character
 * Subtype: Alien
 * Title: Dexter Jettster
 */
public class Card1292_008 extends AbstractAlien {
    public Card1292_008() {
        super(Side.LIGHT, 3, 3, 3, 3, 4, "Dexter Jettster", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_2, Rarity.V);
        setLore("Information broker.");
        setGameText("Deploys -1 to Coruscant or to same location as Obi-Wan. Adds one battle destiny with Jango. During your move phase, may use X Force to \"break cover\" of opponent's Undercover spy at same site, where X = spy's printed deploy cost -2.");
        addKeywords(Keyword.INFORMATION_BROKER);
        setSpecies(Species.BESALISK);
        addIcons(Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.or(Filters.Coruscant_location, Filters.sameLocationAs(self, Filters.ObiWan))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.Jango_Fett), 1));
        return modifiers;
    }

    /**
     * X = spy's printed deploy cost - 2 (not less than 0).
     */
    private static int breakCoverCost(PhysicalCard spy) {
        Float printedDeployCost = spy.getBlueprint().getDeployCost();
        return Math.max(0, (int) Math.ceil((printedDeployCost != null ? printedDeployCost : 0) - 2));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Map<InactiveReason, Boolean> spotOverride = SpotOverride.INCLUDE_UNDERCOVER;
        Filter spyFilter = Filters.and(Filters.opponents(self), Filters.undercover_spy, Filters.atSameSite(self));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)) {
            List<PhysicalCard> affordableSpies = new LinkedList<PhysicalCard>();
            for (PhysicalCard spy : Filters.filterActive(game, self, spotOverride, spyFilter)) {
                if (GameConditions.canUseForce(game, playerId, breakCoverCost(spy))) {
                    affordableSpies.add(spy);
                }
            }
            final Filter targetFilter = Filters.and(spyFilter, Filters.in(affordableSpies));
            if (!affordableSpies.isEmpty()
                    && GameConditions.canTarget(game, self, spotOverride, targetFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("'Break cover' of an Undercover spy");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target opponent's Undercover spy", spotOverride, targetFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard spy) {
                                action.addAnimationGroup(spy);
                                int cost = breakCoverCost(spy);
                                // Pay cost(s)
                                if (cost > 0) {
                                    action.appendCost(
                                            new UseForceEffect(action, playerId, cost));
                                }
                                // Allow response(s)
                                action.allowResponses("'Break cover' of " + GameUtils.getCardLink(spy),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new BreakCoverEffect(action, action.getPrimaryTargetCard(targetGroupId)));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
