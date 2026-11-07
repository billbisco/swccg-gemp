package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.ArrayList;
import java.util.List;

/** Legacy Block 6.1 Boba Fett, Relentless Bounty Hunter. */
public class Card1261_005 extends AbstractAlien {
    public Card1261_005() {
        super(Side.DARK, 1, 4, 4, 3, 6, "Boba Fett, Relentless Bounty Hunter", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setArmor(5);
        setLore("Boba Fett's helmet has infrared capabilities, a motion tracking system, a macrobinocular viewer, an internal comlink and a broadband antenna. He doesn't miss a thing.");
        setGameText("Adds 3 to power of anything he pilots. May 'fly' (landspeed = 3). While armed with a blaster, may add one destiny to total power or attrition. During your move phase, if escorting a captive, may use 2 Force to relocate both to a docking bay. Immune to attrition < 3.");
        addPersona(Persona.BOBA_FETT);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.JABBAS_PALACE);
        addKeywords(Keyword.BOUNTY_HUNTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 3), new DefinedByGameTextLandspeedModifier(self, 3),
                new ImmuneToAttritionLessThanModifier(self, 3));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();
        GameTextActionId destinyId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isInBattle(game, self) && GameConditions.isArmedWith(game, self, Filters.blaster)
                && GameConditions.isOncePerBattle(game, self, playerId, sourceId, destinyId)) {
            if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId, destinyId);
                action.setText("Add one destiny to total power");
                action.appendUsage(new OncePerBattleEffect(action));
                action.appendEffect(new AddDestinyToTotalPowerEffect(action, 1));
                actions.add(action);
            }
            if (GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId, destinyId);
                action.setText("Add one destiny to attrition");
                action.appendUsage(new OncePerBattleEffect(action));
                action.appendEffect(new AddDestinyToAttritionEffect(action, 1));
                actions.add(action);
            }
        }
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE) && GameConditions.isEscortingCaptive(game, self)) {
            Filter destination = Filters.and(Filters.docking_bay,
                    Filters.locationCanBeRelocatedTo(self, false, true, false, 2, false));
            if (GameConditions.canSpotLocation(game, destination)) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId, GameTextActionId.OTHER_CARD_ACTION_2);
                action.setText("Relocate with captive to docking bay");
                action.appendTargeting(new ChooseCardOnTableEffect(action, playerId, "Choose docking bay", destination) {
                    @Override
                    protected void cardSelected(PhysicalCard bay) {
                        action.addAnimationGroup(self); action.addAnimationGroup(bay);
                        action.appendCost(new PayRelocateBetweenLocationsCostEffect(action, playerId, self, bay, 2));
                        action.allowResponses("Relocate Boba Fett and captive to docking bay", new UnrespondableEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(new RelocateBetweenLocationsEffect(action, self, bay));
                            }
                        });
                    }
                });
                actions.add(action);
            }
        }
        return actions;
    }
}
