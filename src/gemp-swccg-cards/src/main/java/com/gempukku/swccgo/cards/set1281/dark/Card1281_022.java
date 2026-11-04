package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
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
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Sub City Lair
 */
public class Card1281_022 extends AbstractSite {
    public Card1281_022() {
        super(Side.DARK, "Coruscant: Sub City Lair", Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLocationDarkSideGameText("During your move phase, may relocate an assassin here to a site you occupy as a regular move.");
        setLocationLightSideGameText("Unless your spy present, Force drain -1 here.");
        setGameText("Dark: During your move phase, may relocate an assassin here to a site you occupy as a regular move. Light: Unless your spy present, Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.UNDERGROUND);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = playerOnDarkSideOfLocation;
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CORUSCANT_SUB_CITY_LAIR__RELOCATE_ASSASSIN;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)) {
            List<PhysicalCard> validAssassins = new ArrayList<PhysicalCard>();
            for (PhysicalCard assassin : Filters.filterActive(game, self,
                    Filters.and(Filters.your(playerId), Filters.assassin, Filters.here(self), Filters.hasNotPerformedRegularMove))) {
                if (GameConditions.canSpotLocation(game, siteToRelocateTo(playerId, self, assassin))) {
                    validAssassins.add(assassin);
                }
            }
            if (!validAssassins.isEmpty()) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate an assassin as a regular move");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose assassin", Filters.in(validAssassins)) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard assassin) {
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(assassin) + " to", siteToRelocateTo(playerId, self, assassin)) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard site) {
                                                action.addAnimationGroup(assassin);
                                                action.addAnimationGroup(site);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, assassin, site, 0));
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(assassin) + " to " + GameUtils.getCardLink(site),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, assassin, site, true));
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

    private static Filter siteToRelocateTo(String playerId, PhysicalCard self, PhysicalCard assassin) {
        return Filters.and(Filters.site, Filters.not(self), Filters.occupies(playerId), Filters.locationCanBeRelocatedTo(assassin, 0));
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self,
                new UnlessCondition(new PresentCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.spy))),
                -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}
