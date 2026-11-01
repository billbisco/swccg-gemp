package com.gempukku.swccgo.cards.set1113.light;

import com.gempukku.swccgo.cards.AbstractNonuniqueStarshipSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Location
 * Subtype: Site
 * Title: Redemption: Command Post
 */
public class Card1113_011 extends AbstractNonuniqueStarshipSite {
    public Card1113_011() {
        super(Side.LIGHT, Title.Redemption_Command_Post, Filters.title("Redemption"), Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setLocationLightSideGameText("During your move phase, may relocate your leader here to a site you occupy.");
        setLocationDarkSideGameText("While Redemption is at a system you do not occupy, you may only deploy spies here.");
        setGameText("Light: During your move phase, may relocate your leader here to a site you occupy. Dark: While Redemption is at a system you do not occupy, you may only deploy spies here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.A_NEW_HOPE, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self,
                Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.not(Filters.spy)),
                new OnTableCondition(self, Filters.and(Filters.relatedStarshipOrVehicle(self),
                        Filters.at(Filters.and(Filters.system, Filters.not(Filters.occupies(playerOnDarkSideOfLocation)))))),
                self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, Phase.MOVE)) {
            final Filter siteYouOccupyFilter = Filters.and(Filters.site, Filters.otherLocation(self), Filters.occupies(playerOnLightSideOfLocation));
            // allowSameLocation: leader is at this starship site (aboard Redemption); default relocate rejects aboardAnyStarship
            Filter leaderFilter = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.leader, Filters.here(self),
                    Filters.canBeRelocatedToLocation(siteYouOccupyFilter, false, false, true, 0, false, true));
            if (GameConditions.canSpot(game, self, leaderFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
                action.setText("Relocate a leader here to a site you occupy");
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerOnLightSideOfLocation, "Choose leader", leaderFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard leaderTargeted) {
                                Filter siteToRelocateLeader = Filters.and(siteYouOccupyFilter, Filters.locationCanBeRelocatedTo(leaderTargeted, false, false, true, 0, false, true));
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerOnLightSideOfLocation, "Choose site to relocate " + GameUtils.getCardLink(leaderTargeted) + " to", siteToRelocateLeader) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard siteSelected) {
                                                action.addAnimationGroup(leaderTargeted);
                                                action.addAnimationGroup(siteSelected);
                                                action.appendCost(
                                                        new PayRelocateBetweenLocationsCostEffect(action, playerOnLightSideOfLocation, leaderTargeted, siteSelected, 0));
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(leaderTargeted) + " to " + GameUtils.getCardLink(siteSelected),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, leaderTargeted, siteSelected));
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
