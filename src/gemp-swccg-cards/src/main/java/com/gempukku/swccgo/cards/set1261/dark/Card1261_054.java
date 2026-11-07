package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNonuniqueStarshipSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.List;

/** Legacy Block 6.1 Star Destroyer: Command Station. */
public class Card1261_054 extends AbstractNonuniqueStarshipSite {
    private static final Filter JIR = Filters.or(Filters.title("Commander Daine Jir"),
            Filters.title("Daine Jir"), Filters.title("Jir"));

    public Card1261_054() {
        super(Side.DARK, "Star Destroyer: Command Station", Filters.Star_Destroyer,
                Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLocationDarkSideGameText("If you control, related starship may not have its power reduced and may move as a 'react.' Once per game, if Devastator on table, may [download] Jir here.");
        setLocationLightSideGameText("Unless you control, related starship is immune to attrition < 4 or adds 2 to immunity (if on Devastator and opponent controls this site, immune to attrition).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.MOBILE, Icon.STARSHIP_SITE, Icon.SCOMP_LINK, Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String player, SwccgGame game, PhysicalCard self) {
        var related = Filters.relatedStarshipOrVehicle(self);
        var controls = new ControlsCondition(player, self);
        return List.of(new MayNotHavePowerReducedModifier(self, related, controls, player),
                new MayNotHavePowerReducedModifier(self, related, controls, game.getOpponent(player)));
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String player, SwccgGame game, PhysicalCard self) {
        var related = Filters.relatedStarshipOrVehicle(self);
        var doesNotControl = new NotCondition(new ControlsCondition(player, self));
        var alreadyImmune = Filters.alreadyHasImmunityToAttrition(self);
        return List.of(new ImmuneToAttritionLessThanModifier(self, Filters.and(related, Filters.not(alreadyImmune)),
                        doesNotControl, 4),
                new ImmunityToAttritionChangeModifier(self, Filters.and(related, alreadyImmune), doesNotControl, 2),
                new ImmuneToAttritionModifier(self, Filters.and(related, Filters.Devastator),
                        new ControlsCondition(game.getOpponent(player), self)));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String player, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__STAR_DESTROYER_COMMAND_STATION__DEPLOY_JIR;
        if (!GameConditions.canSpot(game, self, Filters.Devastator)
                || !GameConditions.isOncePerGame(game, self, id)
                || !GameConditions.canDeployCardFromReserveDeck(game, player, self, id))
            return null;
        var action = new TopLevelGameTextAction(self, player, sourceId, id);
        action.setText("Deploy Jir here from Reserve Deck");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new DeployCardToLocationFromReserveDeckEffect(action, JIR, Filters.here(self), true));
        return List.of(action);
    }
}
