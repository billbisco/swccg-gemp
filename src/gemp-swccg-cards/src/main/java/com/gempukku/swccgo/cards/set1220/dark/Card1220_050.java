package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilDamageSegmentOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeExcludedFromBattle;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeLostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Vader's Bounty
 */
public class Card1220_050 extends AbstractNormalEffect {
    public Card1220_050() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Vaders_Bounty, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'We would be honored if you would join us.'");
        setGameText("Deploy on table. Slave I is destiny +2 when drawn for weapon or battle destiny. During battle: while non-[Reflections 3] Boba Fett with Vader, neither may be excluded or lost until the damage segment, and may place this Effect in Lost Pile to make Boba Fett's total weapon destiny +3.");
        addIcons(Icon.CLOUD_CITY);
    }

    private Filter nonReflections3BobaWithVader(PhysicalCard self) {
        return Filters.and(Filters.Boba_Fett, Filters.not(Icon.REFLECTIONS_III), Filters.participatingInBattle, Filters.with(self, Filters.Vader));
    }

    private Filter vaderWithNonReflections3Boba(PhysicalCard self) {
        return Filters.and(Filters.Vader, Filters.participatingInBattle, Filters.with(self, Filters.and(Filters.Boba_Fett, Filters.not(Icon.REFLECTIONS_III))));
    }

    private Filter bobaAndVaderTogether(PhysicalCard self) {
        return Filters.or(nonReflections3BobaWithVader(self), vaderWithNonReflections3Boba(self));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, Filters.Slave_I, 2));
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, Filters.Slave_I, 2));
        modifiers.add(new MayNotBeExcludedFromBattle(self, bobaAndVaderTogether(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.canSpot(game, self, nonReflections3BobaWithVader(self))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("May not be lost until damage segment");
            action.appendEffect(
                    new AddUntilDamageSegmentOfBattleModifierEffect(action,
                            new MayNotTargetToBeLostModifier(self, bobaAndVaderTogether(self)),
                            "Boba Fett and Vader may not be lost until the damage segment"));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canSpot(game, self, nonReflections3BobaWithVader(self))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place in Lost Pile to add 3 to Boba Fett's total weapon destiny");
            action.appendCost(
                    new PlaceCardInLostPileFromTableEffect(action, self));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new TotalWeaponDestinyModifier(self, Filters.any, Filters.Boba_Fett, 3),
                            "Boba Fett's total weapon destiny is +3"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
