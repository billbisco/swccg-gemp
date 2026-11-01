package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Effect
 * Title: Uncharted Settlements
 */
public class Card1115_033 extends AbstractNormalEffect {
    public Card1115_033() {
        super(Side.LIGHT, 2, PlayCardZoneOption.ATTACHED, "Uncharted Settlements", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setGameText("Deploy on [Virtual Set 15] Hidden Base. [Virtual Set 8] Ellorrs Madak, Organized Attack, S-foils, and Your Destiny (unless you chose Rebel/Rebel Starship) are canceled. You may not deploy operatives or [Virtual Set 6] Rebel Planners. Opponent's combat vehicles, droids, Imperials, and Maul are deploy +2 to Rebel Cell sites. (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_II);
        addImmuneToCardTitle(Title.Alter);
    }

    private static Filter fromSet(Filterable base, final ExpansionSet set) {
        return Filters.and(base, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getExpansionSet() == set;
            }
        });
    }

    private boolean choseRebelPair(PhysicalCard self) {
        PhysicalCard hiddenBase = self.getAttachedTo();
        return hiddenBase != null && hiddenBase.getWhileInPlayData() != null
                && "Rebel".equals(hiddenBase.getWhileInPlayData().getTextValue());
    }

    private Filter cardsToCancel(PhysicalCard self) {
        Filter always = Filters.or(Filters.Organized_Attack, Filters.S_foils,
                fromSet(Filters.Ellorrs_Madak, ExpansionSet.VIRTUAL_ORIGINAL_SET_8));
        if (choseRebelPair(self)) {
            return always;
        }
        return Filters.or(always, Filters.Your_Destiny);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return fromSet(Filters.Hidden_Base, ExpansionSet.VIRTUAL_ORIGINAL_SET_15);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter rebelCellSites = Filters.titleContains("Rebel Cell");

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Filters.operative,
                fromSet(Filters.Rebel_Planners, ExpansionSet.VIRTUAL_ORIGINAL_SET_6)), playerId));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self),
                Filters.or(Filters.combat_vehicle, Filters.droid, Filters.Imperial, Filters.Maul)),
                2, rebelCellSites));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        Filter toCancel = cardsToCancel(self);
        if (TriggerConditions.isPlayingCard(game, effect, toCancel)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return java.util.Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        Filter toCancel = cardsToCancel(self);
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, toCancel)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, toCancel, "card");
            actions.add(action);
        }
        return actions;
    }
}
