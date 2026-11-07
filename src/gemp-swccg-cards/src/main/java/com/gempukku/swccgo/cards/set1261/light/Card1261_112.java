package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.IsPoweredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UnderHothEnergyShieldModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Legacy Block 6.1; final Light master p41
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Main Power Generators (1st Marker)
 */
public class Card1261_112 extends AbstractSite {
    public Card1261_112() {
        super(Side.LIGHT, Title.Main_Power_Generators, Title.Hoth, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("'Hoth Energy Shield Rules' in effect. If 'blown away' Light Side loses 8 Force. Artillery weapons on Hoth are powered.");
        setLocationLightSideGameText("While Liberation on table, Hoth Energy Shield covers only this site and Echo sites. Unique (•) speeders cannot be 'thrown.'");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.MARKER_1);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBlownAwayCalculateForceLossStep(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(playerOnDarkSideOfLocation);
            action.setText("Make Light Side lose 8 Force");
            action.appendEffect(
                    new LoseForceEffect(action, game.getLightPlayer(), 8));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        var covered = GameConditions.canSpot(game, self, Filters.title(Title.Liberation))
                ? Filters.or(Filters.sameCardId(self), Filters.Echo_site)
                : Filters.or(Filters.Echo_site, Filters.First_Marker, Filters.Second_Marker, Filters.Third_Marker);
        return List.of(new UnderHothEnergyShieldModifier(self, covered));
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        return List.of(new IsPoweredModifier(self, Filters.and(Filters.artillery_weapon, Filters.at(Filters.Hoth_site))));
    }
}
