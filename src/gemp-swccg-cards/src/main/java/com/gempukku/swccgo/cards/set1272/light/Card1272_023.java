package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponTargetingModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.LegacyWeaponDestinyMayNotBeFurtherReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetArmorModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
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
        setGameText("Deploy on table. Canceled if your Senate or [Reflections 2] objective on table. Rogue Squadron pilots are spies, and, unless your character of ability > 4 at a site, power +2, armor = 4, immune to attrition < 4, and weapon destiny draws targeting them are -1 (may not be further reduced). (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    static Filter cancelingCard(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Galactic_Senate, Filters.and(Filters.Objective, Icon.REFLECTIONS_II)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final Condition unlessAbilityFiveAtSite = new UnlessCondition(new OnTableCondition(self, characterOfAbilityFiveAtSite(self)));
        final Filter rogues = Filters.Rogue_Squadron_pilot;
        // the weapon destiny modifier has no condition of its own: the condition goes in its target filter
        Filter roguesWhileNoAbilityFive = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return unlessAbilityFiveAtSite.isFulfilled(gameState, modifiersQuerying)
                        && rogues.accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, rogues, Keyword.SPY));
        modifiers.add(new PowerModifier(self, rogues, unlessAbilityFiveAtSite, 2));
        modifiers.add(new ResetArmorModifier(self, rogues, unlessAbilityFiveAtSite, 4));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, rogues, unlessAbilityFiveAtSite, 4));
        modifiers.add(new EachWeaponDestinyForWeaponTargetingModifier(self, roguesWhileNoAbilityFive, -1));
        modifiers.add(new LegacyWeaponDestinyMayNotBeFurtherReducedModifier(self, rogues, unlessAbilityFiveAtSite, 1));
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
