package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Kashyyyk: Forest Depths, Virtual Block 6.1. */
public class Card1261_113 extends AbstractSite {
    public Card1261_113() {
        super(Side.LIGHT, "Kashyyyk: Forest Depths", Title.Kashyyyk, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLocationLightSideGameText("In battles at Kashyyyk locations, attrition against opponent is +1 (limit +3) for each Kashyyyk location you occupy with a Wookiee.");
        setLocationDarkSideGameText("Unless you occupy, your Force generation here is canceled. No starships or vehicles here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeyword(Keyword.FOREST);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerId, SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new AttritionModifier(self, Filters.Kashyyyk_location,
                new MaxLimitEvaluator(new OnTableEvaluator(self, Filters.and(Filters.Kashyyyk_location,
                        Filters.occupiesWith(playerId, self, Filters.Wookiee))), 3), game.getOpponent(playerId)));
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerId, SwccgGame game, PhysicalCard self) {
        return Arrays.asList(
                new GenerateNoForceModifier(self, self, new NotCondition(new OccupiesCondition(playerId, self)), playerId),
                new MayNotExistAtLocationModifier(self, Filters.or(Filters.starship, Filters.vehicle), self));
    }
}
