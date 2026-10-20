package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyAndChooserChoosesInsteadEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOneWeaponTwicePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Alien
 * Title: Han Solo, Innocent Scoundrel
 */
public class Card1281_027 extends AbstractAlien {
    public Card1281_027() {
        super(Side.LIGHT, 1, 4, 4, 3, 7, "Han Solo, Innocent Scoundrel", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("'Well, that was a long time ago. I'm sure he's forgotten about that.' Since he needed Calrissian's help, Han regretted having introduced Lando to the Tonnika sisters.");
        setGameText("Adds 3 to the power of anything he pilots. Draws one battle destiny if unable to otherwise. During battle, Han may fire a (Virtual Block 8) blaster twice. Once per turn, if opponent is about to draw a weapon destiny targeting Han, they draw two and you choose one. Immune to attrition < 3 (<4 if armed).");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addPersona(Persona.HAN);
    }

    @Override
    public final boolean hasSpecialDefenseValueAttribute() {
        return true;
    }

    @Override
    public final float getSpecialDefenseValue() {
        return 5;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new MayFireOneWeaponTwicePerBattleModifier(self, new DuringBattleCondition(), Filters.and(Icon.LEGACY_BLOCK_8, Filters.blaster)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 4, new ArmedWithCondition(self, Filters.weapon))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HAN_SOLO_INNOCENT_SCOUNDREL__WEAPON_DESTINY_CHOOSE;
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isAboutToDrawWeaponDestinyTargeting(game, effectResult, Filters.any, self)
                && TriggerConditions.isAboutToDrawWeaponDestiny(game, effectResult, opponent)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDrawDestinyAndChoose(game, 1)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Opponent draws two weapon destinies; choose one");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DrawDestinyAndChooserChoosesInsteadEffect(action, 2, 1, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
