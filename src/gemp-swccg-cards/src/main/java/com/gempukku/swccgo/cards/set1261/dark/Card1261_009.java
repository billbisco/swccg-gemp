package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFlippedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.Collections;
import java.util.List;

/** Legacy Block6.1 Commander Daine Jir. */
public class Card1261_009 extends AbstractImperial {
    public Card1261_009() {
        super(Side.DARK, 2, 3, 3, 3, 5, "Commander Daine Jir", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Typical Imperial leader. Uses constant training and crisis simulations to help maintain high performance levels. Commander Daine Jir is known to be bold and outspoken.");
        setGameText("While at a mobile site, your Imperials here are immune to Demotion, and, once per turn, may use 1 Force to deploy a unique (•) Imperial warrior (except a pilot) from Reserve Deck; reshuffle. While No One To Stop Us This Time on table, it may not flip.");
        addIcons(Icon.WARRIOR, Icon.A_NEW_HOPE);
        addKeywords(Keyword.LEADER, Keyword.COMMANDER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.Imperial, Filters.here(self)),
                        new AtCondition(self, Filters.mobile_site), Title.Demotion),
                new MayNotBeFlippedModifier(self, Filters.No_One_To_Stop_Us_This_Time));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        GameTextActionId id = GameTextActionId.LEGACY__COMMANDER_DAINE_JIR__DEPLOY_IMPERIAL_WARRIOR;
        if (GameConditions.isAtLocation(game, self, Filters.mobile_site)
                && GameConditions.isOncePerTurn(game, self, playerId, sourceId, id)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId, id);
            action.setText("Deploy unique Imperial warrior from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 1));
            // Printed use1 is a search cost; the deployed warrior still pays its normal deployment cost.
            action.appendEffect(new DeployCardFromReserveDeckEffect(action,
                    Filters.and(Filters.unique, Filters.Imperial, Filters.warrior, Filters.not(Filters.pilot)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
