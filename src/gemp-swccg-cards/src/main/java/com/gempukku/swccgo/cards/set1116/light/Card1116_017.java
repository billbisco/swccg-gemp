package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Location
 * Subtype: Site
 * Title: Kashyyyk: Wookiee Haven (Forest)
 */
public class Card1116_017 extends AbstractSite {
    public Card1116_017() {
        super(Side.LIGHT, "Kashyyyk: Wookiee Haven (Forest)", Title.Kashyyyk, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setLocationLightSideGameText("Your Force generation is +1 at Kashyyyk sites where you have a Wookiee.");
        setLocationDarkSideGameText("At end of opponent's deploy phase, if opponent controls this site with at least two Wookiees and has 8 or more cards in hand, lose 1 Force.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeyword(Keyword.FOREST);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter whereWookiee = Filters.and(Filters.Kashyyyk_site, Filters.wherePresent(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Wookiee)));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, whereWookiee, 1, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerOnDarkSideOfLocation);
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.DEPLOY, opponent)
                && GameConditions.controls(game, opponent, self)
                && Filters.countActive(game, self, Filters.and(Filters.owner(opponent), Filters.Wookiee, Filters.at(self))) >= 2
                && game.getGameState().getHand(opponent).size() >= 8) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Lose 1 Force");
            action.appendEffect(new LoseForceEffect(action, playerOnDarkSideOfLocation, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
