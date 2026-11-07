package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToDeployCostModifiersToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 6.1 Hutt Influence (V). */
public class Card1261_032 extends AbstractNormalEffect {
    public Card1261_032() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Hutt_Influence, Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jabba's criminal empire extends to all reaches of the Outer Rim.");
        setGameText("Deploy on a pit. 'Imperial' on Human Shield may be treated as 'bounty hunter'. While I Shall Enjoy Watching You Die on table, bounty hunters are forfeit +2 and immune to Goo Nee Tay. Once per game, if Nal Hutta on table, may take All Wrapped Up into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.PREMIUM);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
            PlayCardOptionId id, boolean asReact) {
        return Filters.pit;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var watching = new OnTableCondition(self, Filters.I_Shall_Enjoy_Watching_You_Die);
        return List.of(
                new ForfeitModifier(self, Filters.bounty_hunter, watching, 2),
                new ImmuneToTitleModifier(self, Filters.bounty_hunter, watching, Title.Goo_Nee_Tay),
                new ImmuneToDeployCostModifiersToLocationModifier(self,
                        Filters.bounty_hunter, watching, Filters.Goo_Nee_Tay, Filters.any));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__HUTT_INFLUENCE_V__UPLOAD_ALL_WRAPPED_UP;
        if (!GameConditions.canSpot(game, self, Filters.Nal_Hutta_system)
                || !GameConditions.isOncePerGame(game, self, id)
                || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id))
            return null;
        var action = new TopLevelGameTextAction(self, playerId, sourceId, id);
        action.setText("Take All Wrapped Up into hand from Reserve Deck");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.All_Wrapped_Up, true));
        return List.of(action);
    }
}
