package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Effect
 * Title: Trained In The Jedi Arts (V)
 */
public class Card1115_067 extends AbstractNormalEffect {
    public Card1115_067() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Trained In The Jedi Arts", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("While Qui-Gon spent years maintaining peace, Maul was sharpening his combat skills. Those years of training came to fruition in one fateful moment.");
        setGameText("Deploy on table. Your lightsaber and [Maintenance] cards are lost. 'Vader' on your Interrupts may be treated as 'Maul or Mara Jade.' Wherever you have an Effect with 'skill' in its game text, your Force drains and battle destiny draws are +1. (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_III);
        addImmuneToCardTitle(Title.Alter);
    }

    private Filter skillEffect(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.Effect, new Filter() {
            @Override
            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                String text = physicalCard.getBlueprint().getGameText();
                return text != null && text.toLowerCase().contains("skill");
            }
        });
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition skillOnTable = new OnTableCondition(self, skillEffect(self));
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.any, skillOnTable, 1, playerId));
        modifiers.add(new TotalBattleDestinyModifier(self, skillOnTable, 1, playerId, true));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.or(Filters.lightsaber, Icon.MAINTENANCE)))) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Lost lightsaber or Maintenance card");
            action.appendEffect(new com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect(action, self.getOwner(), "Choose card to lose",
                    Filters.and(Filters.your(self), Filters.or(Filters.lightsaber, Icon.MAINTENANCE))) {
                @Override
                protected void cardSelected(PhysicalCard selectedCard) {
                    action.appendEffect(new PlaceCardInLostPileFromTableEffect(action, selectedCard));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
