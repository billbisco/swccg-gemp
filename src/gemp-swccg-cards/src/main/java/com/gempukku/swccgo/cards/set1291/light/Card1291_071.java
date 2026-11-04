package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotSeatOccupiedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendPermanentPilotModifier;
import com.gempukku.swccgo.logic.modifiers.T47OnWeaponsTreatedAsModelTypeModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Vehicle
 * Subtype: Combat
 * Title: AT-RT
 */
public class Card1291_071 extends AbstractCombatVehicle {
    public Card1291_071() {
        super(Side.LIGHT, 3, 2, 3, null, 3, 3, 3, "AT-RT", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setGameText("May add 1 pilot (suspends permanent pilot). May move as a 'react.' Permanent pilot provides ability of 2. \"T-47\" on [Hoth] weapons may be treated as \"AT-RT\". When deployed, may [download] Dual Laser Cannon aboard.");
        addIcons(Icon.CLONE_ARMY, Icon.PILOT, Icon.EPISODE_I);
        addModelType(ModelType.AT_RT);
        setPilotCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendPermanentPilotModifier(self, new HasPilotSeatOccupiedCondition(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new T47OnWeaponsTreatedAsModelTypeModifier(self, Filters.and(Filters.weapon, Icon.HOTH), ModelType.AT_RT));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__AT_RT__DEPLOY_DUAL_LASER_CANNON;
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Dual Laser Cannon aboard from Reserve Deck");
            action.setActionMsg("Deploy Dual Laser Cannon aboard from Reserve Deck");
            action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action, Filters.Dual_Laser_Cannon, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
