package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Droid
 * Title: R5-M2 (Arfive-Emmtoo)
 */
public class Card1220_098 extends AbstractDroid {
    public Card1220_098() {
        super(Side.LIGHT, 4, 1, 1, 3, "R5-M2 (Arfive-Emmtoo)", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Programmed to pilot sublight tactical courses. R5 units are in high demand for combat starships. R5-M2 helped plan evacuation routes from Echo Base. Owned by Shawn Valdez.");
        setGameText("Your capital starships are deploy -1 and are destiny +2 when drawn for weapon or battle destiny. Once per game, Shawn Valdez may deploy for free to same site.");
        addIcons(Icon.NAV_COMPUTER, Icon.HOTH);
        addModelType(ModelType.ASTROMECH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourCapitals = Filters.and(Filters.your(self), Filters.capital_starship);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, yourCapitals, -1));
        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, yourCapitals, 2));
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, yourCapitals, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__R5M2_V__DEPLOY_SHAWN_VALDEZ;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isAtLocation(game, self, Filters.site)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Shawn Valdez for free");
            action.setActionMsg("Deploy Shawn Valdez for free to same site");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.title("Shawn Valdez"), Filters.sameSite(self), true, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
