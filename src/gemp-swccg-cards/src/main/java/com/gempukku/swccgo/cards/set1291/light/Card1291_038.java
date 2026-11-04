package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyAndChooseInsteadEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Republic
 * Title: Clone Specialist
 */
public class Card1291_038 extends AbstractRepublic {
    public Card1291_038() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Clone Specialist", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setArmor(3);
        setLore("Clone trooper.");
        setGameText("Adds 2 to the power of anything he pilots. While piloting a [Clone Army] starship or vehicle, adds 1 to its defense value and he is forfeit +2. Once per battle, when starship or vehicle he is piloting fires a weapon, may draw two weapon destiny and choose one.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.EPISODE_I, Icon.CLONE_ARMY);
        addKeywords(Keyword.CLONE_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter cloneArmyStarshipOrVehicle = Filters.and(Icon.CLONE_ARMY, Filters.or(Filters.starship, Filters.vehicle));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DefenseValueModifier(self, Filters.and(cloneArmyStarshipOrVehicle, Filters.hasPiloting(self)), 1));
        modifiers.add(new ForfeitModifier(self, new PilotingCondition(self, cloneArmyStarshipOrVehicle), 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CLONE_SPECIALIST__DRAW_TWO_WEAPON_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isAboutToDrawWeaponDestiny(game, effectResult, playerId, Filters.any)
                && isFiredByStarshipOrVehicleHePilots(game, self)
                && GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDrawDestinyAndChoose(game, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw two weapon destiny and choose one");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyAndChooseInsteadEffect(action, 2, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    /**
     * Determines if the weapon being fired is fired by (or is mounted on, or is a permanent weapon of) a starship or
     * vehicle this character is piloting.
     */
    private static boolean isFiredByStarshipOrVehicleHePilots(SwccgGame game, PhysicalCard self) {
        WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
        if (weaponFiringState == null)
            return false;
        Filter pilotedStarshipOrVehicle = Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.hasPiloting(self));
        PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();
        if (cardFiringWeapon != null && pilotedStarshipOrVehicle.accepts(game, cardFiringWeapon))
            return true;
        PhysicalCard weapon = weaponFiringState.getCardFiring();
        if (weapon != null && weapon.getAttachedTo() != null && pilotedStarshipOrVehicle.accepts(game, weapon.getAttachedTo()))
            return true;
        SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
        return permanentWeapon != null && pilotedStarshipOrVehicle.accepts(game, permanentWeapon.getPhysicalCard(game));
    }
}
