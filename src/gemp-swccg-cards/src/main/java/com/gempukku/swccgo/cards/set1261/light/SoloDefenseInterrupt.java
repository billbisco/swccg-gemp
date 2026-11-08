package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.List;

/** Shared play modes of the three Block 6 solo-defense Interrupts. */
public abstract class SoloDefenseInterrupt extends AbstractLostInterrupt {
    private final Filter searchFilter;
    private final Filter defender;
    private final GameTextActionId searchId;

    protected SoloDefenseInterrupt(String title, Filter persona, boolean limitSearchSet, boolean limitDefenderSet, GameTextActionId searchId) {
        super(Side.LIGHT, 4, title, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        this.searchId = searchId;
        this.defender = Filters.and(persona, Filters.character, limitDefenderSet ? Filters.not(Icon.REFLECTIONS_III) : Filters.any);
        var personaOnPhysicalPrinting = Filters.or(persona, Filters.hasPermanentAboard(persona));
        this.searchFilter = new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying querying, PhysicalCard card) {
                // Evaluate the set on the actual card, including a printing with the persona aboard.
                return (!limitSearchSet || !card.getBlueprint().hasIcon(Icon.REFLECTIONS_III))
                        && personaOnPhysicalPrinting.accepts(state, querying, card);
            }
        };
    }

    /** The cards the first mode deploys from Reserve Deck or retrieves. */
    protected Filter searchFilter() {
        return searchFilter;
    }

    /**
     * Solo Han's PC card-list text (PC-E1): "he is power +1 for each character and vehicle present ... he adds one
     * destiny to power"; the sheet text adds the bonus to the destiny instead ("one destiny to power (+1 for each ...)").
     */
    protected boolean bonusIsPowerNotDestiny() {
        return false;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        if (!GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) return List.of();
        var actions = new ArrayList<PlayInterruptAction>();
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, searchId, true, false)) {
            actions.add(search(playerId, game, self, false));
        }
        if (GameConditions.canSearchLostPile(game, playerId, self, searchId)) {
            actions.add(search(playerId, game, self, true));
        }
        return actions;
    }

    private PlayInterruptAction search(String playerId, SwccgGame game, PhysicalCard self, boolean retrieve) {
        var action = new PlayInterruptAction(game, self, searchId);
        action.setText(retrieve ? "Retrieve from Lost Pile" : "Deploy from Reserve Deck");
        action.appendCost(new UseForceEffect(action, playerId, 1));
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(retrieve ? new RetrieveCardEffect(action, playerId, searchFilter())
                        : new DeployCardFromReserveDeckEffect(action, searchFilter(), true));
            }
        });
        return action;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(
            String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
        var target = Filters.and(Filters.your(self), defender, Filters.alone,
                Filters.participatingInBattle, Filters.defendingBattle, Filters.at(Filters.site));
        if (!TriggerConditions.battleInitiated(game, result, game.getOpponent(playerId))
                || !GameConditions.isDuringBattleWithParticipant(game, target)) return List.of();
        var action = new PlayInterruptAction(game, self);
        action.setText("Protect solo defender");
        action.setImmuneTo(Title.Sense);
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose the solo defender", target) {
            @Override
            protected void cardTargeted(int group, PhysicalCard card) {
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        var defender = action.getPrimaryTargetCard(group);
                        action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                new MayNotHaveForfeitValueReducedModifier(self, defender), null));
                        var present = new PresentEvaluator(defender, Filters.or(Filters.character, Filters.vehicle));
                        if (bonusIsPowerNotDestiny()) {
                            action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                    new PowerModifier(self, defender, present), null));
                            action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                    new AddsDestinyToPowerModifier(defender, 1, playerId), null));
                        } else {
                            action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                    new AddsDestinyToPowerModifier(defender, 1, playerId).setBonusForEachDestinyDraw(present), null));
                        }
                        action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, defender, 1), null));
                    }
                });
            }
        });
        return List.of(action);
    }
}
