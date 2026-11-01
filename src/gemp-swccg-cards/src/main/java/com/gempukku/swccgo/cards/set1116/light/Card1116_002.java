package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Alien
 * Title: Senator Jar Jar Binks
 */
public class Card1116_002 extends AbstractAlien {
    public Card1116_002() {
        super(Side.LIGHT, 2, 2, 3, 3, 4, "Senator Jar Jar Binks", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setPolitics(2);
        setGameText("Agendas: justice, order. At same and related sites, vehicle weapon destiny draws are -2. Once per turn, may deploy an electropole, a [Coruscant] Naboo location, or a [Theed Palace] vehicle from Reserve Deck; reshuffle. If at Senate, may place Jar Jar and a [Coruscant] Political Effect out of play.");
        addIcons(Icon.THEED_PALACE, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.JUSTICE));
        modifiers.add(new AgendaModifier(self, Agenda.ORDER));
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.vehicle_weapon, -2, Filters.at(Filters.sameOrRelatedSite(self))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THERON_NETT_V__DEPLOY_ABOARD;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.or(
                    Filters.title("electropole"),
                    Filters.and(Icon.CORUSCANT, Filters.Naboo_location),
                    Filters.and(Icon.THEED_PALACE, Filters.vehicle)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
