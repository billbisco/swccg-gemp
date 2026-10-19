package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Location
 * Subtype: Site
 * Title: Corulag: ISB Logistics Office
 */
public class Card1272_011 extends AbstractSite {
    public Card1272_011() {
        super(Side.DARK, "Corulag: ISB Logistics Office", Title.Corulag, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setLocationDarkSideGameText("During your move phase, may use 1 Force to relocate up to three ISB agents between two sites with 'ISB' in title.");
        setLocationLightSideGameText("If you control, ISB agents are deploy +1 here. Immune to Revolution.");
        setGameText("Dark: During your move phase, may use 1 Force to relocate up to three ISB agents between two sites with 'ISB' in title. Light: If you control, ISB agents are deploy +1 here. Immune to Revolution.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.ISB_agent, new ControlsCondition(playerOnLightSideOfLocation, self), 1, self));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ISB_LOGISTICS_OFFICE__RELOCATE_ISB_AGENTS;
        Filter isbSite = Filters.and(Filters.site, Filters.titleContains("ISB"));

        if (GameConditions.isDuringYourPhase(game, playerOnDarkSideOfLocation, Phase.MOVE)
                && GameConditions.canUseForce(game, playerOnDarkSideOfLocation, 1)
                && GameConditions.canSpotLocation(game, 2, isbSite)) {

            Collection<PhysicalCard> fromSites = Filters.filterTopLocationsOnTable(game, Filters.and(isbSite, Filters.sameLocationAs(self, Filters.ISB_agent)));
            if (!fromSites.isEmpty()) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate ISB agents");
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerOnDarkSideOfLocation, "Choose site to relocate from", Filters.in(fromSites)) {
                            @Override
                            protected void cardSelected(final PhysicalCard fromSite) {
                                Filter toSiteFilter = Filters.and(Filters.site, Filters.titleContains("ISB"), Filters.not(fromSite));
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerOnDarkSideOfLocation, "Choose site to relocate to", toSiteFilter) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard toSite) {
                                                Filter agents = Filters.and(Filters.ISB_agent, Filters.at(fromSite), Filters.canBeRelocatedToLocation(toSite, true, 0));
                                                action.appendTargeting(
                                                        new TargetCardsOnTableEffect(action, playerOnDarkSideOfLocation, "Choose ISB agents to relocate", 1, 3, agents) {
                                                            @Override
                                                            protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> cardsToRelocate) {
                                                                action.addAnimationGroup(cardsToRelocate);
                                                                action.addAnimationGroup(toSite);
                                                                action.appendCost(
                                                                        new UseForceEffect(action, playerOnDarkSideOfLocation, 1));
                                                                action.allowResponses("Relocate " + GameUtils.getAppendedNames(cardsToRelocate) + " to " + GameUtils.getCardLink(toSite),
                                                                        new UnrespondableEffect(action) {
                                                                            @Override
                                                                            protected void performActionResults(Action targetingAction) {
                                                                                Collection<PhysicalCard> finalAgents = action.getPrimaryTargetCards(targetGroupId);
                                                                                action.appendEffect(
                                                                                        new RelocateBetweenLocationsEffect(action, finalAgents, toSite));
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
