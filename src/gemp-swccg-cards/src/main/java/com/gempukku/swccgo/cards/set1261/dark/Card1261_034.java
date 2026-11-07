package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.List;

/** Legacy Block 6.1 Fleet Security Protocols. */
public class Card1261_034 extends AbstractNormalEffect {
    private static final Filter DOWNLOADABLE_SITE = Filters.and(Filters.non_unique, Filters.site,
            Filters.or(Filters.starship_site, Filters.vehicle_site));

    public Card1261_034() {
        super(Side.DARK, 3f, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Fleet Security Protocols",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Since the debacle at Yavin, the Emperor places a premium on the security of his costly war machine.");
        setGameText("Deploy on table. During battle involving your starship (or vehicle), it is power +2 for each of your characters at its related sites (they may be forfeited to reduce battle damage and attrition). Once per turn, may [Deploy] a non-unique starship (or vehicle) site (if such sites leave table, place them and all your cards there in Used Pile). [Immune to Alter.]");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter vessels = Filters.and(Filters.your(self), Filters.or(Filters.starship, Filters.vehicle),
                Filters.participatingInBattle);
        return List.of(new PowerModifier(self, vessels, new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState state, ModifiersQuerying modifiers, PhysicalCard vessel) {
                return 2 * Filters.countActive(state.getGame(), self, Filters.and(Filters.your(self), Filters.character,
                        Filters.at(Filters.siteOfStarshipOrVehicle(vessel))));
            }
        }));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__FLEET_SECURITY_PROTOCOLS__DEPLOY_VESSEL_SITE;
        if (!GameConditions.isOncePerTurn(game, self, playerId, sourceId, id)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id))
            return null;
        var action = new TopLevelGameTextAction(self, playerId, sourceId, id);
        action.setText("Deploy non-unique starship or vehicle site");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, DOWNLOADABLE_SITE, true));
        return List.of(action);
    }
}
