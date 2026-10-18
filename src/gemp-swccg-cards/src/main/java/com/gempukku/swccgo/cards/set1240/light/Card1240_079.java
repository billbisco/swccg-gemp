package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.ControlsWithEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Corellian Engineering Corporation
 */
public class Card1240_079 extends AbstractNormalEffect {
    public Card1240_079() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Corellian_Engineering_Corporation, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Only one of the 'Big Three' starship manufacturers to sell primarily to civilians. CEC employees take great delight in calling their highly modifiable designs 'stock'.");
        setGameText("Deploy on table if [Virtual Block 4] Watch Your Step on table. X = number of spaceport sites controlled by Corellians. While you control Corellia, your total battle destiny is +X, and once per turn, if you are about to lose Force (except from your card), may reduce loss by X. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    private static Filter virtualSuffix() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().hasVirtualSuffix();
            }
        };
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.and(Filters.Watch_Your_Step, virtualSuffix()));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, new ControlsCondition(playerId, Filters.Corellia_system),
                new ControlsWithEvaluator(self, playerId, Filters.spaceport_site, Filters.Corellian), playerId, true));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)
                && !TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, playerId, Filters.your(self))
                && GameConditions.canReduceForceLoss(game)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.controls(game, playerId, Filters.Corellia_system)) {

            int x = Filters.countTopLocationsOnTable(game, Filters.and(Filters.spaceport_site, Filters.controlsWith(playerId, self, Filters.Corellian)));
            if (x > 0) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Reduce Force loss by " + x);
                action.appendUsage(
                        new OncePerTurnEffect(action));
                action.appendEffect(
                        new ReduceForceLossEffect(action, playerId, x));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
