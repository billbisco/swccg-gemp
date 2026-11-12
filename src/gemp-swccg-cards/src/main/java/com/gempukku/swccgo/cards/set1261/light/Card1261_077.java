package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledByModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeModifiedByModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;

import java.util.Arrays;
import java.util.List;

/** Virtual Block 6.1, final official text. The conversion printing shares this implementation. */
public class Card1261_077 extends AbstractDefensiveShield {
    public Card1261_077() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Lets_Keep_A_Little_Optimism_Here,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setBasePrintingDisplayTitle("Let's Keep A Little Optimism Here (Ref III)");
        setLore("The heroes of the Rebellion know that where there is life, there is hope.");
        setGameText("Plays on table. Ounee Ta is canceled. At opponent's Ralltiir and Renegade Planet sites, your Rebels are deploy -2 and your Force drains may not be modified or canceled by opponent. Your game text at opponent's <>Swamp and <>Jungle is canceled. Your characters present at opponent's <>Forest are immune to attrition.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Filter protectedSites = Filters.and(Filters.opponents(self), Filters.site,
                Filters.or(Filters.Ralltiir_site, Filters.Renegade_planet_location));
        Filter swampOrJungle = Filters.and(Filters.opponents(self), Filters.generic_site,
                Filters.or(Filters.swamp, Filters.jungle));
        Filter forest = Filters.and(Filters.opponents(self), Filters.generic_site, Filters.forest);
        return Arrays.asList(
                new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.Rebel), -2, protectedSites),
                new ForceDrainsMayNotBeModifiedByModifier(self, Filters.opponents(self), null, playerId, protectedSites),
                new ForceDrainsMayNotBeCanceledByModifier(self, Filters.opponents(self), playerId, protectedSites),
                new CancelsGameTextOnSideOfLocationModifier(self, swampOrJungle, playerId),
                new ImmuneToAttritionModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.presentAt(forest))));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Ounee_Ta))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.title(Title.Ounee_Ta))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title(Title.Ounee_Ta), "Ounee Ta");
            return Collections.singletonList(action);
        }
        return null;
    }
}
