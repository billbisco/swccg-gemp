package com.gempukku.swccgo.cards.set1272.light;

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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetArmorModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Effect
 * Title: Rogue Insertion
 */
public class Card1272_023 extends AbstractNormalEffect {
    public Card1272_023() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Rogue Insertion", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setLore("Each Rebel soldier is driven by bravery and a belief in the Rebel Alliance's ideal of freedom. Courageous and quick-thinking Rebels often defeat Imperial legions.");
        setGameText("Deploy on table. Canceled if your Senate or [Reflections 2] objective on table. Unless your character of ability > 4 at a site, Rogue Squadron pilots are power +1 (power +2 if ability < 3), and armor = 4. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    static Filter cancelingCard(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Galactic_Senate, Filters.and(Filters.Objective, Icon.REFLECTIONS_II)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        // Virtual Cards Master List (2014) text; the 2010 Block 7.2 slip's spy, immunity and weapon-destiny clauses are gone.
        final Condition unlessAbilityFiveAtSite = new UnlessCondition(new OnTableCondition(self, characterOfAbilityFiveAtSite(self)));
        final Filter rogues = Filters.Rogue_Squadron_pilot;
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(rogues, Filters.not(Filters.abilityLessThan(3))), unlessAbilityFiveAtSite, 1));
        modifiers.add(new PowerModifier(self, Filters.and(rogues, Filters.abilityLessThan(3)), unlessAbilityFiveAtSite, 2));
        modifiers.add(new ResetArmorModifier(self, rogues, unlessAbilityFiveAtSite, 4));
        return modifiers;
    }

    static Filter characterOfAbilityFiveAtSite(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(4), Filters.at(Filters.site));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, cancelingCard(self))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel " + GameUtils.getFullName(self));
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
