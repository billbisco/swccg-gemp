package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: You Will Go To The Dagobah System
 */
public class Card1220_164 extends AbstractUsedInterrupt {
    public Card1220_164() {
        super(Side.LIGHT, 4, Title.You_Will_Go_To_The_Dagobah_System, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'There you will learn from Yoda, the Jedi Master who instructed me.'");
        setGameText("Deploy a character, device, starship, vehicle, or weapon to a Dagobah site (regardless of Dagobah deployment restrictions) from Reserve Deck; reshuffle. OR During your control phase, relocate your character to a Dagobah site.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId deployId = GameTextActionId.LEGACY__YOU_WILL_GO_TO_THE_DAGOBAH_SYSTEM_V__DEPLOY;
        Filter cardFilter = Filters.or(Filters.character, Filters.device, Filters.starship, Filters.vehicle, Filters.weapon);
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId)
                && GameConditions.canSpot(game, self, Filters.Dagobah_site)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, deployId);
            action.setText("Deploy card to Dagobah site from Reserve Deck");
            action.allowResponses("Deploy a character, device, starship, vehicle, or weapon to a Dagobah site from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardToLocationFromReserveDeckEffect(action, cardFilter, Filters.Dagobah_site, false, DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(), true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)) {
            final Filter locationFilter = Filters.Dagobah_site;
            Filter characterFilter = Filters.and(Filters.your(self), Filters.character,
                    Filters.canBeRelocatedToLocation(locationFilter, true, false, true, 0, false));
            if (GameConditions.canTarget(game, self, characterFilter)
                    && GameConditions.canSpot(game, self, locationFilter)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Relocate character to a Dagobah site");
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", characterFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard character) {
                                action.addAnimationGroup(character);
                                Collection<PhysicalCard> locations = Filters.filterTopLocationsOnTable(game, locationFilter);
                                Collection<PhysicalCard> validLocations = new LinkedList<PhysicalCard>();
                                for (PhysicalCard location : locations) {
                                    if (Filters.canBeRelocatedToLocation(location, true, false, true, 0, false).accepts(game.getGameState(), game.getModifiersQuerying(), character)) {
                                        validLocations.add(location);
                                    }
                                }
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose Dagobah site", Filters.in(validLocations)) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard location) {
                                                action.addAnimationGroup(location);
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(character) + " to " + GameUtils.getCardLink(location),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId1);
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, finalCharacter, location));
                                                            }
                                                        }
                                                );
                                            }
                                        });
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}
