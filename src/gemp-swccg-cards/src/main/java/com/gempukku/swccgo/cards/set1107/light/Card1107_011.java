package com.gempukku.swccgo.cards.set1107.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 7: Shadows Of The Empire
 * Type: Effect
 * Title: Uh-oh! (V)
 */
public class Card1107_011 extends AbstractNormalEffect {
    public Card1107_011() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Uh-oh!", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_7, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial law had no place in the Hutt's domain.");
        setGameText("Deploy on table. During your deploy phase, may ▼ one Clak'dor VII, Neb Dulo, or a musician. While you control Clak'dor VII, your musicians are power and forfeit +2. During your control phase, may retrieve 1 Force for each battleground site you control with a musician. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourMusicians = Filters.and(Filters.your(self), Filters.musician);
        ControlsCondition controlClakdor = new ControlsCondition(playerId, Filters.Clakdor_VII_system);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourMusicians, controlClakdor, 2));
        modifiers.add(new ForfeitModifier(self, yourMusicians, controlClakdor, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId deployId = GameTextActionId.OVS__UH_OH__DEPLOY_CLAKDOR_OR_MUSICIAN;
        Filter deployFilter = Filters.or(Filters.Clakdor_VII_system, Filters.title("Neb Dulo"), Filters.musician);

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, deployId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Clak'dor VII, Neb Dulo, or a musician from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, deployFilter, true));
            actions.add(action);
        }

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)) {
            final int amount = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground_site, Filters.controlsWith(playerId, self, Filters.musician)));
            if (amount > 0) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Retrieve " + amount + " Force");
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, amount));
                actions.add(action);
            }
        }

        return actions;
    }
}
