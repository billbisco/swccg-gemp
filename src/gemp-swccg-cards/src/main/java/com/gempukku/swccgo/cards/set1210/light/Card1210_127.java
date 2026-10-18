package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Subtype: Immediate
 * Title: I Can't Believe He's Gone
 */
public class Card1210_127 extends AbstractImmediateEffect {
    public Card1210_127() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.I_Cant_Believe_Hes_Gone, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Even though Luke felt the pain of losing his mentor, Obi-Wan continued to give him strength and guidance through the Force.");
        setGameText("If Obi-Wan is out of play, use 1 Force to deploy on table. During battle, may use 1 Force to add 5 to your total power there for remainder of turn. Once per turn, if Luke a captive, may activate 1 Force. (Immune to Control.)");
        addIcons(Icon.TATOOINE);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.isOutOfPlay(game, Filters.ObiWan);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.isOutOfPlay(game, Filters.ObiWan);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canUseForce(game, playerId, 1)) {
            final PhysicalCard battleLocation = game.getGameState().getBattleLocation();
            if (battleLocation != null) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Add 5 to total power");
                action.setActionMsg("Add 5 to your total power at the battle location for remainder of turn");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                // Perform result(s)
                action.appendEffect(
                        new AddUntilEndOfTurnModifierEffect(action,
                                new TotalPowerModifier(self, Filters.sameCardId(battleLocation), 5, playerId),
                                "Adds 5 to total power"));
                actions.add(action);
            }
        }

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canActivateForce(game, playerId)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Luke, Filters.captive))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Activate 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            actions.add(action);
        }
        return actions;
    }
}
