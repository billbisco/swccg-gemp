package com.gempukku.swccgo.cards.set1117.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Jedi Archives
 */
public class Card1117_004 extends AbstractSite {
    public Card1117_004() {
        super(Side.LIGHT, "Coruscant: Jedi Archives", Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Once per game, if Jedi Council Chamber on table, may take Speak With The Jedi Council into hand from Reserve Deck; reshuffle.");
        setLocationDarkSideGameText("May not be separated from Jedi Council Chamber (if on table). Force drain +1 here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__JEDI_ARCHIVES__TAKE_SPEAK_WITH_COUNCIL;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && Filters.canSpot(game, self, Filters.Jedi_Council_Chamber)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Speak With The Jedi Council into hand");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerOnLightSideOfLocation, Filters.title("Speak With The Jedi Council"), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }
}
