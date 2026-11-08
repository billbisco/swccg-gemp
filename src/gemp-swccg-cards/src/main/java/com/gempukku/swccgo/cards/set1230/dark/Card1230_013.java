package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelDuelModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Imperial
 * Title: Lord Vader
 * Text: the Virtual Cards Master List (PC errata of the Block 3 sheet text "May deploy an opponent's lightsaber just
 * lost from same site on him (for free)"; that original is 2230_13).
 */
public class Card1230_013 extends AbstractImperial {
    public Card1230_013() {
        super(Side.DARK, 1, 6, 6, 6, 8, "Lord Vader", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Forgiving administrator of Imperial policy. Emperor Palpatine's most trusted leader. Believes converting Skywalker is key to the Alliance's downfall.");
        setGameText("Adds 3 to power of anything he pilots. May steal (and then use) an opponent's lightsaber just lost from same site; may then use two weapons. While The Quick And Easy Path on table, Jedi Escape and What Was It? may not cancel duels. Immune to attrition < 5.");
        addPersona(Persona.VADER);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        // "(and then use)": he may use a lightsaber he stole
        modifiers.add(new MayUseWeaponModifier(self, Filters.and(Filters.stolen, Filters.lightsaber)));
        modifiers.add(new IconModifier(self, new ArmedWithCondition(self, Filters.lightsaber), Icon.WARRIOR));
        modifiers.add(new MayNotCancelDuelModifier(self,
                Filters.or(Filters.title(Title.Jedi_Escape), Filters.title(Title.What_Was_It)),
                new OnTableCondition(self, Filters.title(Title.The_Quick_And_Easy_Path))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LORD_VADER_V__DEPLOY_OPPONENT_LIGHTSABER;
        Filter opponentsLightsaberInLostPile = Filters.and(Filters.lightsaber, Filters.inLostPile(game.getOpponent(playerId)));
        Set<PhysicalCard> lightsabers = new LinkedHashSet<PhysicalCard>();

        // An opponent's lightsaber just lost from same site
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.lightsaber), Filters.sameSite(self))) {
            PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            lightsabers.addAll(Filters.filter(Collections.singletonList(justLostCard), game, self, TargetingReason.TO_BE_STOLEN, opponentsLightsaberInLostPile));
        }
        // A lightsaber lost from same site along with an opponent's character
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.sameSite(self))) {
            PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            lightsabers.addAll(Filters.filter(justLostCard.getCardsPreviouslyAttached(), game, self, TargetingReason.TO_BE_STOLEN, opponentsLightsaberInLostPile));
        }
        if (lightsabers.isEmpty()) {
            return null;
        }

        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        for (PhysicalCard saber : lightsabers) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Steal " + GameUtils.getFullName(saber));
            action.setActionMsg("Steal " + GameUtils.getCardLink(saber) + " onto " + GameUtils.getCardLink(self));
            action.appendEffect(
                    new StealCardAndAttachFromLostPileEffect(action, playerId, self, Filters.sameCardId(saber)));
            actions.add(action);
        }
        return actions;
    }
}
