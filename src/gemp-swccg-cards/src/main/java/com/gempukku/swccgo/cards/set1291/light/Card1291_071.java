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
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
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
    /** Current text: the Competitive Balance Team errata of 2014-07-25 (PC-E3, PC forum "Current Rulings/Errata" p895797),
     * which corrected the errata posted 2014-07-23 (PC-E2, p894811): power 1, the permanent pilot adds 2 to power, and
     * no Dual Laser Cannon text (the posted text has none). */
    public static final String CURRENT_TEXT = "May add 1 pilot (suspends permanent pilot). May move as a 'react.' Permanent pilot provides ability of 2 and adds 2 to power. \"T-47\" on [Hoth] weapons may be treated as \"AT-RT\".";

    private final boolean _permanentPilotAddsTwoToPower;
    private final boolean _dualLaserCannonOnDeploy;
    private final boolean _t47Clause;

    public Card1291_071() {
        this(1, CURRENT_TEXT, true, false, true);
    }

    /** For the version cards (22xx): printed power, text and which clauses the version has. */
    protected Card1291_071(int power, String gameText, boolean permanentPilotAddsTwoToPower, boolean dualLaserCannonOnDeploy, boolean t47Clause) {
        super(Side.LIGHT, 3, 2, power, null, 3, 3, 3, "AT-RT", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setGameText(gameText);
        addIcons(Icon.CLONE_ARMY, Icon.PILOT, Icon.EPISODE_I);
        addModelType(ModelType.AT_RT);
        setPilotCapacity(1);
        _permanentPilotAddsTwoToPower = permanentPilotAddsTwoToPower;
        _dualLaserCannonOnDeploy = dualLaserCannonOnDeploy;
        _t47Clause = t47Clause;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        if (!_permanentPilotAddsTwoToPower)
            return Collections.singletonList(new AbstractPermanentPilot(2) {});
        return Collections.singletonList(new AbstractPermanentPilot(2) {
            @Override
            public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                List<Modifier> modifiers = new LinkedList<Modifier>();
                modifiers.add(new PowerModifier(self, 2));
                return modifiers;
            }
        });
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
        if (!_t47Clause)
            return modifiers;
        modifiers.add(new T47OnWeaponsTreatedAsModelTypeModifier(self, Filters.and(Filters.weapon, Icon.HOTH), ModelType.AT_RT));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__AT_RT__DEPLOY_DUAL_LASER_CANNON;
        if (_dualLaserCannonOnDeploy
                && TriggerConditions.justDeployed(game, effectResult, self)
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
