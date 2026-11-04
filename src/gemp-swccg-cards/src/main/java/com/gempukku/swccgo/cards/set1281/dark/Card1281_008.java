package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireWeaponFiredByForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ResetLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ResetManeuverModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Device
 * Title: Jet Pack (V)
 */
public class Card1281_008 extends AbstractCharacterDevice {
    public Card1281_008() {
        super(Side.DARK, 4, Title.Jet_Pack, Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Mitrinomon Z-6 jet pack. Exhaust vents are used to maneuver in midflight. Gyro-stabilizer automatically applies counterthrust when landing.");
        setGameText("Use 2 Force to deploy on your alien warrior (free on Jodo Kast or any Fett). Character 'flies' (landspeed = 3 and maneuver = 6), may fire weapons for free, and may move as a 'react' for +1 Force.");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        modifiers.add(new ResetDeployCostToTargetModifier(self, (Condition) null, 0, Filters.or(Filters.Jodo, Filters.Fett)));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.alien, Filters.warrior);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.alien, Filters.warrior);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter character = Filters.hasAttached(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetLandspeedModifier(self, character, 3));
        modifiers.add(new ResetManeuverModifier(self, character, 6));
        modifiers.add(new MayFireWeaponFiredByForFreeModifier(self, character, Filters.weapon));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        PhysicalCard attachedTo = self.getAttachedTo();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__JET_PACK_V__MOVE_AS_REACT;

        // Check condition(s)
        if (attachedTo != null
                && (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent)
                || TriggerConditions.battleInitiated(game, effectResult, opponent))
                && Filters.canMoveAsReactAsActionFromOtherCard(self, false, 1, false).accepts(game, attachedTo)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Move as a 'react'");
            action.setActionMsg("Move " + GameUtils.getCardLink(attachedTo) + " as a 'react' for +1 Force");
            // Perform result(s)
            action.appendEffect(
                    new MoveAsReactEffect(action, attachedTo, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
