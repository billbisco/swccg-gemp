package com.gempukku.swccgo.cards.set1107.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 7: Shadows Of The Empire
 * Type: Effect
 * Title: Flagship Operations
 */
public class Card1107_014 extends AbstractNormalEffect {
    public Card1107_014() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Flagship_Operations, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_7, Rarity.V);
        setVirtualSuffix(true);
        setLore("'There's too many of them!'");
        setGameText("Deploy on a system. Your TIEs may relocate (as a regular move) to a system from 0 to 3 parsecs away. For each battleground system you control with two TIEs piloted by Imperials, you may add 3 to your total power in battles. Opponent's Force drain is +3 here. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.system;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), 3, opponent));
        return modifiers;
    }

    private int countQualifyingSystems(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        int count = 0;
        Collection<PhysicalCard> systems = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.system, Filters.battleground, Filters.controls(playerId)));
        for (PhysicalCard system : systems) {
            Collection<PhysicalCard> ties = Filters.filterActive(game, self,
                    Filters.and(Filters.your(self), Filters.TIE, Filters.hasPiloting(self, Filters.Imperial), Filters.at(system)));
            if (ties.size() >= 2) {
                count++;
            }
        }
        return count;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId relocateId = GameTextActionId.LEGACY__FLAGSHIP_OPERATIONS_V__RELOCATE_TIE;
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)) {
            Collection<PhysicalCard> ties = Filters.filterActive(game, self,
                    Filters.and(Filters.your(self), Filters.TIE, Filters.piloted, Filters.at(Filters.system), Filters.hasNotPerformedRegularMove));
            List<PhysicalCard> validTiesToRelocate = new ArrayList<PhysicalCard>();
            for (PhysicalCard tie : ties) {
                if (GameConditions.canSpotLocation(game, Filters.and(Filters.system, Filters.withinParsecsOf(tie, 3), Filters.locationCanBeRelocatedTo(tie, 0)))) {
                    validTiesToRelocate.add(tie);
                }
            }
            if (!validTiesToRelocate.isEmpty()) {
                final TopLevelGameTextAction relocate = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, relocateId);
                relocate.setText("Relocate a TIE as a regular move");
                relocate.appendTargeting(
                        new TargetCardOnTableEffect(relocate, playerId, "Choose TIE", Integer.MAX_VALUE, false, Filters.in(validTiesToRelocate)) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard tieToRelocate) {
                                Filter systemToRelocateTo = Filters.and(Filters.system, Filters.withinParsecsOf(tieToRelocate, 3), Filters.locationCanBeRelocatedTo(tieToRelocate, 0));
                                relocate.appendTargeting(
                                        new ChooseCardOnTableEffect(relocate, playerId, "Choose system to relocate " + GameUtils.getCardLink(tieToRelocate) + " to", systemToRelocateTo) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard systemSelected) {
                                                relocate.addAnimationGroup(tieToRelocate);
                                                relocate.addAnimationGroup(systemSelected);
                                                relocate.appendCost(
                                                        new PayRelocateBetweenLocationsCostEffect(relocate, playerId, tieToRelocate, systemSelected, 0));
                                                relocate.allowResponses("Relocate " + GameUtils.getCardLink(tieToRelocate) + " to " + GameUtils.getCardLink(systemSelected),
                                                        new UnrespondableEffect(relocate) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                relocate.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(relocate, tieToRelocate, systemSelected, true));
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                actions.add(relocate);
            }
        }

        GameTextActionId powerId = GameTextActionId.LEGACY__FLAGSHIP_OPERATIONS_V__ADD_POWER;
        int qualifying = countQualifyingSystems(game, self);
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, powerId)
                && qualifying > 0) {
            final int bonus = qualifying * 3;
            final TopLevelGameTextAction power = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, powerId);
            power.setText("Add " + bonus + " to total power");
            power.appendUsage(
                    new OncePerBattleEffect(power));
            power.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(power,
                            new TotalPowerModifier(self, Filters.battleLocation, bonus, playerId),
                            "Adds " + bonus + " to total power"));
            actions.add(power);
        }

        return actions;
    }
}
