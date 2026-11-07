package com.gempukku.swccgo.cards.set1250.light;

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
import com.gempukku.swccgo.logic.modifiers.DeploysAdjacentToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeploySitesBetweenSitesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 5 Coruscant: Jedi Archives, printing 71. */
public class Card1250_071 extends AbstractSite {
    public Card1250_071() {
        super(Side.LIGHT, "Coruscant: Jedi Archives", Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setLocationLightSideGameText("Once per game, if Jedi Council Chamber on table, may take Speak With The Jedi Council into hand from Reserve Deck; reshuffle.");
        setLocationDarkSideGameText("May not be separated from Jedi Council Chamber (if on table). Force drain +1 here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.PLANET);
    }

    @Override
    public List<Modifier> getAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new DeploysAdjacentToLocationModifier(self, self, Filters.Jedi_Council_Chamber, true));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation,
            SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.LEGACY__JEDI_ARCHIVES__TAKE_SPEAK_WITH_COUNCIL;
        if (!GameConditions.isOncePerGame(game, self, id)
                || !GameConditions.canSpot(game, self, Filters.Jedi_Council_Chamber)
                || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerOnLightSideOfLocation, self, id))
            return null;

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, id);
        action.setText("Take Speak With The Jedi Council into hand");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerOnLightSideOfLocation,
                Filters.and(Filters.title("Speak With The Jedi Council"), Filters.canBeTargetedBy(self)), true));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation,
            SwccgGame game, PhysicalCard self) {
        return List.of(new DeploysAdjacentToLocationModifier(self, Filters.Jedi_Council_Chamber, self, true),
                new MayNotDeploySitesBetweenSitesModifier(self, self, Filters.Jedi_Council_Chamber),
                new ForceDrainModifier(self, Filters.here(self), 1, playerOnDarkSideOfLocation));
    }
}
