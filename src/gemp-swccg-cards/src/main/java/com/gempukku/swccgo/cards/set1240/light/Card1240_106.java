package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
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
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyDestinyDrawsForActionSourceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Location
 * Subtype: Site
 * Title: Yavin 4: Jedi Academy
 */
public class Card1240_106 extends AbstractSite {
    public Card1240_106() {
        super(Side.LIGHT, "Yavin 4: Jedi Academy", Title.Yavin_4, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLocationLightSideGameText("If a Dark Jedi on table, Force drain -1 here. Once per game, may deploy Jedi Luke here from Reserve Deck; reshuffle.");
        setLocationDarkSideGameText("While opponent's non-[Episode I] Jedi here, destiny draws for Sense and Alter may not be modified.");
        setGameText("Light: If a Dark Jedi on table, Force drain -1 here. Once per game, may deploy Jedi Luke here from Reserve Deck; reshuffle. Dark: While opponent's non-[Episode I] Jedi here, destiny draws for Sense and Alter may not be modified.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.A_NEW_HOPE, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        return Collections.<Modifier>singletonList(new ForceDrainModifier(self, new OnTableCondition(self, Filters.Dark_Jedi), -1, playerOnLightSideOfLocation));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__YAVIN_4_JEDI_ACADEMY__DEPLOY_JEDI_LUKE;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Jedi Luke here from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.Luke, Filters.Jedi), Filters.sameLocationId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(playerOnDarkSideOfLocation);
        return Collections.<Modifier>singletonList(new MayNotModifyDestinyDrawsForActionSourceModifier(self, Filters.or(Filters.Sense, Filters.Alter),
                new OnTableCondition(self, Filters.and(Filters.owner(opponent), Filters.Jedi, Filters.not(Icon.EPISODE_I), Filters.here(self)))));
    }
}
