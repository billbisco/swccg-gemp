package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Effect
 * Title: Learn About The Force, Luke
 */
public class Card1115_018 extends AbstractNormalEffect {
    public Card1115_018() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Learn About The Force, Luke", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setLore("Luke relied on Obi-Wan's knowledge and advice to learn the ways of the Force. Obi-Wan continued to counsel Luke long after the old Jedi's apparent 'death.'");
        setGameText("Deploy on table. Your non-[Jabba's Palace] Interrupts with 'alone' in game text are Used Interrupts, but if played during battle you draw no more than two battle destiny. Wherever you have a card with 'skill' in its game text, your Force drains and battle destiny draws are +1. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourAloneInterrupts = Filters.and(Filters.your(self), Filters.Interrupt, Filters.not(Icon.JABBAS_PALACE), Filters.gameTextContains("alone"));
        Filter skillLocations = Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.gameTextContains("skill")));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new UsedInterruptModifier(self, yourAloneInterrupts));
        modifiers.add(new ForceDrainModifier(self, skillLocations, 1, playerId));
        modifiers.add(new EachBattleDestinyModifier(self, skillLocations, 1, playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        Filter yourAloneInterrupts = Filters.and(Filters.your(self), Filters.Interrupt, Filters.not(Icon.JABBAS_PALACE), Filters.gameTextContains("alone"));

        if (TriggerConditions.isPlayingCard(game, effect, playerId, yourAloneInterrupts)
                && GameConditions.isDuringBattle(game)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__LEARN_ABOUT_THE_FORCE_LUKE__LIMIT_BATTLE_DESTINY);
            action.setText("Limit battle destiny draws to 2");
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new MayNotDrawMoreThanBattleDestinyModifier(self, 2, playerId),
                            "You draw no more than two battle destiny"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
