package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardsInHandFewerThanCondition;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Location
 * Subtype: Site
 * Title: Kashyyyk: Sacred Forest
 */
public class Card1116_016 extends AbstractSite {
    public Card1116_016() {
        super(Side.LIGHT, "Kashyyyk: Sacred Forest", Title.Kashyyyk, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setLocationLightSideGameText("At start of your turn, may draw a card from top of Reserve Deck for each Kashyyyk site where all your ability is provided only by Wookiees.");
        setLocationDarkSideGameText("Unless Mosep on table, Force lost from your Force drains here must come from hand, if possible. While opponent has less than 13 cards in hand, Wookiees are immune to Monnok.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeyword(Keyword.FOREST);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(playerOnDarkSideOfLocation);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Wookiee, new CardsInHandFewerThanCondition(opponent, 13), Title.Monnok));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(final String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isStartOfYourTurn(game, effectResult, playerOnLightSideOfLocation)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Draw from Reserve Deck");
            action.appendEffect(new DrawCardIntoHandFromReserveDeckEffect(action, playerOnLightSideOfLocation));
            return Collections.singletonList(action);
        }
        return null;
    }
}
