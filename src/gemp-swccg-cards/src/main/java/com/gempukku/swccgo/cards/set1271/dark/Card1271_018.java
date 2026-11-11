package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseWeaponEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.actions.CommencePrimaryIgnitionState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.List;

/** Commence Primary Ignition (V), Virtual Block 7.1. */
public class Card1271_018 extends AbstractEpicEventDeployable {
    private static final Filter CENTRAL_CORE = Filters.and(Filters.title(Title.Death_Star_Central_Core), Icon.LEGACY_BLOCK_7);

    public Card1271_018() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.Commence_Primary_Ignition, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setVirtualSuffix(true);
        setGameText("If Death Star orbits Alderaan (and [Virtual Block 7] Central Core on table), deploy on Death Star and fire Superlaser at Alderaan; it is 'blown away.' Once during your control phase, if Death Star orbiting a planet (except a Rebel Base) without related sites, may fire Superlaser at planet (regardless of Objective restrictions) as follows: Draw destiny. Add 2 if you control that system. If (destiny + Y - Z) > 8, target is 'blown away' and opponent loses 4 Force from Life Force. Y = total Death Star sites where opponent has no presence. Z = total sites at Yavin 4.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (!GameConditions.canSpotLocation(game, CENTRAL_CORE)) return Filters.none;
        PhysicalCard alderaan = Filters.findFirstFromTopLocationsOnTable(game, Filters.Alderaan_system);
        if (alderaan == null || laser(game, self, alderaan) == null) return Filters.none;
        return Filters.and(Filters.Death_Star_system, Filters.isOrbiting(Title.Alderaan));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(SwccgGame game, PhysicalCard self) {
        return Filters.Death_Star_system;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
            PhysicalCard self, int sourceId) {
        if (!TriggerConditions.justDeployed(game, result, self)) return null;
        PhysicalCard alderaan = Filters.findFirstFromTopLocationsOnTable(game, Filters.Alderaan_system);
        PhysicalCard superlaser = alderaan == null ? null : laser(game, self, alderaan);
        if (superlaser == null || !superlaser.getAttachedTo().equals(self.getAttachedTo())) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setPerformingPlayer(self.getOwner());
        action.setText("Fire Superlaser at Alderaan");
        appendShot(action, self, superlaser, alderaan, true);
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        PhysicalCard deathStar = self.getAttachedTo();
        if (deathStar == null || !GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                || !GameConditions.canDrawDestiny(game, playerId)
                || !GameConditions.isOncePerTurn(game, self, playerId, sourceId)) return null;
        PhysicalCard planet = Filters.findFirstFromTopLocationsOnTable(game,
            Filters.and(Filters.planet_system, Filters.isOrbitedBy(deathStar), Filters.not(Filters.Rebel_Base_system)));
        if (planet == null || Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.site, Filters.partOfSystem(planet.getTitle()))) != null) return null;

        // Do not apply an Objective's target restriction to this printing of Commence Primary Ignition.
        PhysicalCard superlaser = laser(game, self, planet);
        if (superlaser == null || !deathStar.equals(superlaser.getAttachedTo())) return null;
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, sourceId);
        action.setText("Fire Superlaser at " + planet.getTitle());
        action.appendUsage(new OncePerTurnEffect(action));
        appendShot(action, self, superlaser, planet, false);
        return List.of(action);
    }

    private PhysicalCard laser(SwccgGame game, PhysicalCard self, PhysicalCard planet) {
        return Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.superlaserThatCanFireAtPlanetSystem(planet)));
    }

    private void appendShot(Action action, PhysicalCard self, PhysicalCard superlaser, PhysicalCard planet, boolean initial) {
        PhysicalCard deathStar = superlaser.getAttachedTo();
        String playerId = self.getOwner();
        action.appendEffect(new UseWeaponEffect(action, deathStar, superlaser));
        action.appendEffect(new PassthruEffect(action) {
            @Override protected void doPlayEffect(SwccgGame game) {
                var state = game.getGameState();
                var epic = new CommencePrimaryIgnitionState(self);
                epic.setSuperlaser(superlaser);
                epic.setPlanetSystem(planet);
                state.beginEpicEvent(epic);
                state.activatedCard(playerId, superlaser);
                state.cardAffectsCard(playerId, superlaser, planet);
                state.beginWeaponFiring(superlaser, null);
                state.getWeaponFiringState().setCardFiringWeapon(deathStar);
                state.getWeaponFiringState().setTarget(planet);
            }
        });
        if (initial) {
            action.appendEffect(new BlowAwayEffect(action, planet, true));
        } else {
            action.appendEffect(new DrawDestinyEffect(action, playerId, 1, DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY) {
                @Override protected void destinyDraws(SwccgGame game, List<PhysicalCard> cards, List<Float> values, Float total) {
                    if (total == null) return;
                    var state = game.getGameState();
                    var modifiers = game.getModifiersQuerying();
                    String opponent = game.getOpponent(playerId);
                    float y = modifiers.getVariableValue(state, self, Variable.Y, Filters.countTopLocationsOnTable(game,
                        Filters.and(Filters.Death_Star_site, Filters.notIgnoredDuringEpicEventCalculation(true), Filters.not(Filters.occupies(opponent)))));
                    float z = modifiers.getVariableValue(state, self, Variable.Z, Filters.countTopLocationsOnTable(game,
                        Filters.and(Filters.Yavin_4_site, Filters.notIgnoredDuringEpicEventCalculation(true))));
                    float controlBonus = Filters.controls(playerId).accepts(game, planet) ? 2 : 0;
                    float shot = modifiers.getEpicEventCalculationTotal(state, self, total + controlBonus + y - z);
                    if (shot <= 8) return;
                    action.appendEffect(new BlowAwayEffect(action, planet, true));
                    action.appendEffect(new PassthruEffect(action) {
                        @Override protected void doPlayEffect(SwccgGame game) {
                            if (planet.isBlownAway()) action.appendEffect(new LoseForceEffect(action, opponent, 4, false, true));
                        }
                    });
                }
            });
        }
        action.appendAfterEffect(new PassthruEffect(action) {
            @Override protected void doPlayEffect(SwccgGame game) {
                game.getActionsEnvironment().emitEffectResult(new FiredWeaponResult(game, superlaser, null, deathStar, false, List.of(planet)));
                game.getGameState().finishWeaponFiring();
                game.getGameState().finishEpicEvent();
            }
        });
    }
}
