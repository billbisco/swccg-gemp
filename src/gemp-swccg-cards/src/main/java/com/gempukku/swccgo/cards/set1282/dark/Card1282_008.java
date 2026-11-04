package com.gempukku.swccgo.cards.set1282.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.modifiers.UnderHothEnergyShieldModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Main Power Generators (1st Marker) (V)
 */
public class Card1282_008 extends AbstractSite {
    public Card1282_008() {
        super(Side.DARK, Title.Main_Power_Generators, Title.Hoth, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("While [Virtual Block 2] Imperial Occupation on table, Hoth Energy Shield covers only this site and Echo sites. You may not attempt [Hoth] Epic Events from beyond the 3rd Marker.");
        setLocationLightSideGameText("'Hoth Energy Shield Rules' in effect. If 'blown away,' Light Side loses 3 Force (may not be reduced).");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.HOTH);
        addKeywords(Keyword.MARKER_1);
    }

    /**
     * [Virtual Block 2] Imperial Occupation (1220_72; the Objective front, flip side Imperial Control).
     */
    private static Condition vb2ImperialOccupationOnTable(PhysicalCard self) {
        return new OnTableCondition(self, Filters.and(Filters.Imperial_Occupation, Icon.LEGACY_BLOCK_2));
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self) {
        final Condition imperialOccupation = vb2ImperialOccupationOnTable(self);
        // Hoth Energy Shield Rules: the shield covers Echo sites and the 1st through 3rd Markers. While [Virtual Block 2]
        // Imperial Occupation is on table (Dark Side text), it covers only this site (1st Marker) and Echo sites.
        Filter secondOrThirdMarkerUnlessOccupation = Filters.and(Filters.or(Filters.Second_Marker, Filters.Third_Marker),
                new Filter() {
                    @Override
                    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                        return !imperialOccupation.isFulfilled(gameState, modifiersQuerying);
                    }
                });
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new UnderHothEnergyShieldModifier(self, Filters.or(self, Filters.Echo_site, Filters.First_Marker, secondOrThirdMarkerUnlessOccupation)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBlownAwayCalculateForceLossStep(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(playerOnLightSideOfLocation);
            action.setText("Make Light Side lose 3 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, game.getLightPlayer(), 3, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.LEGACY__MAY_NOT_ATTEMPT_HOTH_EPIC_EVENTS_BEYOND_THIRD_MARKER, playerOnDarkSideOfLocation));
        return modifiers;
    }
}
