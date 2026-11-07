package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToPlaceCardOutOfPlayFromTableResult;

import java.util.ArrayList;
import java.util.List;

/** He Can Go About His Business (V), Virtual Block 6.1. */
public class Card1261_078 extends AbstractDefensiveShield {
    private static final Filter LEGACY_EPIC_DUEL = new Filter() {
        @Override public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard card) {
            return "1261_39".equals(card.getBlueprintId(true));
        }
    };
    private static final Filter CANCELED = Filters.or(Filters.A_Dangerous_Time, Filters.Bad_Feeling_Have_I,
        Filters.Responsibility_Of_Command, Filters.You_Overestimate_Their_Chances);
    private static final List<String> CANCELED_TITLES = List.of(Title.A_Dangerous_Time, Title.Bad_Feeling_Have_I,
        Title.Responsibility_Of_Command, Title.You_Overestimate_Their_Chances);

    public Card1261_078() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "He Can Go About His Business", ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'You can go about your business.'");
        setGameText("Plays on table. Cancels A Dangerous Time, Bad Feeling Have I, Responsibility Of Command, and You Overestimate Their Chances. Unless [Virtual] Epic Duel on table, Force loss from epic duels is reduced to 0 and characters about to be placed out of play by a duel are instead lost.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter epicDuelSource = new Filter() {
            @Override public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard card) {
                var duel = state.getDuelState();
                return duel != null && duel.isEpicDuel() && card.equals(duel.getCardInitiatedDuel())
                    && !GameConditions.canSpot(game, self, LEGACY_EPIC_DUEL);
            }
        };
        return List.of(new LimitForceLossFromCardModifier(self, epicDuelSource, 0, self.getOwner()),
            new LimitForceLossFromCardModifier(self, epicDuelSource, 0, game.getOpponent(self.getOwner())));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect,
            PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isPlayingCard(game, effect, CANCELED)
                || !GameConditions.canCancelCardBeingPlayed(game, self, effect)) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
            PhysicalCard self, int sourceId) {
        List<RequiredGameTextTriggerAction> actions = new ArrayList<>();
        if (TriggerConditions.isTableChanged(game, result)) {
            for (String title : CANCELED_TITLES) {
                Filter named = Filters.title(title);
                if (!GameConditions.canTargetToCancel(game, self, named)) continue;
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
                CancelCardActionBuilder.buildCancelCardAction(action, named, title);
                actions.add(action);
            }
        }

        var duel = game.getGameState().getDuelState();
        if (duel != null && result instanceof AboutToPlaceCardOutOfPlayFromTableResult placement
                && duel.getCardInitiatedDuel().equals(placement.getSourceCard())
                && duel.getDuelParticipants().contains(placement.getCardToBePlacedOutOfPlay())
                && !GameConditions.canSpot(game, self, LEGACY_EPIC_DUEL)) {
            PhysicalCard character = placement.getCardToBePlacedOutOfPlay();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
            action.setText("Lose duel participant instead of placing out of play");
            action.appendEffect(new PassthruEffect(action) {
                @Override protected void doPlayEffect(SwccgGame game) {
                    placement.getPreventableCardEffect().preventEffectOnCard(character);
                    action.appendEffect(new LoseCardFromTableEffect(action, character));
                }
            });
            actions.add(action);
        }
        return actions;
    }
}
