package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AbilityRequiredForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.List;

/** Legacy Block 5 Light, final printed Kashyyyk (75E). */
public class Card1250_076 extends AbstractSystem {
    public Card1250_076() {
        super(Side.LIGHT, Title.Kashyyyk, 6, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Once per turn, if this was your starting location, may deploy one Kashyyyk site from Reserve Deck; reshuffle.");
        setLocationDarkSideGameText("Total ability of 6 or more required for you to draw battle destiny here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.A_NEW_HOPE, Icon.PLANET, Icon.EPISODE_I);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation,
            SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.LEGACY__KASHYYYK_V__DEPLOY_SITE;
        if (GameConditions.isStartingLocation(game, self)
                && GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, id)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, id)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, id);
            action.setText("Deploy a Kashyyyk site");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardToSystemFromReserveDeckEffect(action,
                    Filters.and(Filters.site, Filters.canBeTargetedBy(self)), Title.Kashyyyk, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation,
            SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new AbilityRequiredForBattleDestinyModifier(self, self, 6, playerOnDarkSideOfLocation));
    }
}
