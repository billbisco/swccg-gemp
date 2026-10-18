package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfWeaponFiringModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Rebel
 * Title: Dack Ralter
 */
public class Card1273_022 extends AbstractRebel {
    public Card1273_022() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Dack Ralter", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Born in an Imperial labor camp on Kalist 6. Joined the Rebellion after his escape. Young student of Commander Skywalker. Experienced crackshot gunner.");
        setGameText("Adds 2 to the power of anything he pilots. Heroic. Rogue Squadron Pilot. During battle when Dack (or a starship or vehicle he pilots) fires a weapon, may add 1 to total weapon destiny and hit targets are power -2. If Dack is about to leave table may place him in Used Pile instead.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.ROGUE_SQUADRON, Keyword.GUNNER, Keyword.HEROIC);
        addPersona(Persona.DACK);
        setMatchingVehicleFilter(Filters.Rogue_1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        GameTextActionId weaponActionId = GameTextActionId.LEGACY__DACK_RALTER_V__MODIFY_WEAPON_DESTINY;
        if (GameConditions.isDuringBattle(game)
                && (TriggerConditions.isAboutToCompleteWeaponDestinyDraw(game, effectResult)
                    || TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult))
                && game.getGameState().getWeaponFiringState() != null) {
            PhysicalCard weaponUser = game.getGameState().getWeaponFiringState().getCardFiringWeapon();
            if (weaponUser == self
                    || (weaponUser != null && Filters.hasPiloting(self).accepts(game, weaponUser))) {
                Collection<PhysicalCard> targets = game.getGameState().getWeaponFiringState().getTargets();
                PhysicalCard weapon = game.getGameState().getWeaponFiringState().getCardFiring();
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, weaponActionId);
                action.setText("Add 1 to total weapon destiny");
                action.setActionMsg("Add 1 to total weapon destiny and make hit targets power -2");
                if (weapon != null) {
                    action.appendEffect(new AddUntilEndOfWeaponFiringModifierEffect(action,
                            new TotalWeaponDestinyModifier(self, Filters.sameCardId(weapon), 1),
                            "Adds 1 to total weapon destiny"));
                }
                if (!targets.isEmpty()) {
                    action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                            new PowerModifier(self, Filters.and(Filters.in(targets), Filters.hit), -2),
                            "Hit targets are power -2"));
                }
                actions.add(action);
            }
        }
        List<OptionalGameTextTriggerAction> usedPile = getPlaceInUsedPileAction(game, effectResult, self, gameTextSourceCardId);
        if (usedPile != null) {
            actions.addAll(usedPile);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersWhenInactiveInPlay(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return getPlaceInUsedPileAction(game, effectResult, self, gameTextSourceCardId);
    }

    private List<OptionalGameTextTriggerAction> getPlaceInUsedPileAction(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__DACK_RALTER_V__PLACE_IN_USED_PILE;
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)
                && !TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, self)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place " + GameUtils.getFullName(self) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile instead");
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(self);
                            action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, self));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
