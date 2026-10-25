package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilStartOfPlayersNextTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Rebel
 * Title: Romas 'Lock' Navander
 */
public class Card1220_100 extends AbstractRebel {
    public Card1220_100() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Romas 'Lock' Navander", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Corellian pilot. Defected from the Empire shortly after graduation from the Academy. Tech communications officer at Echo Base. Relays orders to nearby Rebel starships.");
        setGameText("Adds 2 to power of anything he pilots. During your deploy phase, if present with a scomp link, may deploy a battleground system on table from Reserve Deck; reshuffle. If at Echo Command Center, may use 1 Force to suspend 'Hoth Energy Shield Rules' until start of your next turn.");
        addIcons(Icon.PILOT, Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ROMAS_V__DEPLOY_BATTLEGROUND_SYSTEM;

        boolean withScomp = GameConditions.isAtLocation(game, self, Filters.icon(Icon.SCOMP_LINK))
                || GameConditions.isPresentWith(game, self, Filters.has_Scomp_link);
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && withScomp
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction deployAction = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            deployAction.setText("Deploy battleground system");
            deployAction.setActionMsg("Deploy a battleground system from Reserve Deck");
            deployAction.appendEffect(
                    new DeployCardFromReserveDeckEffect(deployAction, Filters.battleground_system, true));
            actions.add(deployAction);
        }

        if (GameConditions.isAtLocation(game, self, Filters.Echo_Command_Center)
                && GameConditions.canUseForce(game, playerId, 1)) {
            final TopLevelGameTextAction shieldAction = new TopLevelGameTextAction(self, gameTextSourceCardId);
            shieldAction.setText("Suspend Hoth Energy Shield Rules");
            shieldAction.setActionMsg("Suspend 'Hoth Energy Shield Rules' until the start of " + playerId + "'s next turn");
            shieldAction.appendCost(
                    new UseForceEffect(shieldAction, playerId, 1));
            shieldAction.appendEffect(
                    new AddUntilStartOfPlayersNextTurnModifierEffect(shieldAction, playerId,
                            new SpecialFlagModifier(self, ModifierFlag.HOTH_ENERGY_SHIELD_RULES_SUSPENDED),
                            "Hoth Energy Shield Rules suspended until the start of " + playerId + "'s next turn"));
            actions.add(shieldAction);
        }
        return actions;
    }
}
