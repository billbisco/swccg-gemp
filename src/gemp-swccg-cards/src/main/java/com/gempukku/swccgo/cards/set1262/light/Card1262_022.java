package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PreventEffectOnCardEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AbstractModifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.SuspendModifierEffectsModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;

import java.util.List;

/** At Peace (V), final Legacy Light master p21. */
public class Card1262_022 extends AbstractNormalEffect {
    public Card1262_022() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.At_Peace, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("To recover from the strenuous Jedi training routine and revitalize the mind and body, an apprentice must rest to be calm and at peace.");
        setGameText("Deploy on Yoda's Hut. May use 1 Force to prevent Broken Concentration from modifying your training destiny (and Strong Is Vader from retrieving Force) this turn. If Luke has completed Jedi Test #5 and is about to be hit, may place this Effect out of play to prevent it.");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return Filters.Yodas_Hut;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        if (!GameConditions.canUseForce(game, playerId, 1)) return List.of();
        var action = new TopLevelGameTextAction(self, sourceId);
        action.setText("Use 1 Force to protect training destiny this turn");
        action.appendCost(new UseForceEffect(action, playerId, 1));
        // Broken Concentration's destiny adjustment is its only modifier on Jedi Tests; its separate pile action stays active.
        action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                new SuspendModifierEffectsModifier(self, Filters.and(Filters.your(self), Filters.Jedi_Test), Filters.Broken_Concentration), null));
        action.appendEffect(new AddUntilEndOfTurnModifierEffect(action, new SuppressStrongIsVaderRetrieval(self), null));
        return List.of(action);
    }

    // Card-local contract: only Strong Is Vader's retrieval branch consumes this marker.
    private static final class SuppressStrongIsVaderRetrieval extends AbstractModifier {
        private SuppressStrongIsVaderRetrieval(PhysicalCard self) {
            super(self, "Strong Is Vader cannot retrieve Force this turn",
                    Filters.and(Filters.your(self), Filters.title(Title.Strong_Is_Vader)), ModifierType.MODIFY_GAME_TEXT);
        }
    }

    public static boolean suppressesStrongIsVaderRetrieval(SwccgGame game, PhysicalCard strongIsVader) {
        return game.getModifiersQuerying().getModifiersAffectingCard(game.getGameState(), ModifierType.MODIFY_GAME_TEXT, strongIsVader)
                .stream().anyMatch(SuppressStrongIsVaderRetrieval.class::isInstance);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(
            String playerId, SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isAboutToBeHit(game, result, Filters.and(Filters.your(self), Filters.Luke))) return List.of();
        var hit = (AboutToBeHitResult) result;
        var luke = hit.getCardToBeHit();
        if (!GameConditions.canSpot(game, self, Filters.and(Filters.Jedi_Test_5, Filters.completed_Jedi_Test,
                Filters.jediTestTargetingApprentice(Filters.sameCardId(luke))))) return List.of();
        var action = new OptionalGameTextTriggerAction(self, sourceId);
        action.setText("Place out of play to prevent Luke from being hit");
        action.appendCost(new PlaceCardOutOfPlayFromTableEffect(action, self));
        action.appendEffect(new PreventEffectOnCardEffect(action, hit.getPreventableCardEffect(), luke, null));
        return List.of(action);
    }
}
