package com.gempukku.swccgo.cards.set1111.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Starship
 * Subtype: Capital
 * Title: Medium Transport
 */
public class Card1111_010 extends AbstractCapitalStarship {
    public Card1111_010() {
        super(Side.LIGHT, 3, 2, 1, 3, null, 4, 4, "Medium Transport", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("Passenger and cargo transport built by Gallofree Yards. Enormous cargo bays can be outfitted to safely accommodate large numbers of troops, munitions or supplies.");
        setGameText("May add 1 pilot, 4 passengers and 1 vehicle. Deploys and moves like a starfighter. Permanent pilot provides no ability. During battle, may target an opponent's capital starship present. Draw destiny. Target and this starship are lost if destiny + 2 > target's defense value.");
        addIcons(Icon.HOTH, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.MEDIUM_TRANSPORT);
        addModelType(ModelType.TRANSPORT);
        setPilotCapacity(1);
        setPassengerCapacity(4);
        setVehicleCapacity(1);
    }

    @Override
    public boolean isDeploysLikeStarfighter() {
        return true;
    }

    @Override
    public boolean isMovesLikeStarfighter() {
        return true;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(0) {});
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.capital_starship, Filters.present(self));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Target opponent's capital starship");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target opponent's capital starship", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Target " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard target = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            if (totalDestiny == null || target == null) {
                                                                return;
                                                            }
                                                            float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), target);
                                                            if ((totalDestiny + 2) > defenseValue) {
                                                                action.appendEffect(new LoseCardFromTableEffect(action, target));
                                                                action.appendEffect(new LoseCardFromTableEffect(action, self));
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
