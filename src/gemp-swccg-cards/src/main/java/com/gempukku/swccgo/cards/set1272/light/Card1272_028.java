package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Location
 * Subtype: System
 * Title: Roche (V)
 */
public class Card1272_028 extends AbstractSystem {
    public Card1272_028() {
        super(Side.LIGHT, Title.Roche, 7, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Unless opponent controls this system, Commence Primary Ignition may not target it.");
        setLocationDarkSideGameText("Force Drain -1 here. If you control, Asteroid Sanctuary at related sector is canceled.");
        setGameText("Light: Unless opponent controls this system, Commence Primary Ignition may not target it. Dark: Force Drain -1 here. If you control, Asteroid Sanctuary at related sector is canceled.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(playerOnLightSideOfLocation);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByModifier(self, self, new UnlessCondition(new ControlsCondition(opponent, self)), Filters.Commence_Primary_Ignition));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, -1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.Asteroid_Sanctuary, Filters.at(Filters.relatedSector(self)));

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, filter)
                && GameConditions.controls(game, playerOnDarkSideOfLocation, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, filter, Title.Asteroid_Sanctuary);
            return Collections.singletonList(action);
        }
        return null;
    }
}
